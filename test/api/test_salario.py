

def test_get_salario(client, test_get_salario):
    response = client.get("/salario/")

    assert response.status_code == 200
    assert response.json()["salario"] == test_get_salario
    assert response.json()["total"] == 2500.0

def test_post_salario(client, test_post_salario):
    for indice, salario in enumerate(test_post_salario, start=1):
        response = client.post("/salario/", json=salario)

        assert response.status_code == 201
        assert response.json() == {"id": indice,
                                "title": salario["title"],
                                "valor": salario["valor"]}


def test_put_salario(client, test_post_salario):
    # Primeiro, adicionamos um salário para atualizar
    response_post = client.post("/salario/", json=test_post_salario[0])
    assert response_post.status_code == 201
    salario_id = response_post.json()["id"]

    # Agora, atualizamos o salário
    updated_data = {"title": "Salário atualizado", "valor": 3000.0}
    response_put = client.put(f"/salario/{salario_id}", json=updated_data)

    assert response_put.status_code == 200
    assert response_put.json() == {"id": salario_id,
                                    "title": updated_data["title"],
                                    "valor": updated_data["valor"]}

def test_put_salario_not_found(client):
    updated_data = {"title": "Salário atulizado", "valor": 3000.0}
    response_put = client.put(f"/salario/999", json=updated_data)
    assert response_put.status_code == 404
    assert response_put.json() == {"detail": "Salário não encontrado"}
    
