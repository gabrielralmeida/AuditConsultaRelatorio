package br.com.fiap.auditrelatorio.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.com.fiap.auditrelatorio.model.Relatorio;

public final class MemoriaRelatorioStore implements RelatorioStore {

    private final List<Relatorio> relatorios = new ArrayList<>();
    private int consultas;

    public void adicionar(Relatorio relatorio) {
        relatorios.add(relatorio);
    }

    public int consultas() {
        return consultas;
    }

    @Override
    public List<Relatorio> consultar(LocalDateTime inicioInclusive, LocalDateTime fimInclusive, Integer codigoCurso) {
        consultas++;
        return relatorios.stream()
                .filter(relatorio -> !relatorio.getDataHoraGeracao().isBefore(inicioInclusive))
                .filter(relatorio -> !relatorio.getDataHoraGeracao().isAfter(fimInclusive))
                .filter(relatorio -> codigoCurso == null || codigoCurso.equals(relatorio.getCodigoCurso()))
                .sorted(Comparator.comparing(Relatorio::getDataHoraGeracao)
                        .thenComparing(Relatorio::getCodigoCurso)
                        .thenComparing(Relatorio::getCodigoProfessor))
                .toList();
    }
}
