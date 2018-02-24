package com.chikchiksoftware.service;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 24.02.2018.
 */
public class UserInteractions {
    private final Scanner scanner = new Scanner(System.in);
    private long frequency;
    private long producerTimeToWork;
    private int producersCount;
    private int consumersCount;

    public void askForProducersQuantity() {
        System.out.println("Input Producers quantity: ");

        this.producersCount = scanForNumbers().intValue();
    }

    public void askForConsumersQuantity() {
        System.out.println("Input Consumers quantity: ");

        this.consumersCount = scanForNumbers().intValue();
    }

    public void askForFrequency() {
        System.out.println("Input data generation frequency(milliseconds): ");

        this.frequency = scanForNumbers().longValue();
    }

    public void askForProducerTimeToWork() {
        System.out.println("Input Producers time to work(seconds): ");

        this.producerTimeToWork = scanForNumbers().longValue() * 1000;
    }

    private Number scanForNumbers() {
        Number number = null;
        boolean done = false;

        while(!done) {
            try {
                number = scanner.nextLong();
                done = true;
            }catch(InputMismatchException e) {
                System.out.println("\tInvalid input type (must be a number)");
                scanner.nextLine();
            }
        }

        return number;
    }

    public void close() {
        scanner.close();
    }


    public long getFrequency() {
        return frequency;
    }

    public long getProducerTimeToWork() {
        return producerTimeToWork;
    }

    public int getProducersCount() {
        return producersCount;
    }

    public int getConsumersCount() {
        return consumersCount;
    }
}
