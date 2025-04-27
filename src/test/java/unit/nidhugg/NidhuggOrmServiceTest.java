package unit.nidhugg;

import com.belman.belsign.framework.nidhugg.application.service.ORMService;
import com.belman.belsign.framework.nidhugg.domain.BaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class NidhuggOrmServiceTest {
    private ORMService ormService;
    private final Map<UUID, BaseEntity> database = new HashMap<>();


    static class TestEntity extends BaseEntity {
        private String name;

        public TestEntity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @BeforeEach
    void setUp() {
        ormService = new ORMService();
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

        Optional<BaseEntity> found = ormService.findById(entity.getId());
        assertTrue(found.isPresent(), "Entity should be found by ID.");
        assertEquals(entity.getId(), found.get().getId(), "Found entity ID should match.");
    }

    @Test
    void testQueryAllEntities() {
        ormService.save(new TestEntity("A"));
        ormService.save(new TestEntity("B"));

        List<BaseEntity> allEntities = ormService.findAll();
        assertEquals(2, allEntities.size(), "Should retrieve all saved entities.");
    }

    @Test
    void testQueryWithFilter() {
        ormService.save(new TestEntity("Alpha"));
        ormService.save(new TestEntity("Beta"));

        List<BaseEntity> filtered = ormService.findAll(entity -> ((TestEntity) entity).getName().startsWith("A"));
        assertEquals(1, filtered.size(), "Should filter entities correctly.");
        assertEquals("Alpha", ((TestEntity) filtered.get(0)).getName(), "Filtered entity should match condition.");
    }


    public void save(BaseEntity entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        database.put(entity.getId(), entity);
    }

    public Optional<BaseEntity> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    public List<BaseEntity> findAll() {
        return new ArrayList<>(database.values());
    }

    public List<BaseEntity> findAll(Predicate<BaseEntity> filter) {
        return database.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public void update(BaseEntity entity) {
        if (entity.getId() == null || !database.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity must exist to be updated.");
        }
        database.put(entity.getId(), entity);
    }

    public void delete(BaseEntity entity) {
        if (entity.getId() == null || !database.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity must exist to be deleted.");
        }
        database.remove(entity.getId());
    }
    @Test
    void testUpdateEntity() {
        TestEntity entity = new TestEntity("Original");
        ormService.save(entity);

        entity.name = "Updated"; // Direkte adgang for test
        ormService.update(entity);

        Optional<BaseEntity> found = ormService.findById(entity.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated", ((TestEntity) found.get()).getName());
    }

    @Test
    void testUpdateNonexistentEntityThrowsException() {
        TestEntity entity = new TestEntity("Ghost");
        entity.setId(UUID.randomUUID()); // Fiktiv ID

        assertThrows(IllegalArgumentException.class, () -> ormService.update(entity));
    }

    @Test
    void testDeleteEntity() {
        TestEntity entity = new TestEntity("ToDelete");
        ormService.save(entity);

        ormService.delete(entity);

        Optional<BaseEntity> found = ormService.findById(entity.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteNonexistentEntityThrowsException() {
        TestEntity entity = new TestEntity("Nonexistent");
        entity.setId(UUID.randomUUID());

        assertThrows(IllegalArgumentException.class, () -> ormService.delete(entity));
    }


}
