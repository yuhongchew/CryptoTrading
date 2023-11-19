package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repositories.TradingHistoryRepository;

@RestController
@RequestMapping("/api/trading-history")
public class TradingHistoryController {
	  
	@Autowired
	private TradingHistoryRepository tradingHistoryRepository;
}
