import grpc
from gRPC.pb import ai_pb2, ai_pb2_grpc


def run():
    channel = grpc.insecure_channel('localhost:50051')
    stub = ai_pb2_grpc.AiGuideServiceStub(channel)

    user_input = ai_pb2.UserInput(
        session_id="12345",
        input=[
            ai_pb2.EmotionData(date="2025-01-01", emotions={"happy": 0.7, "sad": 0.1, "neutral": 0.2}),
            ai_pb2.EmotionData(date="2025-01-02", emotions={"happy": 0.3, "sad": 0.5, "neutral": 0.2}),
        ]
    )

    response = stub.GetEmotionalReport(user_input)
    print(f"Report for session {response.session_id}: {response.report}")

if __name__ == "__main__":
    run()
