import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import student.TestableRandom;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Basic handling of binary data files.
 * Uses a single byte array as a buffer for disc operations
 * Each record is one long, and one double. Sorting key is the double.
 * A record is 16 bytes long, and there are 512 records per block.
 * 
 * Can be extended in several ways (writeSortedRecords()? readBlock(int)?)
 * 
 * @author CS Staff, Patrick Sullivan
 * @version 2023 March
 * 
 * @author Alex Kammann
 * @version 3.27.23
 */
public class ByteFile {
    /**
     * the number of records ina block
     */
    final static int RECORDS_PER_BLOCK = 512;

    /**
     * the number of bytes in a record
     */
    final static int BYTES_PER_RECORD = 16;

    /**
     * the number of bytes in a block
     */
    final static int BYTES_PER_BLOCK = BYTES_PER_RECORD * RECORDS_PER_BLOCK;

    /**
     * the number of blocks allowed in ram
     */
    final static int BLOCKS_IN_RAM = 8;

    // the indexes of the runs used in multiwayMerge
    private List<Long> runs;
    private String filename;
    private int numBlocks;

    /**
     * constructs a new ByteFile
     * based on teh filename and numBlocks
     * 
     * @param filename
     *            the name of the file
     * @param numBlocks
     *            the number of blocks
     */
    public ByteFile(String filename, int numBlocks) {
        runs = new ArrayList<>();
        this.filename = filename;
        this.numBlocks = numBlocks;
    }


    /**
     * gets the positions of
     * the runs
     * 
     * @return
     *         the list of run positions
     */
    public List<Long> getRuns() {
        return this.runs;
    }


    /**
     * calls writeRandomRecords
     * 
     * @throws IOException
     *             if the file does not exist
     */
    public void writeRandomRecords() throws IOException {
        writeRandomRecords(null);
    }


    /**
     * writes random records to a file
     * 
     * @param rng
     *            the random generator used
     * @throws IOException
     *             If the file does not exist
     */
    private void writeRandomRecords(Random rng) throws IOException {
        if (rng == null) {
            rng = new TestableRandom();
        }

        byte[] basicBuffer = new byte[BYTES_PER_BLOCK];
        ByteBuffer bb = ByteBuffer.wrap(basicBuffer);
        File theFile = new File(filename);
        theFile.delete();
        // Deletes all old data in file,
        // ensuring file will have only the new data

        RandomAccessFile raf = new RandomAccessFile(theFile, "rw");
        for (int block = 0; block < numBlocks; block++) {
            bb.position(0); // resets to byte position zero in ByteBuffer

            for (int rec = 0; rec < RECORDS_PER_BLOCK; rec++) {
                // puts the data in the basicBuffer...
                bb.putLong(rng.nextLong()); // a random recID
                bb.putDouble(rng.nextDouble()); // a random recKey
            }
            raf.write(basicBuffer);
            // ^^^ the slow operation! However, using one large
            // amount of data is better than using many small amounts
            bb.clear();
        }
        raf.close(); // be sure to close file
    }


    /**
     * checks if a file is sorted or not
     * 
     * @return
     *         true if a file is sorted, false otherwise
     * @throws IOException
     *             if a file error occurs
     */
    public boolean isSorted() throws IOException {
        byte[] basicBuffer = new byte[BYTES_PER_BLOCK];
        ByteBuffer bb = ByteBuffer.wrap(basicBuffer);

        File theFile = new File(filename);
        RandomAccessFile raf = new RandomAccessFile(theFile, "r");
        raf.seek(0);
        double prevRecKey = Double.NEGATIVE_INFINITY;

        for (int block = 0; block < numBlocks; block++) {
            raf.read(basicBuffer);
            // ^^^ the slow, costly operation!!! Good thing we use buffer

            bb.position(0); // goes to byte position zero in ByteBuffer
            for (int rec = 0; rec < RECORDS_PER_BLOCK; rec++) {
                long recID = bb.getLong();
                // ^^^ reading the recID is important to advance the byteBuffer
                // position, but it is not used in the sort order
                double recKey = bb.getDouble();
                if (recKey < prevRecKey) {
                    raf.close();
                    return false;
                }
                else {
                    prevRecKey = recKey;
                }
            }
        }
        raf.close(); // be sure to close file
        return true;
    }


