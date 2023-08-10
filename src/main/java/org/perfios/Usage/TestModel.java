package org.perfios.Usage;

public class TestModel {
    String name;
    String age;
    String netIncome;
    Object primaryAddress;
    Object secondaryAddress;

    public TestModel(String name, String age, String netIncome, Object primaryAddress, Object secondaryAddress) {
        this.name = name;
        this.age = age;
        this.netIncome = netIncome;
        this.primaryAddress = primaryAddress;
        this.secondaryAddress = secondaryAddress;
    }

    public TestModel() {
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getNetIncome() {
        return netIncome;
    }

    public Object getPrimaryAddress() {
        return primaryAddress;
    }

    public Object getSecondaryAddress() {
        return secondaryAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setNetIncome(String netIncome) {
        this.netIncome = netIncome;
    }

    public void setPrimaryAddress(Object primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public void setSecondaryAddress(Object secondaryAddress) {
        this.secondaryAddress = secondaryAddress;
    }
}
