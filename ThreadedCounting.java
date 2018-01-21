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
    	
    	/*
    	 * The main control loop.
    	 * 
    	 * This (main) thread is responsible for
    	 *  - iterating through from 1 to 100
    	 *  - informing a given thread that it is OK to proceed (to call counter.count(i))
    	 *  - Synchronising the activities of each counter thread and itself.
    	 *  
    	 *  For each integer between 1 and 100 inclusive.
    	 *      Inform the relevant counter thread, that it is OK to go.
    	 *      Execute a synchronised wait.
    	 *      
    	 *      note: each thread is responsible for assisting in this synchronisation by
    	 *      1) releasing the lock on the monitor.
    	 *      2) resetting value so that it is not OK to go (it's work is done for the moment
    	 *        until the main loop determines otherwise). 
    	 *      
    	 */
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

	/*
	 * This flag is used to control which Thread is able to take control of the lock
	 * when it becomes available.
	 * e.g. if the main loop relinquishes the lock on counter, in order for thread1
	 * to call counter.count(i), where i is one of the numbers of form 3n+1, it may
	 * be possible that thread2 acquires control of the lock, resulting in the wrong
	 * thread calling counter.count(i). 
	 */
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
					counter.notify();			// notify waiting threads.
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
