import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * test class for ByteFile
 * 
 * @author Alex Kammann
 * @version 03.27.23
 */
public class ByteFileTest extends student.TestCase {

    private ByteFile bf;
    private String output;
    private File copySample;

    /**
     * sets up the ByteFile for each method call
     * 
     * @throws IOException
     *             if there is an error with the files
     */
    public void setUp() throws IOException {

        copySample = new File("input16Copy.bin");
        ByteFile.copyFile(new File("sampleInput16.bin"), copySample);
        bf = new ByteFile("input16Copy.bin", ByteFile.countBlocks(
            "input16Copy.bin"));
    }


    /**
     * tests the ByteFile's printFirstRecordsOfBlocks method
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void testPrintFirstRecordsOfBlocks() throws IOException {

        bf.replacementSelection();
        bf.multiwayMerge(bf.getRuns(), "input16Copy.bin", "mergeTempFile.bin");
        bf.printFirstRecordsOfBlocks();
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
    }


    /**
     * tests the ByteFile's getRun method
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void testGetRun() throws IOException {
        assertEquals(bf.getRuns().toString(), "[]");
        bf.replacementSelection();
        assertEquals(bf.getRuns().toString(), "[0, 8192, 16384,"
            + " 24576, 32768, 40960, " + "49152, 57344, 65536, 73728, 81920, "
            + "90112, 98304, 106496, 114688, 122880, 131072]");
    }


    /**
     * tests the ByteFile's writeRandomRecords method
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void testWriteRandomRecords() throws IOException {
        File copy = new File("AKTestFile");
        File myFile = new File("input16Copy.bin");
        ByteFile.copyFile(myFile, copy);

        RandomAccessFile raf = new RandomAccessFile(myFile, "r");
        RandomAccessFile raf2 = new RandomAccessFile(copy, "rw");
        ByteFile randomBF = new ByteFile("AKTestFile", ByteFile.countBlocks(
            "AKTestFile"));
        randomBF.writeRandomRecords();
        byte[] og = new byte[ByteFile.BYTES_PER_BLOCK];
        byte[] cp = new byte[ByteFile.BYTES_PER_BLOCK];
        ByteBuffer ogBB = ByteBuffer.wrap(og);
        ByteBuffer cpBB = ByteBuffer.wrap(cp);
        ogBB.position(0);
        cpBB.position(0);

        raf.read(og);
        raf2.read(cp);
        int dif = 0;

        while (ogBB.position() < ByteFile.BYTES_PER_BLOCK) {
            Record ogRec = new Record(ogBB.getLong(), ogBB.getDouble());
            Record cpRec = new Record(cpBB.getLong(), cpBB.getDouble());
            if (ogRec.getRecID() != cpRec.getRecID()) {
                dif++;
            }

        }

        raf2.close();
        raf.close();
        copy.delete();
    }


    /**
     * tests the ByteFile's isSorted method
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void testIsSorted() throws IOException {
        bf.writeRandomRecords();
        assertFalse(bf.isSorted());
        bf.replacementSelection();
        bf.multiwayMerge(bf.getRuns(), "input16Copy.bin", "mergeTempFile.bin");
        assertTrue(bf.isSorted());
    }


    /**
     * tests the ByteFile's replacementSelection method
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void testReplacementSelection() throws IOException {
        bf.writeRandomRecords();
        bf.replacementSelection();
        assertEquals(bf.getRuns().toString(), "[0, 8192, 16384,"
            + " 24576, 32768, 40960, " + "49152, 57344, 65536, 73728, 81920, "
            + "90112, 98304, 106496, 114688, 122880, 131072]");
        bf.writeRandomRecords();
        bf.replacementSelection();
        assertEquals(bf.getRuns().toString(), "[0, 8192, 16384,"
            + " 24576, 32768, 40960, " + "49152, 57344, 65536, 73728, 81920, "
            + "90112, 98304, 106496, 114688, 122880, 131072]");
    }


    /**
     * tests the ByteFile's multiWayMerge method
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void testMultiwayMerge() throws IOException {
        bf.writeRandomRecords();
        bf.replacementSelection();
        assertFalse(bf.isSorted());
        bf.multiwayMerge(bf.getRuns(), "input16Copy.bin", "mergeTempFile.bin");
        assertTrue(bf.isSorted());

        bf.writeRandomRecords();
        bf.replacementSelection();
        assertFalse(bf.isSorted());
        bf.multiwayMerge(bf.getRuns(), "input16Copy.bin", "mergeTempFile.bin");
        assertTrue(bf.isSorted());

    }


    /**
     * tests the ByteFile's copyFile method
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void testCopyFile() throws IOException {
        File copy = new File("AKTestFile");
        File myFile = new File("sampleInput16.bin");
        ByteFile.copyFile(myFile, copy);

        RandomAccessFile raf = new RandomAccessFile(myFile, "r");
        RandomAccessFile raf2 = new RandomAccessFile(copy, "rw");
        byte[] og = new byte[ByteFile.BYTES_PER_BLOCK];
        byte[] cp = new byte[ByteFile.BYTES_PER_BLOCK];
        ByteBuffer ogBB = ByteBuffer.wrap(og);
        ByteBuffer cpBB = ByteBuffer.wrap(cp);

        while (raf.read(og) != -1 && raf2.read(cp) != -1) {
            ogBB.position(0);
            cpBB.position(0);

            while (ogBB.position() < og.length && ogBB.position() < og.length) {
                Record ogRec = new Record(ogBB.getLong(), ogBB.getDouble());
                Record cpRec = new Record(cpBB.getLong(), cpBB.getDouble());
                assertTrue(ogRec.getRecID() == cpRec.getRecID());
                assertTrue(ogRec.getKey() == cpRec.getKey());
            }
        }

        raf2.close();
        raf.close();
        copy.delete();

    }


    /**
     * tests the ByteFile's countBlocks method
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void testCountBlocks() throws IOException {
        assertEquals(16, ByteFile.countBlocks("sampleInput16.bin"));
        copySample.delete();
    }

}
