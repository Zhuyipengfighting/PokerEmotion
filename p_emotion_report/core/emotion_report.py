# Author: Taoismimmortal
# Time: 2024/1/6
# Title: FastAPI about AI
from humanfriendly.terminal import output
from pydantic import BaseModel
from langchain.agents import create_tool_calling_agent, AgentExecutor
from sympy.physics.units import temperature

from tools import WebSearch
from typing import Optional, List, Dict
from dotenv import load_dotenv
import os
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.runnables.history import RunnableWithMessageHistory
from langchain_openai import ChatOpenAI
from langchain_redis import RedisChatMessageHistory

# 加载环境变量
load_dotenv()

class UserInput(BaseModel):
    session_id: str
    input: List[Dict]
class AIOutput(BaseModel):
    session_id: str
    report: str



class AiGuide:
    agent_executor = None
    sys_prompt = (
        """
    # 角色
    你是一位专业的情绪分析师，能够根据用户提供的情绪分类数据生成详细的情绪报告。\n
    ## 技能
    ### 技能 1：分析情绪变化情况
    1. 根据用户提供的情绪分类数据，总结一段时间内的情绪变化趋势。
    2. 描述主要情绪和次要情绪在不同时间段的表现。\n
    
    ### 技能 2：评价情绪状况
    1. 评估情绪的稳定程度，分为稳定、较稳定、不稳定三个等级。
    2. 具体分析情绪变化的原因和特点。
    3. 结合可能最近的节日（使用搜索工具看看是什么）、新闻大事或周期性事件（例如招聘、高考、高低气温等）推测可能影响情绪的因素。\n
    
    ### 技能 3：提供建议
    根据用户的具体情绪情况，给出针对性的建议。\n
    1.例如：持续性开心、期待时，提醒期望越高失望越大；开心为峰值后慢慢低落下来时，提供维持、提高快乐的方法；情绪变化起伏很大时，提醒冲动是魔鬼以及情绪稳定的重要性。
    \n
    2.给出具体建立，例如：听一些励志音乐或阅读一本能够提升情绪的书，散步等等\n
    ### 技能 4：搜索
    1.你可以使用搜索工具，搜索相关的信息，帮助你更好的回答问题\n
    可以用来辅助你的回答，例如：1.你要给建议的时候，对相应的情绪给出针对性的建议，你就搜索（相应情绪）的关键词的缓解方式，可以帮助你查找相关信息。\n
    2.对应日期发生了什么事件，你可以搜索（日期）的事件，可以帮助你查找相关信息。\n
    3.帮助你完善技能 2和技能 3的回答\n
    ## 限制：
    - 输出内容必须按照给定的格式进行组织，不能偏离框架要求。
    - 分析和建议要客观、合理，具有可操作性。\n
        """
    )

    def __init__(self, streams: bool = False):
        redis_url = os.getenv("REDIS_URL", "redis://localhost:6379/0")
        self.redis_namespace = "aiguide:chat"

        self.stream = streams
        model = ChatOpenAI(
            model_name=os.getenv("ZHIPU_MODEL"),
            openai_api_key=os.getenv("ZHIPU_API_KEY"),
            openai_api_base=os.getenv("ZHIPU_BASE_URL"),
            temperature=0.2,
        )
        prompt = ChatPromptTemplate.from_messages(
            [
                ("system", self.sys_prompt),
                MessagesPlaceholder(variable_name="chat_history"),
                ("user", "Emotion Data: {input}"),
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

    def invoke_with_history(self, user_input: UserInput):
        print(f"User Input: {str(user_input)}")

        response = self.agent_with_chat_history.invoke(
            {"input": user_input.input},
            config={"configurable": {"session_id": user_input.session_id}},
        )["output"]
        return AIOutput(session_id=user_input.session_id, report=response)

if __name__ == "__main__":
    aiguide = AiGuide(streams=True)

    input_data = [
        {"date": "2025-01-01", "emotions": {"happy": 0.7, "sad": 0.1, "neutral": 0.2}},
        {"date": "2025-01-02", "emotions": {"happy": 0.3, "sad": 0.5, "neutral": 0.2}}
    ]

    response = aiguide.invoke_with_history(UserInput(session_id="0", input=input_data))
    print("AI Response:", response.report)


