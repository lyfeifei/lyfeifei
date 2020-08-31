package org.xinhua.cbcloud.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadJson {

    /**
     * 将字符串转化为Reader流
     * 这一种方式只是用于演示功能,因为整个字符串会加载在内存中,并不能降低内存消耗
     *
     * @return
     */
    public static Reader getStringReader(String jsonString) {
        Reader reader = new StringReader(jsonString);//转化为流
        return reader;
    }

    /**
     * 获取文件Reader流,推荐这一种方式
     *
     * @param pathFile json文件路径
     * @return
     */
    public static Reader getFileReader(String pathFile) throws FileNotFoundException {
        Reader reader = new FileReader(pathFile);//转化为流
        return reader;
    }

    /**
     * 获取其它Reader流,推荐http响应流等等
     *
     * @param inputStream 流
     * @return
     */
    public static Reader getHttpResponseReader(InputStream inputStream) {
        Reader reader = new InputStreamReader(inputStream);
        return reader;
    }

    /**
     * 按照JSON数组的方式解析
     * 数据示例: [{},{}]
     */
    public static void analysisArray(Reader reader) {
        JSONReader jsonArray = new JSONReader(reader);//传入流
        jsonArray.startArray();//相当于开始读整个json的Object对象。
        while (jsonArray.hasNext()) {//是否有下一个对象 {}
            Object jsonOne = jsonArray.readObject();
            //处理单个JSON对象内容 {}.这里可以再一次使用流式解析analysisObject,解析单个对象
            System.out.println(jsonOne);
        }
        jsonArray.endArray();//结束读取
        jsonArray.close();//关闭reader流
    }

    /**
     * 按照JSON对象的方式解析
     * 数据示例: {"k1":"v1","k2":"v2"}
     */
    public static void analysisObject(Reader reader) {
        JSONReader jsonObject = new JSONReader(reader);//传入流
        jsonObject.startObject();//相当于开始读整个json的Object对象。
        while (jsonObject.hasNext()) {//是否有下一个k-v值
            Object key = jsonObject.readObject();//获取key
            Object value = jsonObject.readObject();//获取value
            System.out.println(key + ":" + value);//处理kv值
        }
        jsonObject.endObject();//结束读取
        jsonObject.close();//关闭reader流
    }

    /**
     * 测试用例
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        File file = new File("D:\\DAG_UNFINISHED_PLATE_0824.dat");
        JSONReader reader = new JSONReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
        reader.startObject();
        while (reader.hasNext()) {
            Object value = null;
            Object key = reader.readObject();
            if ("docIds".equals(key)) {
                value = reader.readObject();
            }
            if ("photoPlateNum".equals(key)) {
                value = reader.readObject();
            }
            System.out.println(key + ":" + value);
        }
        reader.endObject();
        reader.close();
    }
}
