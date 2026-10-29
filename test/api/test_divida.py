def test_post_divida(client, divida_test):
    for indice, divida in enumerate(divida_test, start=1):
        response = client.post("/divida/", json=divida)

        assert response.status_code == 201
        assert response.json() == {"id": indice,
                                   "title": divida["title"],
                                   "valor": divida["valor"]}


def test_get_divida(client, divida_test):
    criadas = []
    for divida in divida_test:
        response = client.post("/divida/", json=divida)
        assert response.status_code == 201
        criadas.append(response.json())

    response = client.get("/divida/")

    assert response.status_code == 200
    assert response.json()["dividas"] == criadas
    assert response.json()["total"] == 120.0


def test_put_divida(client, divida_test):
    response_post = client.post("/divida/", json=divida_test[0])
    assert response_post.status_code == 201
    divida_id = response_post.json()["id"]

    updated_data = {"title": "Divida atualizada", "valor": 2000.0}
    response_put = client.put(f"/divida/{divida_id}", json=updated_data)

    assert response_put.status_code == 200
    assert response_put.json() == {"id": divida_id,
                                   "title": updated_data["title"],
                                   "valor": updated_data["valor"]}


def test_put_divida_not_found(client):
    updated_data = {"title": "Divida atualizada", "valor": 2000.0}
    response_put = client.put("/divida/999", json=updated_data)

    assert response_put.status_code == 404
    assert response_put.json() == {"detail": "Essa Divida com esse id: 999 não existe"}


def test_delete_divida(client, divida_test):
    response_post = client.post("/divida/", json=divida_test[0])
    assert response_post.status_code == 201
    divida_id = response_post.json()["id"]

    response_delete = client.delete(f"/divida/{divida_id}")
    assert response_delete.status_code == 204
    response_get = client.get("/divida/")
    assert response_get.status_code == 200
    assert response_get.json()["dividas"] == []
    assert response_get.json()["total"] == 0


def test_delete_divida_not_found(client):
    response_delete = client.delete("/divida/999")

    assert response_delete.status_code == 404
    assert response_delete.json() == {"detail": "Essa divida com esse id: 999 não existe"}


def test_get_saldo(client, salario_test, divida_test):
    for salario in salario_test:
        response_salario = client.post("/salario/", json=salario)
        assert response_salario.status_code == 201

    for divida in divida_test:
        response_divida = client.post("/divida/", json=divida)
        assert response_divida.status_code == 201

    response_saldo = client.get("/salario/saldo")
    total_salario = sum(salario["valor"] for salario in salario_test)
    total_divida = sum(divida["valor"] for divida in divida_test)
    saldo = total_salario - total_divida
    response_data = response_saldo.json()

    assert response_saldo.status_code == 200
    assert response_data["total_salario"] == total_salario
    assert response_data["total_divida"] == total_divida
    assert response_data["saldo"] == saldo
