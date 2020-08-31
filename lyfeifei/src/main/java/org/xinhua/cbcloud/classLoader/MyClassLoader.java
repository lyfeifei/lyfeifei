package org.xinhua.cbcloud.classLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MyClassLoader extends ClassLoader {

    private String path = "/target/classes/";    //默认加载路径

    private String name;                    //类加载器名称

    private final String filetype = ".class"; //文件类型

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("findClass");
        byte[] b = loadClassData(name);
        return defineClass(name, b, 0, b.length);
    }

    /**
     * 构造方法中去调用ClassLoader无参构造方法从ClassLoader源码中可以得出：
     *  调用此构造方法会让系统类加载器成为该类加载器的父加载器
     * @param name
     */
    public MyClassLoader(String name) {
        super();
        this.name = name;
    }

    public MyClassLoader(ClassLoader parent, String name) {
        super(parent);
        this.name = name;
    }

    private byte[] loadClassData(String name) {
        byte[] data = null;
        InputStream in = null;
        name = name.replace('.', '/');
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            String filePath = path + name + filetype;
            System.out.println(filePath);
            in = new FileInputStream(new File(filePath));
            int len = 0;
            while (-1 != (len = in.read())) {
                out.write(len);
            }
            data = out.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
