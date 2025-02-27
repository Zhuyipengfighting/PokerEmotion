import grpc
from concurrent import futures
import time
from humanfriendly.terminal import output
from pydantic import BaseModel
from typing import List, Dict, Union
from core.emotion_report import AiGuide


from gRPC.pb import ai_report_pb2, ai_report_pb2_grpc

class UserInput(BaseModel):
    input: List[Dict[str, Union[str, float]]]

class AIOutput(BaseModel):
    report: str

aiguide = AiGuide(streams=True)

# 实现 gRPC 服务
class EmotionAnalyticsServiceServicer(ai_report_pb2_grpc.EmotionAnalyticsServiceServicer):
    def GenerateReport(self, request, context):
        input_data = []
        for data in request.data:
            input_data.append({
                "date": data.date,
                "main_emotion": data.main_emotion,
                "main_class_ratio": data.main_class_ratio,
                "secondary_emotion": data.secondary_emotion,
                "secondary_class_ratio": data.secondary_class_ratio,
            })
        user_input = UserInput(input=input_data)
        response = aiguide.invoke_with_history(user_input)
        return ai_report_pb2.AIOutput(report=response.report)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    ai_report_pb2_grpc.add_EmotionAnalyticsServiceServicer_to_server(EmotionAnalyticsServiceServicer(), server)
    server.add_insecure_port('[::]:50050')
    server.start()
    print("gRPC 服务已启动，监听端口 50050")
    try:
        while True:
            time.sleep(86400)  # 保持服务运行
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    serve()