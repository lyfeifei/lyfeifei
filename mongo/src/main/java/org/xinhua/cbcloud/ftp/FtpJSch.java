package org.xinhua.cbcloud.ftp;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xinhua.cbcloud.util.ReadTxtFile;
import org.xinhua.cbcloud.util.WriteTxtFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class FtpJSch {

    private final static Logger logger = LoggerFactory.getLogger(FtpJSch.class);

    private static ChannelSftp sftp = null;

    private static Session sshSession = null;

    private static String HOST = "localhost";

    private static int PORT = 97;

    private static String USER = "archieves";

    private static String PASSWORD = "Dag.2019.photo";

    private static String DIRECTORY = "/dagdata/data1/real";

    private static String savefile = "D:\\tmp\\";

    private boolean flag;

    /**
     * 建立连接
     *
     * @return
     */
    public static FtpJSch getConnect() {
        FtpJSch ftp = new FtpJSch();
        try {
            JSch jsch = new JSch();

            //获取sshSession  账号-ip-端口
            sshSession = jsch.getSession(USER, HOST, PORT);
            //添加密码
            sshSession.setPassword(PASSWORD);
            Properties sshConfig = new Properties();
            //严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");

            sshSession.setConfig(sshConfig);
            //开启sshSession链接
            sshSession.connect();
            //获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            //开启
            channel.connect();
            sftp = (ChannelSftp) channel;
            System.out.println("连接到SFTP成功：" + HOST + ":" + PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ftp;
    }

    /**
     * 断开连接
     */
    public static void disconnect() {
        //断开sftp连接
        if (sftp != null) {
            sftp.disconnect();
            logger.debug("SSH Channel disconnected.channel={}", sftp);
        }
        //断开sftp连接之后，再断开session连接
        if (sshSession != null) {
            sshSession.disconnect();
            logger.debug("SSH session disconnected.session={}", sshSession);
        }
    }

    /**
     * 上传文件
     *
     * @param uploadFile 上传文件的路径
     * @return 服务器上文件名
     */
    public static String upload(String uploadFile) {
        File file = null;
        String fileName = null;
        try {
            sftp.cd(DIRECTORY);
            file = new File(uploadFile);
            //获取原文件名
            fileName = file.getName();
            //调用ftp进行上传
            sftp.put(new FileInputStream(file), fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file == null ? null : fileName;
    }

    /**
     * 下载文件
     *
     * @param downloadFileName 下载文件名
     */
    public void download(String downloadFileName) {
        try {
            sftp.cd(DIRECTORY);
            File file = new File(savefile + downloadFileName);
            sftp.get(downloadFileName, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量下载文件
     *
     * @param fileNames 下载文件名
     */
    public void downloadFiles1(List<String> fileNames) {
        File file = null;
        OutputStream outputStream = null;
        try {
            sftp.cd(DIRECTORY);
            String downloadFileName = null;
            for (int i = 0; i < fileNames.size(); i++) {
                downloadFileName = fileNames.get(i);
                outputStream = new FileOutputStream(file);
                file = new File(savefile + downloadFileName);
                try {
                    sftp.get(downloadFileName, outputStream);
                    outputStream.flush();
                    System.out.println("正在执行下载任务：当前下载图片：" + downloadFileName + " 成功！");
                } catch (Exception e) {
                    outputStream.flush();
                    System.out.println("正在执行下载任务：" + downloadFileName + " 下载失败！");
                    continue;
                } finally{
                    if(outputStream != null){
                        IOUtils.closeQuietly(outputStream);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量下载文件
     *
     * @param fileNames 下载文件名
     */
    public static void downloadFiles(List<String> fileNames) {
        FtpJSch.getConnect();
        List<String> list = new ArrayList<>();
        try {
            sftp.cd(DIRECTORY);
            String downloadFileName = null;
            for (int i = 0; i < fileNames.size(); i++) {
                downloadFileName = fileNames.get(i);
                File file = new File(savefile + downloadFileName);
                try {
                    sftp.get(downloadFileName, new FileOutputStream(file));
                    System.out.println("正在执行下载任务：当前下载图片：" + downloadFileName + " 成功！");
                } catch (Exception e) {
                    list.add(downloadFileName);
                    System.out.println("正在执行下载任务：" + downloadFileName + " 下载失败！");
                    continue;
                }
            }
            WriteTxtFile.writeTxtForError(list);
            FtpJSch.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按指定路径进行下载
     * @param filePath
     * @param fileName
     * @return
     */
    public static boolean downloadFile(String filePath, String fileName) {
        boolean result = false;
        try {
            sftp.cd(filePath);
            File file = new File(savefile + fileName);
            sftp.get(fileName, new FileOutputStream(file));
            result = true;
            System.out.println("正在执行下载任务：当前下载图片：" + fileName + " 成功！");
        } catch (Exception e) {
            System.out.println("正在执行下载任务：" + fileName + " 下载失败！");
        }
        return result;
    }

    /**
     * 删除文件
     *
     * @param deleteFile 要删除的文件名字
     */
    public void delete(String deleteFile) {
        try {
            sftp.cd(DIRECTORY);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @return
     * @throws SftpException
     */
    public Vector listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        // 建立连接
        FtpJSch.getConnect();


//        String tmp = "/dagdata/data1/DAG_unfinished_plate/2020/0709/aececd0258a447228d2e3d71eaeafd00.JPG";
//
//        String fileName = tmp.substring(tmp.lastIndexOf("/") + 1, tmp.length());
//        String filePath = tmp.substring(0, tmp.lastIndexOf("/"));
//        System.out.println("处理路径：" + filePath + "/" + fileName);
//        boolean result = FtpJSch.downloadFile(filePath, fileName);
//
//        System.out.println("处理结果：" + result);

        String tmp = null;
        String fileName = null;
        String filePath = null;
        boolean result = false;
        List<String> errorList = new ArrayList<>();
        List<String> paths = ReadTxtFile.readTxtForList();
        for (int i = 0; i < paths.size(); i++) {
            tmp = paths.get(i);
            fileName = tmp.substring(tmp.lastIndexOf("/") + 1, tmp.length());
            filePath = tmp.substring(0, tmp.lastIndexOf("/"));
            result = FtpJSch.downloadFile(filePath, fileName);
            if (!result) {
                errorList.add(tmp);
            }
        }
        WriteTxtFile.writeTxtForError(errorList);

//        System.out.println(downloadFile("/dagdata/data1/real", "a0ff48066b434891bee3449d0ba30193.jpg"));
//        System.out.println(downloadFile("/dagdata/data1/real", "271491944050496da962d88a63b8facd.JPG"));



        // 断开连接
        FtpJSch.disconnect();
    }
}
