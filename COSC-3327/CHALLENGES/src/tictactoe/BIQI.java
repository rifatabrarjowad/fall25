package tictactoe;

public class BIQI {
    private final int[] intArray;
    private int front;
    private int back;
    private int s; // size (number of elements currently stored)

    // pre: capacity >= 0
    public BIQI(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("capacity >= 0 required");
        s = 0;
        intArray = new int[capacity];
        front = 0;
        back  = intArray.length - 1; // so first enqueue lands at index 0
    }

    public int getSize() {
        return s;
    }

    public int getCapacity() {
        int capacity = intArray.length;
        return capacity;
    }

    public boolean isEmpty() { return s == 0; }
    public boolean isFull()  { return s == intArray.length; }

    /** Enqueue at the logical "back". */
    public void enqueue(int x) {
        if (isFull()) throw new IllegalStateException("queue full");
        back = (back + 1) % intArray.length;
        intArray[back] = x;
        s++;
    }

    /** Remove and return from the logical "front". */
    public int dequeue() {
        if (isEmpty()) throw new IllegalStateException("queue empty");
        int rv = intArray[front];
        front = (front + 1) % intArray.length;
        s--;
        return rv;
    }

    /** Peek at the front element without removing. */
    public int peek() {
        if (isEmpty()) throw new IllegalStateException("queue empty");
        return intArray[front];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("tictactoe.BIQI[");
        for (int i = 0; i < s; i++) {
            if (i > 0) sb.append(", ");
            sb.append(intArray[(front + i) % intArray.length]);
        }
        sb.append("] size=").append(s).append("/").append(intArray.length);
        return sb.toString();
    }
}
