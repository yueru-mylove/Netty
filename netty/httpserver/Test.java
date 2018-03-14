package com.phei.netty.httpserver;

import java.io.File;

public class Test {

    @org.junit.Test
    public void test() {

        System.out.println(File.separator);
        System.out.println(File.separatorChar);
        System.out.println(System.getProperty("user.dir") + File.separator);

        File file = new File("/src/main/java/com/");
        System.out.println(file.getPath());
    }
}
