package com.targoddess.threads;

import java.util.ArrayList;
import java.util.List;

public class Counter {
	private List<Integer> counterList = new ArrayList<Integer>();
	
	public void count(int i) {
		counterList.add(i);
	}
	
	public List<Integer> getList() {
		return counterList;
	}
}
