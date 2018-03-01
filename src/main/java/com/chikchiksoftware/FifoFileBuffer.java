package com.chikchiksoftware;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by
 *
 * @author Anton Nagornyi
 * on 17.02.2018.
 */

public class FifoFileBuffer<T> implements java.io.Serializable {
    private final Object lock;
    private final String fileName = new Timestamp(System.currentTimeMillis()).getTime() + ".tmp";
    private final File dataFile;
    private long count;
    private long offset;
    private long consumed;
    private final long dataFileMaxLength;

    /**
     * Creates an {@code FifoFileBuffer} with default params
     *
     */
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
                System.err.println("Put failed: " + e.getMessage());
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
                while(getDataFileLength() > dataFileMaxLength) {
                    lock.wait();
                }
            }catch(InterruptedException e) {
                System.err.println("Put failed: " + e.getMessage());
            }

            String item = null;

            try(Stream<String> lines = Files.lines(Paths.get(dataFile.getName()))) {
                item = lines.skip(offset).findFirst().get();
            }catch(IOException e) {
                System.err.println("Take failed: " + e.getMessage());
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
     * Get difference between count of added elements and current offset
     *
     * @return long
     */
    public long getSize() {
        return (count - offset);
    }

    /**
     * Get count of added elements
     *
     * @return long
     */
    public long getProducedItems() {
        return count;
    }

    /**
     * Get count of taken elements
     *
     * @return long
     */
    public long getConsumedItems() {
        return consumed;
    }

    /**
     * Get length of data file
     *
     * @return long
     */
    public long getDataFileLength() {
        return dataFile.length();
    }

    /**
     * Get data file length limitation
     *
     * @return long
     */
    public long getDataFileMaxLength() {
        return dataFileMaxLength;
    }

    /**
     * Dump data file when it reaches length limitation
     *
     * @throws IOException if file is unreachable
     */
    public void fileDump() throws IOException {
        synchronized(lock) {
            List<String> fileLines;

            try(Stream<String> lines = Files.lines(Paths.get(dataFile.getPath()))) {
                fileLines = lines.skip(offset).collect(Collectors.toList());
            }

            try(PrintWriter out = new PrintWriter(new FileWriter(dataFile))) {
                out.write("");
                out.flush();
            }

            try(PrintWriter out = new PrintWriter(new FileWriter(dataFile))) {
                fileLines.forEach(out::println);
                out.flush();
            }

            offset = 0;
            lock.notifyAll();
        }
    }
}
