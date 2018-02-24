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
    private int size;
    private long producedItems;
    private long consumedItems;
    private T currentItem;

    FileOutputStream fileWriter;
    ObjectOutputStream objectOutputStream;

    FileInputStream fileReader;
    ObjectInputStream objectInputStream;

    public FifoFileBuffer() {
        this.dataFile = new File("data.tmp");

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

            System.out.println(Thread.currentThread().getName() + " Produced " + data);
            lock.notify();
        }


    }

    public T take() throws Exception {
        synchronized(lock) {
            while(size == 0 && producedItems != consumedItems) {
                lock.wait();
            }


            this.currentItem = (T) objectInputStream.readObject();
            Thread.sleep(150);
            checkNotNull(currentItem);
            T result = currentItem;
            size--;
            consumedItems++;
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
