from humanfriendly.terminal import output
from pydantic import BaseModel
from langchain.agents import create_tool_calling_agent, AgentExecutor
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

# 定义用户输入的数据模型
class UserInput(BaseModel):
    session_id: str
    input: List[Dict[str, float]]  # 情绪数据数组
    output: Optional[str] = ''

class AiGuide:
    agent_executor = None
    sys_prompt = (
        """
# 角色
你是一位专业的情绪分析师，能够根据用户提供的情绪分类数据生成详细的情绪报告\n
## 技能
### 技能 1：分析情绪变化情况
1. 根据用户提供的情绪分类数据，总结一段时间内的情绪变化趋势。
2. 描述主要情绪和次要情绪在不同时间段的表现\n

### 技能 2：评价情绪状况
1. 评估情绪的稳定程度，分为稳定、较稳定、不稳定三个等级。
2. 具体分析情绪变化的原因和特点。
3. 你可以通过网络搜索相关时间与事件，结合当前的节日、新闻大事或周期性事件（招聘、高考、高低气温等）推测可能影响情绪的因素。\n

### 技能 3：提供建议
根据用户的具体情绪情况，给出针对性的建议。例如：持续性开心、期待时，提醒期望越高失望越大；开心为峰值后慢慢低落下来时，提供维持、提高快乐的方法；情绪变化起伏很大时，提醒冲动是魔魅以及情绪稳定的重要性。\n

## 限制：
- 仅根据用户提供的情绪分类数据进行分析，不进行主观臟断。
- 输出内容必须按照给定的格式进行组织，不能偏离框架要求。
- 分析和建议要客观、合理，具有可操作性\n
        """
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

    def invoke_with_history(self, user_input: UserInput, stream=False):
        print(f"User Input: {str(user_input)}")
        return self.agent_with_chat_history.invoke(
            {
                "input": user_input.input,
            },
            config={"configurable": {"session_id": user_input.session_id}}
        )["output"]

if __name__ == "__main__":
    aiguide = AiGuide(streams=True)

    response1 = aiguide.invoke_with_history(
        UserInput(session_id="test", input=[{"happy": 0.7, "sad": 0.1, "neutral": 0.2}], output='')
    )
    print("AI Response 1:", response1)

    response2 = aiguide.invoke_with_history(
        UserInput(session_id="test", input=[{"happy": 0.3, "sad": 0.5, "neutral": 0.2}], output='')
    )
    print("AI Response 2:", response2)
