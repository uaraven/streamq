/*
 * streamq: MarkedQ.java
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

import javax.validation.constraints.NotNull;

/**
 * Builder for {@link MarkedQueue}. Allows to set capacity, type (linked or blocked) and marker object.
 *
 * @param <E> Type of elements in the Queue
 */
public final class MarkedQ<E> {
    private int capacity;
    private QueueType queueType;
    private final E marker;

    public static <T> MarkedQ<T> withMarker(final @NotNull T marker) {
        return new MarkedQ<>(marker);
    }

    private MarkedQ(final @NotNull E marker) {
        if (marker == null) {
            throw new IllegalArgumentException("marker cannot be null");
        }
        this.marker = marker;
        this.queueType = QueueType.LINKED;
        this.capacity = Integer.MAX_VALUE;
    }

    public MarkedQ<E> linked() {
        this.queueType = QueueType.LINKED;
        return this;
    }

    public MarkedQ<E> array() {
        this.queueType = QueueType.ARRAY;
        return this;
    }

    public MarkedQ<E> withCapacity(final int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be greater than zero");
        this.capacity = capacity;
        return this;
    }

    public MarkedQueue<E> build() {
        if (queueType == QueueType.LINKED) {
            return new LinkedMarkedQueue<>(capacity, marker);
        } else {
            return new ArrayMarkedQueue<>(capacity, marker);
        }
    }

    private enum QueueType {
        LINKED,
        ARRAY;
    }


}
