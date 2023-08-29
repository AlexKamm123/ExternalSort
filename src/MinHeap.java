import java.io.IOException;

/**
 * Min-heap implementation by Patrick Sullivan, based on OpenDSA Heap code
 * Can use `java -ea` (Java's VM arguments) to Enable Assertions
 * These assertions will check valid heap positions
 * 
 * @author Alex Kammann
 * @version 03.27.23
 *
 * @param <T>
 *            the type of data used in this heap
 */
class MinHeap<T extends Comparable<T>> {
    private T[] heap; // Pointer to the heap array
    private int capacity; // Maximum size of the heap
    private int n; // Number of active things currently in heap

    /**
     * constructs a minHeap
     * 
     * @param arrayForHeap
     *            the base array of the heap
     * @param heapSize
     *            the current size of the array
     * @param capacity
     *            the max size of the minHeap
     * @throws IOException
     */
    MinHeap(T[] arrayForHeap, int heapSize, int capacity) throws IOException {
        if (capacity > arrayForHeap.length || heapSize > capacity) {
            throw new IOException(
                "capacity is beyond array limits or Heap size is beyond max");
        }
        heap = arrayForHeap;
        n = heapSize;
        this.capacity = capacity;
        // build the heap from the array
        buildHeap();
    }


    /**
     * Return position for left child of pos
     * 
     * @param pos
     *            the position to return
     * @return
     *         the position of left child of pos
     */
    public static int leftChild(int pos) {
        return 2 * pos + 1;
    }


    /**
     * Return position for right child of pos
     * 
     * @param pos
     *            the position to return
     * @return
     *         the position of the right child of pos
     */
    public static int rightChild(int pos) {
        return 2 * pos + 2;
    }


    /**
     * Return position for the parent of pos
     * 
     * @param pos
     *            the position to return
     * @return
     *         the position of the parent of pos
     */
    public static int parent(int pos) {
        return (pos - 1) / 2;
    }


    /**
     * Forcefully changes the heap size. May need a buildHeap() afterwards
     * 
     * @param newSize
     *            the new size of the heap
     */
    public void setHeapSize(int newSize) {
        n = newSize;
    }


    /**
     * 
     * @return
     *         current size of the heap
     */
    public int heapSize() {
        return n;
    }


    /**
     * 
     * @param pos
     *            position to check for
     * @return Return true if pos a leaf position, false otherwise
     */
    public boolean isLeaf(int pos) {
        return (n / 2 <= pos) && (pos < n);
    }


    /**
     * insert a value in the heap
     * 
     * @param key
     *            the value being inserted
     * @throws IOException
     *             if n > capacity of heap
     */
    public void insert(T key) throws IOException {
        if (n > capacity)
            throw new IOException("Heap is full; cannot insert");
        heap[n] = key;
        n++;
        siftUp(n - 1);
    }


    /**
     * Organize contents of array to satisfy the heap structure
     * 
     * @throws IOException
     *             if sift down has an invalid position
     */
    public void buildHeap() throws IOException {
        // Call sift down on each internal node, starting from bottom
        for (int i = parent(n - 1); i >= 0; i--) {
            siftDown(i);
        }
    }


    /**
     * Moves an element down to its correct place
     * 
     * @param pos
     *            position to sift down
     * @throws IOException
     *             if pos is an invalid position
     */
    public void siftDown(int pos) throws IOException {
        if (0 > pos || pos >= n)
            throw new IOException("Invalid heap position");
        while (!isLeaf(pos)) {
            int child = leftChild(pos);
            // compare the left and right children
            if ((child + 1 < n) && isLessThan(child + 1, child)) {
                child = child + 1; // child is now the index with the smaller
                                   // value
            }
            if (!isLessThan(child, pos)) {
                return; // stop early
            }
            swap(pos, child);
            pos = child; // keep sifting down
        }
    }


    /**
     * Moves an element up to its correct place
     * 
     * @param pos
     *            position to sift up
     * @throws IOException
     *             if pos is an invalid heap position
     */
    public void siftUp(int pos) throws IOException {
        if (0 > pos || pos >= n)
            throw new IOException("Invalid heap position");
        while (pos > 0) {
            int parent = parent(pos);
            if (isLessThan(parent, pos)) {
                return; // stop early
            }
            swap(pos, parent);
            pos = parent; // keep sifting up
        }
    }


    /**
     * removes the min value of the heap
     * 
     * @return the minimum value removed
     * @throws IOException
     *             if the heap is empty
     */
    public T removeMin() throws IOException {
        if (n <= 0)
            throw new IOException("Heap is empty; cannot remove");
        n--;
        if (n > 0) {
            swap(0, n); // Swap minimum with last value
            siftDown(0); // Put new heap root val in correct place
        }
        return heap[n];
    }


    /**
     * modifies the value a position
     * 
     * @param pos
     *            the position to change
     * @param newVal
     *            the new value to be inserted into pos
     * @return
     *         the old value that was removed
     * @throws IOException
     *             if there was an invalid heap position
     */
    public T modify(int pos, T newVal) throws IOException {
        if (0 > pos || pos >= n)
            throw new IOException("Invalid heap position");
        T temp = heap[pos];
        heap[pos] = newVal;
        update(pos);
        return temp;
    }


    /**
     * updates the given pos
     * 
     * @param pos
     *            the position to be updated
     * @throws IOException
     *             if there was an invalid heap position
     */
    public void update(int pos) throws IOException {
        siftUp(pos); // priority goes up
        siftDown(pos); // unimportant goes down
    }


    /**
     * swaps the values at the given positions
     * 
     * @param pos1
     *            the first position
     * @param pos2
     *            the second position
     */
    public void swap(int pos1, int pos2) {
        T temp = heap[pos1];
        heap[pos1] = heap[pos2];
        heap[pos2] = temp;
    }


    /**
     * does fundamental comparison used for checking heap validity
     * 
     * @param pos1
     *            the first position to check
     * @param pos2
     *            the second position to compare to
     * @return
     *         true is the first value is less than the second, false otherwise
     */
    private boolean isLessThan(int pos1, int pos2) {
        return heap[pos1].compareTo(heap[pos2]) < 0;
    }


    /**
     * gets the minimum value of a heap
     * 
     * @return
     *         the minimum value of the heap
     */
    public T getMin() {
        return heap[0];
    }

}
