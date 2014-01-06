/**
 * Copyright 2014 Mysema Ltd
 * Copyright 2009-2013 Oy Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.addon.jpacontainer;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.addon.jpacontainer.testdata.Person;
import com.vaadin.addon.jpacontainer.testdata.QPerson;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare.Equal;

/**
 * Test case for {@link JPAContainer}.
 * 
 * @author Petter Holmström (Vaadin Ltd)
 * @since 1.0
 */
@SuppressWarnings("serial")
public class QuerydslJPAContainerExtTest {

    private QuerydslJPAContainer<Person> container;
    private EntityProvider<Person> entityProviderMock;
    private CachingEntityProvider<Person> cachingEntityProviderMock;
    private MutableEntityProvider<Person> mutableEntityProviderMock;
    private BatchableEntityProvider<Person> batchableEntityProviderMock;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        entityProviderMock = createMock(EntityProvider.class);
        expect(entityProviderMock.getLazyLoadingDelegate()).andStubReturn(null);

        cachingEntityProviderMock = createMock(CachingEntityProvider.class);
        expect(cachingEntityProviderMock.getLazyLoadingDelegate())
                .andStubReturn(null);

        mutableEntityProviderMock = createMock(MutableEntityProvider.class);
        expect(mutableEntityProviderMock.getLazyLoadingDelegate())
                .andStubReturn(null);

        batchableEntityProviderMock = createMock(BatchableEntityProvider.class);
        expect(batchableEntityProviderMock.getLazyLoadingDelegate())
                .andStubReturn(null);

