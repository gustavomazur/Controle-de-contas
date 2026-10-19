from pydantic import BaseModel
from typing import Optional



class DividaCreate(BaseModel):
    title: str
    valor: float
    
class DividaResponse(BaseModel):
    id: int
    title: str
    valor: float

    class Config:
        from_attributes = True

class DividaListResponse(BaseModel):
    dividas: list[DividaResponse]
    total: float

class TaskAtulizacao(BaseModel):
    title: Optional[str] = None
    valor: float

class SalarioCreate(BaseModel):
    title: str
    valor: float

class SalarioResponse(BaseModel):
    id: int
    title: str
    valor: float

    class Config:
        from_attributes = True

class SalarioListResponse(BaseModel):
    salario: list[SalarioResponse]
    total: float

class SaldoResponse(BaseModel):
    total_salario: float
    total_divida: float
    saldo: float
    