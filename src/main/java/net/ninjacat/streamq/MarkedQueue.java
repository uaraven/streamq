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

import java.util.concurrent.BlockingQueue;

/*
 * {@link java.util.concurrent.BlockingQueue} that provides {@link Iterator} to handle end of queue
 * differently from standard implementation. Iterator doesn't terminate when queue is empty, it will use
 * marker object to determine the end of queue.
 *
 * @param <E>
 */
public interface MarkedQueue<E> extends BlockingQueue<E> {
    /**
     * Get marker object instnace
     *
     * @return Marker object
     */
    E getMarker();
}
