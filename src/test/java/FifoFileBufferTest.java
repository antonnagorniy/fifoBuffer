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

        try {
            assertEquals("Wrong object.", timestamp.toString(), timestampBuffer.take());
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }

    }

    @Test
    public void testPutString() {
        stringBuffer.put(testString);

        try {
            assertEquals("Wrong object.", testString, stringBuffer.take());
        }catch(Exception e) {
            System.err.println(e);
        }
    }

    @Test
    public void testPutInteger() {
        integerBuffer.put(integer);

        try {
            assertEquals("Wrong object.", integer, Integer.valueOf(integerBuffer.take()));
        }catch(Exception e) {
            System.err.println(e);
        }
    }

    @Test
    public void testFifo() {

       for(String str : stringsList) {
           stringBuffer.put(str);
       }

       try {
           for(String str : stringsList) {
               assertEquals(
                       str + " is in incorrect order.",
                       str,
                       stringBuffer.take());
           }
       }catch(Exception e) {
           System.err.println(e.getMessage());
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
        }catch(Exception e) {
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
                stringBuffer.getProducedItems());
    }

    @Test
    public void testConsumedCount() {
        for(String str : stringsList) {
            stringBuffer.put(str);
        }

        long producedCount = stringBuffer.getProducedItems();

        try {
            for(String str : stringsList) {
                stringBuffer.take();
            }
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }

        assertEquals(
                "Consumed count is incorrect.",
                producedCount,
                stringBuffer.getConsumedItems());
    }

    @After
    public void tearDown() {
        timestampBuffer.deleteFile();
        stringBuffer.deleteFile();
        integerBuffer.deleteFile();
    }

}
