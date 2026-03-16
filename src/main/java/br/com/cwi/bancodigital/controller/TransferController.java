package br.com.cwi.bancodigital.controller;

import br.com.cwi.bancodigital.constant.ApiPaths;
import br.com.cwi.bancodigital.dto.TransferRequest;
import br.com.cwi.bancodigital.dto.TransferResponse;
import br.com.cwi.bancodigital.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.PATH_TRANSFERS)
@Tag(name = "Transferências", description = "Transferência entre contas e consulta")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @Operation(summary = "Realizar transferência")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }

    @GetMapping("/" + ApiPaths.SEGMENT_ACCOUNT + "/{" + ApiPaths.PATH_PARAM_ACCOUNT_UUID + "}")
    @Operation(summary = "Listar transferências da conta")
    public List<TransferResponse> listByAccount(@PathVariable(ApiPaths.PATH_PARAM_ACCOUNT_UUID) UUID accountUuid) {
        return transferService.listByAccount(accountUuid);
    }
}
