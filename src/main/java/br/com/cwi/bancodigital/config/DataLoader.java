package br.com.cwi.bancodigital.config;

import br.com.cwi.bancodigital.domain.Account;
import br.com.cwi.bancodigital.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadAccounts(AccountRepository accountRepository) {
        return args -> {
            if (accountRepository.count() > 0) return;
            accountRepository.save(new Account("Maria Silva", new BigDecimal("1000.00")));
            accountRepository.save(new Account("João Santos", new BigDecimal("500.50")));
            accountRepository.save(new Account("Ana Oliveira", new BigDecimal("2500.00")));
        };
    }
}
