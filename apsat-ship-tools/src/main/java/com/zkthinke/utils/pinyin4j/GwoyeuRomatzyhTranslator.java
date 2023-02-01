package com.zkthinke.utils.pinyin4j;


import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.ParseException;

/**
 * A class contains logic that translates from Hanyu Pinyin to Gwoyeu Romatzyh
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class GwoyeuRomatzyhTranslator {
  /**
   * @param hanyuPinyinStr
   *            Given unformatted Hanyu Pinyin with tone number
   * @return Corresponding Gwoyeu Romatzyh; null if no mapping is found.
   */
  static String convertHanyuPinyinToGwoyeuRomatzyh(String hanyuPinyinStr) {
    String pinyinString = TextHelper.extractPinyinString(hanyuPinyinStr);
    String toneNumberStr = TextHelper.extractToneNumber(hanyuPinyinStr);

    // return value
    String gwoyeuStr = null;
    try {
      // find the node of source Pinyin system
      String xpathQuery1 =
          "//" + PinyinRomanizationType.HANYU_PINYIN.getTagName() + "[text()='" + pinyinString
              + "']";

      Document pinyinToGwoyeuMappingDoc =
          GwoyeuRomatzyhResource.getInstance().getPinyinToGwoyeuMappingDoc();

      Element hanyuNode = pinyinToGwoyeuMappingDoc.xpathSelectElement(xpathQuery1);

      if (null != hanyuNode) {
        // find the node of target Pinyin system
        String xpathQuery2 =
            "../" + PinyinRomanizationType.GWOYEU_ROMATZYH.getTagName()
                + tones[Integer.parseInt(toneNumberStr) - 1] + "/text()";
        String targetPinyinStrWithoutToneNumber = hanyuNode.xpathSelectString(xpathQuery2);

        gwoyeuStr = targetPinyinStrWithoutToneNumber;
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return gwoyeuStr;
  }

  /**
   * The postfixs to distinguish different tone of Gwoyeu Romatzyh
   * 
   * <i>Should be removed if new xPath parser supporting tag name with number.</i>
   */
  static private String[] tones = new String[] {"_I", "_II", "_III", "_IV", "_V"};
}

