package com.liftindia.app.caldroid;

import java.util.HashMap;

public class MonthMap { // datewise
	private HashMap<Integer, DateMap> monthWisemap;

	public HashMap<Integer, DateMap> getMonthMap() {
		return monthWisemap;
	}

	public void setMonthMap(HashMap<Integer, DateMap> map) {
		this.monthWisemap = map;
	}
}
