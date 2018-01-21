A General description of how this program works:

There is a Main Thread and three counter threads.

First of all the main thread locks the counter monitor.
The Main Thread runs a loop, iterating from 1 to 100 inclusive. 
At each iteration of "i", the main thread determines which form "i" is (3n, 3n+1, 3n+2).

Once the main thread has determined which form the current iteration, "i" is, it then causes the appropriate counter thread to call counter.count(i). It does this by setting a flag which is an instance variable of the thread, then executes the .wait() call on the counter, releasing the lock, allowing the thread to call the appropriate value. 

Without the main thread using the lock, the loop and the main threads instructions to each
thread will run out of sync with the calls to counter.count(i).