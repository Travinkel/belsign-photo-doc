package tests;

import orm.BaseEntity;
import orm.Query;
import services.ORMService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ORMServiceTest {

    public static class Person extends BaseEntity {
        public String name;
        public int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + " (" + age + ")";
        }
    }

    @Test
    public void testSaveAndQuery() {
        ORMService<Person> ormService = new ORMService<>();
        Person alice = new Person("Alice", 30);
        Person bob = new Person("Bob", 25);

        ormService.save(alice);
        ormService.save(bob);

        Query<Person> query = ormService.query();
        List<Person> allPeople = query.select(p -> p);

        assertEquals(2, allPeople.size());
        assertTrue(allPeople.contains(alice));
        assertTrue(allPeople.contains(bob));
    }

    @Test
    public void testWhereClause() {
        ORMService<Person> ormService = new ORMService<>();
        ormService.save(new Person("Alice", 30));
        ormService.save(new Person("Bob", 25));
        ormService.save(new Person("Charlie", 20));

        Query<Person> query = ormService.query().where(p -> p.age > 20);
        List<Person> filteredPeople = query.select(p -> p);

        assertEquals(2, filteredPeople.size());
        assertTrue(filteredPeople.stream().anyMatch(p -> p.name.equals("Alice")));
        assertTrue(filteredPeople.stream().anyMatch(p -> p.name.equals("Bob")));
    }

    @Test
    public void testGetTableName() {
        Person person = new Person("Alice", 30);
        assertEquals("person", person.getTableName());
    }

    @Test
    public void testGetColumnNames() {
        Person person = new Person("Alice", 30);
        String[] columnNames = person.getColumnNames();

        assertArrayEquals(new String[]{"name", "age"}, columnNames);
    }
}
