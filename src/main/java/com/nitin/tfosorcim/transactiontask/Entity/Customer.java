package com.nitin.tfosorcim.transactiontask.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nitin.tfosorcim.transactiontask.Entity.Transaction;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.*;
@Table(name="Customer")
public class Customer {

    @Id
    @Column("customerId")
    @JsonProperty("customerId")
    private Long customerId;

    @Column("name")
    @JsonProperty("name")
    private String name;

    public Customer() {

    }
    public Customer(String name) {
        this.name = name;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    @Override
    public String toString() {
        return "Customer{ id=" + customerId + ", name='" + name + "'}";
    }

}
