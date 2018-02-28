package com.chikchiksoftware;

import com.chikchiksoftware.service.FileCleaningService;
import com.chikchiksoftware.service.TimeConversionService;
import com.chikchiksoftware.service.Timer;
import com.chikchiksoftware.service.UserInteractions;

import java.sql.Timestamp;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */

/*
Нужно создать тестовое приложение которое будет реализовывать простой однофайловый FIFO буфер и демонстрировать его работу.
В качестве записываемых данных использовать таймстамп на момент генерации.
Буфер должен быть потокозащищенным.
Буфер должен быть быстрым
Буфер должен быть реализован так, чтобы файл буфера со временем сильно не рос (при нормальной работе) и правильно "зачищался" при опустошении.
Данные в буфере можно хранить в любом виде
Пользоваться сторонними фреймворками запрещено
Многопоточность должна быть реализована классически (synchronized-notify)
Консъюмеры работают

Логика работы:
На старте утилита запрашивает количество генерируещих (producer) и потребляющих(consumer) потоков, частоту генерации данных продъюсерами и время работы продъюсеров
Утилита создает файл-буфер в который продъюсеры записывают свои данные и из которого консьюмеры эти данные забирают
Консъюмеры выводят вытащенные из буфера данные в консоль с указанием потока, который их вывел.
Раз в 10 секунд нужно выводить статистику работы. Кол-во сгенерированных данных, количество потребелнных данных, время работы и оставшееся время работы.
По окончании времени работы все продъюсеры прекращают генерацию данных, а консъюмеры продолжают работать пока не выгребут все данные из буфера.
*/

public class Main {

    private static UserInteractions interactions = new UserInteractions();
    private static ThreadGroup producers = new ThreadGroup("Producers");
    private static ThreadGroup consumers = new ThreadGroup("Consumers");

    public static void main(String[] args) {

        interactions.producersQuantityInput();
        interactions.consumersQuantityInput();
        interactions.dataGenerationFrequencyInput();
        interactions.producerTimeToWorkInput();
        interactions.close();

        final long start = System.currentTimeMillis();

        FifoFileBuffer<Timestamp> buffer = new FifoFileBuffer<>();
        Timer serviceTimer = new Timer(buffer, start, interactions.getProducerTimeToWork());

        for(int i = 0; i < interactions.getProducersCount(); i++) {
            new Thread(producers, new Producer(
                    buffer,
                    interactions.getFrequency(),
                    interactions.getProducerTimeToWork())
            ).start();
        }


        Thread timerDaemon = new Thread(serviceTimer);
        timerDaemon.setDaemon(true);
        timerDaemon.start();

        for(int i = 0; i < interactions.getConsumersCount(); i++) {
            new Thread(consumers, new Consumer(buffer)).start();
        }

        Runnable statistics = () -> {
            while(producers.activeCount() > 0 || consumers.activeCount() > 0) {
                try {
                    Thread.sleep(500);
                }catch(InterruptedException e) {
                    System.err.println();
                }
            }

            long end = System.currentTimeMillis();
            System.out.println("===================================");
            System.out.println("Totals:");
            System.out.println("Produced: " + buffer.getProducedItems());
            System.out.println("Consumed: " + buffer.getConsumedItems());
            System.out.println("Time elapsed: " + TimeConversionService.millisToDHMS(end - start));
            System.out.println("Data file length: " + (Math.round(buffer.getDataFileLength() / 1024)) + " Kb");
            System.out.println("===================================");
        };

        Thread fileCleaningDaemon = new Thread(new FileCleaningService(buffer));
        fileCleaningDaemon.setDaemon(true);
        fileCleaningDaemon.start();

        Thread finalStatisticsService = new Thread(statistics);
        finalStatisticsService.start();
    }
}
