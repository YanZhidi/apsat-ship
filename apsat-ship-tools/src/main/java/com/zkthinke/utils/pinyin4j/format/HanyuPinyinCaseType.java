package com.zkthinke.utils.pinyin4j.format;

public class HanyuPinyinCaseType {

  /**
   * The option indicates that hanyu pinyin is outputted as uppercase letters
   */
  public static final HanyuPinyinCaseType UPPERCASE = new HanyuPinyinCaseType("UPPERCASE");

  /**
   * The option indicates that hanyu pinyin is outputted as lowercase letters
   */
  public static final HanyuPinyinCaseType LOWERCASE = new HanyuPinyinCaseType("LOWERCASE");

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

  /**
   * Constructor
   */
  protected HanyuPinyinCaseType(String name) {
    setName(name);
  }

  protected String name;
}

