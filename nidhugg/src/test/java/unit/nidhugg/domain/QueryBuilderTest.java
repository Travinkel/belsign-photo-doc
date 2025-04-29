package unit.nidhugg.domain;
/*
import com.belman.belsign.framework.nidhugg.application.service.ORMService;
import com.belman.belsign.framework.nidhugg.domain.BaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryBuilderTest {

    private ORMService ormService;

    @BeforeEach
    void setUp() {
        ormService = new ORMService();

        TestEntity entity1 = new TestEntity("Stefan", 33);
        TestEntity entity2 = new TestEntity("Mikkel", 24);
        TestEntity entity3 = new TestEntity("Stefan", 18);

        ormService.save(entity1);
        ormService.save(entity2);
        ormService.save(entity3);
    }

    @Test
    void testSimpleWhereQuery() {
        List<BaseEntity> results = ormService.query()
                .where(e -> ((TestEntity) e).getName().equals("Stefan"))
                .find();

        assertEquals(2, results.size());
    }

    @Test
    void testOrWhereQuery() {
        List<BaseEntity> results = ormService.query()
                .where(e -> ((TestEntity) e).getName().equals("Mikkel"))
                .orWhere(e -> ((TestEntity) e).getName().equals("Stefan"))
                .find();

        assertEquals(3, results.size());
    }

    @Test
    void testEmptyQueryReturnsAll() {
        List<BaseEntity> results = ormService.query()
                .find();

        assertEquals(3, results.size());
    }
}
*/