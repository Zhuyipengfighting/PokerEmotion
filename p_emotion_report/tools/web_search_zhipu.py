
import os
from dotenv import load_dotenv
import requests
from typing import Optional, Type
from pydantic import BaseModel, Field
from langchain_core.tools import BaseTool
from langchain_core.callbacks import (
    AsyncCallbackManagerForToolRun,
    CallbackManagerForToolRun,
)
from zhipuai import ZhipuAI


load_dotenv()


class WebSearchInput(BaseModel):
    keyword: str = Field(description="One word to search for")


class WebSearch(BaseTool):
    name: str = "WebSearch"
    description: str = "Useful for when you need to search for information about emotion."
    args_schema: Type[BaseModel] = WebSearchInput
    return_direct: bool = False
    query_header: str = ""

    def _run(self, keyword: str, run_manager: Optional[CallbackManagerForToolRun] = None) -> list:

        if not keyword.startswith(self.query_header):
            keyword = ' ' + keyword

        api_key = os.getenv("ZHIPU_API_KEY")
        base_url = "https://open.bigmodel.cn/api/paas/v4"
        client = ZhipuAI(api_key=api_key, base_url=base_url)

        try:

            response = client.chat.completions.create(
                model="web-search-pro",
                messages=[{"role": "user", "content": keyword}],
                top_p=0.7,
                temperature=0.1,
                stream=False
            )

            #search result
            search_result = response.choices[0].message.tool_calls[1].search_result
            # search_result里面会有很多内容，content,title,url等等
            contents = [result['content'] for result in search_result]
            print(f"Web search result: {search_result}")

            return contents  # Return the result of the search
        except Exception as e:
            print(f"Error during web search: {e}")
            return []

    async def _arun(self, keyword: str, run_manager: Optional[AsyncCallbackManagerForToolRun] = None) -> list:
        """Use the tool"""
        return self._run(keyword, run_manager=run_manager.get_sync())



if __name__ == "__main__":

    web_search_tool = WebSearch()
    query = "今天几号"
    result = web_search_tool._run(query)
    print(f"Search Result: {result}")
