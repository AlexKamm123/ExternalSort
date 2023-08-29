import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * class to represent a run in the multiway merge
 * 
 * @author Alex Kammann
 * @version 03.27.23
 */
public class Run implements Comparable<Run> {

    // fields

    private Long startRun;
    private Long endRun;
    private Record record;
    private byte[] buff;
    private ByteBuffer bb;
    private RandomAccessFile raf;
    private long bitsPassed;

    /**
     * constructs a new run based on the given arguments
     * 
     * @param startRun
     *            the starting position of the run in the file
     * @param endRun
     *            the ending position of the run in the file
     * @param filename
     *            the name of the file
     * @throws IOException
     *             if there is an error with the file
     */
    public Run(Long startRun, Long endRun, String filename) throws IOException {
        this.startRun = startRun;
        this.endRun = endRun;
        // used when there is part of a block to read from
        bitsPassed = (long)0;
        // read in one block at a time
        buff = new byte[ByteFile.BYTES_PER_BLOCK];
        bb = ByteBuffer.wrap(buff);
        bb.position(0);
        raf = new RandomAccessFile(new File(filename), "r");
        // get to start position and read in first block
        // then set the first record to the first found record
        raf.seek(startRun);
        raf.read(buff);
        this.record = new Record(bb.getLong(), bb.getDouble());
    }


    /**
     * @return the record
     */
    public Record getRecord() {
        return record;
    }


    /**
     * changes the current record to the next
     * record in the file
     * 
     * @return
     *         true if there was another record to
     *         advance to, false otherwise
     * @throws IOException
     *             if there was an error dealing with the files
     */
    public boolean advanceRecord() throws IOException {

        // if we are in a partial block and there is none left to read
        if (bb.position() + bitsPassed > buff.length) {
            return false;
        }

        // if there is another record to read and we still have more in buffer
        if (bb.position() <= buff.length - ByteFile.BYTES_PER_RECORD) {
            this.record = new Record(bb.getLong(), bb.getDouble());
        }
        // if we need to read in more data
        else {
            bb.clear();
            // if we are at end of file
            if (raf.read(buff) == -1)
                return false;
            bb.position(0);
            // if the new file pointer is after end of run
            if (raf.getFilePointer() > endRun) {
                // denote how many bits passed to check
                // for partial blocks if we are not a full block
                // passed the end of file
                bitsPassed = raf.getFilePointer() - endRun;
            }
            // if we are a full block passed end of file then there is
            // no partial block data to read in
            if (bitsPassed == (ByteFile.BYTES_PER_BLOCK))
                return false;
            // or else start going into the partial block
            this.record = new Record(bb.getLong(), bb.getDouble());
        }
        return true;

    }


    /**
     * @param o
     *            other run to compare to
     * @return value of compare to
     */
    @Override
    public int compareTo(Run o) {
        return Double.compare(this.record.getKey(), o.getRecord().getKey());
    }


    /**
     * closes the file in the run
     * 
     * @throws IOException
     *             if there is an error with the file
     */
    public void close() throws IOException {
        raf.close();
    }

}
