package unit.nidhugg.domain;

import com.belman.belsign.framework.nidhugg.domain.BaseEntity;

public class TestEntity extends BaseEntity {
    private String name;
    private int age;

    public TestEntity(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}