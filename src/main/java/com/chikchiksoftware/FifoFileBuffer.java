package com.chikchiksoftware;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final String fileName = new Timestamp(System.currentTimeMillis()).getTime() + ".tmp";
    private File dataFile;
    private long count;
    private long offset;
    private long consumed;
    private final long dataFileMaxLength;


    public FifoFileBuffer() {
        lock = new Object();
        this.dataFile = new File(fileName);
        dataFile.deleteOnExit();
        this.dataFileMaxLength = 104857600;
    }

    /**
     * Inserts the specified element at the head of this buffer
     *
     * @param data the element to put
     */
    public void put(T data) {

        synchronized(lock) {
            try {
                while(getDataFileLength() > dataFileMaxLength) {
                    lock.wait();
                }
            }catch(InterruptedException e) {
                System.err.println("Waiting for file dump interrupted.");
            }

            try(FileWriter fileWriter = new FileWriter(dataFile, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                bufferedWriter.write(data.toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();
                count++;

            }catch(IOException e) {
                System.err.println("Put failed: " + e.getMessage());
            }finally {
                lock.notifyAll();
            }
        }
    }

    /**
     * Takes element from the head of this buffer
     *
     * @return Data.toString
     * @throws java.util.NoSuchElementException if element is null
     */
    public String take() {
        synchronized(lock) {
            try {
                if(getDataFileLength() > dataFileMaxLength) {
                    lock.wait();
                }
            }catch(InterruptedException e) {
                System.err.println("Waiting for file dump interrupted.");
            }

            String item = null;

            try(Stream<String> lines = Files.lines(Paths.get(dataFile.getName()))) {
                item = lines.skip(offset).findFirst().get();
            }catch(IOException e) {
                System.err.println("File reading problem: " + e.getMessage());
            }

            offset++;
            consumed++;
            lock.notifyAll();
            return item;
        }
    }

    /**
     * Checks if there are no taken elements
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return (count == offset);
    }

    /**
     * Returns difference between count of added elements and current offset
     *
     * @return long
     */
    public long getSize() {
        return (count - offset);
    }

    /**
     * Returns count of added elements
     *
     * @return long
     */
    public long getProducedItems() {
        return count;
    }

    /**
     * Returns count of taken elements
     *
     * @return long
     */
    public long getConsumedItems() {
        return consumed;
    }

    /**
     * Returns length of data file
     *
     * @return long
     */
    public long getDataFileLength() {
        return dataFile.length();
    }

    /**
     * Returns data file length limitation
     *
     * @return long
     */
    public long getDataFileMaxLength() {
        return dataFileMaxLength;
    }

    /**
     * Dump data file when it reaches length limitation
     *
     * @throws IOException
     */
    public void fileDump() throws IOException {

        synchronized(lock) {
            Path temp = Files.createTempFile("temp", ".tmp");
            temp.toFile().deleteOnExit();

            try(PrintWriter out = new PrintWriter(new FileWriter(temp.toFile()));
                Stream<String> lines = Files.lines(Paths.get(dataFile.getPath()))) {

                lines.skip(offset).forEachOrdered(out::println);
            }

            try(PrintWriter out = new PrintWriter(new FileWriter(dataFile));
                Stream<String> lines = Files.lines(temp)) {

                lines.forEachOrdered(out::println);
            }

            offset = 0;
            lock.notifyAll();
        }
    }
}
