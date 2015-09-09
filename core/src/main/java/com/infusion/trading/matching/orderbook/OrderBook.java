package com.infusion.trading.matching.orderbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.infusion.trading.matching.algo.IOrderPlacementAlgorithm;
import com.infusion.trading.matching.domain.LimitOrder;
import com.infusion.trading.matching.domain.OrderSide;

public class OrderBook {

	private IOrderArrivalTimeService arrivalTimeService;
	private IOrderPlacementAlgorithm orderPlacementAlgorithm;
	private List<LimitOrder> buyOrders = new LinkedList<LimitOrder>();
	private List<LimitOrder> sellOrders = new LinkedList<LimitOrder>();
	private final int TOP = 0;
	private String symbol;
	private final ReadWriteLock LOCK = new ReentrantReadWriteLock();

	public OrderBook(IOrderArrivalTimeService arrivalTimeService, IOrderPlacementAlgorithm orderPlacementAlgorithm, String symbol) {
		this.symbol = symbol;
		this.arrivalTimeService = arrivalTimeService;
		this.orderPlacementAlgorithm = orderPlacementAlgorithm;
	}

	public List<LimitOrder> getStagedOrders(OrderSide side) {
		List<LimitOrder> orders = getOrders(side);
		List<LimitOrder> stagedOrders = new ArrayList<LimitOrder>();
		for (LimitOrder order : orders) {
			if (order.isHoldInStaging()) {
				stagedOrders.add(order);
			}
		}
		return Collections.<LimitOrder> unmodifiableList(stagedOrders);
	}

	public void addLimitOrder(LimitOrder order) {

		order.setArrivalTimeInOrderBook(arrivalTimeService.getArrivalTimeInOrderBook());

		/*
		 * Want to lock the entire order book at one time. Only one order,
		 * regardless if it's buy or sell allowed in at one time
		 */

		LinkedList<LimitOrder> orders = (LinkedList<LimitOrder>) getOrders(order.getSide());

		int position = orderPlacementAlgorithm.findPositionToPlaceInBook(orders, order);

		if (position == 0) {
			orders.addFirst(order);
		}
		else if (position == -1) {
			orders.addLast(order);
		}
		else {
			orders.add(position, order);
		}

		/*
		 * Backup to DB while still locking. If you do it after lock is
		 * released, it could be stale
		 */
	}

	public LimitOrder retrieveOrder(OrderSide side) {

		LimitOrder order = null;
		List<LimitOrder> orders = getOrders(side);

		int start = TOP;

		while (orders.isEmpty() == false && start < orders.size()) {

			order = orders.get(start);

			if (order.isHoldInStaging() == false) {
				return order;
			}
			start++;
		}
		return null;
	}

	public void removeCompletedOrder(OrderSide side, boolean incomingOrderAllowsPartialFills) {

		List<LimitOrder> orders = getOrders(side);

		if (incomingOrderAllowsPartialFills == false) {
			orders.get(TOP).holdInStaging();
		}
		else {
			orders.remove(TOP);
		}

		/*
		 * Backup to DB while still locking. If you do it after lock is
		 * released, it could be stale
		 */
	}

	/*
	 * Think these through. Maybe need to synchronize and return them as a
	 * Pair()
	 */
	public List<LimitOrder> getBuyOrders() {
		return Collections.unmodifiableList(buyOrders);
	}

	public List<LimitOrder> getSellOrders() {
		return Collections.unmodifiableList(sellOrders);
	}

	/*
	 * ONLY FOR TESTING! Find a way to get rid of this
	 */
	public void clear() {
		sellOrders.clear();
		buyOrders.clear();
	}

	public boolean isLiquidityLeft(OrderSide side) {

		OrderSide sideToCheck = side.getOppositeSide();
		return getOrders(sideToCheck).isEmpty() == false;
	}

	public void revertStagedOrders(OrderSide side) {

		List<LimitOrder> orders = getOrders(side);

		for (LimitOrder order : orders) {

			if (order.isHoldInStaging()) {
				order.reset();
			}
		}
	}

	public void completeStagedOrders(OrderSide side) {

		List<LimitOrder> orders = getOrders(side);

		// This mess is necessary to avoid concurrent modification exception
		Iterator<LimitOrder> ordersIterator = orders.iterator();

		while (ordersIterator.hasNext()) {
			LimitOrder order = ordersIterator.next();
			if (order.isHoldInStaging()) {
				ordersIterator.remove();
			}
		}
	}

	private List<LimitOrder> getOrders(OrderSide side) {

		switch (side) {
			case BUY:
				return buyOrders;
			case SELL:
				return sellOrders;
			default:
				return null;
		}
	}

	public void lockForWrite() {
		LOCK.writeLock().lock();
	}

	public void unlockWriteLock() {
		LOCK.writeLock().unlock();
	}

	public void registerReader() {
		LOCK.readLock().lock();
	}

	public void unregisterReader() {
		LOCK.readLock().unlock();
	}

	public String getSymbol() {
		return symbol;
	}

	@Override
	public boolean equals(Object obectToTest) {
		if (obectToTest instanceof OrderBook) {
			OrderBook orderBookToTest = (OrderBook) obectToTest;

			if (orderBookToTest.getSymbol().equals(getSymbol()) && getBuyOrders().equals(getBuyOrders()) && orderBookToTest.getSellOrders().equals(getSellOrders())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {

		int hashCode = 32;
		hashCode += symbol.hashCode();

		if (buyOrders.isEmpty() == false) {
			hashCode += buyOrders.get(0).hashCode();
		}

		if (sellOrders.isEmpty() == false) {
			hashCode += sellOrders.get(0).hashCode();
		}

		return hashCode;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Symbol: ").append(symbol).append(" Buy Orders: ").append(buyOrders).append(" Sell Orders: ").append(sellOrders);
		return buffer.toString();
	}
}