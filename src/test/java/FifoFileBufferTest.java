import com.chikchiksoftware.FifoFileBuffer;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 20.02.2018.
 */
public class FifoFileBufferTest extends TestCase {
    private Timestamp timestamp;
    private String testString;
    private Integer integer;
    private FifoFileBuffer<Timestamp> timestampBuffer;
    private FifoFileBuffer<String> stringBuffer;
    private FifoFileBuffer<Integer> integerBuffer;
    private List<String> stringsList;

    @Before
    protected void setUp() {
        timestampBuffer = new FifoFileBuffer<>();
        stringBuffer = new FifoFileBuffer<>();
        integerBuffer = new FifoFileBuffer<>();
        timestamp = new Timestamp(System.currentTimeMillis());
        testString = "TestString";
        integer = 1789;
        stringsList = new ArrayList<>();

        for(int i = 0; i < 20; i++) {
            stringsList.add(i, "String " + i);
        }
    }

    @Test
    public void testPutTimestamp() {
        timestampBuffer.put(timestamp);

        assertEquals(
                    "Wrong object.",
                    timestamp,
                    timestampBuffer.take());


        timestampBuffer.finish();
    }

    @Test
    public void testPutString() {
        stringBuffer.put(testString);

        assertEquals(
                    "Wrong object.",
                    testString,
                    stringBuffer.take());


        stringBuffer.finish();
    }

    @Test
    public void testPutInteger() {
        integerBuffer.put(integer);

        assertEquals(
                    "Wrong object.",
                    integer,
                    Integer.valueOf(integerBuffer.take()));


        integerBuffer.finish();
    }

    @Test
    public void testFifo() {

       for(int i = 0; i < stringsList.size(); i++) {
           stringBuffer.put(stringsList.get(i));
       }

       for(int i = 0; i < stringsList.size(); i++) {
               assertEquals(
                       stringsList.get(i) + " is in incorrect order.",
                       stringsList.get(i),
                       stringBuffer.take());
           }


       stringBuffer.finish();
    }

    @Test
    public void testGetBufferItemsCount() {
        for(String str : stringsList) {
            stringBuffer.put(str);
        }

        assertEquals(
                "Buffer size is incorrect.",
                stringsList.size(),
                stringBuffer.getSize());

        int itemsToTakeCount = 6;

        for(int i = 0; i < itemsToTakeCount; i++) {
                stringBuffer.take();
            }


        assertEquals(
                "Buffer size after " + itemsToTakeCount + " takes is incorrect.",
                (stringsList.size() - itemsToTakeCount),
                stringBuffer.getSize());

        stringBuffer.finish();
    }

    @Test
    public void testProducedCount() {
        for(String str : stringsList) {
            stringBuffer.put(str);
        }

        assertEquals(
                "Produced count is incorrect",
                stringsList.size(),
                stringBuffer.getProducedItems());

        stringBuffer.finish();
    }

    @Test
    public void testConsumedCount() {
        for(String str : stringsList) {
            stringBuffer.put(str);
        }

        long producedCount = stringBuffer.getProducedItems();

        for(int i = 0; i < stringsList.size(); i++) {
                stringBuffer.take();
            }

            assertEquals(
                "Consumed count is incorrect.",
                producedCount,
                stringBuffer.getConsumedItems());

        stringBuffer.finish();
    }

    @After
    public void tearDown() {

    }

}
