/*
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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
 *
 *  SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class SortTest {
    public static final String NAME = "name";

    @Test
    @DisplayName("Should throw NullPointerException when one of the properties are null")
    void shouldReturnErrorWhenPropertyDirectionNull() {
        assertThatNullPointerException().isThrownBy(() -> Sort.of(null, null, false));
        assertThatNullPointerException().isThrownBy(() -> Sort.of(NAME, null, true));
        assertThatNullPointerException().isThrownBy(() -> Sort.of(null, Direction.ASC, false));
    }

    @Test
    @DisplayName("Should use ascending sort when direction is ASC")
    void shouldCreateAscendingSort() {
        Sort<?> order = Sort.of(NAME, Direction.ASC, false);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isTrue();
            softly.assertThat(order.isDescending()).isFalse();
            softly.assertThat(order.ignoreCase()).isFalse();
        });
    }

    @Test
    @DisplayName("Should descending short when direction is DESC")
    void shouldCreateDescendingSort() {
        Sort<?> order = Sort.of(NAME, Direction.DESC, true);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isFalse();
            softly.assertThat(order.isDescending()).isTrue();
            softly.assertThat(order.ignoreCase()).isTrue();
        });
    }

    @Test
    @DisplayName("Should ascending sort when Sort.asc method is used")
    void shouldCreateAsc() {
        Sort<?> order = Sort.asc("name");

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isTrue();
            softly.assertThat(order.isDescending()).isFalse();
            softly.assertThat(order.ignoreCase()).isFalse();
        });
    }

    @Test
    @DisplayName("Should use ascending sort ignoring case when Sort.ascIgnoreCase method is used")
    void shouldCreateAscIgnoreCase() {
        Sort<?> order = Sort.ascIgnoreCase("name");

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isTrue();
            softly.assertThat(order.isDescending()).isFalse();
            softly.assertThat(order.ignoreCase()).isTrue();
        });
    }

    @Test
    @DisplayName("Should descending sort when Sort.desc method is used")
    void shouldCreateDesc() {
        Sort<?> order = Sort.desc(NAME);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isFalse();
            softly.assertThat(order.isDescending()).isTrue();
            softly.assertThat(order.ignoreCase()).isFalse();
        });
    }

    @Test
    @DisplayName("Should use descending sort ignoring case when Sort.descIgnoreCase method is used")
    void shouldCreateDescIgnoreCase() {
        Sort<?> order = Sort.descIgnoreCase(NAME);

        assertSoftly(softly -> {
            softly.assertThat(order).isNotNull();
            softly.assertThat(order.property()).isEqualTo(NAME);
            softly.assertThat(order.isAscending()).isFalse();
            softly.assertThat(order.isDescending()).isTrue();
            softly.assertThat(order.ignoreCase()).isTrue();
        });
    }
}
