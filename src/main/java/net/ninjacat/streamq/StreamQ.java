package net.ninjacat.streamq;

import java.util.Queue;
import java.util.stream.Stream;

/**
 * Fluent helper to facilitate copying {@link Stream} into {@link Queue}
 */
public final class StreamQ {
    private StreamQ() {
    }

    /**
     * Creates a reader to read from a {@link Stream} of objects into a queue.
     * <p>
     * To start reading one has to provide end of stream marker object with {@link StreamReader#withMarker(Object)}
     * method
     *
     * @param stream Stream to read
     * @param <T>    Type of objects in
     * @return Continuation of fluent reader builder
     */
    public static <T> StreamReader<T> read(final Stream<T> stream) {
        return new StreamReader<T>(stream);
    }

    public static final class StreamReader<T> {
        private final Stream<T> stream;

        StreamReader(final Stream<T> stream) {
            this.stream = stream;
        }

        /**
         * Sets end-of-stream marker object for this reader.
         *
         * @param endOfStream Object to indicate end-of-stream. This should be a singleton object,
         *                    as {@link BlockingQueueIterator} will use reference comparision to
         *                    find the end-of-stream
         * @return StreamReader with configured end-of-stream marker
         */
        public TerminatingStreamReader<T> withMarker(final T endOfStream) {
            return new TerminatingStreamReader<T>(stream, endOfStream);
        }
    }

    /**
     * Continuation of fluent stream-to-queue builder
     *
     * @param <T> Type of objects in the stream
     */
    public static final class TerminatingStreamReader<T> {
        private final Stream<T> stream;
        private final T endOfStream;

        TerminatingStreamReader(final Stream<T> stream, final T endOfStream) {
            this.stream = stream;
            this.endOfStream = endOfStream;
        }

        /**
         * Sets the queue to receive elements from the stream. This method will start reading from the
         * stream until it is exhausted and then close it with {@link Stream#close()} method.
         *
         * @param queue Queue to accept elements from the stream
         */
        public void into(final Queue<T> queue) {
            streamToQueue(stream, queue, endOfStream);
        }
    }

    private static <T> void streamToQueue(final Stream<T> stream, final Queue<T> queue, final T endOfStream) {
        try (final Stream<T> dataStream = stream) {
            dataStream.forEach(queue::add);
        } finally {
            queue.add(endOfStream);
        }
    }
}
