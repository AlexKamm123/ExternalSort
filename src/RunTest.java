import java.io.IOException;

/**
 * test class for run.java
 * 
 * @author Alex Kammann
 * @version 03.27.23
 */
public class RunTest extends student.TestCase {

    private Run run;

    /**
     * sets up the fields for each run
     */
    public void setUp() throws Exception {
        run = new Run((long)0, (long)8192, "sampleInput16.bin");
    }


    /**
     * tests the run's advance record method
     * 
     * @throws IOException
     */
    public void testAdvanceRecord() throws IOException {
        for (int i = 0; i < 511; i++) {
            assertTrue(run.advanceRecord());
        }
        run.close();
    }

}
