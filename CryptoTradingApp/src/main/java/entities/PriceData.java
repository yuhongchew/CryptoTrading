package entities;

import java.math.BigDecimal;

public class PriceData {
	private String symbol;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public BigDecimal getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice;
	}
	public BigDecimal getAskPrice() {
		return askPrice;
	}
	public void setAskPrice(BigDecimal askPrice) {
		this.askPrice = askPrice;
	}


}
