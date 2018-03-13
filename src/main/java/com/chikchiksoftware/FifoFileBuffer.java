package com.chikchiksoftware;

import java.io.*;
import java.util.NoSuchElementException;

/**
 * Created by
 *
 * @author Anton Nagornyi
 * on 17.02.2018.
 */

public class FifoFileBuffer<T> implements java.io.Serializable {

    private final Object lock = new Object();
    private File dataFile;
    private final long dataFileMaxLength;
    private volatile long count;
    private volatile long offset;
    private long produced;
    private long consumed;
    private final boolean createTempFile;

    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;

    /**
     * Creates an {@code FifoFileBuffer} with default params
     *
     */
    public FifoFileBuffer(long bufferBytesLength, boolean createTempFile) {
        this.createTempFile = createTempFile;
        createNewEmptyDataFile();
        this.dataFileMaxLength = bufferBytesLength;
    }

    /**
     * Inserts the specified element at the offset position of this buffer
     *
     * @param data the element to put
     */
    public void put(T data) {
        synchronized(lock) {
            if(data != null) {
                try {
                    if(!dataFile.exists()) {
                        createNewEmptyDataFile();
                    }
                    if(objectOutputStream == null) {
                        objectOutputStream = new ObjectOutputStream(new FileOutputStream(dataFile, true));
                    }
                    objectOutputStream.writeObject(data);
                    objectOutputStream.flush();
                    count++;
                    produced++;
                }catch(IOException e) {
                    System.err.println("Error writing to file " + e.getCause());
                }catch(NoSuchElementException e) {
                    System.err.println("Invalid input: " + e.getCause());
                }finally {
                    lock.notifyAll();
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
     * @return T
     * @throws EOFException if file is empty
     */
    public T take() throws IOException{
        synchronized(lock) {
            try {
                while(isEmpty()) {
                    if(isDataFileFull()) {
                        finish();
                    }
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
                offset++;
                consumed++;
            }catch(ClassNotFoundException e) {
                System.err.println("Object deserialization failed: " + e.getCause());
            }

            lock.notifyAll();
            return currentFirstElement;
        }
    }

    /**
     * Checks if there are no elements
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
    public long getCount() {
        return count;
    }

    /**
     * Get count of taken elements
     *
     * @return long
     */
    public long getOffset() {
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
     * Creates new empty data file
     *
     */
    private void createNewEmptyDataFile() {
        if(createTempFile) {
            try {
                dataFile = File.createTempFile("temp", ".tmp");
            }catch(IOException e) {
                e.printStackTrace();
            }
        }else {
            dataFile = new File("dataBuffer.dta");
        }
        dataFile.deleteOnExit();
    }

    /**
     * Checks if data file reached length limit
     *
     * @return boolean
     */
    private boolean isDataFileFull() {
        return (dataFile.length() >= dataFileMaxLength);
    }

    /**
     * For testing purposes
     *
     *
     * @return count of all added elements
     */
    public long getProduced() {
        return produced;
    }

    /**
     * For testing purposes
     *
     * @return count of all taken elements
     */
    public long getConsumed() {
        return consumed;
    }

    /**
     * Closes Input and Output streams and
     * deletes data file
     *
     */
    private void finish() {
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
        }
        if(dataFile.exists()) {
            dataFile.delete();
        }
        count = 0;
        offset = 0;
    }
}
