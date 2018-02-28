package com.chikchiksoftware.service;

import com.chikchiksoftware.FifoFileBuffer;

import java.sql.Timestamp;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 28.02.2018.
 */
public class FileCleaningService implements Runnable {
    private FifoFileBuffer<Timestamp> buffer;

    public FileCleaningService(FifoFileBuffer<Timestamp> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while(true) {
            try {
                if(buffer.getDataFileLength() < buffer.getDataFileMaxLength()) {
                    Thread.sleep(1000);
                }else {
                    System.out.println("File cleaning in progress...");
                    buffer.fileDump();
                    System.out.println("File cleaned.");
                }
            }catch(Exception e) {
                System.err.println("Service thread failed: " + e.getMessage());
            }
        }
    }
}
