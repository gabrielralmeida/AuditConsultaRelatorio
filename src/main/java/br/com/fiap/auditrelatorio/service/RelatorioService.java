package br.com.fiap.auditrelatorio.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import br.com.fiap.auditrelatorio.dto.ConsultaRelatorioRequest;
import br.com.fiap.auditrelatorio.dto.ConsultaRelatorioResponse;
import br.com.fiap.auditrelatorio.repository.RelatorioStore;

@ApplicationScoped
public class RelatorioService {

    private final Validator validator;
    private final RelatorioStore store;

    @Inject
    public RelatorioService(Validator validator, RelatorioStore store) {
        this.validator = validator;
        this.store = store;
    }

    public ConsultaRelatorioResponse consultar(ConsultaRelatorioRequest request) {
        if (request == null) {
            return ConsultaRelatorioResponse.erro(List.of("Os dados da consulta são obrigatórios."));
        }
        ConsultaRelatorioRequest normalizado = request.normalizado();
        List<String> mensagens = validator.validate(normalizado).stream()
                .map(ConstraintViolation::getMessage)
                .distinct()
                .sorted()
                .toList();
        if (!mensagens.isEmpty()) {
            return ConsultaRelatorioResponse.erro(mensagens);
        }
        if (normalizado.getDataInicio().isAfter(normalizado.getDataFim())) {
            return ConsultaRelatorioResponse.erro(
                    List.of("A data inicial deve ser menor ou igual à data final"));
        }

        LocalDateTime inicio = normalizado.getDataInicio().atStartOfDay();
        LocalDateTime fim = normalizado.getDataFim().atTime(LocalTime.of(23, 59, 59));
        return ConsultaRelatorioResponse.ok(
                store.consultar(inicio, fim, normalizado.getCodigoCurso()));
    }
}
