def test_post_salario(client, salario_test):
    for indice, salario in enumerate(salario_test, start=1):
        response = client.post("/salario/", json=salario)

        assert response.status_code == 201
        assert response.json() == {"id": indice,
                                   "title": salario["title"],
                                   "valor": salario["valor"]}


def test_get_salario(client, salario_test):
    criados = []
    for salario in salario_test:
        response = client.post("/salario/", json=salario)
        assert response.status_code == 201
        criados.append(response.json())

    response = client.get("/salario/")

    assert response.status_code == 200
    assert response.json()["salario"] == criados
    assert response.json()["total"] == 2500.0


def test_put_salario(client, salario_test):
    response_post = client.post("/salario/", json=salario_test[0])
    assert response_post.status_code == 201
    salario_id = response_post.json()["id"]
    updated_data = {"title": "Salário atualizado", "valor": 3000.0}
    response_put = client.put(f"/salario/{salario_id}", json=updated_data)

    assert response_put.status_code == 200
    assert response_put.json() == {"id": salario_id,
                                   "title": updated_data["title"],
                                   "valor": updated_data["valor"]}


def test_put_salario_not_found(client):
    updated_data = {"title": "Salário atualizado", "valor": 3000.0}
    response_put = client.put("/salario/999", json=updated_data)

    assert response_put.status_code == 404
    assert response_put.json() == {"detail": "Esse Salario com esse id: 999 não existe"}


def test_delete_salario(client, salario_test):
    response_post = client.post("/salario/", json=salario_test[0])
    assert response_post.status_code == 201
    salario_id = response_post.json()["id"]

    response_delete = client.delete(f"/salario/{salario_id}")
    assert response_delete.status_code == 204
    response_get = client.get("/salario/")
    assert response_get.status_code == 200
    assert response_get.json()["salario"] == []
    assert response_get.json()["total"] == 0


def test_delete_salario_not_found(client):
    response_delete = client.delete("/salario/999")

    assert response_delete.status_code == 404
    assert response_delete.json() == {"detail": "Esse Salario com esse id: 999 não existe"}
