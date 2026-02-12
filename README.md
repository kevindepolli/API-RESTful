# API RESTful com Play Framework (Java)

Este projeto é uma API RESTful desenvolvida utilizando **Java 17** e o **Play Framework**. Abaixo está o guia completo para configurar o ambiente do zero e rodar a aplicação com banco de dados MySQL integrado via Docker.

## 🚀 Pré-requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas:

### 1.1 Java JDK 17
O Play Framework requer uma versão LTS específica. Estamos usando a **17**.
* **Verificar instalação:** Abra o terminal e digite `java -version`.

### 1.2 SBT (Scala Build Tool)
Gerenciador de dependências e construção do projeto.
* **Download:** [Site oficial do SBT](https://www.scala-sbt.org/download/).

### 1.3 Docker & Docker Compose
Utilizamos containers para rodar o banco de dados sem "sujeira" na máquina local.
* **Download:** Instale o [Docker Desktop](https://www.docker.com/products/docker-desktop/).
* **Verificar instalação:** Digite `docker -v` e `docker-compose -v`.

---

## 🐳 Configuração do Banco de Dados

O projeto já possui um arquivo `docker-compose.yml` configurado com o MySQL 8.3.

### Credenciais de Acesso (Desenvolvimento)
Caso precise acessar o banco via Workbench, DBeaver ou terminal, utilize:

| Configuração | Valor |
| :--- | :--- |
| **Host** | `localhost` |
| **Porta** | `3306` |
| **Database** | `db_api_restful` |
| **Usuário** | `usuario` |
| **Senha** | `senha` |
| **Senha Root** | `1234` |

---

## ▶️ Como Rodar o Projeto

A ordem de execução é importante: primeiro o banco, depois a aplicação.

### Passo 1: Subir o Banco de Dados
Na raiz do projeto (onde está o arquivo `docker-compose.yml`), abra o terminal e execute:

```bash
docker-compose up -d
```
_O parâmetro -d (detached) libera o terminal após subir o banco._

### Passo 2: Rodar a Aplicação
No mesmo terminal (após o banco subir), execute:

```bash
sbt run
```

### Passo 3: Acessar a Aplicação
1. Aguarde o terminal exibir a mensagem: `Server started, ... listening on http://localhost:9000.`
2. Acesse no seu navegador: http://localhost:9000.

_Dica: O Play Framework possui "Hot Reload". Você pode alterar arquivos .java e dar F5 no navegador sem precisar reiniciar o servidor._

### Passo 4: Logar
A maioria das funcionalidades da API exigem uma identificação.
1. Use uma das credenciais listadas abaixo para fazer o login em http://localhost:9000/login.

2. O corpo da requisição deve ter o seguinte formato e ser do tipo JSON:
    ```json
    {
       "email": "root@root.com",
       "senha": "root123"
    }
    ```
3. O retorno será um Bearer Token com validade de 24 horas.

---
Credenciais para fazer o login:

| E-mail          | Senha         | Cargo         |
|:----------------|:--------------|:--------------|
| root@root.com   | root123       | Root          |
| admin@email.com | senhafrote123 | Administrador |
| joao@email.com  | 123456        | Usuário       |
| maria@email.com | maria123      | Usuário       |
| pedro@email.com | pedro@99      | Usuário       |
| ana@email.com   | ana_segura    | Usuário       |

_Observação: solicitações de criação, edição e exclusão só podem ser feitas por administradores ou usuários com cargo root._