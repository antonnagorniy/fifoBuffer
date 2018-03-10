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
                Thread.sleep(1000);
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
            while(buffer.getDataFileLength() > buffer.getDataFileMaxLength()) {
                try {
                    System.out.println("File current size: " + (buffer.getDataFileLength() / 1024) + "Kb");
                    System.out.println("File cleaning in progress...");
                    buffer.fileDump();
                    System.out.println("File cleaned.");
                    System.out.println("File new size: " + (buffer.getDataFileLength() / 1024) + "Kb");
                }catch(Exception e) {
                    System.err.println("Cleaning Service thread failed:");
                    e.printStackTrace();
                }
            }


        }

    }
}
