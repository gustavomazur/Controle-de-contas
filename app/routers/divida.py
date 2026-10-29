from fastapi import APIRouter, Depends, HTTPException, status, Response
from .. import models, schemas
from ..database import get_db
from sqlalchemy.orm import Session


router = APIRouter(
    prefix="/divida",
    tags=["Divida"]
)

@router.post("/", status_code=status.HTTP_201_CREATED, response_model=schemas.DividaResponse)
def create_divida(divida: schemas.DividaCreate, db: Session = Depends(get_db)):
    new_divida = models.Divida(**divida.model_dump())
    db.add(new_divida)
    db.commit()
    db.refresh(new_divida)
    return new_divida

@router.get("/", status_code=status.HTTP_200_OK, response_model=schemas.DividaListResponse)
def get_dividas(response: Response, db: Session = Depends(get_db)):

    lista_dividas = db.query(models.Divida).all()
    total_somado = sum(divida.valor for divida in lista_dividas)
    response.headers["x-Total-Divida"] = str(total_somado)

    return {"dividas": lista_dividas,
            "total": total_somado}

@router.put("/{divida_id}", status_code=status.HTTP_200_OK, response_model=schemas.DividaResponse)
def update_divida(divida_id: int, divida: schemas.DividaCreate, db: Session = Depends(get_db)):
    db_divida = db.query(models.Divida).filter(models.Divida.id == divida_id).first()
    if db_divida == None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail=f"Essa Divida com esse id: {divida_id} não existe")

    db_divida.title = divida.title
    db_divida.valor = divida.valor
    db.commit()
    db.refresh(db_divida)
    return db_divida

@router.delete("/{divida_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_divida(divida_id: int, db: Session = Depends(get_db)):
    db_divida = db.query(models.Divida).filter(models.Divida.id == divida_id).first()
    if db_divida == None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail=f"Essa divida com esse id: {divida_id} não existe")

    db.delete(db_divida)
    db.commit()
    return None





