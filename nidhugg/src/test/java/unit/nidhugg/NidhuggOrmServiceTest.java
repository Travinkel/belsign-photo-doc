package unit.nidhugg;

import com.belman.belsign.framework.nidhugg.application.service.ORMService;
import com.belman.belsign.framework.nidhugg.domain.BaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class NidhuggOrmServiceTest {
    private ORMService<TestEntity> ormService;

    static class TestEntity extends BaseEntity {
        private String name;

        public TestEntity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @BeforeEach
    void setUp() {
        ormService = new ORMService<>();
    }

    @Test
    void testSaveEntity() {
        TestEntity entity = new TestEntity("TestName");
        ormService.save(entity);
        assertNotNull(entity.getId(), "Entity should have an ID after saving.");
    }

    @Test
    void testFindEntityById() {
        TestEntity entity = new TestEntity("FindMe");
        ormService.save(entity);

        Optional<TestEntity> found = ormService.findById(entity.getId());
        assertTrue(found.isPresent(), "Entity should be found by ID.");
        assertEquals(entity.getId(), found.get().getId(), "Found entity ID should match.");
    }

    @Test
    void testQueryAllEntities() {
        ormService.save(new TestEntity("A"));
        ormService.save(new TestEntity("B"));

        List<TestEntity> allEntities = ormService.findAll();
        assertEquals(2, allEntities.size(), "Should retrieve all saved entities.");
    }

    @Test
    void testQueryWithFilter() {
        ormService.save(new TestEntity("Alpha"));
        ormService.save(new TestEntity("Beta"));

        List<TestEntity> filtered = ormService.findAll(entity -> entity.getName().startsWith("A"));
        assertEquals(1, filtered.size(), "Should filter entities correctly.");
        assertEquals("Alpha", filtered.get(0).getName(), "Filtered entity should match condition.");
    }

    @Test
    void testUpdateEntity() {
        TestEntity entity = new TestEntity("Original");
        ormService.save(entity);

        entity.setName("Updated");
        ormService.update(entity);

        Optional<TestEntity> found = ormService.findById(entity.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated", found.get().getName());
    }

    @Test
    void testUpdateNonexistentEntityThrowsException() {
        TestEntity entity = new TestEntity("Ghost");
        entity.setId(UUID.randomUUID());

        assertThrows(IllegalArgumentException.class, () -> ormService.update(entity));
    }

    @Test
    void testDeleteEntity() {
        TestEntity entity = new TestEntity("ToDelete");
        ormService.save(entity);

        ormService.delete(entity);

        Optional<TestEntity> found = ormService.findById(entity.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteNonexistentEntityThrowsException() {
        TestEntity entity = new TestEntity("Nonexistent");
        entity.setId(UUID.randomUUID());

        assertThrows(IllegalArgumentException.class, () -> ormService.delete(entity));
    }
}
