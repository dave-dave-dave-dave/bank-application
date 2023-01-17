package com.lit.bank.transaction.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@RequiredArgsConstructor
public class Transaction {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @Column(name = "mutation", nullable = false)
    private BigDecimal mutation;

    @Column(name = "description")
    private String description;

    @Column(name = "start_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal startBalance;


    @Column(name = "end_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal endBalance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transaction that = (Transaction) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}