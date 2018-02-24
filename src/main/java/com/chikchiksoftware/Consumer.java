package com.chikchiksoftware;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Consumer implements Runnable {

    private final FifoFileBuffer buffer;

    public Consumer(FifoFileBuffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        boolean done = false;
        do{
            try {
                System.out.println(Thread.currentThread().getName() + " Consumed " + buffer.take());
            }catch(Exception e) {
                done = true;
            }
        }while(!done);
    }
}
