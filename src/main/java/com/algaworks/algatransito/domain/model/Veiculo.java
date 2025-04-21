package com.algaworks.algatransito.domain.model;

import com.algaworks.algatransito.domain.exception.NegocioException;
import com.algaworks.algatransito.domain.validation.ValidationGroups;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.ConvertGroup;
import jakarta.validation.groups.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    //comentarios removendo as validações, pois já se encontram nas classes de input
  //  @NotNull
    @ManyToOne
  //  @Valid
  //  @ConvertGroup(from = Default.class, to = ValidationGroups.ProprietarioId.class)
    //@JoinColumn(name = "proprietario_id")
    private Proprietario proprietario;

    //@NotBlank
    private String marca;

    //@NotBlank
    private String modelo;

//    @NotBlank
  //  @Pattern(regexp = "[A-Z]{3}[0-9][0-9A-Z][0-9]{2}")
    private String placa;

   // @JsonProperty(access = Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    private StatusVeiculo status;

   // @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime dataCadastro;

    //@JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime dataApreensao;

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL)
    private List<Autuacao> autuacoes = new ArrayList<>();


    public Autuacao adicionarAutuacao(Autuacao autuacao){
        autuacao.setVeiculo(this);
        autuacao.setDataOcorrencia(OffsetDateTime.now());
        getAutuacoes().add(autuacao);
        return autuacao;
    }

    public void apreender(){
        if(estaApreendido()){
            throw new NegocioException("Veiculo já se encontra apreendido");
        }
        setStatus(StatusVeiculo.APREENDIDO);
        setDataApreensao(OffsetDateTime.now());

    }

    private boolean estaApreendido() {
        return StatusVeiculo.APREENDIDO.equals(getStatus());
    }

    public void removerApreensao() {
        if(naoEstaApreendido()){
            throw new NegocioException("Veiculo não está apreendido");
        }
        setStatus(StatusVeiculo.REGULAR);
        setDataApreensao(null);
    }

    private boolean naoEstaApreendido() {
        return !estaApreendido();
    }
}
