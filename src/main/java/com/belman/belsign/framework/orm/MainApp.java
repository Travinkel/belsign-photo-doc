package com.belman.belsign.framework.orm;

import com.belman.belsign.application.service.ORMService;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;


public class MainApp extends Application {

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

    @Override
    public void start(Stage primaryStage) {
        ORMService<Person> ormService = new ORMService<>();
        ormService.save(new Person("Alice", 30));
        ormService.save(new Person("Bob", 25));

        ObservableList<Person> people = ormService.query()
                .where(p -> p.age > 20)
                .toObservableList();

        ListView<Person> listView = new ListView<>(people);

        primaryStage.setScene(new Scene(listView, 400, 300));
        primaryStage.setTitle("JavaFX ORM Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
