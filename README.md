# Contas — API + Frontend

Esse projeto foi desenvolvido para resolver um problema pessoal: organizar meu dinheiro. Eu até utilizava planilhas, mas, com o tempo, acabava me perdendo nas informações e esquecendo de atualizá-las.

Criar esse projeto me trouxe muita alegria, porque desenvolvi uma solução para uma dificuldade real do meu dia a dia. Agora, posso registrar minhas contas diretamente no sistema, separando-as por categorias ou cadastrando novas informações.

Atualmente, os dados ficam armazenados em **PostgreSQL**, mas pretendo aprimorar o projeto com o tempo e, futuramente, talvez fazer o deploy. Por enquanto, estou aproveitando a aplicação localmente e aprendendo cada vez mais com ela.


- FRONT END desenvolvida com IA
-  BACKEND desenvolvido por mim


API em **FastAPI** para gerenciar dívidas e salários, com um frontend em
**HTML + CSS + JavaScript puro** (sem build) que consome a API.

```
contas/
├── app/                  # API FastAPI
│   ├── main.py           # cria o app, registra CORS e os routers
│   ├── config.py         # carrega as variáveis do .env
│   ├── database.py       # conexão com o PostgreSQL (SQLAlchemy)
│   ├── models.py         # tabelas Divida e Salario
│   ├── schemas.py        # modelos Pydantic
│   └── routers/
│       ├── divida.py     # /divida
│       └── salario.py    # /salario
├── frontend/             # Frontend que consome a API
│   ├── index.html
│   ├── styles.css
│   └── app.js
│
├── test/                 # Testes automatizados (pytest)
│   ├── api/
│   │   ├── test_divida.py
│   │   └── test_salario.py
│   ├── __init__.py
│   └── conftest.py       # fixtures (client e banco SQLite em memória)
│
├── .env.example          # exemplo das variáveis de ambiente
├── .gitignore
├── requirements.txt
└── README.md
```

## Pré-requisitos

- Python 3.10+

## 1. Rodar a API

A partir da pasta `contas`:

```bash
# recomendado criar e ativar o ambiente virtual
python3 -m venv venv
source venv/bin/activate       
# Windows:
venv\Scripts\activate

# para desativar o venv
deactivate

# instalar as dependências
pip install -r requirements.txt
# para conferir se instalou tudo
pip list

# criar o .env com as credenciais do PostgreSQL
cp .env.example para .env
# edite o .env com host, porta, usuário, senha e nome do banco

# subir o servidor
python -m uvicorn app.main:app --reload
```

A API fica disponível em `http://localhost:8000`.

- Documentação interativa (Swagger): http://localhost:8000/docs
- Documentação alternativa (ReDoc): http://localhost:8000/redoc

## 2. Rodar o Frontend

```bash
# ↓ entre na pasta `frontend`
cd frontend
#rode o comanando ↓ 
python3 -m http.server 5500
```

Depois abra no navegador: **http://localhost:5500**


O frontend aponta automaticamente para a API em `http://localhost:8000`.
Se você subir a API em outro endereço, altere a constante `DEFAULT_API`
no início de `frontend/app.js`.

## 3. Rodar os testes

A partir da pasta `contas`, com o ambiente virtual ativado:

```bash
source venv/bin/activate        
# Windows:
venv\Scripts\activate

# instalar as dependências (inclui pytest e httpx)
pip install -r requirements.txt

# rodar todos os testes
python -m pytest

# rodar mostrando o nome de cada teste
python -m pytest -v
```

Os testes usam um **SQLite em memória** (veja `test/conftest.py`), então
**não precisam do PostgreSQL rodando** e não alteram os dados reais.
Ainda assim, o arquivo `.env` precisa existir para a API ser importada;
se você já seguiu o passo 1, ele já está criado.

Para rodar um arquivo específico:

```bash
python -m pytest test/api/test_salario.py -v
```

## Endpoints da API

| Método | Rota                       | Descrição                        |
| ------ | -------------------------- | -------------------------------- |
| GET    | `/divida/`                 | Lista dívidas e o total          |
| POST   | `/divida/`                 | Cria uma dívida                  |
| PUT    | `/divida/{id}`             | Atualiza uma dívida              |
| DELETE | `/divida/{id}`             | Exclui uma dívida                |
| GET    | `/salario/`                | Lista salários e o total         |
| POST   | `/salario/`                | Cria um salário                  |
| GET    | `/salario/saldo`           | Retorna totais e o saldo         |
| PUT    | `/salario/{id}`            | Atualiza um salário              |
| DELETE | `/salario/{id}`            | Exclui um salário                |

## O que o frontend faz

- Tela inicial de boas-vindas com atalhos.
- **Menu sanduíche** (☰) no topo (à direita) para navegar entre as páginas:
  **Início**, **Saldo**, **Dívidas** e **Salários**.
- Botão de **tema claro/escuro** no topo; a preferência fica salva no navegador.
- Página **Saldo**: total de salários, total de dívidas e o saldo.
- Páginas **Dívidas** e **Salários**: formulário para adicionar e uma
  tabela com **#**, **Título** e **Valor** bem visíveis.
- **Edição inline**: clique em *Editar*, altere os campos na própria linha
  e confirme em *Salvar* (ou `Enter`); *Cancelar*/`Esc` descarta.
- **Exclusão**: clique em *Excluir* na linha; uma confirmação é exibida
  antes de remover a dívida/salário e atualizar o saldo.

## Observações

- Os dados ficam no **PostgreSQL** configurado no `.env`.
- Os testes usam **SQLite em memória** e não afetam o banco real.
- O CORS está liberado para todas as origens em `app/main.py`
