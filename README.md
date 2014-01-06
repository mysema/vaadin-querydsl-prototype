## Vaadin Querydsl prototype

Prototype of Vaadin/Querydsl JPA integration

vaadin-querydsl-prototype provides extensions to the Vaadin `JPAContainer` methods to support Querydsl types.

`QuerydslJPAContainerFactory` and `QuerydslJPAContainer` can be used as replacements for `JPAContainerFactory` and `JPAContainer`.

### Examples

filtering

    QPerson person = QPerson.person; // Querydsl generated type
    QuerydslJPAContainer<Person> container = QuerydslJPAContainerFactory.make(Person.class, entityManager);
    
    // container.addContainerFilter(new Equal("firstName", "Hello"));
    // container.addContainerFilter(new Equal("lastName", "World"));
    container.addContainerFilter(person.firstName.eq("Hello"));
    container.addContainerFilter(person.lastName.eq("World"));
    
filtering and sorting    

    // container.addContainerFilter(new Equal("firstName", "Hello"));
    // container.sort(new Object[] { "firstName" }, new boolean[] { true });
    container.addContainerFilter(person.firstName.eq("Hello"));
    container.sort(person.firstName.asc());
    
complex filter

    // container.addContainerFilter(new Or(new Equal("firstName", "Hello"), 
                                           new Equal("lastName", "World")));
    container.addContainerFilter(person.firstName.eq("Hello")
                                 .or(person.lastName.eq("World")));
                                        

The commented out code shows the original way to add filters and sorting.

The supported Querydsl operations are `and`, `or`, `not`, `like`, `eq`, `ne`, `isNull`, `startsWith`,
`startsWithIc`, `contains`, `between`, `lt`, `gt`, `loe` and `goe`.

This code is just a prototype of a possible integration of Vaadin and Querydsl for JPA querying. It should not be used in production code.
