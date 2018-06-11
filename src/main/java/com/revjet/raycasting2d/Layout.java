package com.revjet.raycasting2d;

import org.openjdk.jol.info.ClassLayout;

/**
 * @author <a href="mailto:ivan.zaytsev@webamg.com">Ivan Zaytsev</a>
 * 2018-06-10
 */
public class Layout {

    public static void main(String[] args) {
        System.out.println(ClassLayout.parseClass(Lighting.T.class).toPrintable());
    }
}
