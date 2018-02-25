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

    ;

    ;

    public FifoFileBuffer() {
        this.dataFile = new File("data.tmp");
        dataFile.deleteOnExit();
    }

    public void put(T data) {

        checkNotNull(data);

        synchronized(lock) {
            try(FileOutputStream fileWriter = new FileOutputStream(this.dataFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileWriter)) {
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
                lock.wait(500);
            }
            T result;

            try(FileInputStream fileReader = new FileInputStream(this.dataFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileReader)) {
                result = (T) objectInputStream.readObject();
            }

            if(consumedItems == producedItems) {
                result = null;
            }
            checkNotNull(result);
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

    public boolean deleteFile() {
        return dataFile.delete();
    }

}
