package com.lit.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lit.bank.dto.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BankIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private TransactionDTO transactionDTO;
    private String transactionRequestBody;

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
    }


    @Test
    void saveTransaction() throws Exception {
        mockMvc.perform(post("/api/v1/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionRequestBody))
                .andExpect(status().isCreated());
    }

}
