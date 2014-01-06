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

import javax.annotation.Nullable;

import com.mysema.query.types.Constant;
import com.mysema.query.types.FactoryExpression;
import com.mysema.query.types.Operation;
import com.mysema.query.types.Operator;
import com.mysema.query.types.Ops;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.PathType;
import com.mysema.query.types.SubQueryExpression;
import com.mysema.query.types.TemplateExpression;
import com.mysema.query.types.Visitor;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Compare.Greater;
import com.vaadin.data.util.filter.Compare.GreaterOrEqual;
import com.vaadin.data.util.filter.Compare.Less;
import com.vaadin.data.util.filter.Compare.LessOrEqual;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;

/**
 * XXX Prototype of Vaadin/Querydsl integration, don't use in production code
 * 
 * @author tiwe
 *
 */
public final class VaadinExpressionVisitor implements Visitor<Object, Void> {
    
    public static final VaadinExpressionVisitor DEFAULT = new VaadinExpressionVisitor();
    
    private Object handle(Operation<?> expr, int i) {
        return expr.getArg(i).accept(this, null);
    }

    public Object visit(Constant<?> expr, @Nullable Void context) {
        return expr.getConstant();
    }

    public Object visit(FactoryExpression<?> expr, @Nullable Void context) {
        throw new UnsupportedOperationException();
    }

    public Object visit(Operation<?> expr, @Nullable Void context) {
        Operator<?> op = expr.getOperator();
        if (op == Ops.OR) {
            return new Or((Filter)handle(expr, 0), (Filter)handle(expr, 1));
        } else if (op == Ops.AND) {
            return new And((Filter)handle(expr, 0), (Filter)handle(expr, 1));
        } else if (op == Ops.NOT) {
            return new Not((Filter)handle(expr, 0));
        } else if (op == Ops.LIKE) {
            return new Like((String)handle(expr, 0), (String)handle(expr, 1));
        } else if (op == Ops.EQ) {
            return new Equal(handle(expr, 0), handle(expr, 1));
        } else if (op == Ops.EQ_IGNORE_CASE) {
            // not supported
        } else if (op == Ops.NE) {
            return new Not(new Equal(handle(expr, 0), handle(expr, 1)));
        } else if (op == Ops.IS_NULL) {    
            return new IsNull(handle(expr, 0));
        } else if (op == Ops.STARTS_WITH) {
            return new SimpleStringFilter(handle(expr, 0), (String)handle(expr, 1), false, true);
        } else if (op == Ops.STARTS_WITH_IC) {
            return new SimpleStringFilter(handle(expr, 0), (String)handle(expr, 1), true, true);
        } else if (op == Ops.ENDS_WITH) {
            // not supported            
        } else if (op == Ops.ENDS_WITH_IC) {
            // not supported
        } else if (op == Ops.STRING_CONTAINS) {
            return new SimpleStringFilter(handle(expr, 0), (String)handle(expr, 1), false, false);
        } else if (op == Ops.STRING_CONTAINS_IC) {
            return new SimpleStringFilter(handle(expr, 0), (String)handle(expr, 1), true, false);
        } else if (op == Ops.BETWEEN) {
            return new Between((Object)handle(expr, 0), (Comparable)handle(expr, 1), (Comparable)handle(expr, 2));
        } else if (op == Ops.IN) {
            // not supported            
        } else if (op == Ops.NOT_IN) {
            // not supported
        } else if (op == Ops.LT) {
            return new Less(handle(expr, 0), handle(expr, 1));
        } else if (op == Ops.GT) {
            return new Greater(handle(expr, 0), handle(expr, 1));
        } else if (op == Ops.LOE) {
            return new LessOrEqual(handle(expr, 0), handle(expr, 1));
        } else if (op == Ops.GOE) {
            return new GreaterOrEqual(handle(expr, 0), handle(expr, 1));
        } 
        throw new UnsupportedOperationException("Illegal operation " + expr);                
    }

    public Object visit(ParamExpression<?> expr, @Nullable Void context) {
        throw new UnsupportedOperationException();
    }

    public Object visit(Path<?> expr, @Nullable Void context) {
        PathMetadata<?> metadata = expr.getMetadata();
        if (metadata.getPathType() == PathType.PROPERTY) {
            Object parent = visit(metadata.getParent(), context);
            String name = metadata.getName();
            return parent != null ? parent + "." + name : name;
        } else {
            return null;
        }
    }

    public Object visit(SubQueryExpression<?> expr, @Nullable Void context) {
        throw new UnsupportedOperationException();
    }

    public Object visit(TemplateExpression<?> expr, @Nullable Void context) {
        throw new UnsupportedOperationException();
    }
    
    private VaadinExpressionVisitor() {}

}
