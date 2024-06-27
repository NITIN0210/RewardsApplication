package com.nitin.tfosorcim.transactiontask.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


@Table(name="Transaction")
public class Transaction {



    @Id
    @Column("transactionId")
    @JsonProperty("transactionId")
    private Long transactionId;

    @Column("date")
    @JsonProperty("date")
    private LocalDate date;

    @Column("amount")
    @JsonProperty("amount")
    private double amount;

    @Column("customerId")
    @JsonProperty("customerId")
    private Long customerId;


    public Transaction() {}

    public Transaction(LocalDate date, double amount, Long customerId) {
        this.date = date;
        this.amount = amount;
        this.customerId = customerId;

    }
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


    @Override
    public String toString() {
        return "Transaction{ id=" + transactionId + ", date=" + date + ", amount=" + amount + "}";
    }

}
