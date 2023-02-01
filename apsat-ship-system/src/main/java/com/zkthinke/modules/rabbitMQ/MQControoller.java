package com.zkthinke.modules.rabbitMQ;

import com.zkthinke.modules.common.constant.Constant;
import com.zkthinke.response.ResponseResult;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;

/**
 * @description: MQControoller
 */
@RestController
public class MQControoller {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @return
     */
    @GetMapping("/mqSend")
    public ResponseResult send() {

        //发送消息到Fanout交换机
        rabbitTemplate.convertAndSend(Constant.EXCHANGE_FANOUT, null, Constant.EXCHANGE_FANOUT);

        //发送消息到Driect交换机
        rabbitTemplate.convertAndSend(Constant.EXCHANGE_DIRECT, Constant.DIRECT_KEY_EMAIL, "email");
        //短信发送消息
        rabbitTemplate.convertAndSend(Constant.EXCHANGE_DIRECT, Constant.DIRECT_KEY_SMS, "sms");
        //系统发送消息
        rabbitTemplate.convertAndSend(Constant.EXCHANGE_DIRECT, Constant.DIRECT_KEY_SYS, "sys");

        //发送消息到Topic交换机
        //邮件发送消息
        rabbitTemplate.convertAndSend(Constant.EXCHANGE_TOPIC, Constant.KEY_TOPIC_A, Constant.EXCHANGE_TOPIC);
        rabbitTemplate.convertAndSend(Constant.EXCHANGE_TOPIC, Constant.KEY_TOPIC_B, Constant.EXCHANGE_TOPIC);

        return ResponseResult.ok("发送成功");
    }
}

