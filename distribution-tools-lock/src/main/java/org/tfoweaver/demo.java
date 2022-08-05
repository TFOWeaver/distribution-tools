package org.tfoweaver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description:
 * @title: demo
 * @Author Star_Chen
 * @Date: 2022/8/3 16:05
 * @Version 1.0
 */
public class demo {

    public static void main(String[] args) {

        //多的  目前的release
        File file2 = new File("C:\\Users\\Administrator\\Desktop\\beforelib\\lib");
        //少的  目前的性能环境
        File file = new File("C:\\Users\\Administrator\\Desktop\\afterlib\\lib");


        List<String> set1 = new ArrayList<>();
        List<String> set2 = new ArrayList<>();

        //性能环境
        printName(file, set1);
        // release环境
        printName(file2, set2);

        List<String> collect = set2.stream().filter(num -> !set1.contains(num)).collect(Collectors.toList());
        System.out.println("目前的release环境对比 性能环境的jar区别："+collect);

    }

    public static void printName(File file, List<String> set) {

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                set.add(f.getName());
                printName(f, set);//递归调用
            }
        }
    }
}
