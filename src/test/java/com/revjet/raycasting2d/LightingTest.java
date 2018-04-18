package com.revjet.raycasting2d;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:ivan.zaytsev@webamg.com">Ivan Zaytsev</a>
 * 2018-04-17
 */
public class LightingTest {
    @Test
    public void testRecalcLighting() {

        Lighting l = new Lighting(3, 3);

        int[][] objects = new int[][]{
                {0, 0, 0},
                {1, 0, 0},  // <-  this light is actually on top (whole thing is rotated CCW)
                {0, 0, 0}
        };

        l.recalculateLighting(objects, 1.0 / 3);

        for (int y = 0; y < l.h; y++) {
            for (int x = 0; x < l.w; x++) {
                System.out.print(l.getLight(x, y));
                System.out.print(", ");
            }
            System.out.println();
        }

        Assert.assertEquals(l.getLight(0, 0), 0.0);
        Assert.assertEquals(l.getLight(2, 0), 0.0);

        Assert.assertEquals(l.getLight(1, 0), 2.0, 0.0001);

        Assert.assertTrue(l.getLight(1, 0) >= l.getLight(1, 1));
        Assert.assertTrue(l.getLight(1, 1) >= l.getLight(1, 2));

        Assert.assertTrue(l.getLight(1, 1) >
                l.getLight(1, 2)
        );

        Assert.assertEquals(l.getLight(0, 2), l.getLight(2, 2), 0.01);
    }

    @Test
    public void testGenerateImage() throws IOException {
        int w = 100;
        int h = 100;

        Lighting l = new Lighting(w, h);

        int[][] objects = new int[w][h];
        objects[0][w / 2]     = 1;
        objects[0][w / 3]     = 1;
        objects[0][w * 2 / 3] = 1;

        objects[h / 2]    [w / 2]  = 2;
        objects[h / 2 + 1][w / 2] = 2;
        objects[h / 2 + 2][w / 2] = 2;

        l.recalculateLighting(objects, 1.0 / w / 10);

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < l.h; y++) {
            for (int x = 0; x < l.w; x++) {
                double brightness = Math.min(1, l.light[x][y] * 50 + 0.01);
                int comp = (int) (0xff * brightness);
                int rgb = (((comp << 8) | comp) << 8) | comp;
                bi.setRGB(x, y, rgb);
            }
        }

        File outputfile = new File("img.png");
        ImageIO.write(bi, "png", outputfile);
    }
}