package com.chikchiksoftware;

import java.io.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by
 *
 * @author Anton Nagornyi
 * on 17.02.2018.
 */

public class FifoFileBuffer<T> implements java.io.Serializable {
    private final Object lock = new Object();
    private File dataFile = new File(new Timestamp(System.currentTimeMillis()).getTime() + ".dta");
    private final long dataFileMaxLength = 104857600/*20480*//*1024*/;
    private long count;
    private long offset;

    /**
     * Used as data store while Data file is in
     * cleaning process
     */
    private final List<T> cacheList = new CopyOnWriteArrayList<>();

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
            if(data != null) {
                if(isDataFileFull()) {
                    cacheList.add(data);
                    lock.notifyAll();
                }else {
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
            }else {
                lock.notifyAll();
                throw new IllegalArgumentException("Argument is empty.");
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

            }catch(ClassNotFoundException e) {
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
     * Closes Input and Output streams and
     * deletes data file
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
    public long getAllAddedItemsCount() {
        return count;
    }

    /**
     * Get count of taken elements
     *
     * @return long
     */
    public long getAllTakenItemsCount() {
        return offset;
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
     * Creates new empty data file
     *
     */
    private void createNewEmptyDataFile() {
        dataFile = new File(new Timestamp(System.currentTimeMillis()).getTime() + ".dta");

    }

    /**
     * Checks if data file reached length limit
     *
     * @return
     */
    public boolean isDataFileFull() {
        return (dataFile.length() >= dataFileMaxLength);
    }

    /**
     * Dumps data file
     *
     */
    public void fileDump() {
        synchronized(lock) {
            if(isDataFileFull() && isEmpty()) {
                finish();
                createNewEmptyDataFile();

                for(T object : cacheList) {
                    put(object);
                }

                cacheList.clear();
                System.out.println("File cleaned.");
            }else {
                try {
                    lock.wait();
                }catch(InterruptedException e) {
                    System.err.print("File dump failed: ");
                    e.printStackTrace();
                }
            }

            lock.notifyAll();
        }
    }
}
