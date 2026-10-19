
def test_post_divida(client, test_post_divida):
    for indice, divida in enumerate(test_post_divida, start=1):
        response = client.post("/divida/", json=divida)

        assert response.status_code == 201
        assert response.json() == {"id": indice,
                                "title": divida["title"],
                                "valor": divida["valor"]}

def test_get_divida(client, test_post_divida):
    for indice, divida in enumerate(test_post_divida, start=1):
        response = client.post("/divida/", json=divida)

        assert response.status_code == 201
        assert response.json() == {"id": indice,
                                   "title": divida["title"],
                                   "valor": divida["valor"]}

def test_put_divida(client, test_post_divida):
    # Primeiro, adicionamos uma divida para atualizar
    response_post = client.post("/divida/", json=test_post_divida[0])
    assert response_post.status_code == 201
    divida_id = response_post.json()["id"]

    # Agora, atualizamos a divida
    updated_data = {"title": "Divida atulizada", "valor": 2000.0}
    response_put = client.put(f"/divida/{divida_id}", json=updated_data)

    assert response_put.status_code == 200
    assert response_put.json() == {"id": divida_id,
                                   "title": updated_data["title"],
                                   "valor": updated_data["valor"]}

def test_put_divida_not_found(client):
    updated_data = {"title": "Divida atulizada", "valor": 2000.0}
    response_put = client.put(f"/divida/999", json=updated_data)

    assert response_put.status_code == 404
    assert response_put.json() == {"detail": "Dívida não encontrada"}

def test_get_saldo(client, test_post_salario, test_post_divida):
    # Adiciona um salário
    for salario in test_post_salario:
        response_salario = client.post("/salario/", json=salario)
        assert response_salario.status_code == 201

        # Adiciona uma divida
    for divida in test_post_divida:
        response_divida = client.post("/divida/", json=divida)
        assert response_divida.status_code == 201

        # Agora, obtém o saldo
    response_saldo = client.get("/salario/saldo")
    total_salario = sum(salario["valor"] for salario in test_post_salario)
    total_divida = sum(divida["valor"] for divida in test_post_divida)
    saldo = total_salario - total_divida
    response_data = response_saldo.json()
    assert response_saldo.status_code == 200
    assert response_data["total_salario"] == total_salario
    assert response_data["total_divida"] == total_divida
    assert response_data["saldo"] == saldo
        