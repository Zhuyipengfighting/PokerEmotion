from langchain_redis import RedisChatMessageHistory
import os

# 定义获取聊天历史记录的函数
def get_redis_history(session_id: str) -> RedisChatMessageHistory:
    return RedisChatMessageHistory(
        session_id=session_id,
        redis_url=os.getenv("REDIS_URL", "redis://localhost:6379/0"),
    )

# 创建聊天历史记录
session_id = "test"
history = get_redis_history(session_id)

# 添加用户消息
history.add_user_message("Hello, how are you?")

# 添加 AI 消息
history.add_ai_message("I'm fine, thank you! How about you?")

# 获取聊天历史记录
messages = history.messages
for message in messages:
    print(message)

# 清空聊天历史记录
history.clear()