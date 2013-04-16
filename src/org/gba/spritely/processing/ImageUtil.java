 package org.gba.spritely.processing;
 
 import java.awt.AlphaComposite;
 import java.awt.Graphics2D;
 import java.awt.Image;
 import java.awt.Toolkit;
 import java.awt.image.BufferedImage;
 import java.awt.image.FilteredImageSource;
 import java.awt.image.ImageFilter;
 import java.awt.image.ImageProducer;
 import java.awt.image.RGBImageFilter;
 import java.io.File;
 import java.io.IOException;
 import java.net.URL;
 import javax.imageio.ImageIO;
 
 public class ImageUtil
 {
   public static BufferedImage getFromURL(String sUrl)
   {
     BufferedImage image = null;
     BufferedImage out = null;
     try {
       URL url = new URL(sUrl);
       image = ImageIO.read(url);
       if (image == null)
         return null;
       if ((image.getWidth() > 1000) || (image.getHeight() > 1000))
         return null;
       out = new BufferedImage(image.getWidth(), image.getHeight(), 2);
       Graphics2D g2d = out.createGraphics();
       g2d.setComposite(AlphaComposite.SrcOver);
       g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
       g2d.dispose();
     }
     catch (Exception localException)
     {
     }
 
     return out;
   }
 
   public static BufferedImage writeToFile(BufferedImage image, String file)
   {
     BufferedImage resultImage1 = null;
     try {
       BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), 2);
       Graphics2D g2d = out.createGraphics();
       g2d.setComposite(AlphaComposite.Clear);
       g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
       g2d.setComposite(AlphaComposite.SrcOver);
       g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
       g2d.dispose();
 
       Image transpImg1 = TransformGrayToTransparency(image);
       resultImage1 = ImageToBufferedImage(transpImg1, image.getWidth(), image.getHeight());
 
       File outputfile = new File(file);
       outputfile.mkdirs();
       ImageIO.write(resultImage1, "png", outputfile);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     return resultImage1;
   }
 
   private static Image TransformGrayToTransparency(BufferedImage image) {
     ImageFilter filter = new RGBImageFilter() {
       public final int filterRGB(int x, int y, int rgb) {
         if (rgb == 0) {
           return 0;
         }
 
         return rgb;
       }
     };
     ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
     return Toolkit.getDefaultToolkit().createImage(ip);
   }
 
   private static BufferedImage ImageToBufferedImage(Image image, int width, int height) {
     BufferedImage dest = new BufferedImage(width, height, 2);
     Graphics2D g2 = dest.createGraphics();
     g2.drawImage(image, 0, 0, null);
     g2.dispose();
     return dest;
   }
 }

/* Location:           /develop/libs/spritely/
 * Qualified Name:     org.gba.spritely.processing.ImageUtil
 * JD-Core Version:    0.6.2
 */
