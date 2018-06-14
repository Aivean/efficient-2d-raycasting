package com.aivean.raycasting2d;

import org.openjdk.jol.info.ClassLayout;

/**
 * Prints layout for the class Beam
 */
public class Layout {

    public static void main(String[] args) {
        System.out.println(ClassLayout.parseClass(Lighting.Beam.class).toPrintable());
    }
}
