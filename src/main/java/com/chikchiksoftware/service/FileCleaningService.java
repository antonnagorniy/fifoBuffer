package com.chikchiksoftware.service;

import com.chikchiksoftware.FifoFileBuffer;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 28.02.2018.
 */
public class FileCleaningService implements Runnable {
    private FifoFileBuffer buffer;

    public FileCleaningService(FifoFileBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
            }catch(InterruptedException e) {
                e.printStackTrace();
            }

            buffer.fileDump();
        }
    }
}
