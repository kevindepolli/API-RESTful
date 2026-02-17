# --- !Ups
-- Insert de dados ficticios para testes

-- 1. Inserindo CATEGORIAS
INSERT INTO CATEGORIA (ID, DESCRICAO) VALUES
                                          (1, 'Eletrônicos'),
                                          (2, 'Livros'),
                                          (3, 'Móveis'),
                                          (4, 'Vestuário'),
                                          (5, 'Brinquedos');


-- 2. Inserindo USUÁRIOS (Reutilizando endereços para teste)
INSERT INTO USUARIO (ID, NOME, EMAIL, SENHA, ROLE) VALUES
                                                  (1, 'Admin', 'admin@email.com', '$2a$12$T8Xdqde3hvX/QtZDOjQsE.aVvINDj7n6CaBQcwFSnHwu3u16E7yUG', 'ADMIN'),
                                                  (2, 'João Silva', 'joao@email.com', '$2a$12$Wgpfl8pXxGG7YP.EDs5ogO1ZLiCvmMHVsDrE3NuhJUJLDF/gnVXfq', 'USER'),
                                                  (3, 'Maria Oliveira', 'maria@email.com', '$2a$12$2ZQzMR4zA84E82Iov8Yvm.QfP3JYZpZP.I3JsT8pgYJ6BY1azwHS2', 'USER'),
                                                  (4, 'Pedro Santos', 'pedro@email.com', '$2a$12$FMSzBoDVuRRtL8JyBCVWMOQIvcmc7nLeITxdLIqjk37OnEbhK1DuO', 'USER'),
                                                  (5, 'Ana Souza', 'ana@email.com', '$2a$12$h.Ah9dmWAy5U6rN2WAQTzu5yHvQzlxIMu23GJeOoHIn1InCcc6BTy', 'USER'),
                                                  (7, 'Root', 'root@root.com', '$2a$12$nqNYWMchK4ivJM.Fi1CpxOPsSDlzf1lqpRkuBMEYmtWLydqbQlIF6', 'ROOT');

-- 3. Inserindo PRODUTOS (Vinculando a Categorias e Fornecedores)
INSERT INTO PRODUTO (ID, DESCRICAO, ID_CATEGORIA, VALOR, QUANTIDADE, QUANTIDADE_MINIMA, VLD_ATIVO) VALUES
                                                                                                                      (1, 'Smartphone Galaxy', 1, 2500.00, 50, 10, 1),
                                                                                                                      (2, 'Notebook Dell', 1, 4500.00, 30, 5, 1),
                                                                                                                      (3, 'Sofá 3 Lugares', 3, 1200.00, 10, 2, 1),
                                                                                                                      (4, 'Camiseta Básica', 4, 49.90, 200, 50, 1),
                                                                                                                      (5, 'O Senhor dos Anéis', 2, 150.00, 100, 20, 1);

# --- !Downs

-- Deletando na ordem inversa para não quebrar Constraints (Foreign Keys)

DELETE FROM PRODUTO WHERE ID IN (1, 2, 3, 4, 5);
DELETE FROM USUARIO WHERE ID IN (1, 2, 3, 4, 5);
DELETE FROM CATEGORIA WHERE ID IN (1, 2, 3, 4, 5);