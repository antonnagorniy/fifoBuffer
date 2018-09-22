package com.chikchiksoftware;

import com.chikchiksoftware.service.Timer;

import java.io.IOException;

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
        try {
            while(true) {
                System.out.println(Thread.currentThread().getName() + " Consumed " + buffer.take());

                Timer.incConsumedItems();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
