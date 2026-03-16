package br.com.cwi.bancodigital.controller;

import br.com.cwi.bancodigital.constant.ApiPaths;
import br.com.cwi.bancodigital.dto.TransferResponse;
import br.com.cwi.bancodigital.exception.BusinessException;
import br.com.cwi.bancodigital.service.TransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    private static final UUID UUID_FROM = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UUID_TO = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID UUID_TRANSFER = UUID.fromString("00000000-0000-0000-0000-000000000010");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;

    @Test
    void transfer_comDadosValidos_retorna200() throws Exception {
        String body = "{\"fromAccountUuid\":\"00000000-0000-0000-0000-000000000001\",\"toAccountUuid\":\"00000000-0000-0000-0000-000000000002\",\"amount\":100.00}";
        when(transferService.transfer(any())).thenReturn(
                new TransferResponse(UUID_TRANSFER, UUID_FROM, UUID_TO, new BigDecimal("100"), Instant.now())
        );

        mockMvc.perform(post(ApiPaths.PATH_TRANSFERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(UUID_TRANSFER.toString()))
                .andExpect(jsonPath("$.fromAccountUuid").value(UUID_FROM.toString()))
                .andExpect(jsonPath("$.toAccountUuid").value(UUID_TO.toString()))
                .andExpect(jsonPath("$.amount").value(100));
        verify(transferService).transfer(any());
    }

    @Test
    void transfer_saldoInsuficiente_retorna422() throws Exception {
        String body = "{\"fromAccountUuid\":\"00000000-0000-0000-0000-000000000001\",\"toAccountUuid\":\"00000000-0000-0000-0000-000000000002\",\"amount\":99999.00}";
        when(transferService.transfer(any())).thenThrow(new BusinessException("Saldo insuficiente na conta de origem."));

        mockMvc.perform(post(ApiPaths.PATH_TRANSFERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void listByAccount_retorna200() throws Exception {
        when(transferService.listByAccount(UUID_FROM)).thenReturn(List.of(
                new TransferResponse(UUID_TRANSFER, UUID_FROM, UUID_TO, new BigDecimal("50"), Instant.now())
        ));

        mockMvc.perform(get(ApiPaths.PATH_TRANSFERS + "/" + ApiPaths.SEGMENT_ACCOUNT + "/" + UUID_FROM))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fromAccountUuid").value(UUID_FROM.toString()))
                .andExpect(jsonPath("$[0].toAccountUuid").value(UUID_TO.toString()));
        verify(transferService).listByAccount(UUID_FROM);
    }
}
