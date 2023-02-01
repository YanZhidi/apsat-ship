package com.zkthinke.modules.rabbitMQ;

import com.rabbitmq.client.Channel;
import com.zkthinke.modules.common.constant.Constant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: RabbitMQConfig 声明队列和交换机  将交换机和队列绑定
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 定义fanout交换机
     * @return
     */
    @Bean(Constant.EXCHANGE_FANOUT)
    public Exchange fanoutExchange(){
        return ExchangeBuilder.fanoutExchange(Constant.EXCHANGE_FANOUT).durable(true).build();
    }

    @Bean(Constant.QUEUE_FANOUT_A)
    public Queue fanoutQueueA(){
        return new Queue(Constant.QUEUE_FANOUT_A,true);
    }


    @Bean(Constant.QUEUE_FANOUT_B)
    public Queue fanoutQueueB(){
        return new Queue(Constant.QUEUE_FANOUT_B,true);
    }

    @Bean
    public Binding bindingFanoutA(@Qualifier(Constant.QUEUE_FANOUT_A) Queue fanoutQueue,@Qualifier(Constant.EXCHANGE_FANOUT) Exchange exchange){
        return BindingBuilder.bind(fanoutQueue).to(exchange).with("").noargs();
    }

    @Bean
    public Binding bindingFanoutB(@Qualifier(Constant.QUEUE_FANOUT_B) Queue fanoutQueue,@Qualifier(Constant.EXCHANGE_FANOUT) Exchange exchange){
        return BindingBuilder.bind(fanoutQueue).to(exchange).with("").noargs();
    }


    /**
     * 定义Direct交换机
     */
    @Bean(Constant.EXCHANGE_DIRECT)
    public Exchange directExchange(){
        return ExchangeBuilder.directExchange(Constant.EXCHANGE_DIRECT).durable(true).build();
    }

    /**
     * 定义短信队列
     */
    @Bean(Constant.QUEUE_SMS)
    public Queue queueSms(){
        return new Queue(Constant.QUEUE_SMS,true);
    }

    /**
     * 将短信队列绑定到Direct交换机
     */
    @Bean
    public Binding bindingSms(@Qualifier(Constant.QUEUE_SMS) Queue smsQueue, @Qualifier(Constant.EXCHANGE_DIRECT) Exchange exchange){
        return BindingBuilder.bind(smsQueue).to(exchange).with(Constant.DIRECT_KEY_SMS).noargs();
    }

    /**
     * 定义邮箱队列
     */
    @Bean(Constant.QUEUE_EMAIL)
    public Queue queueEmail(){
        return new Queue(Constant.QUEUE_EMAIL,true);
    }

    /**
     * 将邮箱队列和Direct交换机绑定
     */
    @Bean
    public Binding bindingEmail(@Qualifier(Constant.QUEUE_EMAIL) Queue emailQueue, @Qualifier(Constant.EXCHANGE_DIRECT) Exchange exchange){
        return BindingBuilder.bind(emailQueue).to(exchange).with(Constant.DIRECT_KEY_EMAIL).noargs();
    }

    /**
     * 定义系统通知队列
     */
    @Bean(Constant.QUEUE_SYS)
    public Queue queueSys(){
        return new Queue(Constant.QUEUE_SYS,true);
    }

    /**
     * 将系统队列和Direct交换机绑定
     */
    @Bean
    public Binding bindingSys(@Qualifier(Constant.QUEUE_SYS) Queue sysQueue, @Qualifier(Constant.EXCHANGE_DIRECT) Exchange exchange){
        return BindingBuilder.bind(sysQueue).to(exchange).with(Constant.DIRECT_KEY_SYS).noargs();
    }


    /**
     * 定义Topic交换机
     */
    @Bean(Constant.EXCHANGE_TOPIC)
    public Exchange topicExchange(){
        return ExchangeBuilder.topicExchange(Constant.EXCHANGE_TOPIC).durable(true).build();
    }

    /**
     * 定义Topic通知队列A
     */
    @Bean(Constant.QUEUE_TOPIC_A)
    public Queue queueA(){
        return new Queue(Constant.QUEUE_TOPIC_A,true);
    }

    /**
     * 定义Topic通知队列B
     */
    @Bean(Constant.QUEUE_TOPIC_B)
    public Queue queueB(){
        return new Queue(Constant.QUEUE_TOPIC_B,true);
    }

    /**
     * 将队列A和Topic交换机绑定
     */
    @Bean
    public Binding bindingA(@Qualifier(Constant.QUEUE_TOPIC_A) Queue queueAll, @Qualifier(Constant.EXCHANGE_TOPIC) Exchange exchange){
        return BindingBuilder.bind(queueAll).to(exchange).with(Constant.KEY_TOPIC_A).noargs();
    }

    /**
     * 将队列B和Topic交换机绑定
     */
    @Bean
    public Binding bindingB(@Qualifier(Constant.QUEUE_TOPIC_B) Queue queueAll, @Qualifier(Constant.EXCHANGE_TOPIC) Exchange exchange){
        return BindingBuilder.bind(queueAll).to(exchange).with(Constant.KEY_TOPIC).noargs();
    }
}

