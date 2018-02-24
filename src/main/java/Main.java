import service.UserInteractions;

import java.time.Duration;
import java.time.Instant;

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
    public static void main(String[] args) {
        UserInteractions interactions = new UserInteractions();

        interactions.askForProducersQuantity();
        interactions.askForConsumersQuantity();
        interactions.askForFrequency();
        interactions.askForProducerTimeToWork();

        Instant start = Instant.now();

        FifoFileBuffer buffer = new FifoFileBuffer();

        for(int i = 0; i < 15; i++) {
            new Thread(new Producer(
                    buffer,
                    interactions.getFrequency(),
                    interactions.getProducerTimeToWork())
            ).start();
        }

        for(int i = 0; i < 3; i++) {
            new Thread(new Consumer(buffer)).start();
        }

        Runnable timer = () -> {
            while(true) {
                try {
                    Thread.sleep(10000);
                }catch(InterruptedException e) {
                    System.err.println(e.getMessage());
                }

                System.out.println("Produced: " + buffer.getProducedItems());
                System.out.println("Consumed: " + buffer.getConsumedItems());

                Instant end = Instant.now();
                System.out.println("Working time: " + Duration.between(start, end).toString().replaceAll("PT", ""));
            }
        };

        new Thread(timer).start();


    }
}
