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
    output: Optional[str]
    emotion: Optional[str] = None

# 定义 AiGuide 类
class AiGuide:
    agent_executor = None

    sys_prompt = (
        "You are a friendly and supportive virtual desk companion called '小海'. "
        "Reply in Chinese"
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
        
        "final answer: "
        # "You are a helpful guide for the Computer Science and Engineering college of GDOU called '小海'. "
        # "Your task is to answer questions about the campus from students. Always follow these rules:\n"
        # "1. Analyze the user’s question and their emotion (e.g., happy, sad, curious) to adjust your tone accordingly.\n"
        # "2. Extract one keyword relevant to the campus or topic the user is asking about.\n"
        # "3. Based on the extracted keyword, decide if you need to use a tool to find more information. If so, proceed with one of the following options:\n"
        # "    a. If the keyword requires a search engine, use the WebSearch tool to search for the keyword.\n"
        # "    b. If local documents or resources might contain relevant information, use the appropriate tool to retrieve it.\n"
        # "4. If you used the WebSearch tool, you may use another tool to retrieve the content of one web page that could be helpful, but only use it once or twice, and optionally provide the URL.\n"
        # "5. Summarize the gathered information clearly and concisely, answer the user’s question, and provide the source of the information at the end of your answer.\n"
        # "6. Adjust your response tone based on the user’s emotion: \n"
        # "    a. If the user is happy, be enthusiastic.\n"
        # "    b. If the user is sad, be empathetic.\n"
        # "    c. If the user is curious, be informative and engaging.\n"
        # "7. If no relevant information is found, ask the user for more details or offer an apology for not being able to find an answer.\n"
        # "8. At the end of your answer, welcome the user to GDOU and invite them to ask more questions about the campus.\n"
        # "final answer: "
    )

    agent_with_chat_history = None
    stream: bool = False

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
                ("user", "{input}"),
                MessagesPlaceholder(variable_name="agent_scratchpad"),
                ("system", "Emotion: {emotion}"),
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
        UserInput(session_id="test", input="你好，", emotion="happy",output='')
    )
    print("AI Response 1:", response1)

    response2 = aiguide.invoke_with_history(
        UserInput(session_id="test", input="我现在的情绪是什么", emotion="伤心",output='')
    )
    print("AI Response 2:", response2)
