package unit.nidhugg;

import com.belman.belsign.framework.nidhugg.application.service.ORMService;
import com.belman.belsign.framework.nidhugg.domain.BaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class NidhuggOrmServiceTest {
    private ORMService ormService;

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
}
}
