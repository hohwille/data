/*
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
 * SPDX-License-Identifier: Apache-2.0
 */
package jakarta.data.repository;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotates a repository method that inserts entities.</p>
 *
 * <p>The {@code Insert} annotation indicates that the annotated repository method requests that one or more entities
 * be inserted into the database. This method must have a single parameter whose type must be one of the following:
 * </p>
 * <ul>
 *     <li>The entity to be inserted.</li>
 *     <li>An {@code Iterable} of entities to be inserted.</li>
 *     <li>An array of entities to be inserted.</li>
 * </ul>
 * <p>The return type of an annotated method that requires a single entity as the parameter must have a return type
 * that is {@code void}, {@code Void}, or the same type as the parameter.
 * The return type of an annotated method that accepts an {@code Iterable} or array of entities as the parameter must
 * have a return type that is {@code void}, {@code Void}, or an {@code Iterable} or array of the entity.
 * For example, if the method is annotated with {@code @Insert} and takes a parameter of type {@code Car car},
 * the return type can be {@code Car}.
 * Similarly, if the parameter is an {@code Iterable<Car>} or an array of {@code Car}, the return type can be
 * {@code Iterable<Car>}.
 * Entities that are returned by the annotated method must include all values that were
 * written to the database, including all automatically generated values and incremented values
 * that changed due to the insert. The position of entities within an {@code Iterable} or array return value
 * must correspond to the position of entities in the parameter based on the unique identifier of the entity.
 * </p>
 * <p>After invoking this method, it is recommended not to use the entity value supplied as a parameter, as this method
 * makes no guarantees about the state of the entity value after insertion.
 * </p>
 * <p>If an entity of this type with the same unique identifier already exists in the database
 * and the databases performs ACID (atomic, consistent, isolated, durable) transactions,
 * then annotated method raises {@link jakarta.data.exceptions.EntityExistsException}.
 * In databases that follow the BASE model or use an append model to write data,
 * this exception is not thrown.
 * </p>
 * <p>For example, consider an interface representing a garage:</p>
 * <pre>
 * {@code @Repository}
 * interface Garage {
 *     {@code @Insert}
 *     Car park(Car car);
 * }
 * </pre>
 * <p>The {@code @Insert} annotation can be used to indicate that the {@code park(Car)} method is responsible for inserting
 * a {@code Car} entity into a database.
 * </p>
 *
 * <p>If this annotation is combined with other operation annotations (e.g., {@code @Update}, {@code @Delete},
 * {@code @Find}, {@code @Query}, {@code @Save}),
 * it will throw an {@link UnsupportedOperationException} because only one operation type can be specified.
 * A Jakarta Data provider implementation must detect (and report) this error at compile time or at runtime.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Insert {
}
