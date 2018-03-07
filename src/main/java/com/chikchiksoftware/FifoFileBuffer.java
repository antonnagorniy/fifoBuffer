package com.chikchiksoftware;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private final Object lock = new Object();
    //private final static String fileName = new Timestamp(System.currentTimeMillis()).getTime() + ".tmp";
    private File dataFile;
    private long count;
    private long offset;
    private long consumed;
    private final long dataFileMaxLength = 104857600;
    T result = null;



    /**
     * Creates an {@code FifoFileBuffer} with default params
     *
     */
    public FifoFileBuffer() {

        try {
            dataFile = File.createTempFile("temp", ".tmp");
        }catch(IOException e) {
            e.printStackTrace();
        }
        dataFile.deleteOnExit();
    }

    /**
     * Inserts the specified element at the head of this buffer
     *
     * @param data the element to put
     */
    public void put(T data) {
        synchronized(lock) {
            try(FileOutputStream fileWriter = new FileOutputStream(dataFile, true);
               ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileWriter)) {
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                count++;
            }catch(IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Takes element from the head of this buffer
     *
     * @return Data.toString
     * @throws java.util.NoSuchElementException if element is null
     */
    public T take() {
        synchronized(lock) {
            try {
                while(isEmpty()) {
                    lock.wait();
                }
            }catch(InterruptedException e) {
                e.printStackTrace();
            }

            try(FileInputStream fileReader = new FileInputStream(dataFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileReader)){
                result = (T) objectInputStream.readObject();
                consumed++;
                offset++;
            }catch(IOException e) {
                System.err.println("Error reading file " + e.getMessage());
            }catch(ClassNotFoundException e) {
                System.err.println("Error creating object " + e.getMessage());
            }

            return result;
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
                fileLines.forEach(out::println);
                out.flush();
            }

            offset = 0;
            lock.notifyAll();
        }
    }
}
