package com.zkthinke.service.impl;

import com.zkthinke.service.Storage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerServer;
import org.csource.fastdfs.UploadCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;


/**
 * FastDFS 文件上传
 * @author weicb
 */
public class FastDFSStorage implements Storage, InitializingBean, DisposableBean {

    private static final String File_SEPARATOR = "/";
    public static final String DEFAULT_GROUP = "default";
    public static final String TRACKER_SERVER_KEY="trackerServerKey";
    private static final Logger LOGGER = LoggerFactory.getLogger(FastDFSStorage.class);
    private GenericKeyedObjectPool<String, TrackerServer> keyTrackerServerPool;
    private int poolSize = 20;
    private ExecutorService executor = null;


    public void setKeyTrackerServerPool(GenericKeyedObjectPool<String, TrackerServer> keyTrackerServerPool) {
        this.keyTrackerServerPool = keyTrackerServerPool;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
        keyTrackerServerPool.close();
    }

    @Override
    public Optional<String> store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        String trackerServerKey = DEFAULT_GROUP;
        UploadCallback callback = new UploadCallback() {
            @Override
            public int send(OutputStream out) throws IOException {
                IOUtils.copyLarge(inputStream, out);
                return 0;
            }
        };

        String ext = null;
        String fileName = keyName;
        int index = fileName.lastIndexOf(".");
        if (index != -1 && index < fileName.length() - 1) {
            ext = fileName.substring(index + 1);
        }
        TrackerServer trackerServer = null;
        try {
            LOGGER.info("fastDFS store file trackerServer key: {}", trackerServerKey);
            trackerServer =  keyTrackerServerPool.borrowObject(trackerServerKey);
            LOGGER.info("fastDFS store file trackerServer host: {}, port: {} ", trackerServer.getInetSocketAddress().getHostString(), trackerServer.getInetSocketAddress().getPort());
            String[] parts = new StorageClient(trackerServer, null).upload_file(DEFAULT_GROUP, contentLength, callback, ext,
                    null);
            if (parts == null) {
                throw new RuntimeException("Can not upload file to FastDFS, returns null");
            }
            String path = parts[0] + File_SEPARATOR + parts[1];
            LOGGER.info("fastDFS store success, path: {}, trackerServer key: {}", path, trackerServerKey);
            return Optional.ofNullable(path);
        } catch (IOException e) {
            LOGGER.error("Can not upload file: " + keyName, e);
        } catch (Exception e) {
            LOGGER.error("loading fastDFS trackerServer is null ", e);
        } finally {
            try {
                keyTrackerServerPool.returnObject(trackerServerKey, trackerServer);
            } catch (Exception e) {
                LOGGER.error("close keyTrackerServerPool trackerServer error ", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String keyName) {
        return null;
    }

    @Override
    public Resource loadAsResource(String keyName) {
        return null;
    }

    @Override
    public void delete(String keyName) {

    }

    @Override
    public String generateUrl(String keyName) {
        return null;
    }
}
