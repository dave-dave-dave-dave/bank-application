package com.lit.bank.transaction.dto;

import com.lit.bank.transaction.validation.ValidIBAN;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TransactionDTO {

    @NotNull
    private Long transactionReference;
    @NotEmpty
    @ValidIBAN
    private String iban;
    @NotEmpty
    private String startBalance;
    @NotEmpty
    private String mutation;
    @NotEmpty
    private String endBalance;
    private String description;

}
