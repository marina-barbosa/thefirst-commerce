# Projeto E-Commerce com Spring Boot

<br>

Este projeto é uma aplicação de e-commerce desenvolvida com Java e Spring Boot, utilizando PostgreSQL como banco de dados em teste e produção e H2 em ambiente de desenvolvimento. A aplicação implementa uma arquitetura de API REST e inclui autenticação e autorização com OAuth2 e JWT.

<br>

![image](https://github.com/carloshenriquefs/dscommerce-java/assets/54969405/cb6bada3-b6f3-4950-a27c-fdfe808824d7)
![image](https://github.com/carloshenriquefs/dscommerce-java/assets/54969405/3a4cd519-b95f-40f4-b751-720b891fac38)

<br>

![dscommerce drawio](https://github.com/carloshenriquefs/dscommerce-java/assets/54969405/bec2b868-5997-4668-957a-8d621f48c715)

<br>
<br>

###  Endpoints:

## Categories

### Listar todas as categorias
- URL: `GET /categories`
  
Response:
- Código de Status: 200 OK
  
```
[
  {
    "id": 1,
    "name": "Electronics"
  },
  {
    "id": 2,
    "name": "Books"
  }
]
```

<br>

## Orders

### Buscar pedido por ID
- URL: `GET /orders/{id}`
- Headers: Authorization: Bearer <token> (ROLE_ADMIN, ROLE_CLIENT)
  
Response:
- Código de Status: 200 OK
  
```
{
  "id": 1,
  "status": "WAITING_PAYMENT",
  "client": {
    "id": 2,
    "name": "John Doe"
  },
  "items": [
    {
      "productId": 3,
      "quantity": 2,
      "price": 100.0
    }
  ]
}
```

### Criar novo pedido
- URL: `POST /orders`
- Headers: Authorization: Bearer <token> (ROLE_ADMIN)
   
Corpo da Requisição:

```
{
  "items": [
    {
      "productId": 3,
      "quantity": 2
    }
  ]
}
```

Response:
- Código de Status: 201 Created
  
```
{
  "id": 10,
  "status": "WAITING_PAYMENT",
  "moment": "2024-01-02T10:00:00Z",
  "client": {
    "id": 2,
    "name": "John Doe"
  }
}
```

<br>

## Products

### Buscar produto por ID
- URL: `GET /products/{id}`
  
Response:
- Código de Status: 200 OK
  
```
{
  "id": 1,
  "name": "Produto A",
  "description": "Descrição do produto A",
  "price": 100.0,
  "imgUrl": "url_da_imagem",
  "categories": [
    {
      "id": 1,
      "name": "Categoria A"
    }
  ]
}
```

### Listar todos os produtos

- URL: `GET /products`
  
  Parâmetros de Consulta (Query Params):
  
```
  name (opcional)
  page (opcional)
  size (opcional)
```

Response:
- Código de Status: 200 OK
  
```
{
  "content": [
    {
      "id": 1,
      "name": "Produto A",
      "description": "Descrição do produto A",
      "price": 100.0,
      "imgUrl": "url_da_imagem"
    }
  ],
  "totalElements": 10,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### Inserir novo produto
- URL: `POST /products`
- Headers: Authorization: Bearer <token> (ROLE_ADMIN)
  
Corpo da Requisição:

```
{
  "name": "Smartwatch",
  "description": "Waterproof",
  "price": 199.99,
  "categories": [
    {
      "id": 1,
      "name": "Electronics"
    }
  ]
}
```

Response:
- Código de Status: 201 Created
  
```
{
  "id": 5,
  "name": "Smartwatch",
  "description": "Waterproof",
  "price": 199.99
}
```

### Atualiza um produto existente pelo ID (apenas admins).
- URL: `PUT /products/{id}`
- Header: Authorization: Bearer <token>.
  
Body: Mesma estrutura do POST.

Response: Produto atualizado.

### Deleta um produto existente pelo ID (apenas admins).
- URL: `DELETE /products/{id}`
- Header: Authorization: Bearer <token>.
  
  Response: 204 No Content.

<br>

## Users

### Buscar usuário autenticado
- URL: `GET /users/me`
- Headers: Authorization: Bearer <token> (ROLE_ADMIN, ROLE_CLIENT)
  
Response:
- Código de Status: 200 OK
  
```
{
  "id": 1,
  "name": "John Doe",
  "email": "johndoe@example.com"
}
```

<br>
<br>

## Tecnologias Utilizadas:
- Java 17
- Spring Boot 3.3.3
- Spring Security com OAuth2 (Authorization Server e Resource Server)
- JWT para autenticação
- Banco de Dados PostgreSQL em produção/testes e H2 para desenvolvimento
- Docker para gerenciamento de containers de banco de dados (PostgreSQL e pgAdmin)
- Maven para gerenciamento de dependências e build da aplicação

<br>
<br>

## Configurações do Aplicativo:
Variáveis de ambiente para flexibilidade em múltiplos ambientes (desenvolvimento, testes, produção):
- spring.profiles.active: Define o perfil ativo (ex: test, dev, prod).
- security.client-id, security.client-secret: Configurações de segurança para OAuth2.
- security.jwt.duration: Tempo de expiração do JWT (configurado como 86400 segundos).
- cors.origins: Controle de origens permitidas para CORS.

<br>
<br>
  
## Docker:
O projeto inclui arquivos de configuração para Docker que facilitam a criação e gerenciamento de containers:

- PostgreSQL: Banco de dados utilizado para ambientes de desenvolvimento e produção.
- pgAdmin: Interface gráfica para gerenciamento do PostgreSQL, acessível via navegador.

<br>
<br>

## Maven:
O projeto utiliza o Maven como ferramenta de build. As dependências incluem:

- Spring Boot Starter Data JPA: Para acesso a dados e manipulação do banco de dados.
- Spring Boot Starter Web: Para criar e expor endpoints REST.
- Spring Boot Starter Security: Para gerenciamento de autenticação e autorização.
- Lombok: Para reduzir o boilerplate de código.
- PostgreSQL Driver: Para conectar-se ao banco de dados PostgreSQL.
- Testes: Dependências para criação de testes automatizados.

<br>
<br>
  
## Instruções para Execução:

### Desenvolvimento
- Suba os containers do PostgreSQL e pgAdmin com o Docker Compose:

```
docker-compose up
```

- Execute a aplicação localmente com o Spring Boot:

```
mvn spring-boot:run
```

### Produção
- Build da aplicação com Maven:
```
mvn clean install -DskipTests
```

- Execute o arquivo .jar gerado:
```
java -jar target/thefirst-0.0.1-SNAPSHOT.jar
```

<br>
<br>
