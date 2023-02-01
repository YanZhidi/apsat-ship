package com.zkthinke.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * create by cjj
 */
@ConfigurationProperties(prefix = "singlematch.storage")
@Setter
@Getter
public class StorageProperties {

    private String active;
    private Aliyun aliyun;
    private Tencent tencent;
    private Qiniu qiniu;
    private FastDFS fastDFS;


    public static class Tencent {
        private String secretId;
        private String secretKey;
        private String region;
        private String bucketName;

        public String getSecretId() {
            return secretId;
        }

        public void setSecretId(String secretId) {
            this.secretId = secretId;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }

    public static class Aliyun {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }

    public static class Qiniu {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucketName;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }

    public static class FastDFS {
        private Integer connectTimeout;
        private Integer networkTimeout;
        private String charset;
        private String trackerServers;
        private Integer trackerHttpPort;
        private Boolean antiStealToken;
        private String secretKey;
        private Integer maxTotal;
        private Integer maxTotalKey;
        private Integer maxIdle;
        private Integer minIdle;
        private Integer maxWaitMillis;
        private Integer poolSize;

        public Integer getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public Integer getNetworkTimeout() {
            return networkTimeout;
        }

        public void setNetworkTimeout(Integer networkTimeout) {
            this.networkTimeout = networkTimeout;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public String getTrackerServers() {
            return trackerServers;
        }

        public void setTrackerServers(String trackerServers) {
            this.trackerServers = trackerServers;
        }

        public Integer getTrackerHttpPort() {
            return trackerHttpPort;
        }

        public void setTrackerHttpPort(Integer trackerHttpPort) {
            this.trackerHttpPort = trackerHttpPort;
        }

        public Boolean getAntiStealToken() {
            return antiStealToken;
        }

        public void setAntiStealToken(Boolean antiStealToken) {
            this.antiStealToken = antiStealToken;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public Integer getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(Integer maxTotal) {
            this.maxTotal = maxTotal;
        }

        public Integer getMaxTotalKey() {
            return maxTotalKey;
        }

        public void setMaxTotalKey(Integer maxTotalKey) {
            this.maxTotalKey = maxTotalKey;
        }

        public Integer getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(Integer maxIdle) {
            this.maxIdle = maxIdle;
        }

        public Integer getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(Integer minIdle) {
            this.minIdle = minIdle;
        }

        public Integer getMaxWaitMillis() {
            return maxWaitMillis;
        }

        public void setMaxWaitMillis(Integer maxWaitMillis) {
            this.maxWaitMillis = maxWaitMillis;
        }

        public Integer getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(Integer poolSize) {
            this.poolSize = poolSize;
        }
    }
}
