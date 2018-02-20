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
    protected Timestamp timestamp;
    FifoFileBuffer<Timestamp> bufferTimestamp;
    FifoFileBuffer<String> bufferString;

    String testString;

    protected void setUp() {
        timestamp = new Timestamp(System.currentTimeMillis());
        bufferTimestamp = new FifoFileBuffer<>();
        bufferString = new FifoFileBuffer<>();
        testString = "TestString";
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

}
