package org.xinhua.cbcloud.aes;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class AESHandler {

    public static void main(String[] args) throws Exception {
        // D:/1920.jpg  D:/1920_encrypted.jpg
        String filePath = "D:\\loneliness-2308923_640.jpg";

        // 生成密钥
        String key = AESUtils.getSecretKey();
        System.out.println("密钥：" + key);
        // 加密
        //encryptFile(key, filePath);
        // 解密 D:/demo_decrypted.mp4
        decryptFile("wac3dz4x7gU3BgzUTAcWKQ==", filePath);
    }

    public static String getSecretKey() throws Exception {
        return AESUtils.getSecretKey();
    }

    /**
     * 文件加密
     * @param key 密钥
     * @param filePaths 多个路径用逗号隔开
     * @throws Exception
     */
    public static void encryptFile(String key, String filePaths) throws Exception {

        String filePath = null;

        String[] paths = filePaths.split(",");

        for (int i = 0; i < paths.length; i++) {
            filePath = paths[i];
            long begin = System.currentTimeMillis();
            log.info("原文件路径：{}", filePath);
            File file = new File(filePath);
            String fileName = file.getName();
            log.info("提取文件名：{}", fileName);

            String newFileName = fileName.substring(0,
                    fileName.indexOf(".")) + "_encrypted" + fileName.substring(fileName.indexOf("."), fileName.length());

            String newFilePath = filePath.replace(fileName, newFileName);
            log.info("生成文件路径：{}", newFilePath);

            // 执行加密
            AESUtils.encryptFile(key, filePath, newFilePath);

            // 替换原文件
            file.delete();
            File oldFile = new File(newFilePath);
            File newFile = new File(filePath);
            oldFile.renameTo(newFile);
            long end = System.currentTimeMillis();
            log.info("加密过程耗时：{} 毫秒", end-begin);
        }
    }

    /**
     * 文件解密
     * @param key 密钥
     * @param sourceFilePath 文件原路径
     * @return 解密后的文件
     * @throws Exception
     */
    public static File decryptFile(String key, String sourceFilePath) throws Exception {
        long begin = System.currentTimeMillis();
        File file = AESUtils.decryptFile(key, sourceFilePath);
        log.info("解密后文件大小：{}", file.length());
        long end = System.currentTimeMillis();
        log.info("解密过程耗时：{} 毫秒", end-begin);
        return file;
    }
}