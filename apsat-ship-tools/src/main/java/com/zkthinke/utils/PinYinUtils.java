package com.zkthinke.utils;

import com.zkthinke.exception.BadRequestException;
import com.zkthinke.utils.pinyin4j.PinyinHelper;
import com.zkthinke.utils.pinyin4j.exception.BadHanyuPinyinOutputFormatCombination;
import com.zkthinke.utils.pinyin4j.format.HanyuPinyinCaseType;
import com.zkthinke.utils.pinyin4j.format.HanyuPinyinOutputFormat;
import com.zkthinke.utils.pinyin4j.format.HanyuPinyinToneType;
import com.zkthinke.utils.pinyin4j.format.HanyuPinyinVCharType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weicb
 * @date 11/2/20 4:25 PM
 */
@Slf4j
public class PinYinUtils {

    /**
     * 将文字转为汉语拼音(大写)
     *
     * @param chineseLanguage 要转成拼音的中文
     */
    public static String toChinesePinyin(String chineseLanguage) {
        if(null == chineseLanguage || chineseLanguage.trim().isEmpty()) {
            log.error("char is empty");
            throw new BadRequestException("char is empty");
        }
        if(chineseLanguage.length() > 64 ) {
            log.error("name too long : "+chineseLanguage);
            throw new BadRequestException("name too long");
        }
        char[] chars = chineseLanguage.trim().toCharArray();
        String pinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 输出拼音全部小写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);// 将中文中的吕转换成lv
        for (int i = 0; i < chars.length; i++) {
            if (String.valueOf(chars[i]).matches("[\u4e00-\u9fa5]+")) {
                // 如果字符是中文,则将中文转为汉语拼音
                try {
                    pinyin += PinyinHelper.toHanyuPinyinStringArray(chars[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    log.error("chinese can not be convert to pinyin," + chars[i],e);
                    throw new BadRequestException("汉字不能被转换成拼音,汉字为:" + chars[i]);
                }
            } else {
                // 如果字符不是中文,则不转换
                pinyin += chars[i];
            }
        }
        return pinyin;
    }
}
