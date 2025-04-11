package com.web.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

@Service
public class FindByImage {

    public Double compare(String src, File source) throws IOException {
        URL url = new URL(src);
        System.out.println("yrds"+url);

        BufferedImage imgsource = ImageIO.read(source);
        Image thumbnail1 = imgsource.getScaledInstance(8, 8, Image.SCALE_AREA_AVERAGING);
        BufferedImage img2 = convertImageTo(thumbnail1);

        Image thumbnail = ImageIO.read(url);
        BufferedImage imglg =null;
        try {
            imglg = convertImageTo(thumbnail);
        }catch (Exception e){return null;}
        Image thumbnailres = imglg.getScaledInstance(8, 8, Image.SCALE_AREA_AVERAGING);
        BufferedImage img1 = convertImageTo(thumbnailres);


        int width1 = img1.getWidth();
        int width2 = img2.getWidth();
        int height1 = img1.getHeight();
        int height2 = img2.getHeight();
        if ((width1 != width2) || (height1 != height2)) {
            throw new IllegalArgumentException("Error: Images dimensions mismatch");
        }

        int diff2 = 0;

        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                int rgb1 = img1.getRGB(j, i);
                int rgb2 = img2.getRGB(j, i);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;

                diff2 += Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2);
            }
        }
        return diff2 * 1.0 / (height1*width1);
    }

    public BufferedImage convertImageTo(Image img){
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

}
