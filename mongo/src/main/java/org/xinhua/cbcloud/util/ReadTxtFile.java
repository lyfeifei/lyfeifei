package org.xinhua.cbcloud.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReadTxtFile {

    private static Logger log = LoggerFactory.getLogger(ReadTxtFile.class);

    static BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        //readTep();
        //String str = "/dagdata/reporter_temp/dag_pic/101002020040990001439_INFMX-PEA00800413.jpg";
        //System.out.println(str.substring(str.lastIndexOf("_") + 1, str.length()).replace(".jpg", ""));
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

    public static void readTxt(BlockingQueue<String> blockingQueue) {
        try {
            File file = new File("D:\\reporter_temp.txt");
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
        try {
            int i = 0;
            String photoPlateNum = null;
            while (!blockingQueue.isEmpty()) {
                String take = blockingQueue.take();
                photoPlateNum = take.substring(take.lastIndexOf("_") + 1, take.length())
                        .replace(".jpg", "")
                        .replace(".JPG", "")
                        .replace(".jpeg", "");
                i++;
                list.add(photoPlateNum);
            }
            System.out.println("处理数据：" + i + " 条");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }
}
