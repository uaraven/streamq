/*
 * streamq: BlockingQueueIterable.java
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

/**
 * Iterable for the {@link MarkedQueue}.
 * <p>
 * Emptiness of the queue is determined by special end-of-stream marker object, so this iterable will produce
 * Iterators which {@link Iterator#hasNext()} method will not return {@code false} if queue is empty, only when
 * marker object is placed into queue.
 *
 * @param <T> Type of elements in the queue
 */
public final class MarkedQueueIterable<T> implements Iterable<T> {

    private final MarkedQueue<T> queue;
    private final T endOfStream;

    private MarkedQueueIterable(final MarkedQueue<T> queue, final T endOfStream) {
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
     * @return new {@link MarkedQueueIterator}
     */
    public static <T> Iterable<T> of(final MarkedQueue<T> queue, final T endOfStream) {
        return new MarkedQueueIterable<>(queue, endOfStream);
    }

    @Override
    public Iterator<T> iterator() {
        return MarkedQueueIterator.of(queue, endOfStream);
    }
}
