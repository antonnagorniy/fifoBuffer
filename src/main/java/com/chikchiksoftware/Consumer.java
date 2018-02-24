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

    @Override
    public void run() {
        boolean done = false;
        while(!done){
            try {
                System.out.println(Thread.currentThread().getName() + " Consumed " + buffer.take());
            }catch(Exception e) {
                done = true;
            }
        }
    }
}
