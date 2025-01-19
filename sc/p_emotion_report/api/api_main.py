from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse, JSONResponse
from pydantic import BaseModel
from typing import Optional
from core.emotion import AiGuide, UserInput
import asyncio

app = FastAPI()


aiguide = AiGuide(streams=True)


class InputData(BaseModel):
    session_id: str
    input: str


@app.post("/get_response/")
async def get_response(data: InputData):

    try:
        response = aiguide.invoke_with_history(data)
        return JSONResponse(content={"response": response})
    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)


@app.post("/stream_response/")
async def stream_response(data: InputData):

    try:
        async def generate():
            for char in aiguide.invoke_with_history(data):
                await asyncio.sleep(0.05)
                yield char

        return StreamingResponse(generate(), media_type="text/plain")
    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)

# 测试路由
@app.get("/")
async def root():
    return {"message": "欢迎来到广东海洋大学阳江校区计算机科学与工程学院，我是小海，你可以我学院的相关问题."}

