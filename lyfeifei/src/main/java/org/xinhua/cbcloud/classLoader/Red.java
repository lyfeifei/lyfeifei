package org.xinhua.cbcloud.classLoader;

public class Red extends Color {
    public Red() {
        System.out.println("Red is loaded by "+this.getClass().getClassLoader());
    }
}
