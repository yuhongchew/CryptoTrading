package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entities.Wallet;
import repositories.WalletRepository;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
	
	 @Autowired
	 private WalletRepository walletRepository;

	 @GetMapping("/{userId}")
	 public ResponseEntity<Wallet> getUserWallet(@PathVariable Long userId) {
	      Wallet wallet = walletRepository.findByUserId(userId);

	      if (wallet != null) {
	           return ResponseEntity.ok(wallet);
	      } else {
	           return ResponseEntity.notFound().build();
	      }
	}

}
