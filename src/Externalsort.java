import java.io.File;
import java.io.IOException;

// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

/**
 * Driver class for this project
 * 
 * @author Alex Kammann
 * @version 03.27.23
 */
public class Externalsort {

    /**
     * @param args
     *            Command line file to sort
     * @throws IOException
     *             if a file exception occurs
     */
    public static void main(String[] args) throws IOException {
        
        /*
         * Input file contains binary data of unsorted "records".
         * A "record" consists of a data (8 bytes) key (8 bytes) pair.
         * The file will be sorted externally using Replacement Selection and 8 Way Multiway Merge
         */
        
        String fileName = "16blocks.bin";

        ByteFile bf = new ByteFile(fileName, ByteFile.countBlocks(fileName));
        bf.replacementSelection();
        bf.multiwayMerge(bf.getRuns(), fileName, "mergeOut.bin");
        bf.printRecords();
    }

}
