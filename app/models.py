from sqlalchemy import Column, Integer, String, Boolean, ForeignKey, DECIMAL
from sqlalchemy.orm import relationship
from sqlalchemy.sql.expression import text
from sqlalchemy.sql.sqltypes import TIMESTAMP
from .database import Base

class Divida(Base):
    __tablename__="divida"

    id = Column(Integer, primary_key=True, nullable=False)
    title = Column(String, nullable=False)
    valor = Column(DECIMAL,nullable=False)
class Salario(Base):
    __tablename__="salario"

    id = Column(Integer, primary_key=True, nullable=False)
    title = Column(String, nullable=False)
    valor = Column(DECIMAL, nullable=False)    