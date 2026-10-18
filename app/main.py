
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers import divida, salario



app = FastAPI(title="API Contas")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
    expose_headers=["X-Total-Dividas", "X-Total-Salario"],
)

app.include_router(divida.router)
app.include_router(salario.router)