package com.targoddess.threads;

public class ThreadedCounting {
	
	private static final int constantDifference = 3;

    public static void countInThreads(Counter counter) {    	
    	CounterThread counterThread1 = new CounterThread(counter, constantDifference, 1);
    	CounterThread counterThread2 = new CounterThread(counter, constantDifference, 2);
    	CounterThread counterThread3 = new CounterThread(counter, constantDifference, 3);
    	
    	counterThread1.start();
    	counterThread2.start();
    	counterThread3.start();
    	
    	synchronized (counter) {
    		for (int i = 1; i <= 100; i++) {
    			if ((i % constantDifference) == 1) {
    				counterThread1.setCanCallCount(true);
    				try {
						counter.wait();
					} catch (InterruptedException e) {
					}
    			}
    			else if ((i % constantDifference) == 2) {
    				counterThread2.setCanCallCount(true);
    				try {
						counter.wait();
					} catch (InterruptedException e) {
					}
    			}
    			else if ((i % constantDifference) == 0) {
    				counterThread3.setCanCallCount(true);
    				try {
						counter.wait();
					} catch (InterruptedException e) {
					}
    			}
    		}
    	}
    	try {
    		counterThread1.join();
    		counterThread2.join();
    		counterThread3.join();
    	} catch (InterruptedException e) {
    		
    	}
    }
}

class CounterThread extends Thread {
	private Counter counter;
	private Integer constantDifference;
	private Integer initialValue;
	private boolean done;
	private boolean canCallCount;
	
	CounterThread(Counter counter, Integer constantDifference, Integer initialValue) {
		this.counter = counter;
		this.constantDifference = constantDifference;
		this.initialValue = initialValue;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public void setCanCallCount(boolean canCallCount) {
		this.canCallCount = canCallCount;
	}
	
	public boolean canCallCount() {
		return canCallCount;
	}
	
	@Override
	public void run() {
		//String tag = Thread.currentThread().getName() + ":\t\t";
		int i = initialValue;
		
		while (!isDone()) {
			if (canCallCount()) {
				synchronized (counter) {
					counter.count(i);
					counter.notify();
				}
				i += constantDifference;
				setCanCallCount(false);
				if (i > 100) {
					setDone(true);
				}
			}
		}
	}
}
