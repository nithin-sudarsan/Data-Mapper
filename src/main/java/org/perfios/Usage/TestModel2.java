package org.perfios.Usage;

public class TestModel2 {
    String empName;
    String empAge;
    Object empAddress;

    public TestModel2() {
    }

    public String getEmpName() {
        return empName;
    }

    public String getEmpAge() {
        return empAge;
    }

    public Object getEmpAddress() {
        return empAddress;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setEmpAge(String empAge) {
        this.empAge = empAge;
    }

    public void setEmpAddress(Object empAddress) {
        this.empAddress = empAddress;
    }

    public TestModel2(String empName, String empAge, Object empAddress) {
        this.empName = empName;
        this.empAge = empAge;
        this.empAddress = empAddress;
    }
}
