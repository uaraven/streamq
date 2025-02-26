/*
 * streamq: StreamProvider.java
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

import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class StreamProvider<T> {

    private final int size;
    private final IntFunction<T> supplier;

    StreamProvider(final int size, final IntFunction<T> supplier) {
        this.size = size;
        this.supplier = supplier;
    }

    Stream<T> produce() {
        return IntStream.range(0, size).mapToObj(supplier).peek(it -> {
            try {
                Thread.sleep(10);
            } catch (final InterruptedException ignored) {
            }
        });
    }
}
