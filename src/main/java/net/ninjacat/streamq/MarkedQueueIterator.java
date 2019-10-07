/*
 * streamq: BlockingQueueIterator.java
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
public final class MarkedQueueIterator<T> implements Iterator<T> {
    private final MarkedQueue<T> queue;
    private final T endOfStreamMark;
    private T peeked;

    /**
     * Creates an iterator for a queue
     * @param queue Queue to iterate over
     * @param endOfStreamMark Object marking the end of stream. Object <strong>instance</strong> is checked,
     *                        with {@code ==} operator, not {@link Object#equals(Object)} method.
     * @param <T> Type of elements in the queue
     * @return new {@link MarkedQueueIterator}
     */
    public static <T> MarkedQueueIterator<T> of(final MarkedQueue<T> queue, final T endOfStreamMark) {
        return new MarkedQueueIterator<>(queue, endOfStreamMark);
    }

    private MarkedQueueIterator(final MarkedQueue<T> queue, final T endOfStreamMark) {
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
