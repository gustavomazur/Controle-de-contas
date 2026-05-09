# Controle de Contas

> Projeto em desenvolvimento 

Sistema para controle de gastos com o intuito de não usar aplicativo nem Excel.
Backend construído do zero com Spring Boot.

##  Tech Stack

- **Java 17** + **Spring Boot 3.5.13**
- Spring Web, Spring Data JPA, Spring Security
- MySQL + Docker Compose
- Lombok
- SpringDoc OpenAPI (Swagger)
- Futuramente: MapStruct, Frontend (IA via terminal)

##  Estrutura do Projeto

```
src/main/java/com/mazur/Controle_de_contas/
├── controller/
│   └── CartaoController.java      # Endpoints REST
├── dto/
│   └── CartaoDTO.java             # Record de entrada/saída
├── mapper/
│   └── CartaoMapper.java          # Manual (em migração para MapStruct)
├── model/
│   └── Cartao.java                # Entidade JPA (tb_cartao)
├── repository/
│   └── CartaoRepository.java      # JPA Repository
└── service/
    └── CartaoService.java         # Lógica de negócio
```

##  Endpoints Atuais

| Método | Rota            | Descrição                |
|--------|-----------------|--------------------------|
| POST   | `/cartao/create` | Cadastra um cartão      |
| GET    | `/cartao/{id}`   | Retorna cartão por ID   |

##  Decisões Técnicas

### 1. Mapper manual → MapStruct
Troca para MapStruct para reduzir boilerplate, evitar erros manuais de mapeamento e facilitar a manutenção em um produto grande.

### 2. Uso de `record` para DTOs
Uso mais simplificado e menos código que o normal — é uma maneira melhor para desenvolver.

### 3. Arquitetura em camadas
Segue o padrão **Controller → Service → Repository**, separando responsabilidades e facilitando manutenção e testes.

##  Como Rodar

```bash
docker compose up -d           # Sobe MySQL
```

Abrir Insomnia (ou similar) e testar com a URL da requisição.

##  Próximos Passos

- [ ] Migrar mapper manual para MapStruct
- [ ] Fazer CRUD completo do cartão
- [ ] Fazer CRUD completo do gasto
- [ ] Fazer CRUD completo de salário
- [ ] Fazer regra de negócio de soma de gastos
- [ ] Finalizar configurações do `application.properties`
- [ ] Ajustar construtor da entidade `Cartao` com Lombok
- [ ] Desenvolver frontend via IA integrada no terminal
- [ ] Publicar vídeo de demonstração no LinkedIn


## Em andamento: documentação dos passos que tomei no projeto.

## 👨‍💻 Desenvolvido por
**Gustavo Bueno Mazur**  
[LinkedIn](https://www.linkedin.com/in/gustavo-bueno-mazuhttps://www.linkedin.com/in/gustavo-bueno-mazur/)
