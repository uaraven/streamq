package net.ninjacat.streamq;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

/**
 * Iterator for the {@link BlockingQueue}.
 *
 * Emptiness of the queue is determined by special end-of-stream marker object, so this iterator's {@link #hasNext()}
 * method will not return {@code false} if queue is empty. Only when marker object is placed into queue this iterator
 * will stop producing new elements.
 *
 * @param <T> Type of elements in the queue
 */
public final class BlockingQueueIterator<T> implements Iterator<T> {
    private final BlockingQueue<T> queue;
    private final T endOfStreamMark;
    private T peeked;

    /**
     * Creates an iterator for a queue
     * @param queue Queue to iterate over
     * @param endOfStreamMark Object marking the end of stream. Object <strong>instance</strong> is checked,
     *                        with {@code ==} operator, not {@link Object#equals(Object)} method.
     * @param <T> Type of elements in the queue
     * @return new {@link BlockingQueueIterator}
     */
    public static <T> BlockingQueueIterator<T> of(final BlockingQueue<T> queue, final T endOfStreamMark) {
        return new BlockingQueueIterator<>(queue, endOfStreamMark);
    }

    private BlockingQueueIterator(final BlockingQueue<T> queue, final T endOfStreamMark) {
        this.queue = queue;
        this.endOfStreamMark = endOfStreamMark;
    }

    @Override
    public boolean hasNext() {
        if (peeked == null) {
            try {
                peeked = queue.take();
            } catch (final InterruptedException e) {
                // do nothing
            }
        }
        return peeked != endOfStreamMark;
    }

    @Override
    public T next() {
        final T local = peeked;
        peeked = null;
        return local;
    }
}
