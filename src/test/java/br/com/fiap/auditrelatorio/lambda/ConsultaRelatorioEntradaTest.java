package br.com.fiap.auditrelatorio.lambda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.auditrelatorio.dto.ConsultaRelatorioRequest;

class ConsultaRelatorioEntradaTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void lePedidoDireto() throws Exception {
        ConsultaRelatorioRequest pedido = ConsultaRelatorioEntrada.ler(mapper.readTree("""
                {"dataInicio":"2026-01-01","dataFim":"2026-11-18","codigoCurso":1}
                """), mapper);

        assertEquals(LocalDate.of(2026, 1, 1), pedido.getDataInicio());
        assertEquals(LocalDate.of(2026, 11, 18), pedido.getDataFim());
        assertEquals(1, pedido.getCodigoCurso());
    }

    @Test
    void leCorpoDoApiGateway() throws Exception {
        ConsultaRelatorioRequest pedido = ConsultaRelatorioEntrada.ler(mapper.readTree("""
                {
                  "version": "2.0",
                  "routeKey": "POST /consultaRelatorio",
                  "isBase64Encoded": false,
                  "body": "{\\"dataInicio\\":\\"2026-01-01\\",\\"dataFim\\":\\"2026-11-18\\",\\"codigoCurso\\":1}"
                }
                """), mapper);

        assertEquals(LocalDate.of(2026, 1, 1), pedido.getDataInicio());
        assertEquals(LocalDate.of(2026, 11, 18), pedido.getDataFim());
        assertEquals(1, pedido.getCodigoCurso());
    }

    @Test
    void leCorpoCodificadoEmBase64() throws Exception {
        String json = "{\"dataInicio\":\"2026-01-01\",\"dataFim\":\"2026-11-18\"}";
        String codificado = Base64.getEncoder().encodeToString(json.getBytes());
        ConsultaRelatorioRequest pedido = ConsultaRelatorioEntrada.ler(mapper.readTree("""
                {"isBase64Encoded": true, "body": "%s"}
                """.formatted(codificado)), mapper);

        assertEquals(LocalDate.of(2026, 1, 1), pedido.getDataInicio());
        assertEquals(LocalDate.of(2026, 11, 18), pedido.getDataFim());
        assertNull(pedido.getCodigoCurso());
    }
}
