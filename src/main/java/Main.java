
import java.util.Scanner;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 17.02.2018.
 */
public class Main {
    public static void main(String[] args) {
        FifoBuffer buffer = new FifoBuffer();
        int dataProduceFrequency = 0;
        int timeToWork = 0;

        try(Scanner scanner = new Scanner(System.in)) {
            System.out.println("Input data produce frequency in seconds:");
            if(scanner.hasNextInt()) {
                dataProduceFrequency = scanner.nextInt();
            }else {
                System.out.println("Wrong input");
            }
            System.out.println("Input time to work for producers in seconds:");
            if(scanner.hasNextInt()) {
                timeToWork = scanner.nextInt();
            }else {
                System.out.println("Wrong input");
            }
        }catch(NumberFormatException e) {
            e.printStackTrace();
        }

    }
}
