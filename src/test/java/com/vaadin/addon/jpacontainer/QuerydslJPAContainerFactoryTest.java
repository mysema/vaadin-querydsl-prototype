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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.addon.jpacontainer.provider.CachingMutableLocalEntityProvider;
import com.vaadin.addon.jpacontainer.testdata.Person;
import com.vaadin.addon.jpacontainer.testdata.Skill;

public class QuerydslJPAContainerFactoryTest {
    
    private static EntityManagerFactory emf = Persistence
            .createEntityManagerFactory("eclipselink-in-memory");

    private EntityManager entityManager;

    @Before
    public void setUp() {
        entityManager = emf.createEntityManager();
    }

    @Test
    public void testCreateJPAContainer() {
        QuerydslJPAContainer<Person> c = QuerydslJPAContainerFactory.make(Person.class, entityManager);
        assertNotNull(c);
    }

    @Test
    public void testCreateJPAContainerHasCorrectEntityProvider() {
        JPAContainer<Person> c = QuerydslJPAContainerFactory.make(Person.class, entityManager);
        assertEquals(CachingMutableLocalEntityProvider.class, c.getEntityProvider().getClass());
        EntityManager entityManagerOfProvider = ((CachingMutableLocalEntityProvider<?>) c.getEntityProvider()).getEntityManager();
        assertNotNull(entityManagerOfProvider);
        assertEquals(entityManager, entityManagerOfProvider);
    }

    @Test
    public void testCreateJPAContainerUsingPersistenceUnitNameHasCorrectEntityProvider() {
        JPAContainer<Person> c = QuerydslJPAContainerFactory.make(Person.class,"eclipselink-in-memory");
        assertNotNull(c);
        assertEquals(CachingMutableLocalEntityProvider.class, c.getEntityProvider().getClass());
        EntityManager entityManagerOfProvider = ((CachingMutableLocalEntityProvider<?>) c.getEntityProvider()).getEntityManager();
        assertNotNull(entityManagerOfProvider);
    }

    @Test
    public void testCreateJPAContainerUsingPersistenceUnitNameReusesEntityManagerFactory() {
        JPAContainer<Person> c = QuerydslJPAContainerFactory.make(Person.class,"eclipselink-in-memory");
        EntityManager em = ((CachingMutableLocalEntityProvider<?>) c.getEntityProvider()).getEntityManager();

        JPAContainer<Skill> c2 = JPAContainerFactory.make(Skill.class,"eclipselink-in-memory");
        EntityManager em2 = ((CachingMutableLocalEntityProvider<?>) c2.getEntityProvider()).getEntityManager();

        assertEquals(em.getEntityManagerFactory(), em2.getEntityManagerFactory());
    }

}
