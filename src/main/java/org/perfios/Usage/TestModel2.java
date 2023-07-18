package org.perfios.Usage;

public class TestModel2 {
    String newName;
    String newAge;
    Object newDetails;

    public TestModel2() {
    }

    public TestModel2(String newName, String newAge, Object newDetails) {
        this.newName = newName;
        this.newAge = newAge;
        this.newDetails = newDetails;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public void setNewAge(String newAge) {
        this.newAge = newAge;
    }

    public void setNewDetails(Object newDetails) {
        this.newDetails = newDetails;
    }

    public String getNewName() {
        return newName;
    }

    public String getNewAge() {
        return newAge;
    }

    public Object getNewDetails() {
        return newDetails;
    }
}
