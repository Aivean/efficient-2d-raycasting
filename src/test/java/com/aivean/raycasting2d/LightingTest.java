package com.aivean.raycasting2d;

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
    @Test(enabled = false)
    public void testRecalcLighting() {
        Lighting l = new Lighting(3);

        int[][] objects = new int[][]{
                {0, 1, 0},
                {0, 0, 0},
                {0, 0, 0}
        };

        l.setInputRotated(objects, Rotation.NO);
        l.recalculateLighting(1F / 3);

        for (int y = 0; y < l.fieldSize; y++) {
            for (int x = 0; x < l.fieldSize; x++) {
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
        int size = 100;

        Lighting l = new Lighting(size);

        int[][] objects = new int[size][size];
        objects[0][size / 2] = 1;
        objects[0][size / 3] = 1;
        objects[0][size * 2 / 3] = 1;

        objects[size / 2][size / 2] = 2;
        objects[size / 2 + 1][size / 2] = 2;
        objects[size / 2 + 2][size / 2] = 2;

        l.setInputRotated(objects, Rotation.NO);
        l.recalculateLighting(1F / size / 10);

        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < l.fieldSize; y++) {
            for (int x = 0; x < l.fieldSize; x++) {
                double brightness = Math.min(1, l.getLight(y, x) * 50 + 0.01);
                int comp = (int) (0xff * brightness);
                int rgb = (((comp << 8) | comp) << 8) | comp;
                bi.setRGB(x, y, rgb);
            }
        }

        File outputfile = new File("result.png");
        ImageIO.write(bi, "png", outputfile);
    }
}