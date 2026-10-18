from fastapi import APIRouter, Depends, HTTPException, status, Response
from typing import List
from .. import models, schemas


router = APIRouter(
    prefix="/divida",
    tags=["Divida"]
)


@router.post("/", status_code=status.HTTP_201_CREATED, response_model=schemas.DividaResponse)
def create_divida(divida: schemas.DividaCreate):
    
    new_divida = divida.model_dump()
    new_divida["id"] = max(
        (divida["id"] for divida in models.my_db_divida),
        default=0
    ) + 1

    models.my_db_divida.append(new_divida)
    return new_divida

@router.get("/", status_code=status.HTTP_200_OK, response_model=schemas.DividaListResponse)
def get_dividas(response: Response):

    lista_dividas = models.my_db_divida

    total_somado = sum(divida["valor"] for divida in lista_dividas)

    response.headers["X-Total-Dividas"] = str(total_somado)

    return {
        "dividas": lista_dividas,
        "total": total_somado
    }


@router.put("/{divida_id}", status_code=status.HTTP_200_OK, response_model=schemas.DividaResponse)
def update_divida(divida_id: int, divida: schemas.DividaCreate):
    for index, existing_divida in enumerate(models.my_db_divida):
        if existing_divida["id"] == divida_id:
            updated_divida = divida.model_dump()
            updated_divida["id"] = divida_id
            models.my_db_divida[index] = updated_divida
            return updated_divida

    raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Dívida não encontrada")






