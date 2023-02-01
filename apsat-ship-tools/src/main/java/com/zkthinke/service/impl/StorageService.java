package com.zkthinke.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.zkthinke.domain.StorageContent;
import com.zkthinke.service.Storage;
import com.zkthinke.service.StorageContentService;
import com.zkthinke.utils.CharUtil;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

/**
 * create by cjj
 */
@Setter
@Getter
public class StorageService  {
        private String active;
        private Storage storage;

        @Autowired
        private StorageContentService storageContentService;

        /**
         * 存储一个文件对象
         *
         * @param inputStream   文件输入流
         * @param contentLength 文件长度
         * @param contentType   文件类型
         * @param fileName      文件索引名
         */
        public StorageContent store(InputStream inputStream, long contentLength, String contentType, String fileName) {
            String key = generateKey(fileName);
            Optional<String> path = storage.store(inputStream, contentLength, contentType, key);

            String url = path.orElseGet(() -> generateUrl(key));
            StorageContent storageContent = new StorageContent();
            Snowflake snowflake = IdUtil.createSnowflake(1, 1);
            storageContent.setId(String.valueOf(snowflake.nextId()));
            storageContent.setName(fileName);
            storageContent.setSize((int) contentLength);
            storageContent.setType(contentType);
            storageContent.setFileKey(key);
            storageContent.setUrl(url);
            storageContentService.create(storageContent);

            return storageContent;
        }

        private String generateKey(String originalFilename) {
            int index = originalFilename.lastIndexOf('.');
            String fileName = originalFilename.substring(0, index);
            String suffix = originalFilename.substring(index);

            String key = null;
            Optional<StorageContent> storageContent = Optional.empty();

            do {
                key = fileName + "-" + CharUtil.getRandomString(20) + suffix;
                storageContent = storageContentService.findByFileKey(key);
            }
            while (storageContent.isPresent());

            return key;
        }

        public Stream<Path> loadAll() {
            return storage.loadAll();
        }

        public Path load(String keyName) {
            return storage.load(keyName);
        }

        public Resource loadAsResource(String keyName) {
            return storage.loadAsResource(keyName);
        }

        public void delete(String keyName) {
            storage.delete(keyName);
        }

        private String generateUrl(String keyName) {
            return storage.generateUrl(keyName);
        }
}
