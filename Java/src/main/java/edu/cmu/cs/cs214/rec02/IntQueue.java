package edu.cmu.cs.cs214.rec02;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Unit tests for IntQueue implementations.
 *
 * Part 1: Specification (black-box) testing against LinkedIntQueue.
 * Part 2: Structural (white-box) testing against ArrayIntQueue,
 *         targeting 100% line coverage and exposing all three bugs.
 */
public class IntQueueTest {

    private IntQueue mQueue;
    private List<Integer> testList;

    @Before
    public void setUp() {
        // Switch comment to test ArrayIntQueue
        mQueue = new LinkedIntQueue();
//        mQueue = new ArrayIntQueue();

        testList = new ArrayList<>(List.of(1, 2, 3));
    }

    // ---------------------------------------------------------------
    // Provided tests
    // ---------------------------------------------------------------

    @Test
    public void testIsEmpty() {
        assertTrue(mQueue.isEmpty());
    }

    @Test
    public void testEnqueue() {
        for (int i = 0; i < testList.size(); i++) {
            mQueue.enqueue(testList.get(i));
            assertEquals(testList.get(0), mQueue.peek());
            assertEquals(i + 1, mQueue.size());
        }
    }

    @Test
    public void testContent() throws IOException {
        InputStream in = new FileInputStream("src/test/resources/data.txt");
        try (Scanner scanner = new Scanner(in)) {
            scanner.useDelimiter("\\s*fish\\s*");

            List<Integer> correctResult = new ArrayList<>();
            while (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                correctResult.add(input);
                System.out.println("enqueue: " + input);
                mQueue.enqueue(input);
            }

            for (Integer result : correctResult) {
                assertEquals(mQueue.dequeue(), result);
            }
        }
    }

    // ---------------------------------------------------------------
    // Part 1 – Specification tests (written against LinkedIntQueue)
    // ---------------------------------------------------------------

    /** A freshly-enqueued queue must NOT be empty. */
    @Test
    public void testNotEmpty() {
        mQueue.enqueue(42);
        assertFalse(mQueue.isEmpty());
    }

    /** peek() on an empty queue must return null (per spec). */
    @Test
    public void testPeekEmptyQueue() {
        assertNull(mQueue.peek());
    }

    /** peek() on a non-empty queue returns the head without removing it. */
    @Test
    public void testPeekNoEmptyQueue() {
        mQueue.enqueue(7);
        mQueue.enqueue(8);
        assertEquals(Integer.valueOf(7), mQueue.peek());
        // peek must not remove the element
        assertEquals(2, mQueue.size());
    }

    /** dequeue() returns elements in FIFO order. */
    @Test
    public void testDequeue() {
        for (int v : testList) {
            mQueue.enqueue(v);
        }
        for (int v : testList) {
            assertEquals(Integer.valueOf(v), mQueue.dequeue());
        }
        assertTrue(mQueue.isEmpty());
    }

    /** dequeue() on an empty queue returns null (per spec). */
    @Test
    public void testDequeueEmptyQueue() {
        assertNull(mQueue.dequeue());
    }

    /** size() reflects the number of elements correctly. */
    @Test
    public void testSize() {
        assertEquals(0, mQueue.size());
        mQueue.enqueue(1);
        assertEquals(1, mQueue.size());
        mQueue.enqueue(2);
        assertEquals(2, mQueue.size());
        mQueue.dequeue();
        assertEquals(1, mQueue.size());
    }

    /** clear() removes all elements; queue becomes empty. */
    @Test
    public void testClear() {
        mQueue.enqueue(1);
        mQueue.enqueue(2);
        mQueue.clear();
        assertTrue(mQueue.isEmpty());
        assertEquals(0, mQueue.size());
    }

    // ---------------------------------------------------------------
    // Part 2 – Structural tests targeting ArrayIntQueue
    //   Switch setUp() to new ArrayIntQueue() to run these.
    //   These tests expose the three bugs and cover every line.
    // ---------------------------------------------------------------

    /**
     * Bug #1 – isEmpty() uses ">=" instead of "==".
     * After enqueuing, isEmpty() incorrectly returns true.
     */
    @Test
    public void testIsEmptyAfterEnqueue() {
        mQueue.enqueue(10);
        assertFalse("isEmpty() must be false after enqueue", mQueue.isEmpty());
    }

    /**
     * Bug #2 – peek() does not guard against an empty queue.
     * This exercises the isEmpty branch inside peek().
     */
    @Test
    public void testPeekOnEmptyReturnsNull() {
        assertNull("peek() on empty queue must return null", mQueue.peek());
    }

    /**
     * Bug #3 – ensureCapacity()'s second loop uses wrong index.
     * Fill beyond INITIAL_SIZE (10) to force a resize, then verify
     * elements still dequeue in the correct FIFO order.
     */
    @Test
    public void testEnqueueBeyondInitialCapacity() {
        // Enqueue 12 elements (> INITIAL_SIZE of 10)
        for (int i = 1; i <= 12; i++) {
            mQueue.enqueue(i);
        }
        assertEquals(12, mQueue.size());
        for (int i = 1; i <= 12; i++) {
            assertEquals(Integer.valueOf(i), mQueue.dequeue());
        }
    }

    /**
     * Forces ensureCapacity() with a non-zero head (wrap-around scenario).
     * Dequeue some elements first so head > 0, then enqueue until resize.
     */
    @Test
    public void testResizeWithWrappedHead() {
        // Fill to capacity
        for (int i = 0; i < 10; i++) {
            mQueue.enqueue(i);
        }
        // Dequeue 3 so head advances to 3
        mQueue.dequeue();
        mQueue.dequeue();
        mQueue.dequeue();
        // Now enqueue 4 more to trigger resize (size will hit 10 again)
        for (int i = 10; i < 14; i++) {
            mQueue.enqueue(i);
        }
        // Verify FIFO order across the resize
        for (int i = 3; i < 14; i++) {
            assertEquals(Integer.valueOf(i), mQueue.dequeue());
        }
    }

    /** clear() resets head and size; subsequent operations work correctly. */
    @Test
    public void testClearThenReuse() {
        mQueue.enqueue(5);
        mQueue.enqueue(6);
        mQueue.clear();
        assertEquals(0, mQueue.size());
        assertTrue(mQueue.isEmpty());
        mQueue.enqueue(99);
        assertEquals(Integer.valueOf(99), mQueue.peek());
        assertEquals(Integer.valueOf(99), mQueue.dequeue());
        assertTrue(mQueue.isEmpty());
    }

    /** enqueue() returns true on success. */
    @Test
    public void testEnqueueReturnsTrue() {
        assertTrue(mQueue.enqueue(1));
    }
}
