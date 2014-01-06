/**
 * Copyright 2014 Mysema Ltd
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

import javax.persistence.EntityManager;

import com.vaadin.addon.jpacontainer.provider.CachingMutableLocalEntityProvider;

/**
 * XXX Prototype of Vaadin/Querydsl integration, don't use in production code
 * 
 * @author tiwe
 *
 */
public final class QuerydslJPAContainerFactory {

    public static <T> QuerydslJPAContainer<T> make(Class<T> type, EntityManager entityManager) {
        EntityProvider<T> entityProvider = new CachingMutableLocalEntityProvider<T>(type, entityManager);
        return makeWithEntityProvider(type, entityProvider);
    }

    private static <T> QuerydslJPAContainer<T> makeWithEntityProvider(
            Class<T> type, EntityProvider<T> entityProvider) {
        QuerydslJPAContainer<T> container = new QuerydslJPAContainer<T>(type);
        container.setEntityProvider(entityProvider);
        return container;
    }

    public static <T> QuerydslJPAContainer<T> make(Class<T> type, String persistenceUnitName) {
        EntityManager entityManager = JPAContainerFactory.createEntityManagerForPersistenceUnit(persistenceUnitName);
        return make(type, entityManager);
    }
    
    private QuerydslJPAContainerFactory() {}
    
}
