# --- !Ups

-- 1. Tabela ENDERECO (Não depende de ninguém)
CREATE TABLE IF NOT EXISTS ENDERECO (
                          ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                          RUA VARCHAR(255) NOT NULL,
                          NUMERO VARCHAR(20) NOT NULL,
                          BAIRRO VARCHAR(255) NOT NULL,
                          CIDADE VARCHAR(255) NOT NULL,
                          ESTADO VARCHAR(2) NOT NULL,
                          PAIS VARCHAR(100) NOT NULL,
                          CEP VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- 2. Tabela CATEGORIA (Não depende de ninguém)
CREATE TABLE IF NOT EXISTS CATEGORIA (
                           ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                           DESCRICAO VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- 3. Tabela FORNECEDOR (Depende de ENDERECO)
CREATE TABLE IF NOT EXISTS FORNECEDOR (
                            ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                            NOME VARCHAR(255) NOT NULL,
                            CNPJ VARCHAR(20) NOT NULL,
                            ID_ENDERECO BIGINT,
                            EMAIL VARCHAR(255) NOT NULL,
                            TELEFONE VARCHAR(50) NOT NULL,
                            CONSTRAINT fk_fornecedor_endereco FOREIGN KEY (ID_ENDERECO) REFERENCES ENDERECO(ID)
) ENGINE=InnoDB;

-- 4. Tabela USUARIO
CREATE TABLE IF NOT EXISTS USUARIO (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         NOME VARCHAR(255) NOT NULL,
                         EMAIL VARCHAR(255) NOT NULL,
                         SENHA VARCHAR(255) NOT NULL,
                         ROLE VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- 5. Tabela PRODUTO (Depende de CATEGORIA e FORNECEDOR)
CREATE TABLE IF NOT EXISTS PRODUTO (
                         ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                         DESCRICAO VARCHAR(255) NOT NULL,
                         ID_CATEGORIA BIGINT,
                         VALOR DECIMAL(10,2) NOT NULL,
                         QUANTIDADE INT NOT NULL,
                         ID_FORNECEDOR BIGINT,
                         QUANTIDADE_MINIMA INT,
                         VLD_ATIVO INT NOT NULL DEFAULT 1,
                         CONSTRAINT fk_produto_categoria FOREIGN KEY (ID_CATEGORIA) REFERENCES CATEGORIA(ID),
                         CONSTRAINT fk_produto_fornecedor FOREIGN KEY (ID_FORNECEDOR) REFERENCES FORNECEDOR(ID)
) ENGINE=InnoDB;

# --- !Downs

-- A ordem do Drop deve ser inversa à criação (Filhos primeiro, depois Pais)
DROP TABLE IF EXISTS PRODUTO;
DROP TABLE IF EXISTS USUARIO;
DROP TABLE IF EXISTS FORNECEDOR;
DROP TABLE IF EXISTS CATEGORIA;
DROP TABLE IF EXISTS ENDERECO;