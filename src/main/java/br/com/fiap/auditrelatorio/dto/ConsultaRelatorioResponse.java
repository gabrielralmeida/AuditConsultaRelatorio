package br.com.fiap.auditrelatorio.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.auditrelatorio.model.Relatorio;

public record ConsultaRelatorioResponse(
        boolean sucesso,
        List<RelatorioItem> relatorios,
        List<String> mensagens) {

    public ConsultaRelatorioResponse {
        relatorios = relatorios == null ? List.of() : List.copyOf(relatorios);
        mensagens = mensagens == null ? List.of() : List.copyOf(mensagens);
    }

    public static ConsultaRelatorioResponse erro(List<String> mensagens) {
        return new ConsultaRelatorioResponse(false, List.of(), mensagens);
    }

    public static ConsultaRelatorioResponse ok(List<Relatorio> relatorios) {
        List<RelatorioItem> itens = relatorios.stream()
                .map(relatorio -> new RelatorioItem(
                        relatorio.getCodigoCurso(),
                        relatorio.getNomeCurso(),
                        relatorio.getCodigoProfessor(),
                        relatorio.getNomeProfessor(),
                        relatorio.getQtdeHoras(),
                        relatorio.getMediaSemanalCurso(),
                        relatorio.getDataHoraGeracao()))
                .toList();
        return new ConsultaRelatorioResponse(true, itens, List.of());
    }

    public record RelatorioItem(
            Integer codigoCurso,
            String nomeCurso,
            Integer codigoProfessor,
            String nomeProfessor,
            Integer qtdeHoras,
            Double mediaSemanalCurso,
            LocalDateTime dataHoraGeracao) {
    }
}
