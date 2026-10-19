from fastapi import APIRouter, Depends, HTTPException, status, Response
from typing import List
from .. import models, schemas


router = APIRouter(
    prefix="/salario",
    tags=["Salario"]
)


@router.post("/", status_code=status.HTTP_201_CREATED, response_model=schemas.SalarioResponse)
def create_salario(salario: schemas.SalarioCreate):
    
    new_salario = salario.model_dump()
    new_salario["id"] = max(
        (salario["id"] for salario in models.my_db_salario),
        default=0
    ) + 1

    models.my_db_salario.append(new_salario)
    return new_salario

@router.get("/", status_code=status.HTTP_200_OK, response_model=schemas.SalarioListResponse)
def get_salario(response: Response):

    lista_salario = models.my_db_salario

    total_somado = sum(salario["valor"]
                    for salario in lista_salario)

    response.headers["X-Total-Salario"] = str(total_somado)

    return {
        "salario": lista_salario,
        "total": total_somado
    }


@router.get("/saldo", status_code=status.HTTP_200_OK, response_model=schemas.SaldoResponse)
def get_saldo(response: Response):

    total_salario = sum(salario["valor"] for salario in models.my_db_salario)
    total_divida = sum(divida["valor"] for divida in models.my_db_divida)
    saldo = total_salario - total_divida
    return {"total_salario": total_salario,
            "total_divida": total_divida,
            "saldo": saldo
    }


@router.put("/{salario_id}", status_code=status.HTTP_200_OK, response_model=schemas.SalarioResponse)
def update_salario(salario_id: int, salario: schemas.SalarioCreate):
    for index, existing_salario in enumerate(models.my_db_salario):
        if existing_salario["id"] == salario_id:
            updated_salario = salario.model_dump()
            updated_salario["id"] = salario_id
            models.my_db_salario[index] = updated_salario
            return updated_salario

    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Salário não encontrado")




