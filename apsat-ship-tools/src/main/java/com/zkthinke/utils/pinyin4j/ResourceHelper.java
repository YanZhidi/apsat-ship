package com.zkthinke.utils.pinyin4j;

import java.io.BufferedInputStream;

class ResourceHelper {

	static BufferedInputStream getResourceInputStream(String resourceName) {
		return new BufferedInputStream(ResourceHelper.class.getResourceAsStream(resourceName));
	}
}
