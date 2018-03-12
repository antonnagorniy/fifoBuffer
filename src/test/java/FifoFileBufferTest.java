import com.chikchiksoftware.FifoFileBuffer;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
        timestampBuffer = new FifoFileBuffer<>(1024, true);
        stringBuffer = new FifoFileBuffer<>(1024, true);
        integerBuffer = new FifoFileBuffer<>(1024, true);
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

        try {
            assertEquals(
                    "Wrong object.",
                    timestamp,
                    timestampBuffer.take());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutString() {
        stringBuffer.put(testString);

        try {
            assertEquals(
                    "Wrong object.",
                    testString,
                    stringBuffer.take());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPutInteger() {
        integerBuffer.put(integer);

        try {
            assertEquals(
                    "Wrong object.",
                    integer,
                    Integer.valueOf(integerBuffer.take()));
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFifo() {

       for(int i = 0; i < stringsList.size(); i++) {
           stringBuffer.put(stringsList.get(i));
       }

        try {
            for(int i = 0; i < stringsList.size(); i++) {
                assertEquals(
                        stringsList.get(i) + " is in incorrect order.",
                        stringsList.get(i),
                        stringBuffer.take());
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
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

        try {
            for(int i = 0; i < itemsToTakeCount; i++) {
                stringBuffer.take();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }

        assertEquals(
                "Buffer size after " + itemsToTakeCount + " takes is incorrect.",
                (stringsList.size() - itemsToTakeCount),
                stringBuffer.getSize());

    }

    @Test
    public void testProducedCount() {
        for(String str : stringsList) {
            stringBuffer.put(str);
        }

        assertEquals(
                "Produced count is incorrect",
                stringsList.size(),
                stringBuffer.getCount());
    }

    @Test
    public void testConsumedCount() {
        for(String str : stringsList) {
            stringBuffer.put(str);
        }

        long producedCount = stringBuffer.getCount();

        try {
            for(int i = 0; i < stringsList.size(); i++) {
                stringBuffer.take();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }

        assertEquals(
                "Consumed count is incorrect.",
                producedCount,
                stringBuffer.getOffset());
    }

    @After
    public void tearDown() {

    }

}
