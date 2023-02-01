package com.zkthinke.utils.pinyin4j.format;

public class HanyuPinyinVCharType {

  /**
   * The option indicates that the output of 'ü' is "u:"
   */
  public static final HanyuPinyinVCharType WITH_U_AND_COLON =
      new HanyuPinyinVCharType("WITH_U_AND_COLON");

  /**
   * The option indicates that the output of 'ü' is "v"
   */
  public static final HanyuPinyinVCharType WITH_V = new HanyuPinyinVCharType("WITH_V");

  /**
   * The option indicates that the output of 'ü' is "ü" in Unicode form
   */
  public static final HanyuPinyinVCharType WITH_U_UNICODE =
      new HanyuPinyinVCharType("WITH_U_UNICODE");

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
  protected HanyuPinyinVCharType(String name) {
    setName(name);
  }

  protected String name;
}

