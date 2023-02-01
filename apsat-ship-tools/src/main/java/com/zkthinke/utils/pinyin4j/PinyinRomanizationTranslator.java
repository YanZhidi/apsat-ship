package com.zkthinke.utils.pinyin4j;

import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.Element;
import com.hp.hpl.sparta.ParseException;

class PinyinRomanizationTranslator {

	static String convertRomanizationSystem(String sourcePinyinStr, PinyinRomanizationType sourcePinyinSystem,
			PinyinRomanizationType targetPinyinSystem) {
		String pinyinString = TextHelper.extractPinyinString(sourcePinyinStr);
		String toneNumberStr = TextHelper.extractToneNumber(sourcePinyinStr);

		// return value
		String targetPinyinStr = null;
		try {
			// find the node of source Pinyin system
			String xpathQuery1 = "//" + sourcePinyinSystem.getTagName() + "[text()='" + pinyinString + "']";

			Document pinyinMappingDoc = PinyinRomanizationResource.getInstance().getPinyinMappingDoc();

			Element hanyuNode = pinyinMappingDoc.xpathSelectElement(xpathQuery1);

			if (null != hanyuNode) {
				// find the node of target Pinyin system
				String xpathQuery2 = "../" + targetPinyinSystem.getTagName() + "/text()";
				String targetPinyinStrWithoutToneNumber = hanyuNode.xpathSelectString(xpathQuery2);
				targetPinyinStr = targetPinyinStrWithoutToneNumber + toneNumberStr;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return targetPinyinStr;
	}
}
