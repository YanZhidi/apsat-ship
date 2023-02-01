package com.zkthinke.modules.rabbitMQ;

import com.rabbitmq.client.Channel;
import com.zkthinke.modules.common.constant.Constant;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

/**
 * @description: RabbitMQHandle 消息监听
 */
@Configuration
public class RabbitMQHandle {
    /**
     * @param msg     接受的消息
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = Constant.QUEUE_SMS),
            exchange = @Exchange(value = Constant.DIRECT_KEY_SMS, type = ExchangeTypes.DIRECT)))
    public void smsMessage(String msg, Message message, Channel channel) {
        System.out.println("发送短信：" + msg);
        // channel.basicReject();
    }

    /**
     * @param msg     接受的消息
     * @param message
     * @param channel
     */
    @RabbitListener(queues = Constant.QUEUE_EMAIL)
    public void emailMessage(String msg, Message message, Channel channel) {
        System.out.println("发送邮件：" + msg);
    }

    /**
     * @param msg     接受的消息
     * @param message
     * @param channel
     */
    @RabbitListener(queues = Constant.QUEUE_SYS)
    public void sysMessage(String msg, Message message, Channel channel) {
        System.out.println("发送系统消息：" + msg);
    }

    @RabbitListener(queues = Constant.QUEUE_TOPIC_A)
    public void AMessage(String msg, Message message, Channel channel) {
        System.out.println("QA发送Topic消息：" + msg);
    }

    @RabbitListener(queues = Constant.QUEUE_TOPIC_B)
    public void BMessage(String msg, Message message, Channel channel) {
        System.out.println("QB发送Topic.*消息：" + msg);
    }

    @RabbitListener(queues = Constant.QUEUE_FANOUT_A)
    public void fanoutMessageA(String msg, Message message, Channel channel) {
        System.out.println("发送fanoutA消息：" + msg);
    }

    @RabbitListener(queues = Constant.QUEUE_FANOUT_B)
    public void fanoutMessageB(String msg, Message message, Channel channel) {
        System.out.println("发送fanoutB消息：" + msg);
    }
}

