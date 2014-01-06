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
