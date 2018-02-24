package com.chikchiksoftware;

import java.io.*;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */

public class FifoFileBuffer<T> {
    private final Object lock = new Object();
    private final File dataFile;
    private long producedItems;
    private long consumedItems;
    private int size;

    FileOutputStream fileWriter;
    ObjectOutputStream objectOutputStream;

    FileInputStream fileReader;
    ObjectInputStream objectInputStream;

    public FifoFileBuffer() {
        this.dataFile = new File("data.tmp");
        dataFile.deleteOnExit();
        try {
            fileWriter = new FileOutputStream(this.dataFile);
            objectOutputStream = new ObjectOutputStream(fileWriter);
            fileReader = new FileInputStream(this.dataFile);
            objectInputStream = new ObjectInputStream(fileReader);
        }catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void put(T data) {

        checkNotNull(data);

        synchronized(lock) {
            try {
                objectOutputStream.writeObject(data);
                size++;
                producedItems++;
            }catch(IOException e) {
                System.err.println(e.getMessage());
            }

            lock.notifyAll();
        }


    }

    public T take() throws InterruptedException, ClassNotFoundException, IOException{
        synchronized(lock) {
            while(isEmpty()) {
                lock.wait();
            }

            T result = (T) objectInputStream.readObject();
            size--;
            consumedItems++;
            lock.notifyAll();
            return result;
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
