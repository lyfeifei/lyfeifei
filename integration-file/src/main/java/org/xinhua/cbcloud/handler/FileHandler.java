package org.xinhua.cbcloud.handler;

import java.io.File;

public class FileHandler {
    public File run(File file) {
        try {
            //只处理xml文件，其他文件直接跳过不处理。后续会自动移除到指定目录
            String fileName=file.getName();
            String fileType=fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
            if(fileType.equalsIgnoreCase("xml")) {
                System.out.println(fileName);
            }else {
                System.out.println("此格式文件不处理，要处理的文件名name：{}" + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
