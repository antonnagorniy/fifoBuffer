package com.chikchiksoftware;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */

public class FifoFileBuffer<T> {
    private final Object lock = new Object();
    private final File dataFile = new File("/data.txt");
    private long producedItems;
    private long consumedItems;
    private int size;
    private int index;


    public FifoFileBuffer() {
        this.index = 0;
    }

    public void put(T data) {

        checkNotNull(data);

        synchronized(lock) {
            try(FileWriter fileWriter = new FileWriter(dataFile, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                bufferedWriter.write(data.toString());
                bufferedWriter.newLine();
                size++;
                producedItems++;
            }catch(IOException e) {
                System.err.println(e.getMessage());
            }

            lock.notifyAll();
        }


    }

    public String take() throws InterruptedException, IOException{
        synchronized(lock) {
            while(isEmpty()) {
                lock.wait();
            }

            String line;
            try (Stream<String> lines = Files.lines(Paths.get("data.txt"))) {
                line = lines.skip(index).findFirst().get();
            }

            checkNotNull(line);
            size--;
            index++;
            consumedItems++;
            return line;
        }
    }

    /**
     * Throws NullPointerException if argument is null.
     *
     * @param v the element
     */
    private static void checkNotNull(Object v) {
        if (v == null)
            throw new NullPointerException();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public long getProducedItems() {
        return producedItems;
    }

    public long getConsumedItems() {
        return consumedItems;
    }


}
