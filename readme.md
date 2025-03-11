# 🚀 Mercado API
![Java](https://img.shields.io/badge/Java-17-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-%234169E1) ![Maven](https://img.shields.io/badge/Maven-Build%20Tool-yellow)

API REST para gestão de pedidos e pagamentos, com autenticação segura, controle de estoque, processamento de pagamentos via Stripe e envio de e-mails para recuperação de senha. O projeto está estruturado seguindo boas práticas de arquitetura de software, garantindo escalabilidade, segurança e facilidade de manutenção.

## 📐 Tecnologias Utilizadas

- **Spring Boot** (Web, Security, Data JPA, Validation)
- **Spring Security** + **JWT** (Autenticação e Autorização)
- **Stripe Webhooks** (Integração com pagamento)
- **Banco de Dados:** PostgreSQL (Docker/Railway) e H2 para testes
- **Envio de e-mails** (SMTP, reset de senha)
- **Docker + Railway** (Deploy em produção)

## 🚀 Deploy Automatizado com CI/CD

O deploy da API foi automatizado utilizando uma esteira de CI/CD, garantindo que cada nova versão seja testada e enviada para produção sem complicações. O pipeline inclui:

Testes antes do deploy, garantindo a estabilidade do código.
Build e deploy automáticos via GitHub CI.
Infraestrutura em Docker para facilitar a replicação do ambiente.
Hospedagem no Railway, com integração contínua para atualizar a aplicação automaticamente.
Isso permite que novas funcionalidades sejam lançadas rapidamente, mantendo a confiabilidade e escalabilidade do sistema. 🚀

A API está hospedada no Railway:

> **URL base:** [https://mercadoapi-production.up.railway.app/api](https://mercadoapi-production.up.railway.app/api)

## ⚙️ Arquitetura e Organização do Projeto

A aplicação segue uma estrutura modular, separando responsabilidades de forma clara:

```
📦 mercadoapi
 ┣ 📂 config        # Configurações gerais (CORS, segurança futura)
 ┣ 📂 controllers   # Endpoints da API
 ┣ 📂 dto           # Data Transfer Objects
 ┃ ┣ request
 ┃ ┣ response
 ┣ 📂 entities      # Modelos do banco de dados
 ┃ ┣ enums
 ┣ 📂 repositories  # Interfaces de acesso ao banco
 ┣ 📂 exception     # Exceções personalizadas e centralizadas (GlobalExceptionHandler)
 ┣ 📂 services      # Regras de negócio
 ┣ 📜 application.properties  # Configurações do ambiente
```

- **controllers**: Responsáveis por expor os endpoints da API.
- **services**: Implementam a lógica de negócios da aplicação.
- **repositories**: Interfaces que comunicam com o banco de dados.
- **exception**: Contém o `GlobalExceptionHandler`, garantindo um tratamento de erros padronizado e amigável.

## 🛠 Funcionalidades Implementadas

### 🔐 Autenticação e Segurança
- Login e registro de usuários (cliente e admin).
- Tokens JWT para autenticação segura.
- Reset de senha via e-mail.

### 📋 Gestão de Pedidos e Pagamentos
- Criar e gerenciar pedidos.
- Checkout com reserva de estoque.
- Pagamento via Stripe com Webhook.
- Reembolso automático em caso de cancelamento.
- Atualização automática de status do pedido.

### 📦 Controle de Estoque
- Reserva de produtos ao adicionar no carrinho.
- Baixa no estoque apenas após pagamento confirmado.
- Reposição automática em cancelamentos.

## 🔗 Endpoints Principais

### 🔐 Autenticação
- `POST /api/auth/register` → Registro de usuário
- `POST /api/auth/login` → Autenticação e geração de token JWT
- `POST /api/auth/forgot-password` → Envio de e-mail para redefinição de senha
- `POST /api/auth/reset-password` → Reset de senha com token recebido por e-mail

### 📦 Gerenciamento de Pedidos
- `POST /api/orders` → Criar um novo pedido (carrinho)
- `POST /api/orders/{orderId}/items` → Adicionar itens ao pedido
- `PUT /api/orders/{orderId}/checkout` → Finalizar o pedido (checkout)
- `PATCH /api/orders/{orderId}/status` → Atualizar status do pedido (admin)
- `PUT /api/orders/{orderId}/cancel` → Cancelar pedido
- `GET /api/orders/{orderId}` → Buscar detalhes de um pedido específico
- `GET /api/orders/my-orders` → Listar pedidos do usuário logado
- `GET /api/orders/tracking/{trackingCode}` → Buscar pedido por código de rastreio
- `GET /api/orders` → Listar todos os pedidos (admin)

### 💳 Pagamentos
- `POST /api/payments/create?orderId={orderId}&currency={currency}` → Criar pagamento para um pedido
- `POST /api/webhooks` → Webhook do Stripe para capturar pagamentos e reembolsos

### 📊 Controle de Estoque
- `GET /api/products` → Listar produtos
- `GET /api/products/{productId}` → Buscar detalhes de um produto específico
- `POST /api/products` → Criar novo produto (admin)
- `PUT /api/products/{productId}` → Atualizar produto (admin)
- `DELETE /api/products/{productId}` → Remover produto (admin)

# 📘 Documentação do Projeto

## 🛠 Tratamento de Exceções

O sistema implementa um tratamento de exceções centralizado utilizando a classe `GlobalExceptionHandler` com `@ControllerAdvice`. Isso garante respostas padronizadas e estruturadas para diferentes tipos de erro, melhorando a experiência do usuário e facilitando a depuração.

### 📌 Principais Exceções Tratadas:

| Exceção                      | Código | Descrição |
|------------------------------|--------|-----------|
| `ResourceNotFoundException`  | 404    | Recurso não encontrado. |
| `BusinessException`          | 400    | Erro de regra de negócio (ex.: requisição inválida). |
| `UnauthorizedException`      | 401    | Falha de autenticação (ex.: JWT inválido). |
| `ForbiddenException`         | 403    | Acesso negado (usuário sem permissão). |
| `OrderStatusException`       | 409    | Tentativa de alteração de status inválida. |
| `StockException`             | 422    | Falha relacionada ao estoque (ex.: sem produtos disponíveis). |
| `PaymentException`           | 500    | Erro interno no processamento de pagamento. |
| `MethodArgumentNotValidException` | 400 | Erros de validação em requisições (ex.: campos inválidos). |
| `Exception`                  | 500    | Erro genérico inesperado. |

### 🔧 Exemplo de Resposta Padrão:

```json
{
  "timestamp": "2025-03-11T14:35:00.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found",
  "errorCode": "PRD-404",
  "path": "/api/products/99"
}
```

Isso melhora a experiência do usuário e facilita a depuração.

## 🚀 Como Rodar o Projeto Localmente

### Pré-requisitos

- Docker (Para PostgreSQL e PGAdmin)
- Java 17+
- Maven

### Passos

1. Clone o repositório:
   ```sh
   git clone https://github.com/seu-usuario/mercadoapi.git
   cd mercadoapi
   ```

2. Suba o banco de dados com Docker:
   ```sh
   docker-compose up -d
   ```

3. Configure as variáveis de ambiente (`application.properties`).

4. Rode a aplicação:
   ```sh
   mvn spring-boot:run
   ```

A API estará disponível em `http://localhost:8080/api`

## 👨‍💻 Autor

Desenvolvido por Ruan Pablo (https://github.com/RuanPablo2). Feedbacks e contribuições são bem-vindos!