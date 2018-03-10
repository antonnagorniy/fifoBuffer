package com.chikchiksoftware;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Consumer implements Runnable {

    private final FifoFileBuffer<Timestamp> buffer;

    public Consumer(FifoFileBuffer<Timestamp> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        boolean running = true;

        try {
            while(running) {
                System.out.println(Thread.currentThread().getName() + " Consumed " + buffer.take());
            }
        }catch(IOException e){
            running = false;
        }


    }
}
