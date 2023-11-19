package controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import repositories.TradingHistoryRepository;
import repositories.UserRepository;
import repositories.WalletRepository;
import services.PriceAggregationService;
import entities.PriceData;
import entities.TradingHistory;
import entities.Wallet;

@RestController
@RequestMapping("/api/user")
public class User {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletRepository walletRepository; 
    
    @Autowired 
    private TradingHistoryRepository tradingHistoryRepository;
    
    @Autowired
    private PriceAggregationService priceAggregationService;
    
//    @PostMapping("/buy")
//    public ResponseEntity<String> buyCrypto(@RequestParam Long userId, @RequestParam Long pairId, @RequestParam BigDecimal quantity) {
//        // Implement logic to handle the buy transaction
//        // Update user's wallet balance, record the transaction in trading history, etc.
//        // Return a success message or handle errors
//
//        return ResponseEntity.ok("Buy transaction successful");
//    }
//    
//    @PostMapping("/sell")
//    public ResponseEntity<String> sellCrypto(@RequestParam Long userId, @RequestParam Long pairId, @RequestParam BigDecimal quantity) {
//        // Implement logic to handle the sell transaction
//        // Update user's wallet balance, record the transaction in trading history, etc.
//        // Return a success message or handle errors
//
//        return ResponseEntity.ok("Sell transaction successful");
//    }
    
    @GetMapping("/transactions")
    public ResponseEntity<List> viewTransactions(@RequestParam Long userId) {
        List transactions = tradingHistoryRepository.findByUserId(userId);

        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> viewWalletBalance(@RequestParam Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId);

        if (wallet != null) {
            return ResponseEntity.ok(wallet.getBalance());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/trade")
    public ResponseEntity<String> tradeCrypto(
            @RequestParam Long userId,
            @RequestParam Long pairId,
            @RequestParam BigDecimal quantity,
            @RequestParam String action // "buy" or "sell"
    ) {
        // Fetch the latest aggregated prices
        PriceData latestPrices = priceAggregationService.getLatestPrices(pairId);

        if (latestPrices == null) {
            return ResponseEntity.badRequest().body("Unable to fetch latest prices. Please try again later.");
        }

        // Determine whether it's a buy or sell action
        if ("buy".equalsIgnoreCase(action)) {
            return buyCrypto(userId, pairId, quantity, latestPrices.getAskPrice());
        } else if ("sell".equalsIgnoreCase(action)) {
            return sellCrypto(userId, pairId, quantity, latestPrices.getBidPrice());
        } else {
            return ResponseEntity.badRequest().body("Invalid action. Use 'buy' or 'sell'.");
        }
    }

    private ResponseEntity<String> buyCrypto(Long userId, Long pairId, BigDecimal quantity, BigDecimal askPrice) {
        Wallet userWallet = walletRepository.findByUserId(userId);

        if (userWallet == null) {
            return ResponseEntity.badRequest().body("User wallet not found.");
        }

        // Calculate the total cost of the purchase
        BigDecimal totalCost = askPrice.multiply(quantity);

        // Check if the user has sufficient balance
        if (userWallet.getBalance().compareTo(totalCost) < 0) {
            return ResponseEntity.badRequest().body("Insufficient balance for the transaction.");
        }

        // Update user's wallet balance (deduct the total cost)
        userWallet.setBalance(userWallet.getBalance().subtract(totalCost));
        walletRepository.save(userWallet);

        // Record the transaction in the trading history
        TradingHistory tradingHistory = new TradingHistory();
        tradingHistory.setUserId(userId);
        tradingHistory.setPairId(pairId);
        tradingHistory.setQuantity(quantity);
        tradingHistory.setPrice(askPrice);
        tradingHistory.setTransactionType("BUY");
        tradingHistory.setTransactionTime(LocalDateTime.now());
        tradingHistoryRepository.save(tradingHistory);

        return ResponseEntity.ok("Buy transaction successful");
    }

    private ResponseEntity<String> sellCrypto(Long userId, Long pairId, BigDecimal quantity, BigDecimal bidPrice) {
        Wallet userWallet = walletRepository.findByUserId(userId);

        if (userWallet == null) {
            return ResponseEntity.badRequest().body("User wallet not found.");
        }

        // Check if the user has sufficient quantity to sell
        BigDecimal userHolding = getUserHolding(userId, pairId);
        if (userHolding.compareTo(quantity) < 0) {
            return ResponseEntity.badRequest().body("Insufficient quantity to sell.");
        }

        // Calculate the total earning from the sale
        BigDecimal totalEarning = bidPrice.multiply(quantity);

        // Update user's wallet balance (add the total earning)
        userWallet.setBalance(userWallet.getBalance().add(totalEarning));
        walletRepository.save(userWallet);

        // Record the transaction in the trading history
        TradingHistory tradingHistory = new TradingHistory();
        tradingHistory.setUserId(userId);
        tradingHistory.setPairId(pairId);
        tradingHistory.setQuantity(quantity);
        tradingHistory.setPrice(bidPrice);
        tradingHistory.setTransactionType("SELL");
        tradingHistory.setTransactionTime(LocalDateTime.now());
        tradingHistoryRepository.save(tradingHistory);

        return ResponseEntity.ok("Sell transaction successful");
    }
    
    private BigDecimal getUserHolding(Long userId, Long pairId) {
        // Retrieve the total quantity of the given pair that the user currently holds
        BigDecimal totalHolding = tradingHistoryRepository.getTotalQuantityForUserAndPair(userId, pairId);
        if (totalHolding == null) {
            return BigDecimal.ZERO;
        }
        return totalHolding;
    }

}
