/*
 * streamq: ArrayMarkedQueue.java
 *
 * Copyright 2019 Oleksiy Voronin <me@ovoronin.info>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ninjacat.streamq;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implementation of {@link MarkedQueue}
 * <p>
 * This implementation uses ArrayBlockingQueue internally
 *
 * @param <E> Type of elements in the Queue
 */
public final class ArrayMarkedQueue<E> extends ArrayBlockingQueue<E> implements MarkedQueue<E> {
    private final E marker;

    public ArrayMarkedQueue(final int capacity, final boolean fair, final E marker) {
        super(capacity, fair);
        if (marker == null) {
            throw new IllegalArgumentException("marker cannot be null");
        }
        this.marker = marker;
    }

    public ArrayMarkedQueue(final int capacity, final E marker) {
        this(capacity, false, marker);
    }

    @Override
    public Iterator<E> iterator() {
        return MarkedQueueIterator.of(this, marker);
    }

    @Override
    public Stream<E> stream() {
        return StreamSupport.stream(MarkedQueueIterable.of(this, marker).spliterator(), false);
    }

    @Override
    public Stream<E> parallelStream() {
        return StreamSupport.stream(MarkedQueueIterable.of(this, marker).spliterator(), true);
    }

    @Override
    public E getMarker() {
        return marker;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayMarkedQueue)) return false;
        final ArrayMarkedQueue<?> markedQueue = (ArrayMarkedQueue<?>) o;
        return Objects.equals(marker, markedQueue.marker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marker);
    }
}
