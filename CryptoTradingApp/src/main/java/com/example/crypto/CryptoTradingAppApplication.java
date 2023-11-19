package com.example.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import services.PriceAggregationService;

@SpringBootApplication
public class CryptoTradingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoTradingAppApplication.class, args);
	}
	
	@Configuration
	public class AppConfig {

	    @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }
	}
	
	@Component
	public class PriceAggregationScheduler {

	    @Autowired
	    private PriceAggregationService priceAggregationService;

	    @Scheduled(fixedRate = 10000) 
	    public void fetchAndStorePrices() {
	        priceAggregationService.fetchAndStorePrices();
	    }
	}

}
