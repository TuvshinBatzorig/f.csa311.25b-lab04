package edu.cmu.cs.cs214.rec02;

import java.util.Arrays;

/**
 * A resizable-array implementation of the {@link IntQueue} interface.
 *
 * Bug fixes applied:
 *  1. isEmpty()        – changed `size >= 0` to `size == 0`
 *  2. peek()           – added null guard for empty queue
 *  3. ensureCapacity() – fixed second loop index from `head - i`
 *                        to `oldCapacity - head + i`
 *
 * @author Alex Lockwood
 * @author Ye Lu
 */
public class ArrayIntQueue implements IntQueue {

    /** An array holding this queue's data */
    private int[] elementData;

    /** Index of the next dequeue-able value */
    private int head;

    /** Current size of queue */
    private int size;

    /** The initial size for new instances of ArrayQueue */
    private static final int INITIAL_SIZE = 10;

    /** Constructs an empty queue with an initial capacity of ten. */
    public ArrayIntQueue() {
        elementData = new int[INITIAL_SIZE];
        head = 0;
        size = 0;
    }

    /** {@inheritDoc} */
    public void clear() {
        Arrays.fill(elementData, 0);
        size = 0;
        head = 0;
    }

    /** {@inheritDoc} */
    public Integer dequeue() {
        if (isEmpty()) {
            return null;
        }
        Integer value = elementData[head];
        head = (head + 1) % elementData.length;
        size--;
        return value;
    }

    /** {@inheritDoc} */
    public boolean enqueue(Integer value) {
        ensureCapacity();
        int tail = (head + size) % elementData.length;
        elementData[tail] = value;
        size++;
        return true;
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        // BUG FIX #1: was `size >= 0` (always true); must be `size == 0`
        return size == 0;
    }

    /** {@inheritDoc} */
    public Integer peek() {
        // BUG FIX #2: missing null guard – return null when empty
        if (isEmpty()) {
            return null;
        }
        return elementData[head];
    }

    /** {@inheritDoc} */
    public int size() {
        return size;
    }

    /**
     * Increases the capacity of this ArrayIntQueue instance, if necessary,
     * to ensure it can hold at least size + 1 elements.
     */
    private void ensureCapacity() {
        if (size == elementData.length) {
            int oldCapacity = elementData.length;
            int newCapacity = 2 * oldCapacity + 1;
            int[] newData = new int[newCapacity];
            // Copy elements from head to end of old array
            for (int i = head; i < oldCapacity; i++) {
                newData[i - head] = elementData[i];
            }
            // BUG FIX #3: was `newData[head - i]` (wrong index);
            // elements before head wrap around to positions after the first segment
            for (int i = 0; i < head; i++) {
                newData[oldCapacity - head + i] = elementData[i];
            }
            elementData = newData;
            head = 0;
        }
    }
}
