package br.com.fiap.auditrelatorio.repository;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import br.com.fiap.auditrelatorio.model.Relatorio;

@ApplicationScoped
public class RelatorioPanacheStore implements RelatorioStore {

    private static final String ORDEM = " order by id.dataHoraGeracao, id.codigoCurso, id.codigoProfessor";

    private final RelatorioRepository repository;

    @Inject
    public RelatorioPanacheStore(RelatorioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public List<Relatorio> consultar(LocalDateTime inicioInclusive, LocalDateTime fimInclusive, Integer codigoCurso) {
        if (codigoCurso == null) {
            return repository.list(
                    "id.dataHoraGeracao >= ?1 and id.dataHoraGeracao <= ?2" + ORDEM,
                    inicioInclusive,
                    fimInclusive);
        }
        return repository.list(
                "id.dataHoraGeracao >= ?1 and id.dataHoraGeracao <= ?2 and id.codigoCurso = ?3" + ORDEM,
                inicioInclusive,
                fimInclusive,
                codigoCurso);
    }
}
