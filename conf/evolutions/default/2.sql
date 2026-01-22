# --- !Ups
-- Insert de dados ficticios para testes

-- 1. Inserindo ENDEREÇOS (Base para Usuários e Fornecedores)
INSERT INTO ENDERECO (ID, RUA, NUMERO, BAIRRO, CIDADE, ESTADO, PAIS, CEP) VALUES
(1, 'Av. Paulista', '1000', 'Bela Vista', 'São Paulo', 'SP', 'Brasil', '01310-100'),
(2, 'Rua das Flores', '200', 'Centro', 'Curitiba', 'PR', 'Brasil', '80000-000'),
(3, 'Av. Atlântica', '500', 'Copacabana', 'Rio de Janeiro', 'RJ', 'Brasil', '22000-000'),
(4, 'Rua da Bahia', '1500', 'Lourdes', 'Belo Horizonte', 'MG', 'Brasil', '30000-000'),
(5, 'Av. Sete de Setembro', '300', 'Barra', 'Salvador', 'BA', 'Brasil', '40000-000');

-- 2. Inserindo CATEGORIAS
INSERT INTO CATEGORIA (ID, DESCRICAO) VALUES
                                          (1, 'Eletrônicos'),
                                          (2, 'Livros'),
                                          (3, 'Móveis'),
                                          (4, 'Vestuário'),
                                          (5, 'Brinquedos');

-- 3. Inserindo FORNECEDORES (Vinculando aos Endereços 1, 2 e 3)
INSERT INTO FORNECEDOR (ID, NOME, CNPJ, ID_ENDERECO, EMAIL, TELEFONE) VALUES
                                                                          (1, 'Tech Distribuidora', '12.345.678/0001-90', 1, 'contato@tech.com', '11 99999-0001'),
                                                                          (2, 'Móveis do Sul', '98.765.432/0001-10', 2, 'vendas@moveisul.com', '41 98888-0002'),
                                                                          (3, 'Importados RJ', '11.222.333/0001-50', 3, 'sac@importadosrj.com', '21 97777-0003'),
                                                                          (4, 'Livraria Central', '44.555.666/0001-20', 4, 'livros@central.com', '31 96666-0004'),
                                                                          (5, 'Moda e Estilo', '77.888.999/0001-30', 5, 'moda@estilo.com', '71 95555-0005');

-- 4. Inserindo USUÁRIOS (Reutilizando endereços para teste)
INSERT INTO USUARIO (ID, NOME, EMAIL, SENHA, ID_ENDERECO) VALUES
                                                              (1, 'Admin Sistema', 'admin@email.com', 'senhafrote123', 1),
                                                              (2, 'João Silva', 'joao@email.com', '123456', 2),
                                                              (3, 'Maria Oliveira', 'maria@email.com', 'maria123', 3),
                                                              (4, 'Pedro Santos', 'pedro@email.com', 'pedro@99', 4),
                                                              (5, 'Ana Souza', 'ana@email.com', 'ana_segura', 5);

-- 5. Inserindo PRODUTOS (Vinculando a Categorias e Fornecedores)
INSERT INTO PRODUTO (ID, DESCRICAO, ID_CATEGORIA, VALOR, QUANTIDADE, ID_FORNECEDOR, QUANTIDADE_MINIMA, VLD_ATIVO) VALUES
                                                                                                                      (1, 'Smartphone Galaxy', 1, 2500.00, 50, 1, 10, 1),
                                                                                                                      (2, 'Notebook Dell', 1, 4500.00, 30, 1, 5, 1),
                                                                                                                      (3, 'Sofá 3 Lugares', 3, 1200.00, 10, 2, 2, 1),
                                                                                                                      (4, 'Camiseta Básica', 4, 49.90, 200, 5, 50, 1),
                                                                                                                      (5, 'O Senhor dos Anéis', 2, 150.00, 100, 4, 20, 1);

# --- !Downs

-- Deletando na ordem inversa para não quebrar Constraints (Foreign Keys)

DELETE FROM PRODUTO WHERE ID IN (1, 2, 3, 4, 5);
DELETE FROM USUARIO WHERE ID IN (1, 2, 3, 4, 5);
DELETE FROM FORNECEDOR WHERE ID IN (1, 2, 3, 4, 5);
DELETE FROM CATEGORIA WHERE ID IN (1, 2, 3, 4, 5);
DELETE FROM ENDERECO WHERE ID IN (1, 2, 3, 4, 5);