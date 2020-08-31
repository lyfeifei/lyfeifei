package org.xinhua.cbcloud.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReadTxtFile {

    private static Logger log = LoggerFactory.getLogger(ReadTxtFile.class);

    static BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        readTxt(blockingQueue);
        extract(blockingQueue);
    }

    public static void readTep() {
//        String str = "D:\\test.txt";
//        readTxt(str);
//        try {
//            Thread.sleep(3000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        writeTxt();
    }

    public static List<String> readTxtForList() {
        List<String> resultList = new ArrayList<>();
        try {
            File file = new File("D:\\no-filePath-insertTime.txt");
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);
            String str = null;
            while ((str = bf.readLine()) != null) {
                resultList.add(str);
            }
            log.info("数据读取完毕");
            IOUtils.closeQuietly(bf);
            IOUtils.closeQuietly(inputReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static void readTxt(BlockingQueue<String> blockingQueue) {
        try {
            File file = new File("D:\\filePath.txt");
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);
            String str = null;
            while ((str = bf.readLine()) != null) {
                blockingQueue.put(str);
            }
            log.info("数据读取完毕");
            IOUtils.closeQuietly(bf);
            IOUtils.closeQuietly(inputReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> extract(BlockingQueue<String> blockingQueue) {
        List<String> list = new ArrayList<>();
        Set set = new HashSet();
        List<String> errorList = new ArrayList<>();
        try {
            int i = 0;
            String photoPlateNum = null;
            while (!blockingQueue.isEmpty()) {
                String take = blockingQueue.take();
                photoPlateNum = take.substring(take.lastIndexOf("_") + 1, take.length())
                        .replace(".jpg", "")
                        .replace(".JPG", "")
                        .replace(".jpeg", "");
                list.add(photoPlateNum);
                if (!set.add(photoPlateNum)) {
                    errorList.add(photoPlateNum);
                }
            }
            WriteTxtFile.writeTxtForError(errorList);
            System.out.println("处理数据：" + i++ + " 条");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> read(String fileName) {
        List<String> resultList = new ArrayList<>();
        try {
            File file = new File("D:\\" + fileName);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);
            String str = null;
            while ((str = bf.readLine()) != null) {
                resultList.add(str);
            }
            log.info("数据读取完毕");
            IOUtils.closeQuietly(bf);
            IOUtils.closeQuietly(inputReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
