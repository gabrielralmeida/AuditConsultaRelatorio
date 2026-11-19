package br.com.fiap.auditrelatorio.repository;

import jakarta.enterprise.context.ApplicationScoped;

import br.com.fiap.auditrelatorio.model.Relatorio;
import br.com.fiap.auditrelatorio.model.RelatorioId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

@ApplicationScoped
public class RelatorioRepository implements PanacheRepositoryBase<Relatorio, RelatorioId> {
}
