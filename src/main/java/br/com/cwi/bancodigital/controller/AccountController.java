package br.com.cwi.bancodigital.controller;

import br.com.cwi.bancodigital.constant.ApiPaths;
import br.com.cwi.bancodigital.dto.AccountRequest;
import br.com.cwi.bancodigital.dto.AccountResponse;
import br.com.cwi.bancodigital.dto.MovementResponse;
import br.com.cwi.bancodigital.service.AccountService;
import br.com.cwi.bancodigital.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.PATH_ACCOUNTS)
@Tag(name = "Contas", description = "Gestão de contas e movimentações")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;

    @GetMapping
    @Operation(summary = "Listar contas")
    public List<AccountResponse> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{" + ApiPaths.PATH_PARAM_UUID + "}")
    @Operation(summary = "Buscar conta por UUID")
    public AccountResponse findByUuid(@PathVariable(ApiPaths.PATH_PARAM_UUID) UUID uuid) {
        return accountService.findByUuid(uuid);
    }

    @GetMapping("/{" + ApiPaths.PATH_PARAM_UUID + "}/" + ApiPaths.SEGMENT_MOVEMENTS)
    @Operation(summary = "Listar movimentações da conta")
    public List<MovementResponse> getMovements(@PathVariable(ApiPaths.PATH_PARAM_UUID) UUID uuid) {
        return transferService.listMovementsByAccount(uuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar nova conta")
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return accountService.create(request);
    }
}
