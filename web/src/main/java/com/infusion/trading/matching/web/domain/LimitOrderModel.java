package com.infusion.trading.matching.web.domain;

import com.infusion.trading.matching.domain.Order;
import com.infusion.trading.matching.domain.OrderSide;

public class LimitOrderModel {

	private String symbol;
	private Double limitPrice;
	private Integer quantity;
	private OrderSide side;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(Double limitPrice) {
		this.limitPrice = limitPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setSide(OrderSide side) {

		this.side = side;
	}

	public OrderSide getSide() {

		return side;
	}

	@Override
	public String toString() {
		return "LimitOrderModel [symbol=" + symbol + ", limitPrice=" + limitPrice + ", quantity=" + quantity + "]";
	}

}