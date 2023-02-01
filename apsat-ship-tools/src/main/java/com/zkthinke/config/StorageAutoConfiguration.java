package com.zkthinke.config;

import com.zkthinke.service.impl.*;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * create by cjj
 */
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(StorageAutoConfiguration.class);

    private final StorageProperties properties;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    @Bean
    public StorageService storageService() {
        StorageService storageService = new StorageService();
        String active = this.properties.getActive();
        storageService.setActive(active);
        if (active.equals("aliyun")) {
            storageService.setStorage(aliyunStorage());
        } else if (active.equals("tencent")) {
            storageService.setStorage(tencentStorage());
        } else if (active.equals("qiniu")) {
            storageService.setStorage(qiniuStorage());
        } else if (active.equals("fastdfs")) {
            storageService.setStorage(fastDFSStorage());
        } else {
            throw new RuntimeException("当前存储模式 " + active + " 不支持");
        }

        return storageService;
    }


    @Bean
    public AliyunStorage aliyunStorage() {
        AliyunStorage aliyunStorage = new AliyunStorage();
        StorageProperties.Aliyun aliyun = this.properties.getAliyun();
        aliyunStorage.setAccessKeyId(aliyun.getAccessKeyId());
        aliyunStorage.setAccessKeySecret(aliyun.getAccessKeySecret());
        aliyunStorage.setBucketName(aliyun.getBucketName());
        aliyunStorage.setEndpoint(aliyun.getEndpoint());
        return aliyunStorage;
    }

    @Bean
    public TencentStorage tencentStorage() {
        TencentStorage tencentStorage = new TencentStorage();
        StorageProperties.Tencent tencent = this.properties.getTencent();
        tencentStorage.setSecretId(tencent.getSecretId());
        tencentStorage.setSecretKey(tencent.getSecretKey());
        tencentStorage.setBucketName(tencent.getBucketName());
        tencentStorage.setRegion(tencent.getRegion());
        return tencentStorage;
    }

    @Bean
    public QiniuStorage qiniuStorage() {
        QiniuStorage qiniuStorage = new QiniuStorage();
        StorageProperties.Qiniu qiniu = this.properties.getQiniu();
        qiniuStorage.setAccessKey(qiniu.getAccessKey());
        qiniuStorage.setSecretKey(qiniu.getSecretKey());
        qiniuStorage.setBucketName(qiniu.getBucketName());
        qiniuStorage.setEndpoint(qiniu.getEndpoint());
        return qiniuStorage;
    }

    @Bean
    public FastDFSStorage fastDFSStorage() {
        FastDFSStorage fastDFSStorage = new FastDFSStorage();
        StorageProperties.FastDFS fastDFS = this.properties.getFastDFS();
        logger.info("load fastDFS config: ");
        logger.info("[connectTimeout="+fastDFS.getConnectTimeout()+"]");
        logger.info("[networkTimeout="+fastDFS.getNetworkTimeout()+"]");
        logger.info("[charset="+fastDFS.getCharset()+"]");
        logger.info("[trackerHttpPort="+fastDFS.getTrackerHttpPort()+"]");
        logger.info("[antiStealToken="+fastDFS.getAntiStealToken()+"]");
        logger.info("[secretKey="+fastDFS.getSecretKey()+"]");
        logger.info("[trackerServers="+fastDFS.getTrackerServers()+"]");
        logger.info("[maxTotal="+fastDFS.getMaxTotal()+"]");
        logger.info("[minIdle="+fastDFS.getMinIdle()+"]");
        logger.info("[maxIdle="+fastDFS.getMaxIdle()+"]");
        logger.info("[maxTotalKey="+fastDFS.getMaxTotalKey()+"]");
        logger.info("[maxWaitMillis="+fastDFS.getMaxWaitMillis()+"]");
        logger.info("[poolSize="+fastDFS.getPoolSize()+"]");

        ClientGlobal.g_connect_timeout = fastDFS.getConnectTimeout() * 1000;
        ClientGlobal.g_network_timeout = fastDFS.getNetworkTimeout() * 1000;
        ClientGlobal.g_charset = fastDFS.getCharset();
        ClientGlobal.g_tracker_http_port = fastDFS.getTrackerHttpPort();
        ClientGlobal.g_anti_steal_token = fastDFS.getAntiStealToken();
        if(fastDFS.getAntiStealToken()){
            ClientGlobal.g_secret_key = fastDFS.getSecretKey();
        }

        // 集群
        Map<String,TrackerGroup> groupMaps = new HashMap();
        String trackerServers = fastDFS.getTrackerServers();
        if (trackerServers.indexOf(":")>-1) {
            String server[]=trackerServers.split("\\:");
            InetSocketAddress[] arr2 = {new InetSocketAddress(server[0], Integer.parseInt(server[1]))};
            groupMaps.put("default", new TrackerGroup(arr2));
        }
        KeyTrackerServerFactory keyTrackerServerFactory = new KeyTrackerServerFactory(groupMaps);
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotal(fastDFS.getMaxTotal());
        config.setMinIdlePerKey(fastDFS.getMinIdle());
        config.setMaxIdlePerKey(fastDFS.getMaxIdle());
        config.setMaxTotalPerKey(fastDFS.getMaxTotalKey());
        config.setMaxWaitMillis(fastDFS.getMaxWaitMillis());
        config.setJmxEnabled(false);
        GenericKeyedObjectPool genericKeyedObjectPool = new GenericKeyedObjectPool(keyTrackerServerFactory, config);

        fastDFSStorage.setKeyTrackerServerPool(genericKeyedObjectPool);
        fastDFSStorage.setPoolSize(fastDFS.getPoolSize());
        return fastDFSStorage;
    }
}
