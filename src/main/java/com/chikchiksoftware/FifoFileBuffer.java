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
    private File dataFile = new File(new Timestamp(System.currentTimeMillis()).getTime() + ".tmp");
    private final long dataFileMaxLength = /*104857600*/20480;
    private long count;
    private long offset;

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
     * Inserts the specified element at the offset position of this buffer
     *
     * @param data the element to put
     */
    public void put(T data) {
        synchronized(lock) {
            try {
                while(dataFile.length() > dataFileMaxLength) {
                    lock.wait();
                }
            }catch(InterruptedException e) {
                System.err.println("Error waiting for file cleaning: ");
                e.printStackTrace();
            }

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
     * @throws EOFException if element is null
     */
    public T take() throws IOException{
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

                currentFirstElement = (T) objectInputStream.readObject();


            }/*catch(EOFException ignore) {
                *//*ignore.printStackTrace();*//*
            }catch(IOException e) {
                System.err.println("Error reading file: ");
                e.printStackTrace();
            }*/catch(ClassNotFoundException e) {
                System.err.println("Object deserialization failed: " + e.getCause());
            }

            offset++;
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

    /**
     * Closes Input and Output streams
     *
     */
    public void finish() {
        try {
            if(objectOutputStream != null) {
                objectOutputStream.close();
                objectOutputStream = null;
            }
            if(objectInputStream != null) {
                objectInputStream.close();
                objectInputStream = null;
            }
        }catch(IOException e) {
            System.err.println("Error closing streams " + e.getCause());
        }finally {
            dataFile.delete();
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
        return offset;
    }

    /**
     * Get length of data file
     *
     * @return long
     */
    public synchronized long getDataFileLength() {
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
    public void fileDump() {
        synchronized(lock) {
            List<T> objects = new ArrayList<>();

            try {
                while(true) {
                    objects.add((T)objectInputStream.readObject());
                }
            }catch(EOFException e) {
                finish();

                dataFile = new File(new Timestamp(System.currentTimeMillis()).getTime() + ".tmp");
                dataFile.deleteOnExit();

                for(T object : objects) {
                    put(object);
                }
            }catch(NullPointerException e) {
                System.err.println("File is empty: ");
                e.printStackTrace();
            }catch(IOException e) {
                System.err.println("File reading error: ");
                e.printStackTrace();
            }catch(ClassNotFoundException e) {
                System.err.println("Error cleaning file: ");
                e.printStackTrace();
            }

            lock.notifyAll();
        }
    }
}
