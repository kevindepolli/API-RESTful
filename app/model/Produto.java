package model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUTO")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Long id;

    @Column(name = "DESCRICAO")
    public String descricao;

    @Column(name = "VALOR")
    public BigDecimal valor;

    @Column(name = "QUANTIDADE")
    public Integer quantidade;

    @Column(name = "QUANTIDADE_MINIMA")
    public Integer quantidadeMinima;

    @Column(name = "VLD_ATIVO")
    public Integer ativo = 1; // Default 1

    // --- Relacionamentos ---

    @ManyToOne
    @JoinColumn(name = "ID_CATEGORIA")
    public Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "ID_FORNECEDOR")
    public Fornecedor fornecedor;
}