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

import java.util.Map;

import com.google.common.collect.Maps;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Path;
import com.mysema.query.types.Predicate;

/**
 * XXX Prototype of Vaadin/Querydsl integration, don't use in production code
 * 
 * @author tiwe
 *
 * @param <T>
 */
public class QuerydslJPAContainer<T> extends JPAContainer<T> {
    
    private static final long serialVersionUID = -4538743357943437161L;
    
    private final Map<Predicate, Filter> filters = Maps.newHashMap();
    
    public QuerydslJPAContainer(Class<T> type) {
        super(type);
    }
    
    public void addContainerFilter(Predicate predicate) {
        Filter filter = (Filter) predicate.accept(VaadinExpressionVisitor.DEFAULT, null);
        filters.put(predicate, filter);
        super.addContainerFilter(filter);
    }

    public void addNestedContainerProperty(Path<?> nestedProperty) {
        super.addNestedContainerProperty(nestedProperty.getMetadata().getName());
    }

    public void removeContainerFilter(Predicate predicate) {
        Filter filter = filters.remove(predicate);
        if (filter != null) {
            super.removeContainerFilter(filter);
        }
    }
    
    public void setAdditionalFilterablePropertyIds(Path<?>... paths) {
        String[] propertyIds = new String[paths.length];
        for (int i = 0; i < propertyIds.length; i++) {
            propertyIds[i] = paths[i].getMetadata().getName();
        }
        super.setAdditionalFilterablePropertyIds(propertyIds);
    }

    public void setSortProperty(Path<?> propertyId, Path<?>  sortProperty) {
        super.setSortProperty(propertyId.getMetadata().getName(), sortProperty.getMetadata().getName());
    }
    
    public void sort(OrderSpecifier<?>... order) {
        Object[] propertyId = new Object[order.length];
        boolean[] ascending = new boolean[order.length];
        for (int i = 0; i < order.length; i++) {
            propertyId[i] = ((Path)order[i].getTarget()).getMetadata().getName();
            ascending[i] = order[i].getOrder() == Order.ASC;
        }
        super.sort(propertyId, ascending);
    }
    
}
