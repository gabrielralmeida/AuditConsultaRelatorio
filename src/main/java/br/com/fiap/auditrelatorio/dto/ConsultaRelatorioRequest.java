package br.com.fiap.auditrelatorio.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public class ConsultaRelatorioRequest {

    @NotNull(message = "Data inicial é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "Data final é obrigatória")
    private LocalDate dataFim;

    private Integer codigoCurso;

    public ConsultaRelatorioRequest normalizado() {
        ConsultaRelatorioRequest copia = new ConsultaRelatorioRequest();
        copia.dataInicio = dataInicio;
        copia.dataFim = dataFim;
        copia.codigoCurso = codigoCurso;
        return copia;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public Integer getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(Integer codigoCurso) {
        this.codigoCurso = codigoCurso;
    }
}
