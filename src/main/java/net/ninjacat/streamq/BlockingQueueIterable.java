package net.ninjacat.streamq;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

/**
 * Iterable for the {@link BlockingQueue}.
 * <p>
 * Emptiness of the queue is determined by special end-of-stream marker object, so this iterable will produce
 * Iterators which {@link Iterator#hasNext()} method will not return {@code false} if queue is empty, only when
 * marker object is placed into queue.
 *
 * @param <T> Type of elements in the queue
 */
public final class BlockingQueueIterable<T> implements Iterable<T> {

    private final BlockingQueue<T> queue;
    private final T endOfStream;

    private BlockingQueueIterable(final BlockingQueue<T> queue, final T endOfStream) {
        this.queue = queue;
        this.endOfStream = endOfStream;
    }

    /**
     * Creates an iterable for a queue
     *
     * @param queue       Queue to iterate over
     * @param endOfStream Object marking the end of stream. Object <strong>instance</strong> is checked,
     *                    with {@code ==} operator, not {@link Object#equals(Object)} method.
     * @param <T>         Type of elements in the queue
     * @return new {@link BlockingQueueIterator}
     */
    public static <T> Iterable<T> of(final BlockingQueue<T> queue, final T endOfStream) {
        return new BlockingQueueIterable<>(queue, endOfStream);
    }

    @Override
    public Iterator<T> iterator() {
        return BlockingQueueIterator.of(queue, endOfStream);
    }
}
