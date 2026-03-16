package br.com.cwi.bancodigital.controller;

import br.com.cwi.bancodigital.constant.ApiPaths;
import br.com.cwi.bancodigital.dto.AccountResponse;
import br.com.cwi.bancodigital.dto.MovementResponse;
import br.com.cwi.bancodigital.exception.ResourceNotFoundException;
import br.com.cwi.bancodigital.service.AccountService;
import br.com.cwi.bancodigital.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    private static final UUID UUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UUID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID UUID_3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final UUID UUID_999 = UUID.fromString("00000000-0000-0000-0000-000000000999");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransferService transferService;

    @Test
    void findAll_retorna200ELista() throws Exception {
        when(accountService.findAll()).thenReturn(List.of(
                new AccountResponse(UUID_1, "Maria", new BigDecimal("1000"))
        ));

        mockMvc.perform(get(ApiPaths.PATH_ACCOUNTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid").value(UUID_1.toString()))
                .andExpect(jsonPath("$[0].name").value("Maria"))
                .andExpect(jsonPath("$[0].balance").value(1000));
    }

    @Test
    void findByUuid_quandoExiste_retorna200() throws Exception {
        when(accountService.findByUuid(UUID_1)).thenReturn(new AccountResponse(UUID_1, "Maria", new BigDecimal("500")));

        mockMvc.perform(get(ApiPaths.PATH_ACCOUNTS + "/" + UUID_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(UUID_1.toString()))
                .andExpect(jsonPath("$.name").value("Maria"));
    }

    @Test
    void findByUuid_quandoNaoExiste_retorna404() throws Exception {
        when(accountService.findByUuid(UUID_999)).thenThrow(new ResourceNotFoundException("Conta não encontrada: " + UUID_999));

        mockMvc.perform(get(ApiPaths.PATH_ACCOUNTS + "/" + UUID_999))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_comDadosValidos_retorna201() throws Exception {
        String body = "{\"name\":\"Ana\",\"initialBalance\":200.00}";
        when(accountService.create(any())).thenReturn(new AccountResponse(UUID_3, "Ana", new BigDecimal("200")));

        mockMvc.perform(post(ApiPaths.PATH_ACCOUNTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uuid").value(UUID_3.toString()))
                .andExpect(jsonPath("$.name").value("Ana"));
        verify(accountService).create(any());
    }

    @Test
    void getMovements_retorna200() throws Exception {
        when(transferService.listMovementsByAccount(UUID_1)).thenReturn(List.of(
                new MovementResponse(UUID_1, "SAIDA", UUID_2, "João", new BigDecimal("-50"), java.time.Instant.now())
        ));

        mockMvc.perform(get(ApiPaths.PATH_ACCOUNTS + "/" + UUID_1 + "/" + ApiPaths.SEGMENT_MOVEMENTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("SAIDA"))
                .andExpect(jsonPath("$[0].amount").value(-50));
        verify(transferService).listMovementsByAccount(UUID_1);
    }
}
