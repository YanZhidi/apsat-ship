package com.zkthinke.utils;

import cn.hutool.core.util.NumberUtil;
import java.math.BigDecimal;

/**
 * Created by kellen on 2019/9/21.
 */
public class MoneyUtil {

  private MoneyUtil() {
  }

  /**
   * 元转分
   */
  public static int yuan2Fen(Number number) {
    return NumberUtil.mul(number, new BigDecimal(100)).intValue();
  }
}
