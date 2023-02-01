package com.zkthinke.modules.common.utils;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
public class GetFtp {

    /**
     * 创建sftp连接
     * 下载文件
     * 删除文件
     */
    public static List<String> createChannelSftp(String host, int port, String username, String password, int timeout, String remotePathList, String localPath, String folders) {
        log.info("sftp下载文件开始");
        ChannelSftp channelSftp = null;
        Session session = null;
        List<String> fileList = new ArrayList<>();
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect(timeout);

            log.info("sftp连接成功");
            Set<String> folderSet = new HashSet<>(Arrays.asList(folders.split(",")));
            fileList = downloadFileAndFolder(channelSftp, remotePathList + DateUtils.getYear(System.currentTimeMillis()), localPath, folderSet);
            fileList.sort(String::compareTo);
            log.info("sftp下载文件数：{}", fileList.size());
        } catch (Exception e) {
            log.error("sftp下载文件异常：", e);
            e.printStackTrace();
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        return fileList;
    }

    public static List<String> downloadFileAndFolder(ChannelSftp channelSftp, String remotePath, String localPath, Set<String> folderSet) throws SftpException {
        log.info("读取远程文件【{}】",remotePath);
        List<String> fileList = new ArrayList<>();
        Vector<ChannelSftp.LsEntry> fileAndFolderList = channelSftp.ls(remotePath);

        fileAndFolderList.sort(Comparator.comparing(ChannelSftp.LsEntry::getFilename));
        int count = 0;

        for (ChannelSftp.LsEntry remoteFile : fileAndFolderList) {
            String fileName = remoteFile.getFilename();
            String remoteAbsFilePath = remotePath + "/" + fileName;
            if (fileName.startsWith(".")) {
                continue;
            }
            if (remoteFile.getAttrs().isDir()) {
                //文件夹白名单 folderSet
                if (!folderSet.contains(fileName)) {
                    continue;
                }
                String localAbsFilePath = localPath + "/" + fileName;
                File file = new File(localAbsFilePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                fileList.addAll(downloadFileAndFolder(channelSftp, remoteAbsFilePath, localAbsFilePath, folderSet));
            } else if (fileName.endsWith(".log")) {
                //按日期分文件夹
                String date = fileName.split("_")[2].substring(0, 8);
                String localAbsPath = localPath + "/" + date;
                File path = new File(localAbsPath);
                if (!path.exists()) {
                    path.mkdirs();
                }
                String localAbsFilePath = localAbsPath + "/" + fileName;
                File file = new File(localAbsFilePath);
                if (file.exists() && remoteFile.getAttrs().getSize() == file.length()) {
                    log.info("文件已存在【{}】", localAbsFilePath);
                    channelSftp.rm(remoteAbsFilePath);
                    continue;
                }
                log.info("下载文件【{}】-->【{}】", remoteAbsFilePath, localAbsFilePath);
                channelSftp.get(remoteAbsFilePath, localAbsFilePath);
                if (remoteFile.getAttrs().getSize() != file.length()){
                    continue;
                }
                channelSftp.rm(remoteAbsFilePath);
                fileList.add(localAbsFilePath);
                count++;
                if (count >= 1000) {
                    break;
                }
            }
        }
        return fileList;
    }

}
