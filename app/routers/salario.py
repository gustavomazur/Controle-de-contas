from fastapi import APIRouter, Depends, HTTPException, status, Response
from .. import models, schemas
from ..database import get_db
from sqlalchemy.orm import Session


router = APIRouter(
    prefix="/salario",
    tags=["Salario"]
)


@router.post("/", status_code=status.HTTP_201_CREATED, response_model=schemas.SalarioResponse)
def create_salario(salario: schemas.SalarioCreate, db: Session = Depends(get_db)):
    new_salario = models.Salario(**salario.model_dump())
    db.add(new_salario)
    db.commit()
    db.refresh(new_salario)
    return new_salario
        
@router.get("/", status_code=status.HTTP_200_OK, response_model=schemas.SalarioListResponse)
def get_salario(response: Response, db: Session = Depends(get_db)):

                lista_salario = db.query(models.Salario).all()
                total_somado = sum(salario.valor for salario in lista_salario)
                response.headers["x-Total-Salario"] = str(total_somado)

                return{"salario": lista_salario,
                       "total": total_somado}

@router.get("/saldo", status_code=status.HTTP_200_OK, response_model=schemas.SaldoResponse)
def get_saldo(response: Response, db: Session = Depends(get_db)):

    lista_salario = db.query(models.Salario).all()
    lista_divida = db.query(models.Divida).all()
    total_salario = sum(salario.valor for salario in lista_salario)
    total_divida = sum(divida.valor for divida in lista_divida)
    saldo = total_salario - total_divida
    return {"total_salario": total_salario,
            "total_divida": total_divida,
            "saldo": saldo
    }

@router.put("/{salario_id}", status_code=status.HTTP_200_OK, response_model=schemas.SalarioResponse)
def update_salario(salario_id: int, salario: schemas.SalarioCreate, db: Session = Depends(get_db)):
    db_salario = db.query(models.Salario).filter(models.Salario.id == salario_id).first()
    if db_salario == None:
          raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                              detail=f"Esse Salario com esse id: {salario_id} não existe")


    db_salario.title = salario.title
    db_salario.valor = salario.valor
    db.commit()
    db.refresh(db_salario)
    return db_salario

@router.delete("/{salario_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_salario(salario_id: int, db: Session = Depends(get_db)):
       db_salario = db.query(models.Salario).filter(models.Salario.id == salario_id).first()
       if db_salario == None:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                                detail=f"Esse Salario com esse id: {salario_id} não existe")

       db.delete(db_salario)
       db.commit()
       return None



