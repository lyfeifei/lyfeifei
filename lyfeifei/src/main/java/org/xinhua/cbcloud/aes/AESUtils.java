package org.xinhua.cbcloud.aes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * AES加密解密工具包
 * </p>
 *
 * @author IceWee
 * @date 2012-5-18
 * @version 1.0
 */
@Slf4j
public class AESUtils {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * 生成随机密钥
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static String getSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE, new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64Utils.encode(secretKey.getEncoded());
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static void encryptFile(String key, String sourceFilePath, String destFilePath) {
        InputStream in = null;
        OutputStream out = null;
        CipherInputStream cin = null;
        try {
            File sourceFile = new File(sourceFilePath);
            if (sourceFile.exists() && sourceFile.isFile()) {
                File destFile = new File(destFilePath);
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();

                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);

                Key k = toKey(Base64Utils.decode(key));
                byte[] raw = k.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                cin = new CipherInputStream(in, cipher);

                byte[] cache = new byte[CACHE_SIZE];

                int nRead = 0;
                while ((nRead = cin.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(cin);
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @throws Exception
     */
    public static File decryptFile(String key, String sourceFilePath) {

        File sourceFile = new File(sourceFilePath);

        Date date = new Date();
        String dataForm = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String path = "/dagdata/temp" + "/" + dataForm;
        File tmp = new File(path);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }

        File destFile = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        CipherOutputStream cout = null;
        try {
            if (sourceFile.exists() && sourceFile.isFile()) {
                destFile = new File(path + "/" + sourceFile.getName());
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                Key k = toKey(Base64Utils.decode(key));
                byte[] raw = k.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                cout = new CipherOutputStream(out, cipher);
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    cout.write(cache, 0, nRead);
                    cout.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(cout);
        }
        return destFile;
    }

    /**
     * <p>
     * 转换密钥
     * </p>
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }
}