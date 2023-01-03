package com.lit.bank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lit.bank.dto.TransactionDTO;
import com.lit.bank.model.BankAccount;
import com.lit.bank.model.Transaction;
import com.lit.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankController.class)
class BankControllerTest {

    @MockBean
    private TransactionService transactionServiceMock;

    @Autowired
    private MockMvc mockMvc;

    private TransactionDTO transactionDTO;
    private String transactionRequestBody;
    private Transaction transaction;

    @BeforeEach
    void setup() throws JsonProcessingException {
        this.transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionReference(1L);
        transactionDTO.setIban("NL64ABNA6291988119"); //this is a random valid IBAN
        transactionDTO.setStartBalance("10");
        transactionDTO.setMutation("5.5");
        transactionDTO.setEndBalance("15.5");
        transactionDTO.setDescription("description of transaction");

        ObjectMapper mapper = new ObjectMapper();
        this.transactionRequestBody = mapper.writeValueAsString(transactionDTO);

        this.transaction = new Transaction();
        transaction.setId(1L);
        transaction.setBankAccount(new BankAccount("NL64ABNA6291988119", new BigDecimal(10)));
        transaction.setStartBalance(new BigDecimal(10));
        transaction.setMutation(new BigDecimal("5.5"));
        transaction.setEndBalance(new BigDecimal("15.5"));
        transaction.setDescription("description of transaction");
    }

    @Test
    void save_happyFlow_Returns201() throws Exception {
        //arrange
        when(transactionServiceMock.processStatement(transactionDTO)).thenReturn(transaction);

        //act & assert
        mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionRequestBody))
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @ValueSource(strings = {"notavalidiban", "NL64ABNA6291988118", ""}) //NL64ABNA6291988118 does not pass the mod97 requirement for IBAN
    void save_invalidIban_Returns409(String iban) throws Exception {
        //arrange
        transactionDTO.setIban(iban);
        ObjectMapper mapper = new ObjectMapper();
        this.transactionRequestBody = mapper.writeValueAsString(transactionDTO);

        //act & assert
        mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionRequestBody))
                .andExpect(status().isBadRequest());
    }
}