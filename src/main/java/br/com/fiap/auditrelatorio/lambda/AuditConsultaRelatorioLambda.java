package br.com.fiap.auditrelatorio.lambda;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.auditrelatorio.dto.ConsultaRelatorioResponse;
import br.com.fiap.auditrelatorio.service.RelatorioService;

@ApplicationScoped
@Named("consultarRelatorio")
public class AuditConsultaRelatorioLambda implements RequestHandler<JsonNode, ConsultaRelatorioResponse> {

    private final RelatorioService service;
    private final ObjectMapper mapper;

    @Inject
    public AuditConsultaRelatorioLambda(RelatorioService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public ConsultaRelatorioResponse handleRequest(JsonNode input, Context context) {
        return service.consultar(ConsultaRelatorioEntrada.ler(input, mapper));
    }
}
