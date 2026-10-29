from fastapi.testclient import TestClient
import pytest
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from app.main import app
from app.database import Base, get_db
from app import models


# ---------------------------------------------------------------------------
# FIXTURES COMPARTILHADAS
#
# Os testes NÃO usam o Postgres real. Cada teste roda em um banco SQLite
# em memória, criado e destruído automaticamente. Assim os testes são
# rápidos, isolados e não sujam os dados de verdade.
# ---------------------------------------------------------------------------


@pytest.fixture(scope="function")
def db_session():
    """Banco de teste: um SQLite em memória, novo para cada teste.

    - create_all: cria as tabelas de Divida e Salario no início do teste.
    - yield db: entrega a sessão para o teste (e para o client).
    - drop_all: apaga as tabelas no final, deixando o próximo teste limpo.
    """
    engine = create_engine(
        # "sqlite://" = banco SQLite em memória (não cria arquivo; some no final)
        # Permite usar a conexão em outra thread (o TestClient roda em outra thread)
        # StaticPool = uma única conexão reaproveitada, para todos verem o mesmo banco
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(bind=engine)
    TestingSession = sessionmaker(bind=engine, autoflush=False, autocommit=False)
    db = TestingSession()
    try:
        yield db
    finally:
        db.close()
        Base.metadata.drop_all(bind=engine)


@pytest.fixture(scope="function")
def client(db_session):
    """Cliente HTTP de teste que fala com a API usando o banco de teste.

    O FastAPI normalmente usa get_db (Postgres). Aqui trocamos o get_db
    pelo override_get_db, que entrega a sessão SQLite (db_session).
    No final, limpamos o override para não afetar os próximos testes.
    """

    # Função que substitui o get_db real durante os testes
    def override_get_db():
        yield db_session

    # Diz ao FastAPI para usar a sessão de teste no lugar do Postgres
    app.dependency_overrides[get_db] = override_get_db

    # Sobe o app e entrega o client para o teste fazer as requisições
    with TestClient(app) as c:
        yield c

    # Remove a substituição para o próximo teste começar do zero
    app.dependency_overrides.clear()


# SALARIO
@pytest.fixture
def salario_test():
    """Dois salários de exemplo para os testes (total: 2500.0)."""
    return [{"title": "Salário principal", "valor": 2000.0},
            {"title": "Renda extra", "valor": 500.0}]


# DIVIDA
@pytest.fixture
def divida_test():
    """Duas dívidas de exemplo para os testes (total: 120.0)."""
    return [{"title": "Tim", "valor": 60.0},
            {"title": "Netflix", "valor": 60.0}]
