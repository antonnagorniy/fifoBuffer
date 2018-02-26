package com.chikchiksoftware;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.stream.Stream;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */

public class FifoFileBuffer<T> {
    private final Object lock;
    private final File dataFile;
    private long count;
    private long index;


    public FifoFileBuffer() {
        lock = new Object();
        this.dataFile = new File(new Timestamp(System.currentTimeMillis()).getTime() + ".tmp");
    }

    public void put(T data) {

        checkNotNull(data);

        synchronized(lock) {
            try(FileWriter fileWriter = new FileWriter(dataFile, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                bufferedWriter.write(data.toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();
                count++;

            }catch(IOException e) {
                System.err.println(e.getMessage());
            }finally {
                lock.notifyAll();
            }
        }


    }

    public String take() {
        synchronized(lock) {

            String item = null;

            try(Stream<String> lines = Files.lines(Paths.get(dataFile.getName()))) {
                item = lines.skip(index).findFirst().get();
            }catch(IOException e) {
                System.err.println(e.getMessage());
            }

            index++;

            lock.notifyAll();
            return item;


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
        return (count == index);
    }

    public long getSize() {
        return (count - index);
    }

    public long getProducedItems() {
        return count;
    }

    public long getConsumedItems() {
        return index;
    }

    /**
     * Delete buffer's data file
     *
     *
     * @return boolean
     */
    public boolean deleteFile() {
        return dataFile.delete();
    }
}
