

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Main {
    public static void main(String[] args) {
        FifoFileBuffer buffer = new FifoFileBuffer();

        for(int i = 0; i < 3; i++) {
            new Thread(new Producer(buffer)).start();
        }


        for(int i = 0; i < 3; i++) {
            new Thread(new Consumer(buffer)).start();
        }
    }
}
