package com.zkthinke.utils.pinyin4j;

class PinyinRomanizationType {
	/**
	 * Hanyu Pinyin system
	 */
	static final PinyinRomanizationType HANYU_PINYIN = new PinyinRomanizationType("Hanyu");

	/**
	 * Wade-Giles Pinyin system
	 */
	static final PinyinRomanizationType WADEGILES_PINYIN = new PinyinRomanizationType("Wade");

	/**
	 * Mandarin Phonetic Symbols 2 (MPS2) Pinyin system
	 */
	static final PinyinRomanizationType MPS2_PINYIN = new PinyinRomanizationType("MPSII");

	/**
	 * Yale Pinyin system
	 */
	static final PinyinRomanizationType YALE_PINYIN = new PinyinRomanizationType("Yale");

	/**
	 * Tongyong Pinyin system
	 */
	static final PinyinRomanizationType TONGYONG_PINYIN = new PinyinRomanizationType("Tongyong");

	/**
	 * Gwoyeu Romatzyh system
	 */
	static final PinyinRomanizationType GWOYEU_ROMATZYH = new PinyinRomanizationType("Gwoyeu");

	/**
	 * Constructor
	 */
	protected PinyinRomanizationType(String tagName) {
		setTagName(tagName);
	}

	/**
	 * @return Returns the tagName.
	 */
	String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName The tagName to set.
	 */
	protected void setTagName(String tagName) {
		this.tagName = tagName;
	}

	protected String tagName;
}
