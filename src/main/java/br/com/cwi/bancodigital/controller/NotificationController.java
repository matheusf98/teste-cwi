package br.com.cwi.bancodigital.controller;

import br.com.cwi.bancodigital.constant.ApiPaths;
import br.com.cwi.bancodigital.domain.Notification;
import br.com.cwi.bancodigital.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiPaths.PATH_NOTIFICATIONS)
@Tag(name = "Notificações", description = "Consulta de notificações por conta")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/" + ApiPaths.SEGMENT_ACCOUNT + "/{" + ApiPaths.PATH_PARAM_ACCOUNT_UUID + "}")
    @Operation(summary = "Listar notificações da conta")
    public List<Map<String, Object>> listByAccount(@PathVariable(ApiPaths.PATH_PARAM_ACCOUNT_UUID) UUID accountUuid) {
        return notificationService.findByAccountUuid(accountUuid).stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toMap(Notification n) {
        return Map.of(
                "uuid", n.getUuid().toString(),
                "message", n.getMessage(),
                "sentAt", n.getSentAt().toString()
        );
    }
}
