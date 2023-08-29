/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.jakarta.tck.data.standalone.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;

import ee.jakarta.tck.data.framework.junit.anno.Assertion;
import ee.jakarta.tck.data.framework.junit.anno.Persistence;
import ee.jakarta.tck.data.framework.junit.anno.Standalone;
import ee.jakarta.tck.data.standalone.persistence.Product.Department;

import jakarta.data.exceptions.MappingException;
import jakarta.data.repository.Sort;
import jakarta.data.repository.Streamable;
import jakarta.inject.Inject;

/**
 * Execute tests with a Persistence specific entity with a repository that requires read and writes (AKA not read-only) 
 */
@Standalone
@Persistence
public class PersistenceEntityTests {
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class).addClasses(Product.class, Catalog.class);
    }
    
    @Inject
    Catalog catalog;

    @Assertion(id = "133", strategy = "Use a repository method with Contains to query for a value with a collection attribute.")
    public void testContainsInCollection() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        catalog.save(Product.of("spade", 9.99, "TEST-PROD-21", Department.TOOLS, Department.GARDEN));
        catalog.save(Product.of("shelves", 109.88, "TEST-PROD-22", Department.FURNITURE, Department.OFFICE));
        catalog.save(Product.of("desk", 315.98, "TEST-PROD-23", Department.FURNITURE, Department.OFFICE));
        catalog.save(Product.of("stapler", 6.79, "TEST-PROD-24", Department.OFFICE));

        Product[] found = catalog.findByDepartmentsContains(Department.FURNITURE);
        assertEquals(2, found.length);
        assertEquals("desk", found[0].getName());
        assertEquals("shelves", found[1].getName());

        found = catalog.findByDepartmentsContains(Department.OFFICE);
        assertEquals(3, found.length);
        assertEquals("desk", found[0].getName());
        assertEquals("shelves", found[1].getName());
        assertEquals("stapler", found[2].getName());

        assertEquals(4L, catalog.deleteByProductNumLike("TEST-PROD-%"));
    }

    @Assertion(id = "133", strategy = "Use a repository method with the Empty keyword.")
    public void testEmpty() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        catalog.save(Product.of("refrigerator", 889.30, "TEST-PROD-41", Department.APPLIANCES));
        catalog.save(Product.of("book", 15.98, "TEST-PROD-42"));
        catalog.save(Product.of("baseball cap", 10.99, "TEST-PROD-43", Department.SPORTING_GOODS, Department.CLOTHING));

        LinkedList<Product> found = catalog.findByDepartmentsEmpty();

        assertEquals(1, found.size());
        assertEquals("book", found.getFirst().getName());

        assertEquals(3L, catalog.deleteByProductNumLike("TEST-PROD-%"));
    }

    @Assertion(id = "133", strategy = "Use a repository method findByIdBetween where the entity's Id attribute is named something other than id.")
    public void testIdAttributeWithDifferentName() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        catalog.save(Product.of("apple", 1.19, "TEST-PROD-12", Department.GROCERY));
        catalog.save(Product.of("pear", 0.99, "TEST-PROD-14", Department.GROCERY));
        catalog.save(Product.of("orange", 1.09, "TEST-PROD-16", Department.GROCERY));
        catalog.save(Product.of("banana", 0.49, "TEST-PROD-17", Department.GROCERY));
        catalog.save(Product.of("plum", 0.89, "TEST-PROD-18", Department.GROCERY));

        Iterable<Product> found = catalog.findByIdBetween("TEST-PROD-13", "TEST-PROD-17", Sort.asc("name"));
        Iterator<Product> it = found.iterator();
        assertEquals(true, it.hasNext());
        assertEquals("banana", it.next().getName());
        assertEquals(true, it.hasNext());
        assertEquals("orange", it.next().getName());
        assertEquals(true, it.hasNext());
        assertEquals("pear", it.next().getName());
        assertEquals(false, it.hasNext());

        assertEquals(5L, catalog.deleteByProductNumLike("TEST-PROD-%"));
    }

    @Assertion(id = "133", strategy = "Use a repository method with the Like keyword.")
    public void testLike() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        catalog.save(Product.of("celery", 1.57, "TEST-PROD-31", Department.GROCERY));
        catalog.save(Product.of("mushrooms", 1.89, "TEST-PROD-32", Department.GROCERY));
        catalog.save(Product.of("carrots", 1.39, "TEST-PROD-33", Department.GROCERY));

        List<Product> found = catalog.findByNameLike("%r_o%");
        assertEquals(List.of("carrots", "mushrooms"),
                     found.stream().map(Product::getName).sorted().collect(Collectors.toList()));

        assertEquals(3L, catalog.deleteByProductNumLike("TEST-PROD-%"));
    }

    @Assertion(id = "119", strategy = "Ensure that this test is only run when provider supports persistence entities")
    public void testNotRunOnNOSQL() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        List<Product> products = new ArrayList<>();
        products.add(Product.of("pen", 2.50, "TEST-PROD-01"));
        products.add(Product.of("pencil", 1.25, "TEST-PROD-02"));
        products.add(Product.of("marker", 3.00, "TEST-PROD-03"));
        products.add(Product.of("calculator", 15.00, "TEST-PROD-04"));
        products.add(Product.of("ruler", 2.00, "TEST-PROD-05"));
        
        products.stream().forEach(product -> catalog.save(product));
        
        int countExpensive = catalog.countByPriceGreaterThanEqual(2.99);
        assertEquals(2, countExpensive, "Expected two products to be more than 3.00");
        
        Assertions.assertThrows(MappingException.class, () -> {
            catalog.countBySurgePriceGreaterThanEqual(2.99);
        });

        assertEquals(5L, catalog.deleteByProductNumLike("TEST-PROD-%"));
    }

    @Assertion(id = "133", strategy = "Use a repository method with the Null keyword.")
    public void testNull() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        catalog.save(Product.of("spinach", 2.28, "TEST-PROD-51", Department.GROCERY));
        catalog.save(Product.of("broccoli", 2.49, "TEST-PROD-52", Department.GROCERY));
        catalog.save(Product.of("rhubarb", null, "TEST-PROD-53", Department.GROCERY));
        catalog.save(Product.of("potato", 0.79, "TEST-PROD-54", Department.GROCERY));

        Collection<Product> found = catalog.findByPriceNull();

        assertEquals(1, found.size());
        assertEquals("rhubarb", found.iterator().next().getName());

        assertEquals(List.of("spinach", "potato"),
                     catalog.findByPriceNotNullAndPriceLessThanEqual(2.30)
                                     .map(Product::getName)
                                     .collect(Collectors.toList()));

        assertEquals(4L, catalog.deleteByProductNumLike("TEST-PROD-%"));
    }

    @Assertion(id = "133", strategy = "Use a repository method that is annotated with Query and includes JPQL with named parameters.")
    public void testQueryWithNamedParameters() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        catalog.save(Product.of("tape measure", 7.29, "TEST-PROD-61", Department.TOOLS));
        catalog.save(Product.of("pry bar", 4.39, "TEST-PROD-62", Department.TOOLS));
        catalog.save(Product.of("hammer", 8.59, "TEST-PROD-63", Department.TOOLS));
        catalog.save(Product.of("adjustable wrench", 4.99, "TEST-PROD-64", Department.TOOLS));
        catalog.save(Product.of("framing square", 9.88, "TEST-PROD-65", Department.TOOLS));
        catalog.save(Product.of("rasp", 6.79, "TEST-PROD-66", Department.TOOLS));

        Stream<Product> found = catalog.withTaxBetween(0.4, 0.6, 0.08125);

        assertEquals(List.of("adjustable wrench", "rasp", "tape measure"),
                     found.map(Product::getName).collect(Collectors.toList()));

        assertEquals(6L, catalog.deleteByProductNumLike("TEST-PROD-%"));
    }

    @Assertion(id = "133", strategy = "Use a repository method that is annotated with Query and includes JPQL with positional parameters.")
    public void testQueryWithPositionalParameters() {
        catalog.deleteByProductNumLike("TEST-PROD-%");

        catalog.save(Product.of("sweater", 23.88, "TEST-PROD-71", Department.CLOTHING));
        catalog.save(Product.of("toothpaste", 2.39, "TEST-PROD-72", Department.PHARMACY, Department.GROCERY));
        catalog.save(Product.of("chisel", 5.99, "TEST-PROD-73", Department.TOOLS));
        catalog.save(Product.of("computer", 1299.50, "TEST-PROD-74", Department.ELECTRONICS, Department.OFFICE));
        catalog.save(Product.of("sunblock", 5.98, "TEST-PROD-75", Department.PHARMACY, Department.SPORTING_GOODS, Department.GARDEN));
        catalog.save(Product.of("basketball", 14.88, "TEST-PROD-76", Department.SPORTING_GOODS));
        catalog.save(Product.of("baseball cap", 12.99, "TEST-PROD-77", Department.SPORTING_GOODS, Department.CLOTHING));

        Streamable<Product> found = catalog.findByDepartmentCountAndPriceBelow(2, 100.0);

        assertEquals(List.of("baseball cap", "toothpaste"),
                     found.stream().map(Product::getName).collect(Collectors.toList()));

        found = catalog.findByDepartmentCountAndPriceBelow(3, 10000.0);

        assertEquals(List.of("sunblock"),
                     found.stream().map(Product::getName).collect(Collectors.toList()));

        assertEquals(7L, catalog.deleteByProductNumLike("TEST-PROD-%"));
    }
}
