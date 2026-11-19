package br.com.fiap.auditrelatorio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.auditrelatorio.dto.ConsultaRelatorioRequest;
import br.com.fiap.auditrelatorio.dto.ConsultaRelatorioResponse;
import br.com.fiap.auditrelatorio.model.Relatorio;
import br.com.fiap.auditrelatorio.repository.MemoriaRelatorioStore;

class RelatorioServiceTest {

    private MemoriaRelatorioStore store;
    private RelatorioService service;

    @BeforeEach
    void setUp() {
        store = new MemoriaRelatorioStore();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        service = new RelatorioService(validator, store);
        store.adicionar(relatorio(1, "Arquitetura Cloud Native", 10, "Ana Lima", 40, 8.50, "2026-11-13T23:59:00"));
        store.adicionar(relatorio(2, "Desenvolvimento Java com Quarkus", 20, "Bruno Souza", 80, 7.25, "2026-11-14T00:00:00"));
        store.adicionar(relatorio(1, "Arquitetura Cloud Native", 11, "Carla Dias", 40, 9.00, "2026-11-14T18:30:00"));
        store.adicionar(relatorio(3, "Banco de dados MySQL", 30, "Diego Alves", 60, 6.75, "2026-11-16T09:00:00"));
        store.adicionar(relatorio(2, "Desenvolvimento Java com Quarkus", 21, "Elena Rocha", 80, 8.10, "2026-11-15T23:59:59"));
    }

    @Test
    void rejeitaPedidoAusente() {
        ConsultaRelatorioResponse resposta = service.consultar(null);

        assertFalse(resposta.sucesso());
        assertEquals(List.of("Os dados da consulta são obrigatórios."), resposta.mensagens());
        assertEquals(List.of(), resposta.relatorios());
        assertEquals(0, store.consultas());
    }

    @Test
    void rejeitaCamposObrigatoriosAusentes() {
        ConsultaRelatorioResponse resposta = service.consultar(pedido(null, null, null));

        assertFalse(resposta.sucesso());
        assertEquals(
                Set.of("Data final é obrigatória", "Data inicial é obrigatória"),
                Set.copyOf(resposta.mensagens()));
        assertEquals(0, store.consultas());
    }

    @Test
    void rejeitaDataInicialMaiorQueDataFinal() {
        ConsultaRelatorioResponse resposta = service.consultar(pedido(LocalDate.of(2026, 11, 16), LocalDate.of(2026, 11, 14), 1));

        assertFalse(resposta.sucesso());
        assertEquals(List.of("A data inicial deve ser menor ou igual à data final"), resposta.mensagens());
        assertEquals(0, store.consultas());
    }

    @Test
    void consultaTodosOsCursosQuandoCodigoAusente() {
        ConsultaRelatorioResponse resposta = service.consultar(pedido(LocalDate.of(2026, 11, 14), LocalDate.of(2026, 11, 15), null));

        assertTrue(resposta.sucesso());
        assertEquals(
                List.of(
                        item(2, "Desenvolvimento Java com Quarkus", 20, "Bruno Souza", 80, 7.25, "2026-11-14T00:00:00"),
                        item(1, "Arquitetura Cloud Native", 11, "Carla Dias", 40, 9.00, "2026-11-14T18:30:00"),
                        item(2, "Desenvolvimento Java com Quarkus", 21, "Elena Rocha", 80, 8.10, "2026-11-15T23:59:59")),
                resposta.relatorios());
    }

    @Test
    void consultaCursoNoIntervaloInclusivo() {
        ConsultaRelatorioResponse resposta = service.consultar(pedido(LocalDate.of(2026, 11, 14), LocalDate.of(2026, 11, 15), 2));

        assertTrue(resposta.sucesso());
        assertEquals(List.of(), resposta.mensagens());
        assertEquals(
                List.of(
                        item(2, "Desenvolvimento Java com Quarkus", 20, "Bruno Souza", 80, 7.25, "2026-11-14T00:00:00"),
                        item(2, "Desenvolvimento Java com Quarkus", 21, "Elena Rocha", 80, 8.10, "2026-11-15T23:59:59")),
                resposta.relatorios());
    }

    @Test
    void consultaUmUnicoDiaDoCurso() {
        ConsultaRelatorioResponse resposta = service.consultar(pedido(LocalDate.of(2026, 11, 14), LocalDate.of(2026, 11, 14), 2));

        assertTrue(resposta.sucesso());
        assertEquals(1, resposta.relatorios().size());
        assertEquals(LocalDateTime.parse("2026-11-14T00:00:00"), resposta.relatorios().get(0).dataHoraGeracao());
    }

    @Test
    void filtraPeloCodigoDoCurso() {
        ConsultaRelatorioResponse resposta = service.consultar(pedido(LocalDate.of(2026, 11, 14), LocalDate.of(2026, 11, 16), 2));

        assertTrue(resposta.sucesso());
        assertEquals(
                List.of(
                        item(2, "Desenvolvimento Java com Quarkus", 20, "Bruno Souza", 80, 7.25, "2026-11-14T00:00:00"),
                        item(2, "Desenvolvimento Java com Quarkus", 21, "Elena Rocha", 80, 8.10, "2026-11-15T23:59:59")),
                resposta.relatorios());
    }

    @Test
    void intervaloSemRelatoriosDevolveListaVazia() {
        ConsultaRelatorioResponse resposta = service.consultar(pedido(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 10), 1));

        assertTrue(resposta.sucesso());
        assertEquals(List.of(), resposta.relatorios());
        assertEquals(List.of(), resposta.mensagens());
        assertEquals(1, store.consultas());
    }

    private static ConsultaRelatorioRequest pedido(LocalDate dataInicio, LocalDate dataFim, Integer codigoCurso) {
        ConsultaRelatorioRequest request = new ConsultaRelatorioRequest();
        request.setDataInicio(dataInicio);
        request.setDataFim(dataFim);
        request.setCodigoCurso(codigoCurso);
        return request;
    }

    private static Relatorio relatorio(
            int codigoCurso,
            String nomeCurso,
            int codigoProfessor,
            String nomeProfessor,
            int qtdeHoras,
            double media,
            String dataHora) {
        return new Relatorio(
                codigoCurso,
                nomeCurso,
                codigoProfessor,
                nomeProfessor,
                qtdeHoras,
                media,
                LocalDateTime.parse(dataHora));
    }

    private static ConsultaRelatorioResponse.RelatorioItem item(
            int codigoCurso,
            String nomeCurso,
            int codigoProfessor,
            String nomeProfessor,
            int qtdeHoras,
            double media,
            String dataHora) {
        return new ConsultaRelatorioResponse.RelatorioItem(
                codigoCurso,
                nomeCurso,
                codigoProfessor,
                nomeProfessor,
                qtdeHoras,
                media,
                LocalDateTime.parse(dataHora));
    }
}
