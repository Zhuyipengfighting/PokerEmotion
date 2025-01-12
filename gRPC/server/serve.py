import grpc
from concurrent import futures
from core.emotion_2 import AiGuide, UserInput
from gRPC.pb import ai_pb2, ai_pb2_grpc


class AiGuideService(ai_pb2_grpc.AiGuideServiceServicer):
    def __init__(self):
        self.aiguide = AiGuide()
    def ChatStream(self, request_iterator, context):
        for request in request_iterator:
            user_input = UserInput(
                session_id=request.session_id,
                input=request.input,
                emotion=request.emotion
            )
            try:
                output = self.aiguide.invoke_with_history(user_input)
                yield ai_pb2.AIResponse(output=output)
            except Exception as e:
                context.set_details(str(e))
                context.set_code(grpc.StatusCode.INTERNAL)
                yield ai_pb2.AIResponse(output="")

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    ai_pb2_grpc.add_AiGuideServiceServicer_to_server(AiGuideService(), server)
    server.add_insecure_port('[::]:50051')
    print("Server started on port 50051")
    server.start()
    server.wait_for_termination()

if __name__ == "__main__":
    serve()