    /**
     * used to print the first record of each
     * block in the sorted file
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void printFirstRecordsOfBlocks() throws IOException {
        byte[] rec = new byte[BYTES_PER_RECORD];
        ByteBuffer bb = ByteBuffer.wrap(rec);
        bb.position(0);
        int count = 0;
        File myFile = new File(this.filename);
        RandomAccessFile raf = new RandomAccessFile(myFile, "r");
        raf.seek(0);

        // loop while there is more to read
        while (raf.read(rec) != -1) {
            bb.position(0);

            // create the record from the buffer
            Record currRec = new Record(bb.getLong(), bb.getDouble());

            System.out.print(currRec);
            if ((count + 1) % 5 == 0 && count != 0)
                System.out.print("\n");
            else
                System.out.print(" ");

            // update the position of file pointer to next block
            raf.seek(BYTES_PER_BLOCK * (count + 1));

            // clear buffer and increment counter
            bb.clear();
            count++;
        }

        // close file
        raf.close();
    }
    
    /**
     * used to print all the records in order
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void printRecords() throws IOException {
        byte[] rec = new byte[BYTES_PER_RECORD];
        ByteBuffer bb = ByteBuffer.wrap(rec);
        bb.position(0);
        int count = 0;
        File myFile = new File(this.filename);
        RandomAccessFile raf = new RandomAccessFile(myFile, "r");
        raf.seek(0);

        // loop while there is more to read
        while (raf.read(rec) != -1) {
            bb.position(0);

            // create the record from the buffer
//            Record currRec = new Record(bb.getLong(), bb.getDouble());
            
            long l = bb.getLong();
            double d = bb.getDouble();
            System.out.println("Data: " + l + " Key: " + d);

            // update the position of file pointer to next block
            raf.seek(BYTES_PER_RECORD * (count + 1));

            // clear buffer and increment counter
            bb.clear();
            count++;
        }

        // close file
        raf.close();
    }
    

    /**
     * External Sort replacement selection algorithm
     * 
     * @throws IOException
     *             if a file error occurs
     */
    public void replacementSelection() throws IOException {
        runs.clear();
        File selectOutput = new File("selectionOutput.bin");
        selectOutput.delete();
        RandomAccessFile outFile = new RandomAccessFile(selectOutput, "rw");
        // buffers
        byte[] inBuff = new byte[BYTES_PER_BLOCK];
        byte[] outBuff = new byte[BYTES_PER_BLOCK];
        byte[] block = new byte[BYTES_PER_BLOCK];
        ByteBuffer inbb = ByteBuffer.wrap(inBuff);
        ByteBuffer outbb = ByteBuffer.wrap(outBuff);
        ByteBuffer bb = ByteBuffer.wrap(block);
        File inFile = new File(filename);
        RandomAccessFile raf = new RandomAccessFile(inFile, "r");
        raf.seek(0);

        // fill in the heap array
        Record[] heapArr = new Record[RECORDS_PER_BLOCK * Math.min(
            this.numBlocks, BLOCKS_IN_RAM)];
        int arrIndex = 0;
        for (int k = 0; k < BLOCKS_IN_RAM; k++) {
            raf.read(block);
            bb.position(0);
            for (int i = 0; i < RECORDS_PER_BLOCK; i++) {
                heapArr[arrIndex] = new Record(bb.getLong(), bb.getDouble());
                arrIndex++;
            }
        }

        // create the heap from the array
        MinHeap<Record> heap = new MinHeap<Record>(heapArr, RECORDS_PER_BLOCK
            * BLOCKS_IN_RAM, RECORDS_PER_BLOCK * BLOCKS_IN_RAM);

        // start process
        inbb.position(0);
        outbb.position(0);

        // read to input buff while there is still data
        while (raf.read(inBuff) != -1) {

            inbb.position(0);

            // continue while there is still more data in inbuff
            while (inbb.position() < inBuff.length - 1) {

                // remove min and add new val, while setting rem to removed
                // Record
                Record rem = heap.modify(0, new Record(inbb.getLong(), inbb
                    .getDouble()));

                // putting data in output buffer
                outbb.putLong(rem.getRecID());
                outbb.putDouble(rem.getKey());

                // when the new min needs to be inactive
                if (heap.getMin().getKey() < rem.getKey() && outbb
                    .position() < outBuff.length - 1) {
                    // swap to end, decrement heap size, then update
                    heap.swap(0, heap.heapSize() - 1);
                    heap.setHeapSize(heap.heapSize() - 1);
                    heap.update(0);
                }
                // if out buffer is full, then write to file and clear
                if (outbb.position() >= outBuff.length - 1) {

                    // add current file pos to runs
                    // then write to outfile
                    runs.add(outFile.getFilePointer());
                    outFile.write(outBuff);
                    outbb.position(0);
                    bb.clear();

                    // reactivate heap after writing the outbuffer to file
                    heap.setHeapSize(RECORDS_PER_BLOCK * BLOCKS_IN_RAM);
                    heap.buildHeap();
                }
            }
        }

        // empty heap after main process
        heap.setHeapSize(RECORDS_PER_BLOCK * BLOCKS_IN_RAM);
        heap.buildHeap();
        outbb.position(0);
        while (heap.heapSize() > 0) {
            // get the min and put in outbuffer
            Record min = heap.removeMin();
            outbb.putLong(min.getRecID());
            outbb.putDouble(min.getKey());

            // when outbuffer is full
            if (outbb.position() >= outBuff.length - 1) {
                runs.add(outFile.getFilePointer());
                outFile.write(outBuff);
                outbb.position(0);
                bb.clear();
            }
        }

        // adding end run position to runs
        runs.add(raf.length());
        // close files
        outFile.close();
        raf.close();

        // copy results back to input and then delete created file
        copyFile(selectOutput, inFile);
        selectOutput.delete();

    }


