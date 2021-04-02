# rabbit-reliability

- 创建数据库 rabbit_reliability，执行 sql 文件
- 安装延迟队列插件
    - https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/tag/v3.8.0
    - rabbitmq-plugins enable rabbitmq_delayed_message_exchange
- 执行 MailServiceImplTest.testMq()，查看 MailConsumer