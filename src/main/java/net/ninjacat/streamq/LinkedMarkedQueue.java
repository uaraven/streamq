/*
 * streamq: MarkedQueue.java
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implementation of {@link MarkedQueue}
 * <p>
 * This implementation uses LinkedBlockingQueue internally
 *
 * @param <E> Type of elements in the Queue
 */
public final class LinkedMarkedQueue<E> extends LinkedBlockingQueue<E> implements MarkedQueue<E> {
    private final E marker;

    public LinkedMarkedQueue(final E marker) {
        this(Integer.MAX_VALUE, marker);
    }

    public LinkedMarkedQueue(final int capacity, final E marker) {
        super(capacity);
        if (marker == null) {
            throw new IllegalArgumentException("marker cannot be null");
        }
        this.marker = marker;
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkedMarkedQueue)) return false;
        final LinkedMarkedQueue<?> markedQueue = (LinkedMarkedQueue<?>) o;
        return Objects.equals(marker, markedQueue.marker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marker);
    }

    @Override
    public E getMarker() {
        return marker;
    }
}
