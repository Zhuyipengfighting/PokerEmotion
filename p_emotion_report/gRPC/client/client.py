import grpc
from gRPC.pb import ai_pb2, ai_pb2_grpc

def run():
    # 连接到 gRPC 服务
    channel = grpc.insecure_channel('localhost:50051')
    stub = ai_pb2_grpc.AiGuideServiceStub(channel)
    emotion_data_1 = ai_pb2.EmotionData(
        date="2025-01-01T09:30:00",
        main_emotion="happy",
        main_class_ratio=0.7,
        secondary_emotion="sad",
        secondary_class_ratio=0.1
    )
    emotion_data_2 = ai_pb2.EmotionData(
        date="2025-01-01T09:50:00",
        main_emotion="neutral",
        main_class_ratio=0.5,
        secondary_emotion="happy",
        secondary_class_ratio=0.3
    )
    request = ai_pb2.UserInput(data=[emotion_data_1, emotion_data_2])
    response = stub.GenerateReport(request)
    print("AI Report:", response.report)

if __name__ == '__main__':
    run()
