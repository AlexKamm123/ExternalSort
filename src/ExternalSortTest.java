import java.io.File;
import java.io.IOException;

/**
 * test class for Externalsort.java
 * 
 * @author Alex Kammann
 * @version 03.24.23
 */
public class ExternalSortTest extends student.TestCase {

    private String output;

    /**
     * set up for tests
     */
    public void setUp() {
        // nothing to set up.
    }


    /**
     * @throws IOException
     *             if there is a file error
     */
    public void testExternalsort() throws IOException {
        File copyInput = new File("myInputCopy.bin");
        ByteFile.copyFile(new File("sampleInput16.bin"), copyInput);
        String[] args = { "myInputCopy.bin" };
        Externalsort.main(args);
        output = systemOut().getHistory();
        assertEquals(output, "5859826799363951096 7.25837957933813E-309 "
            + "872093003042532807 2.846974648265778E-271 "
            + "4746048651426934305 8.021302838493087E-236 "
            + "2465224465483701295 1.979063847945134E-200 "
            + "1050792465528211139 1.9121111284579667E-165\r\n"
            + "6050394105966916791 2.2317027604113507E-127 "
            + "1026023591337815624 1.5574815733570753E-91 "
            + "3727109532527581177 7.578200413844949E-58 "
            + "1666373987716394526 1.948647168795557E-21 "
            + "2109762501594140130 4.7295568637570205E12\r\n"
            + "704373661941503400 2.8332973775294907E48 "
            + "168353935316054591 7.423511124391644E81 "
            + "1813093945163867404 1.123748335113702E114 "
            + "6011240381167188375 5.443479801473815E147 "
            + "8334502261472908423 6.228961731759273E184\r\n"
            + "4290883147614596 1.206088797278413E221 ");
        copyInput.delete();
    }

}
