package hse.java.lectures.lecture6.tasks.queue;


import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class BoundedBlockingQueue<T> {

    private final Object key = new Object();

    int capacity = 0;

    final Queue<T> queue = new ArrayDeque<>();

    public BoundedBlockingQueue(int capacity) {
        synchronized (key) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("Capacity <= 0");
            }
            this.capacity = capacity;
        }
    }

    public void put(T item) throws InterruptedException {
        synchronized (key) {
            if (item == null) {
                throw new NullPointerException("Item must be not null");
            }
            while(queue.size() >= capacity) {
                key.wait();
            }

            queue.add(item);
            key.notifyAll();
        }
    }

    public T take() throws InterruptedException {
        synchronized (key) {
            while (queue.isEmpty()) {
                key.wait();
            }
            T el = queue.poll();
            key.notifyAll();

            return el;

        }
    }

    public int size() {
        synchronized (key) {
            return queue.size();
        }
    }

    public int capacity() {
        return capacity;
    }
}
