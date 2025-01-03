from pydantic import BaseModel
from langchain.agents import create_tool_calling_agent, AgentExecutor

from tools import LocalSearch, WebSearch
from typing import Optional
from dotenv import load_dotenv
import os
#############
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.runnables.history import RunnableWithMessageHistory
from langchain_openai import ChatOpenAI
from langchain_redis import RedisChatMessageHistory

# 加载环境变量
load_dotenv()


class UserInput(BaseModel):
    session_id: str
    input: str
    output: Optional[str]
    emotion: Optional[str] = None

class AiGuide:

    agent_executor = None


    sys_prompt = (
        "You are a friendly and supportive virtual desk companion called '小海'. "
        "Your role is to chat with users, help them feel better, and offer motivation when they are feeling down. "
        "Remember user's emotional state and respond accordingly. "
       "Tell user about user's emotion"
        "You should always follow the following rules to work:\n"
        "1.  Analyze the user’s question or input to identify their emotional state.  If the user is feeling sad, stressed, or anxious, "
        "offer comforting words or suggestions to help them feel better.\n"
        "2.  If the user seems happy or positive, engage in a friendly and uplifting conversation.  You can share motivational quotes, "
        "suggest uplifting music, or recommend inspirational books.\n"
        "3.  If the user does not provide an emotion, respond in a neutral and friendly manner, maintaining a supportive tone.\n"
        "4.  If the user mentions needing a break or something to relax, offer a list of calming music or suggest a good book to read.\n"
        "5.  If the user is feeling low or down, suggest motivational music or a book that could help lift their spirits.\n"
        "6.  Always encourage the user to talk to you whenever they need someone to chat with.  Let them know that you're here to help and offer support.\n"
        "7.  If the user provides an emotion (e.g., happy, sad, stressed, excited), adjust your tone and response accordingly. "
        "For example, if they are sad, you might say something like 'I'm sorry you're feeling this way, but I'm here to listen and help! ' "
        "If they are happy, you might say something like 'That's awesome!  I'm glad you're feeling good! '\n"
        
        "final answer: ")

    # 带有聊天历史的代理
    agent_with_chat_history = None

    # 标记是否使用流
    stream: bool = False

    def __init__(self, streams: bool = False):
        # Redis 配置
        redis_url = os.getenv("REDIS_URL", "redis://localhost:6379/0")
        self.redis_namespace = "aiguide:chat"  # 命名空间，避免不同项目冲突

        self.stream = streams
        # 创建 ChatOpenAI 实例，设置模型、API 密钥、基础 URL 和是否使用流
        model = ChatOpenAI(
            model_name=os.getenv("ZHIPU_MODEL"),
            openai_api_key=os.getenv("ZHIPU_API_KEY"),
            openai_api_base=os.getenv("ZHIPU_BASE_URL")
        )
        # 从 hub 中拉取提示信息，这里使用了另一种方式创建提示模板
        # prompt = hub.pull("hwchase17/react-chat")
        prompt = ChatPromptTemplate.from_messages(
            [
                # 系统提示
                ("system", self.sys_prompt),
                # 聊天历史占位符
                MessagesPlaceholder(variable_name="chat_history"),
                # 用户输入占位符
                ("user", "{input}"),
                # 代理暂存区占位符
                MessagesPlaceholder(variable_name="agent_scratchpad"),
                ("system", "Emotions: {emotion}")
            ]
        )
        # 定义工具列表
        tools = [WebSearch()]

        # 创建工具调用代理
        agent = create_tool_calling_agent(model, tools, prompt)
        # 也可以使用另一种方式创建代理
        # agent = create_react_agent(model, tools, prompt)
        # 创建代理执行器
        agent_executor = AgentExecutor(agent=agent, tools=tools)
        # 创建带有消息历史的可运行对象
        # 历史运行器
        self.agent_with_chat_history = RunnableWithMessageHistory(
            agent_executor,
            self._get_session_history,
            input_messages_key="input",
            history_messages_key="chat_history",
        )

    # 获取会话历史的方法
    def _get_session_history(self, session_id: str) -> BaseChatMessageHistory:
        return RedisChatMessageHistory(
            session_id=session_id,
            redis_url=os.getenv("REDIS_URL", "redis://localhost:6379/0"),

        )

    # 带有历史的调用方法
    def invoke_with_history(self, user_input: UserInput):
        print(f"User Input: {str(user_input)}")
        return self.agent_with_chat_history.invoke(
            {
                "input": user_input.input,
                "emotion": user_input.emotion
            },
            config={"configurable": {"session_id": user_input.session_id}}
        )["output"]


if __name__ == "__main__":

    aiguide = AiGuide(streams=True)

    # 调用带有历史的调用方法，传入用户输入信息
    response1 = aiguide.invoke_with_history(
        UserInput(session_id="test2", input="我现在的情绪是什么",emotion="开心" ,output="")
    )
    print("AI Response 1:", response1)


