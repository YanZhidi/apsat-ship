package com.zkthinke.service;

import com.zkthinke.domain.VerificationCode;
import com.zkthinke.domain.vo.EmailVo;

/**
 * @author Zheng Jie
 * @date 2020-10-26
 */
public interface VerificationCodeService {

    /**
     * 发送邮件验证码
     * @param code
     */
    EmailVo sendEmail(VerificationCode code);

    /**
     * 验证
     * @param code
     */
    void validated(VerificationCode code);
}
