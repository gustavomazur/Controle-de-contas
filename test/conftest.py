from fastapi.testclient import TestClient
import pytest
from app.main import app
from app import models

#test pra quanto eu precisar de um client para fazer requisições para a API
@pytest.fixture(scope="function")
def client():
    with TestClient(app) as c:
        yield c

#SALARIO
@pytest.fixture
def test_get_salario(monkeypatch):
    salarios = [{"id": 1, "title": "Salário principal", "valor": 2000.0},
                {"id": 2, "title": "Renda extra", "valor": 500.0},]
    monkeypatch.setattr(models, "my_db_salario", salarios)
    return salarios

@pytest.fixture
def test_post_salario(monkeypatch):
    salario =  [{"title": "Salário principal", "valor": 2000.0},
                {"title": "Renda extra", "valor": 500.0}]
    monkeypatch.setattr(models, "my_db_salario", [])
    return salario

#DIVIDA
@pytest.fixture
def test_post_divida(monkeypatch):
    divida =  [{"title": "Salário principal", "valor": 2000.0},
               {"title": "Renda extra", "valor": 500.0}]
    monkeypatch.setattr(models, "my_db_divida", [])
    return divida

@pytest.fixture
def test_get_divida(monkeypatch):
    divida = [
        {"id": 1, "title": "Tim", "valor": 60.0},
        {"id": 2, "title": "Netflix", "valor": 60.0},
    ]

    monkeypatch.setattr(models, "my_db_divida", divida)
    return divida
