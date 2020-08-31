package org.xinhua.cbcloud.classLoader;

public class Color {
    public Color() {
        System.out.println("Color is loaded by " + this.getClass().getClassLoader());
    }
}