        container = new QuerydslJPAContainer<Person>(Person.class);
    }

    @Test
    public void testApplyFilters_Delayed() {
        QPerson person = QPerson.person;
        final boolean[] listenerCalled = new boolean[1];
        container.addListener(new ItemSetChangeListener() {

            public void containerItemSetChange(ItemSetChangeEvent event) {
                assertTrue(event instanceof JPAContainer.FiltersAppliedEvent);
                listenerCalled[0] = true;
            }
        });
        // Applied filters should not result in any direct calls to the entity
        // provider
        replay(entityProviderMock);
        container.setEntityProvider(entityProviderMock);

        container.setApplyFiltersImmediately(false);
        assertFalse(container.isApplyFiltersImmediately());
        assertFalse(listenerCalled[0]);
        //container.addContainerFilter(new Equal("firstName", "Hello"));
        container.addContainerFilter(person.firstName.eq("Hello"));

        assertFalse(listenerCalled[0]);
        assertTrue(container.getFilters().contains(
                new Equal("firstName", "Hello")));
        assertTrue(container.getAppliedFilters().isEmpty());
        assertTrue(container.hasUnappliedFilters());

        container.applyFilters();
        assertTrue(listenerCalled[0]);
        assertEquals(container.getFilters(), container.getAppliedFilters());
        assertTrue(container.getFilters().contains(
                new Equal("firstName", "Hello")));
        assertFalse(container.hasUnappliedFilters());

        // Try to remove the filters
        listenerCalled[0] = false;

        container.removeAllContainerFilters();
        assertTrue(container.getFilters().isEmpty());
        assertFalse(container.getAppliedFilters().isEmpty());
        assertTrue(container.hasUnappliedFilters());

        container.applyFilters();
        assertTrue(listenerCalled[0]);
        assertTrue(container.getAppliedFilters().isEmpty());
        assertFalse(container.hasUnappliedFilters());

        verify(entityProviderMock);
    }

    @Test
    public void testApplyFilters_Immediately() {
        QPerson person = QPerson.person;
        final boolean[] listenerCalled = new boolean[1];
        container.addListener(new ItemSetChangeListener() {

            public void containerItemSetChange(ItemSetChangeEvent event) {
                assertTrue(event instanceof JPAContainer.FiltersAppliedEvent);
                listenerCalled[0] = true;
            }
        });
        // Applied filters should not result in any direct calls to the entity
        // provider
        replay(entityProviderMock);
        container.setEntityProvider(entityProviderMock);

        assertTrue(container.isApplyFiltersImmediately());
        assertFalse(listenerCalled[0]);
//        container.addContainerFilter(new Equal("firstName", "Hello"));
        container.addContainerFilter(person.firstName.eq("Hello"));

        assertEquals(container.getFilters(), container.getAppliedFilters());
        assertTrue(container.getFilters().contains(
                new Equal("firstName", "Hello")));
        assertTrue(listenerCalled[0]);
        assertFalse(container.hasUnappliedFilters());

        // Tro to remove all the filters
        listenerCalled[0] = false;

        container.removeAllContainerFilters();
        assertTrue(container.getFilters().isEmpty());
        assertTrue(container.getAppliedFilters().isEmpty());
        assertTrue(listenerCalled[0]);
        assertFalse(container.hasUnappliedFilters());

        verify(entityProviderMock);
    }

    @Test
    public void testSize_WriteThrough() {
        QPerson person = QPerson.person;
        expect(
                entityProviderMock.getEntityCount(container, new And(new Equal(
                        "firstName", "Hello"), new Equal("lastName", "World"))))
                .andReturn(123);
        replay(entityProviderMock);

        assertTrue(container.isApplyFiltersImmediately());
//        container.addContainerFilter(new Equal("firstName", "Hello"));
//        container.addContainerFilter(new Equal("lastName", "World"));
        container.addContainerFilter(person.firstName.eq("Hello"));
        container.addContainerFilter(person.lastName.eq("World"));
        container.setEntityProvider(entityProviderMock);
        container.setWriteThrough(true);

        assertEquals(123, container.size());

        verify(entityProviderMock);
    }

    @Test
    public void testGetIdByIndex_WriteThrough() {
        QPerson person = QPerson.person;
        LinkedList<SortBy> orderby = new LinkedList<SortBy>();
        orderby.add(new SortBy("firstName", true));
        expect(
                entityProviderMock.getEntityIdentifierAt(container, null,
                        new LinkedList<SortBy>(), 1)).andReturn("id1");
        expect(
                entityProviderMock.getEntityIdentifierAt(container, null,
                        new LinkedList<SortBy>(), 2)).andReturn(null);
        expect(
                entityProviderMock.getEntityIdentifierAt(container, new Equal("firstName",
                        "Hello"), orderby, 3)).andReturn("id3");
        replay(entityProviderMock);

        container.setEntityProvider(entityProviderMock);
        container.setWriteThrough(true);

        assertEquals("id1", container.getIdByIndex(1));
        assertNull(container.getIdByIndex(2));

        // Now let's try with a filter and some sorting
//        container.addContainerFilter(new Equal("firstName", "Hello"));
//        container.sort(new Object[] { "firstName" }, new boolean[] { true });
        container.addContainerFilter(person.firstName.eq("Hello"));
        container.sort(person.firstName.asc());

        assertEquals("id3", container.getIdByIndex(3));

        verify(entityProviderMock);
    }

    @Test
    public void testGetIdByIndex_Buffered() {
        QPerson person = QPerson.person;
        Equal filter = new Equal("firstName", "Hello");
        LinkedList<SortBy> orderby = new LinkedList<SortBy>();
        orderby.add(new SortBy("firstName", true));
        
        expect(
                batchableEntityProviderMock.getEntityIdentifierAt(container, null,
                        new LinkedList<SortBy>(), 0)).andStubReturn("id1");
        expect(
                batchableEntityProviderMock.getEntityIdentifierAt(container, null,
                        new LinkedList<SortBy>(), 1)).andStubReturn(null);
        expect(
                batchableEntityProviderMock.getEntityIdentifierAt(container, new Equal(
                        "firstName", "Hello"), orderby, 2))
                .andStubReturn("id3");
        expect(batchableEntityProviderMock.containsEntity(container, "id3", null))
                .andStubReturn(true);
        expect(
                batchableEntityProviderMock.getAllEntityIdentifiers(container, filter,
                        orderby)).andStubReturn(
                Arrays.asList(new Object[] { "id1", "id2", "id3" }));
        expect(batchableEntityProviderMock.containsEntity(container, "id3", filter))
                .andStubReturn(true);
        replay(batchableEntityProviderMock);

        container.setEntityProvider(batchableEntityProviderMock);
        container.setWriteThrough(false);

        assertEquals("id1", container.getIdByIndex(0));
        assertNull(container.getIdByIndex(1));

        // Now let's try with a filter and some sorting
//        container.addContainerFilter(filter);        
//        container.sort(new Object[] { "firstName" }, new boolean[] { true });
        container.addContainerFilter(person.firstName.eq("Hello"));
        container.sort(person.firstName.asc());

        assertEquals("id3", container.getIdByIndex(2));

        // Clear filters and sorting
        container.removeAllContainerFilters();
        container.sort(new Object[] {}, new boolean[] {});

        // Add an item
        Object id = container.addEntity(new Person());
        assertEquals(id, container.getIdByIndex(0));
        assertEquals("id1", container.getIdByIndex(1));
        assertNull(container.getIdByIndex(2));

        // Apply filter and sorting again
//        container.addContainerFilter(new Equal("firstName", "Hello"));
//        container.sort(new Object[] { "firstName" }, new boolean[] { true });
        container.addContainerFilter(person.firstName.eq("Hello"));
        container.sort(person.firstName.asc());

        assertEquals(id, container.getIdByIndex(0));
        assertEquals("id3", container.getIdByIndex(3));

        // Remove last item
        container.removeItem("id3");
        // Should not exist in the container
        assertFalse(container.containsId("id3"));

        verify(batchableEntityProviderMock);
    }

    @Test
    public void testGetItemIds_WriteThrough() {
        QPerson person = QPerson.person;
        LinkedList<SortBy> orderby = new LinkedList<SortBy>();
        orderby.add(new SortBy("firstName", true));

        LinkedList<Object> idList = new LinkedList<Object>();
        idList.add("id1");
        idList.add("id2");
        idList.add("id3");
        idList.add("id4");

        expect(entityProviderMock.getAllEntityIdentifiers(container, null, orderby))
                .andStubReturn(idList);
        replay(entityProviderMock);

        container.setEntityProvider(entityProviderMock);
        container.setWriteThrough(true);
//        container.sort(new Object[] { "firstName" }, new boolean[] { true });
        container.sort(person.firstName.asc());

        Collection<Object> ids = container.getItemIds();
        assertEquals(4, ids.size());
        assertTrue(ids.contains("id1"));
        assertTrue(ids.contains("id2"));
        assertTrue(ids.contains("id3"));
        assertTrue(ids.contains("id4"));

        verify(entityProviderMock);
    }
}