    /**
     * copies contents of one file to another
     * 
     * @param inputFile
     *            the input file
     * @param outputFile
     *            the file to be copied to
     * @throws IOException
     *             if a file error occurs
     */
    public static void copyFile(File inputFile, File outputFile)
        throws IOException {

        // copy one block at a time
        byte[] copy = new byte[BYTES_PER_BLOCK];
        ByteBuffer copyBB = ByteBuffer.wrap(copy);
        copyBB.position(0);
        RandomAccessFile inCopyRaf = new RandomAccessFile(inputFile, "r");
        // delete old contents
        outputFile.delete();
        RandomAccessFile outCopyRaf = new RandomAccessFile(outputFile, "rw");
        // while there is still data to read
        while (inCopyRaf.read(copy) != -1) {
            outCopyRaf.write(copy);
            copyBB.clear();
            copyBB.position(0);
        }
        // close file
        outCopyRaf.close();
        inCopyRaf.close();

    }


    /**
     * External Sorts multiWayMerge algorithm to
     * merge sorted runs together
     * 
     * @param runsList
     *            list of the positions of the sorted runs
     * @param inputFile
     *            the input file to be used
     * @param outputFile
     *            the file to be output to
     * @throws IOException
     *             if a file error occurs
     */
    public void multiwayMerge(
        List<Long> runsList,
        String inputFile,
        String outputFile)
        throws IOException {

        // finished when the runs list is the start and end of file
        if (runsList.size() == 2) {

            // if the data is already in this.filename
            if (inputFile.equals(this.filename)) {

                // delete old data in output file
                File del = new File(outputFile);
                del.delete();
                return;
            }
            // if the resulting data is in the temporary file
            else {
                // copy contents to this.filename and delete old
                File del = new File(inputFile);
                copyFile(del, new File(this.filename));
                del.delete();
                return;
            }
        }

        // create the new list of runs for next call and add 0
        List<Long> nextRunsList = new ArrayList<>();
        nextRunsList.add((long)0);
        // create output file and delete old contents
        File myOutFile = new File(outputFile);
        myOutFile.delete();
        RandomAccessFile out = new RandomAccessFile(myOutFile, "rw");

        byte[] outBuffer = new byte[BYTES_PER_BLOCK];
        ByteBuffer outBB = ByteBuffer.wrap(outBuffer);

        // loop to only get a max of 8 runs at a time
        for (int i = 0; i < runsList.size() - 1; i += 8) {

            // create my heap of Run objects
            MinHeap<Run> runHeap = new MinHeap<>(new Run[8], 0, 8);
            // getting the subList of max 8 runs
            List<Long> subRuns = runsList.subList(i, Math.min(i + 9, runsList
                .size()));

            // adding runs into a list of runs
            for (int k = 0; k < subRuns.size() - 1; k++) {
                Run aRun = new Run(subRuns.get(k), subRuns.get(k + 1),
                    inputFile);
                runHeap.insert(aRun);
            }

            // keep merging the min until all the runs have run out
            // delete a run from heap when its finished
            while (runHeap.heapSize() > 0) {
                Run minRun = runHeap.getMin();

                // put the data in outBuffer
                outBB.putLong(minRun.getRecord().getRecID());
                outBB.putDouble(minRun.getRecord().getKey());

                // if the run is finished then close its file and delete from
                // heap
                if (minRun.advanceRecord() == false) {
                    runHeap.getMin().close();
                    runHeap.removeMin();
                }

                // rebuilding the heap
                runHeap.buildHeap();

                // write to outFile if outBuffer is full
                if (outBB.position() >= outBuffer.length - 1) {
                    out.write(outBuffer);
                    outBB.position(0);
                    outBB.clear();
                }
            }
            nextRunsList.add(subRuns.get(subRuns.size() - 1));

        }

        out.close();
        // recursive call to next multiWayMerge using this outputFile as new
        // input
        multiwayMerge(nextRunsList, outputFile, inputFile);
    }


    /**
     * static method to count the number of blocks in a file
     * 
     * @param filename
     *            the file who's blocks were counting
     * @return
     *         the number of blocks in a file
     * @throws IOException
     *             if a file error occurs
     */
    public static int countBlocks(String filename) throws IOException {
        int count = 0;
        RandomAccessFile raf = new RandomAccessFile(new File(filename), "r");
        byte[] basicBuffer = new byte[BYTES_PER_BLOCK];
        // count while there are more blocks to read
        while (raf.read(basicBuffer) != -1) {
            count++;
        }
        // close file and return count
        raf.close();
        return count;

    }

}
