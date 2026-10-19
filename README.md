# Contas — API + Frontend

Esse projeto foi desenvolvido para resolver um problema pessoal: organizar meu dinheiro. Eu até utilizava planilhas, mas, com o tempo, acabava me perdendo nas informações e esquecendo de atualizá-las.

Criar esse projeto me trouxe muita alegria, porque desenvolvi uma solução para uma dificuldade real do meu dia a dia. Agora, posso registrar minhas contas diretamente no sistema, separando-as por categorias ou cadastrando novas informações.

Atualmente, os dados ficam armazenados em memória, mas pretendo aprimorar o projeto com o tempo e, futuramente, talvez fazer o deploy. Por enquanto, estou aproveitando a aplicação localmente e aprendendo cada vez mais com ela.


- FRONT END desenvolvida com IA
-  BACKEND desenvolvido por mim


API em **FastAPI** para gerenciar dívidas e salários, com um frontend em
**HTML + CSS + JavaScript puro** (sem build) que consome a API.

```
contas/
├── app/                  # API FastAPI
│   ├── main.py           # cria o app, registra CORS e os routers
│   ├── models.py         # "banco de dados" em memória
│   ├── schemas.py        # modelos Pydantic
│   └── routers/
│       ├── divida.py     # /divida
│       └── salario.py    # /salario
├── frontend/             # Frontend que consome a API
│   ├── index.html
│   ├── styles.css
│   └── app.js
|
├──test
|   ├──api
|   |   ├──test_divida.py
|   |   ├──test_salario.py
|   |
|   ├─__init__.py
|   ├─conflest.py
|
├──.gitignore
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
source venv/bin/activate        # Windows: venv\Scripts\activate

# instalar as dependências
pip install -r requirements.txt

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

## Endpoints da API

| Método | Rota                       | Descrição                        |
| ------ | -------------------------- | -------------------------------- |
| GET    | `/divida/`                 | Lista dívidas e o total          |
| POST   | `/divida/`                 | Cria uma dívida                  |
| PUT    | `/divida/{id}`             | Atualiza uma dívida              |
| GET    | `/salario/`                | Lista salários e o total         |
| POST   | `/salario/`                | Cria um salário                  |
| GET    | `/salario/saldo`           | Retorna totais e o saldo         |
| PUT    | `/salario/{id}`            | Atualiza um salário              |

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

## Observações

- Os dados ficam **em memória** (`app/models.py`); ao reiniciar a API tudo
  volta ao estado inicial.
- O CORS está liberado para todas as origens em `app/main.py`
