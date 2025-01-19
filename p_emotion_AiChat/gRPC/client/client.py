import grpc
from gRPC.pb import ai_pb2, ai_pb2_grpc
def chat_stream():
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = ai_pb2_grpc.AiGuideServiceStub(channel)
        inputs = [
            ai_pb2.UserInput(session_id="1", input="你好", emotion="happy"),
            ai_pb2.UserInput(session_id="1", input="我感到有点低落", emotion="sad"),
            ai_pb2.UserInput(session_id="1", input="今天的天气怎么样？", emotion="neutral")
        ]
        responses = stub.ChatStream(iter(inputs))
        for response in responses:
            print(f"AI Response: {response.output}")
if __name__ == "__main__":
    chat_stream()
