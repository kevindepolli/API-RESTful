# --- !Ups
-- 1. Tabela CATEGORIA (Não depende de ninguém)
CREATE TABLE IF NOT EXISTS CATEGORIA (
                           ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                           DESCRICAO VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- 2. Tabela USUARIO
CREATE TABLE IF NOT EXISTS USUARIO (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         NOME VARCHAR(255) NOT NULL,
                         EMAIL VARCHAR(255) NOT NULL,
                         SENHA VARCHAR(255) NOT NULL,
                         ROLE VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- 3. Tabela PRODUTO (Depende de CATEGORIA e FORNECEDOR)
CREATE TABLE IF NOT EXISTS PRODUTO (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         DESCRICAO VARCHAR(255) NOT NULL,
                         ID_CATEGORIA BIGINT,
                         VALOR DECIMAL(10,2) NOT NULL,
                         QUANTIDADE INT NOT NULL,
                         QUANTIDADE_MINIMA INT,
                         VLD_ATIVO INT NOT NULL DEFAULT 1,
                         CONSTRAINT fk_produto_categoria FOREIGN KEY (ID_CATEGORIA) REFERENCES CATEGORIA(ID)
) ENGINE=InnoDB;

# --- !Downs

-- A ordem do Drop deve ser inversa à criação (Filhos primeiro, depois Pais)
DROP TABLE IF EXISTS PRODUTO;
DROP TABLE IF EXISTS USUARIO;
DROP TABLE IF EXISTS CATEGORIA;