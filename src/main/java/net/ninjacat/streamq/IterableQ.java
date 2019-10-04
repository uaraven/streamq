package net.ninjacat.streamq;

import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class IterableQ<T> extends LinkedBlockingQueue<T> {
    private final T marker;

    static <T> IterableQ<T> withMarker(final @NotNull T marker) {
        if (marker == null) {
            throw new IllegalArgumentException("marker cannot be null");
        }
        return new IterableQ<>(marker);
    }

    private IterableQ(final T marker) {
        super();
        this.marker = marker;
    }

    @Override
    public Iterator<T> iterator() {
        return BlockingQueueIterator.of(this, marker);
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(BlockingQueueIterable.of(this, marker).spliterator(), false);
    }

    @Override
    public Stream<T> parallelStream() {
        return StreamSupport.stream(BlockingQueueIterable.of(this, marker).spliterator(), true);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof IterableQ)) return false;
        final IterableQ<?> iterableQ = (IterableQ<?>) o;
        return Objects.equals(marker, iterableQ.marker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marker);
    }
}
