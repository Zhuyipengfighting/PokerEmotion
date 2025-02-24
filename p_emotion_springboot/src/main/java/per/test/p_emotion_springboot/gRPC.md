# AiGrpcClient

- 负责管理与 gRPC 服务的连接。
- 提供了流式通信的入口方法 `startChat`。
- 使用 `@PreDestroy` 确保资源能够正确释放。

# GrpcAutoConfiguration

- 将 `AiGrpcClient` 注册为 Spring 的 Bean。
- 支持外部配置 gRPC 服务的地址和端口。

# ChatController

- 提供了一个 HTTP 接口 `/api/chat/stream`。
- 使用 `SseEmitter` 实现了流式响应。
- 将前端请求转发到 gRPC 服务，并将响应结果实时推送给前端。