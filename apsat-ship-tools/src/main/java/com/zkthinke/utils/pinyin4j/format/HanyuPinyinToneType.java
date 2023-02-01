package com.zkthinke.utils.pinyin4j.format;

public class HanyuPinyinToneType {

  /**
   * The option indicates that hanyu pinyin is outputted with tone numbers
   */
  public static final HanyuPinyinToneType WITH_TONE_NUMBER =
      new HanyuPinyinToneType("WITH_TONE_NUMBER");

  /**
   * The option indicates that hanyu pinyin is outputted without tone numbers
   * or tone marks
   */
  public static final HanyuPinyinToneType WITHOUT_TONE = new HanyuPinyinToneType("WITHOUT_TONE");

  /**
   * The option indicates that hanyu pinyin is outputted with tone marks
   */
  public static final HanyuPinyinToneType WITH_TONE_MARK =
      new HanyuPinyinToneType("WITH_TONE_MARK");

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *            The name to set.
   */
  protected void setName(String name) {
    this.name = name;
  }

  protected HanyuPinyinToneType(String name) {
    setName(name);
  }

  protected String name;
}

