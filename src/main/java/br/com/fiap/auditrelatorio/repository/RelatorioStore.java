package br.com.fiap.auditrelatorio.repository;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.auditrelatorio.model.Relatorio;

public interface RelatorioStore {

    List<Relatorio> consultar(LocalDateTime inicioInclusive, LocalDateTime fimInclusive, Integer codigoCurso);
}
