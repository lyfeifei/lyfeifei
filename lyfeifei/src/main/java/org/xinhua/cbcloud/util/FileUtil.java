package org.xinhua.cbcloud.util;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {

    public static void write(String readData) {

        File file = new File("D:\\docLog.txt");

        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        try {
            fileWriter = new FileWriter(file);

            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(readData + "\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void read() {
        File file = new File("D:\\docLog.txt");

        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        List<String> docLogList = new LinkedList<String>();

        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String readData = "{id=2019111506542869500, docId='null', messageId='1569482824826#b807cc4a-6258-4f61-a445-c97006d9512c', context='111111'}";
            docLogList = new ArrayList<>();

            while ((readData = bufferedReader.readLine()) != null) {
                try {
                    docLogList.add(readData);//读出所有数据，存入ArrayList中
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void method(String conent) {
        File file = new File("D:\\docLog.txt");
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file, true)));
            out.write(conent + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
