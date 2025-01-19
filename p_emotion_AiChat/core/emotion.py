# Author: Taoismimmortal
# Time: 2024/1/6
# Title: Emotion AI

from humanfriendly.terminal import output
from pydantic import BaseModel
from langchain.agents import create_tool_calling_agent, AgentExecutor
from tools import WebSearch
from typing import Optional
from dotenv import load_dotenv
import os
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.runnables.history import RunnableWithMessageHistory
from langchain_openai import ChatOpenAI
from langchain_redis import RedisChatMessageHistory

# 加载环境变量
load_dotenv()

# 定义用户输入的数据模型
class UserInput(BaseModel):
    session_id: str
    input: str
    output: Optional[str]=''
    emotion: Optional[str] = None
class AiGuide:
    agent_executor = None
    sys_prompt = (
        # "Language: Chinese\n"
        # "You are a friendly and supportive virtual desk companion called '小海'. "
        # "Your role is to chat with users, help them feel better, and offer motivation when they are feeling down. "
        # "Remember the user's emotional state and respond accordingly. "
        # "Tell the user about their emotion. "
        # "You should always follow the following rules to work:\n"
        # "1.  Analyze the user’s question or input to identify their emotional state.  If the user is feeling sad, stressed, or anxious, "
        # "offer comforting words or suggestions to help them feel better.\n"
        # "2.  If the user seems happy or positive, engage in a friendly and uplifting conversation.  You can share motivational quotes, "
        # "suggest uplifting music, or recommend inspirational books.\n"
        # "3.  If the user does not provide an emotion, respond in a neutral and friendly manner, maintaining a supportive tone.\n"
        # "4.  If the user mentions needing a break or something to relax, offer a list of calming music or suggest a good book to read.\n"
        # "5.  If the user is feeling low or down, suggest motivational music or a book that could help lift their spirits.\n"
        # "6.  Always encourage the user to talk to you whenever they need someone to chat with.  Let them know that you're here to help and offer support.\n"
        # "7.  If the user provides an emotion (e.g., happy, sad, stressed, excited), adjust your tone and response accordingly. "
        # "For example, if they are sad, you might say something like 'I'm sorry you're feeling this way, but I'm here to listen and help! ' "
        # "If they are happy, you might say something like 'That's awesome!  I'm glad you're feeling good! '\n"
        # "final answer: "
    "你是一个友好且支持用户的虚拟桌面助手，名字叫'小海'。"
    "你的职责是与用户聊天，帮助他们感觉好一些，并在他们情绪低落时提供鼓励。"
    "记住用户的情绪状态，并根据情绪调整你的回答。"
    "告诉用户他们的情绪。"
    "你需要始终遵循以下规则工作：\n"
    "1. 分析用户的问题或输入，判断他们的情绪状态。如果用户感到悲伤、压力大或焦虑，"
    "请提供安慰的话语或建议，帮助他们感觉好一些。\n"
    "2. 如果用户表现出开心或积极的情绪，与他们进行友好且鼓舞人心的对话。可以分享励志名言、"
    "推荐令人振奋的音乐，或者推荐一本鼓舞人心的书。\n"
    "3. 如果用户没有提供情绪信息，以中立且友好的方式回答，并保持支持的语气。\n"
    "4. 如果用户提到需要休息或放松，建议一些舒缓的音乐或推荐一本好书。\n"
    "5. 如果用户情绪低落，建议听一些励志音乐或阅读一本能够提升情绪的书。\n"
    "6. 始终鼓励用户在需要时与你交谈。让他们知道你随时都在，愿意提供帮助和支持。\n"
    "7. 如果用户明确提供了情绪（例如：开心、悲伤、压力大、兴奋），根据他们的情绪调整你的语气和回答。"
    "例如，如果他们感到悲伤，你可以说'我很抱歉你有这样的感觉，但我在这里倾听并帮助你！' "
    "如果他们感到开心，你可以说'那太棒了！我很高兴你感觉很好！'\n"
    "最终回答："
    )

    def __init__(self, streams: bool = False):
        redis_url = os.getenv("REDIS_URL", "redis://localhost:6379/0")
        self.redis_namespace = "aiguide:chat"

        self.stream = streams
        model = ChatOpenAI(
            model_name=os.getenv("ZHIPU_MODEL"),
            openai_api_key=os.getenv("ZHIPU_API_KEY"),
            openai_api_base=os.getenv("ZHIPU_BASE_URL")
        )
        prompt = ChatPromptTemplate.from_messages(
            [
                ("system", self.sys_prompt),
                MessagesPlaceholder(variable_name="chat_history"),
                ("user", "Emotion: {emotion}\nInput: {input}"),
                MessagesPlaceholder(variable_name="agent_scratchpad"),
            ]
        )
        tools = [WebSearch()]
        agent = create_tool_calling_agent(model, tools, prompt)
        agent_executor = AgentExecutor(agent=agent, tools=tools)
        self.agent_with_chat_history = RunnableWithMessageHistory(
            agent_executor,
            self._get_session_history,
            input_messages_key="input",
            history_messages_key="chat_history",
        )

    def _get_session_history(self, session_id: str) -> BaseChatMessageHistory:
        return RedisChatMessageHistory(
            session_id=session_id,
            redis_url=os.getenv("REDIS_URL", "redis://localhost:6379/0"),
        )

    def invoke_with_history(self, user_input: UserInput, stream=False):
        print(f"User Input: {str(user_input)}")
        return self.agent_with_chat_history.invoke(
            {
                "input": user_input.input,
                "emotion": user_input.emotion or "neutral",
            },
            config={"configurable": {"session_id": user_input.session_id}}
        )["output"]
if __name__ == "__main__":
    aiguide = AiGuide(streams=True)

    response1 = aiguide.invoke_with_history(
        UserInput(session_id="test", input="我现在的情绪是？", emotion="happy",output='')
    )
    print("AI Response 1:", response1)

    response2 = aiguide.invoke_with_history(
        UserInput(session_id="test", input="我现在应该干什么？", emotion="难过，愧疚",output='')
    )
    print("AI Response 2:", response2)
