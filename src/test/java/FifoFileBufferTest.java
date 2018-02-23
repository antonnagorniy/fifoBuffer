import junit.framework.TestCase;
import org.junit.Test;

import java.sql.Timestamp;

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
    private FifoFileBuffer<Timestamp> bufferTimestamp;
    private FifoFileBuffer<String> bufferString;
    private FifoFileBuffer<Integer> bufferInteger;


    protected void setUp() {
        bufferTimestamp = new FifoFileBuffer<>();
        bufferString = new FifoFileBuffer<>();
        bufferInteger = new FifoFileBuffer<>();
        timestamp = new Timestamp(System.currentTimeMillis());
        testString = "TestString";
        integer = 1789;
    }

    @Test
    public void testPutTimestamp() {
        bufferTimestamp.put(timestamp);

        assertEquals(bufferTimestamp.poll(), timestamp);
    }

    @Test
    public void testPutString() {
        bufferString.put(testString);

        assertEquals(bufferString.poll(), testString);
    }

    @Test
    public void testPutInteger() {
        bufferInteger.put(integer);

        assertEquals(bufferInteger.poll(), integer);
    }

}
