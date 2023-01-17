package com.lit.bank;

import com.lit.bank.security.model.BalanceRole;
import com.lit.bank.security.model.User;
import com.lit.bank.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }
}

@Component
@RequiredArgsConstructor
class DataLoader implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        //Zahar is allowed to make transactions because of her editor balance-role
        User zahar = User.builder()
                .firstName("Zahar")
                .lastName("Yilmaz")
                .email("zahar@lit.com")
                .password(BCrypt.hashpw("password", BCrypt.gensalt()))
                .authorities(List.of(new SimpleGrantedAuthority(BalanceRole.EDITOR.toClaimValue())))
                .build();

        //Nelson is NOT allowed to make transactions since he doesn't have the right balance-role
        User nelson = User.builder()
                .firstName("Nelson")
                .lastName("Mamba-Samba")
                .email("nelson@lit.com")
                .password(BCrypt.hashpw("password", BCrypt.gensalt()))
                .authorities(List.of(new SimpleGrantedAuthority(BalanceRole.READER.toClaimValue())))
                .build();

        //Zoe is NOT allowed to make transactions, she doesn't have any balance-role
        User zoe = User.builder()
                .firstName("Zoë")
                .lastName("Banks")
                .email("zoe@lit.com")
                .password(BCrypt.hashpw("password", BCrypt.gensalt()))
                .build();

        userRepository.save(zahar);
        userRepository.save(nelson);
        userRepository.save(zoe);
    }
}
