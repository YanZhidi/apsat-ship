package com.zkthinke.utils.pinyin4j.format;

final public class HanyuPinyinOutputFormat {

  public HanyuPinyinOutputFormat() {
    restoreDefault();
  }

  /**
   * Restore default variable values for this class
   * 
   * Default values are listed below:
   * 
   * <p>
   * HanyuPinyinVCharType := WITH_U_AND_COLON <br>
   * HanyuPinyinCaseType := LOWERCASE <br>
   * HanyuPinyinToneType := WITH_TONE_NUMBER <br>
   */
  public void restoreDefault() {
    vCharType = HanyuPinyinVCharType.WITH_U_AND_COLON;
    caseType = HanyuPinyinCaseType.LOWERCASE;
    toneType = HanyuPinyinToneType.WITH_TONE_NUMBER;
  }

  /**
   * Returns the output cases of Hanyu Pinyin characters
   * 
   * @see HanyuPinyinCaseType
   */
  public HanyuPinyinCaseType getCaseType() {
    return caseType;
  }

  /**
   * Define the output cases of Hanyu Pinyin characters
   * 
   * @param caseType
   *            the output cases of Hanyu Pinyin characters
   * 
   * @see HanyuPinyinCaseType
   */
  public void setCaseType(HanyuPinyinCaseType caseType) {
    this.caseType = caseType;
  }

  /**
   * Returns the output format of Chinese tones
   * 
   * @see HanyuPinyinToneType
   */
  public HanyuPinyinToneType getToneType() {
    return toneType;
  }

  /**
   * Define the output format of Chinese tones
   * 
   * @param toneType
   *            the output format of Chinese tones
   * 
   * @see HanyuPinyinToneType
   */
  public void setToneType(HanyuPinyinToneType toneType) {
    this.toneType = toneType;
  }

  /**
   * Returns output format of character 'ü'
   * 
   * @see HanyuPinyinVCharType
   */
  public HanyuPinyinVCharType getVCharType() {
    return vCharType;
  }

  /**
   * Define the output format of character 'ü'
   * 
   * @param charType
   *            the output format of character 'ü'
   * 
   * @see HanyuPinyinVCharType
   */
  public void setVCharType(HanyuPinyinVCharType charType) {
    vCharType = charType;
  }

  private HanyuPinyinVCharType vCharType;

  private HanyuPinyinCaseType caseType;

  private HanyuPinyinToneType toneType;

}
