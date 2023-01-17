package com.lit.bank.transaction.util;


import com.lit.bank.transaction.model.Transaction;
import com.lit.bank.transaction.dto.TransactionDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    ModelMapper mapper;

    public TransactionMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.createTypeMap(TransactionDTO.class, Transaction.class)
                .addMappings(mapper -> mapper.map(TransactionDTO::getTransactionReference, Transaction::setId));
        this.mapper = modelMapper;
    }

    public Transaction toTransaction(TransactionDTO transactionDTO) {
        return mapper.map(transactionDTO, Transaction.class);
    }
}
