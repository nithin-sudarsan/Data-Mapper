package org.perfios.Usage;

public class TestModel {
    String name;
    String age;
    Object details;

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public Object getDetails() {
        return details;
    }

    public TestModel() {
    }

    public TestModel(String name, String age, Object details) {
        this.name = name;
        this.age = age;
        this.details = details;
    }
}
