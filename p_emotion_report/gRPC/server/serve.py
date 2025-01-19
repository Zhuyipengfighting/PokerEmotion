import grpc
from concurrent import futures
from core.emotion_report import AiGuide
from gRPC.pb import ai_pb2_grpc, ai_pb2


class AiGuideServicer(ai_pb2_grpc.AiGuideServiceServicer):
    def __init__(self):
        self.ai_guide = AiGuide()

    def GetEmotionalReport(self, request, context):
        # Use the AiGuide class to process the input and generate the report
        user_input = request
        response = self.ai_guide.invoke_with_history(user_input)
        return ai_pb2.AIOutput(session_id=response.session_id, report=response.report)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    ai_pb2_grpc.add_AiGuideServiceServicer_to_server(AiGuideServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Server started on port 50051...")
    server.wait_for_termination()

if __name__ == "__main__":
    serve()
