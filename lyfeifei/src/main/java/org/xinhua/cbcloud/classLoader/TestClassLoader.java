package org.xinhua.cbcloud.classLoader;

public class TestClassLoader {

    public static void main(String[] args) throws Exception {

        MyClassLoader loader1 = new MyClassLoader("loader1");
        loader1.setPath("/org/xinhua/cbcloud/classLoader/");

        MyClassLoader loader2 = new MyClassLoader(loader1, "loader2");
        loader2.setPath("/org/xinhua/cbcloud/classLoader/");

        // 传null表示使用跟加载器，而此类不属于跟加载器的加载范围，所以由loader3来加载，由此实现向下委派从而打破双亲委派机制
        MyClassLoader loader3 = new MyClassLoader(null, "loader3");
        //loader3.setPath("/org/xinhua/cbcloud/classLoader/");

        loadClassByMyClassLoader("org.xinhua.cbcloud.classLoader.Red", loader2);
        //loadClassByMyClassLoader("org.xinhua.cbcloud.classLoader.Red", loader3);
    }

    private static void loadClassByMyClassLoader(String name, ClassLoader loader) throws Exception {
        System.out.println(loader.getParent());
        Class<?> c = loader.loadClass(name);
        Object obj = c.newInstance();
    }
}
