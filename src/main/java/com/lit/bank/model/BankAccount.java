package com.lit.bank.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@RequiredArgsConstructor
public class BankAccount {
    @Id
    @Column(name = "IBAN", nullable = false)
    private String IBAN;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @OneToMany(mappedBy = "bankAccount")
    @ToString.Exclude
    @JsonIgnore
    private Set<Transaction> transactions = new LinkedHashSet<>();

    public BankAccount(String IBAN, BigDecimal balance) {
        this.IBAN = IBAN;
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BankAccount that = (BankAccount) o;
        return IBAN != null && Objects.equals(IBAN, that.IBAN);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}