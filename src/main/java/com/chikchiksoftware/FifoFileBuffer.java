package com.chikchiksoftware;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by
 *
 * @author Anton Nagornyi
 * on 17.02.2018.
 */

public class FifoFileBuffer<T> implements java.io.Serializable {
    private final Object lock = new Object();
    private final String fileName = new Timestamp(System.currentTimeMillis()).getTime() + ".tmp";
    private File dataFile = new File(fileName);
    private long count;
    private long offset = 0;
    private long consumed;
    private final long dataFileMaxLength = 104857600;
    private long currentDataFileLength;


    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    /**
     * Creates an {@code FifoFileBuffer} with default params
     *
     */
    public FifoFileBuffer() {
        dataFile.deleteOnExit();
    }

    /**
     * Inserts the specified element at the head of this buffer
     *
     * @param data the element to put
     */
    public void put(T data) {
        synchronized(lock) {

            try {
                if(objectOutputStream == null) {
                    objectOutputStream = new ObjectOutputStream(new FileOutputStream(dataFile, true));
                }
                objectOutputStream.writeObject(data);
                objectOutputStream.flush();
                count++;
            }catch(IOException e) {
                System.err.println("Error writing to file " + e.getCause());
            }catch(NoSuchElementException e) {
                System.err.println("Invalid input: " + e.getCause());
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
    public T take() {
        synchronized(lock) {
            try {
                while(isEmpty()) {
                    lock.wait();
                }
            }catch(InterruptedException e) {
                System.err.println("Error " + e.getMessage());
            }

            T currentFirstElement = null;

            try {
                if(objectInputStream == null) {
                    objectInputStream = new ObjectInputStream(new FileInputStream(dataFile));
                }

                currentFirstElement = (T) objectInputStream.readUnshared();
            }catch(EOFException e) {
                System.err.println("EOF " + e.getCause());
            }catch(IOException e) {
                System.err.println("Error reading file: ");
                e.printStackTrace();
            }catch(ClassNotFoundException e) {
                System.err.println("Object deserialization failed: " + e.getCause());
            }

            System.out.println("Norm return");
            offset++;
            consumed++;
            lock.notifyAll();
            return currentFirstElement;
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

    public void finish() {
        try {
            if(objectOutputStream != null) {
                objectOutputStream.close();
            }
            if(objectInputStream != null) {
                objectInputStream.close();
            }
        }catch(IOException e) {
            System.err.println("Error closing streams " + e.getCause());
        }
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
            List<T> objects = new ArrayList<>();

            try {
                while(true) {
                    objects.add((T)objectInputStream.readUnshared());
                }
            }catch(EOFException e) {
                objectOutputStream = null;
                objectInputStream = null;
                dataFile.delete();

                dataFile = new File(fileName);
            }catch(ClassNotFoundException e) {
                e.printStackTrace();
            }


            lock.notifyAll();
        }
    }
}
