package org.xinhua.cbcloud.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class WriteTxtFile {

    public static void main(String[] args) {
        List<Integer> lists = new ArrayList<>();
        for (int i = 1; i <= 26; i++) {
            lists.add(i);
        }
        listBatchUtil(lists);
    }

    public static void listBatchUtil(List<Integer> lists) {
        int temp = 1;
        for (int i = 0; i < lists.size(); i += 10) {
            System.out.println("======================进行第" + temp + "次批处理=======================");
            if (lists.size() - i > 10) {
                System.out.println("@" + lists.subList(i, i + 10).toString());
            } else {
                if (lists.size() > i) {
                    System.out.println(lists.subList(i, lists.size()).toString());
                }
            }
            temp += 1;
        }
    }

    public static void writeTxt(String str) {
        try {
            File file = new File("D:\\writeTxt.txt");
            FileWriter out = new FileWriter(file);
            out.write(str + "\r\n");
            IOUtils.closeQuietly(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeTxt(List<String> list) {
        try {
            File file = new File("D:\\writeTxt.txt");
            FileWriter out = new FileWriter(file);
            for (int i = 0; i < list.size(); i++) {
                out.write(list.get(i) + "\r\n");
            }
            IOUtils.closeQuietly(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
