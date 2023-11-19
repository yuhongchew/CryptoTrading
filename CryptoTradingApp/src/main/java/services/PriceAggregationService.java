package services;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import entities.CryptoPair;
import entities.PriceData;
import repositories.CryptoPairRepository;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Service
public class PriceAggregationService {
	 @Autowired
	 private RestTemplate restTemplate;
	 
	 @Value("${binance.api.url}")
	 private String binanceUrl;
	 
	 @Value("${huobi.api.url}")
	 private String huobiUrl;

	    @Autowired
	    private CryptoPairRepository cryptoPairRepository;

	    public void fetchAndStorePrices() {
	    	java.util.List<CryptoPair> cryptoPairs = cryptoPairRepository.findAll();

	        for (CryptoPair cryptoPair : cryptoPairs) {
	           
	            PriceData binancePrice = fetchPriceFromApi(binanceUrl);
	            PriceData huobiPrice = fetchPriceFromApi(huobiUrl);

	            BigDecimal bestBidPrice = binancePrice.getBidPrice().max(huobiPrice.getBidPrice());
	            BigDecimal bestAskPrice = binancePrice.getAskPrice().min(huobiPrice.getAskPrice());


	        }
	    }

	    private PriceData fetchPriceFromApi(String apiUrl) {
	        try {
	            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
	                    apiUrl,
	                    HttpMethod.GET,
	                    null,
	                    new ParameterizedTypeReference<Map<String, Object>>() {
	                    });

	            Map<String, Object> responseBody = response.getBody();

	            if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
	                PriceData priceData = new PriceData();
	                priceData.setSymbol(responseBody.get("symbol").toString());
	                priceData.setBidPrice(new BigDecimal(responseBody.get("bidPrice").toString()));
	                priceData.setAskPrice(new BigDecimal(responseBody.get("askPrice").toString()));
	                return priceData;
	            }
	        } catch (Exception e) {
	            // Handle exceptions (e.g., network issues, API changes)
	            e.printStackTrace();
	        }

	        return null;
	    }

		public PriceData getLatestPrices(Long pairId) {
	        CryptoPair cryptoPair = cryptoPairRepository.findById(pairId)
	                .orElseThrow(() -> new RuntimeException("Crypto pair not found"));

	        PriceData binancePrice = fetchPriceFromApi(binanceUrl);
	        PriceData huobiPrice = fetchPriceFromApi(huobiUrl);

	        if (binancePrice != null && huobiPrice != null) {
	            // Determine best prices for buy and sell orders
	            BigDecimal bestBidPrice = binancePrice.getBidPrice().max(huobiPrice.getBidPrice());
	            BigDecimal bestAskPrice = binancePrice.getAskPrice().min(huobiPrice.getAskPrice());

	            // Create a new PriceData object with the best prices
	            PriceData latestPrices = new PriceData();
	            latestPrices.setSymbol(cryptoPair.getPairName());
	            latestPrices.setBidPrice(bestBidPrice);
	            latestPrices.setAskPrice(bestAskPrice);

	            return latestPrices;
	        } else {
	            throw new RuntimeException("Failed to fetch prices from one or more sources");
	        }
		}


}
