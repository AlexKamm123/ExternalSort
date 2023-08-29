/**
 * Holds a single record
 * 
 * @author CS Staff: Patrick Sullivan
 * @version 2023 March
 * 
 * @author Alex Kammann
 * @version 03.28.23
 */
public class Record implements Comparable<Record> {

    /**
     * 16 bytes per record
     */
    public static final int BYTES = 16;

    private long recID;
    private double key;

    /**
     * constructor for a record
     * 
     * @param recID
     *            the id of the record
     * @param key
     *            the key of the record
     */
    public Record(long recID, double key) {
        this.recID = recID;
        this.key = key;
    }


    /**
     * @return the recID
     */
    public long getRecID() {
        return recID;
    }


    /**
     * @return the key
     */
    public double getKey() {
        return key;
    }


    /**
     * Compare two records based on their keys
     */
    @Override
    public int compareTo(Record toBeCompared) {
        return Double.compare(this.key, toBeCompared.key);
    }


    /**
     * returns string representation of record
     */
    @Override
    public String toString() {
        return String.format("%s %s", recID, key);
    }

}
