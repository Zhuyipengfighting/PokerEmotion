#文件测试成功。

#测试使用的curl：http://localhost:8080/api/chat/stream?sessionId=12345

前端的测试代码：
```javascript
fetch('http://localhost:8080/api/chat/stream?sessionId=12345')
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error:', error));
```