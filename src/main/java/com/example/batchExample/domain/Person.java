package com.example.batchExample.domain;

import java.util.Objects;

public class Person {
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private String firstName;
    private String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName  = lastName;
    }
    // Regla simple del dominio (modelo rico)
    public Person normalized() {
        String fn = firstName == null ? "" : firstName.trim().toUpperCase();
        String ln = lastName  == null ? "" : lastName.trim().toUpperCase();
        return new Person(fn, ln);
    }

    public Person denormalize(){
        String fn = firstName == null ? "" : firstName.trim().toLowerCase();
        String ln = lastName  == null ? "" : lastName.trim().toLowerCase();
        if(fn.equals("hicham")){fn = "UPDATED NAME";}
        return new Person(fn, ln);
    }
}
