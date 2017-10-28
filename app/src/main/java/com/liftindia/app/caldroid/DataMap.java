package com.liftindia.app.caldroid;

import java.util.HashMap;

public class DataMap { // yearmonth wise
	private HashMap<Integer, MonthMap> yearMap;

	public HashMap<Integer, MonthMap> getYearMap() {
		return yearMap;
	}

	public void setYearMap(HashMap<Integer, MonthMap> map) {
		this.yearMap = map;
	}

}

