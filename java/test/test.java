import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.io.FileWriter;   // Import the FileWriter class


public class test {
	static BufferedImage image;
	static BufferedImage image2;
	static int width;
	static int height;
	
	
	public static void main(String args[])
	{
		try
		{
			File input = new File("img/a.jpg");
			File input2 = new File("img/b.jpg");
			image = ImageIO.read(input);
			image2 = ImageIO.read(input2);
			width = image.getWidth();
			height = image.getHeight();
         	int count = 0;
			FileWriter myWriter = new FileWriter("Results.csv");
			myWriter.write("S.No , Red,Red1,Green,Green1,Blue,Blue1,Alfa,Alfa1 \n");
			for(int i=0; i<height; i++) 
			{
				for(int j=0; j<width; j++) 
				{
					count++;
					Color c = new Color(image.getRGB(j, i));
					Color c2 = new Color(image2.getRGB(j, i));
					myWriter.write(count+","+c.getRed()+","+c2.getRed()+","+c.getGreen()+","+c2.getGreen()+","+c.getBlue()+","+c2.getBlue()+","+ c.getAlpha()+","+ c2.getAlpha()+"\n");
					
				}
			}
			myWriter.close();
		}catch (Exception e) {
			System.out.println("Error: "+e);
		}
		/*
		byte x;
		int u;
		for(int i=0;i<256; i++)
		{
			x = (byte) i;
			u = (int) x;
			System.out.println("Main: "+i+" --- BY: "+x + " --- U: "+ u);
		}*/
	}
}


/*

class Pixel {
   
   
   public Pixel() {
      
   }
   
   static public void main(String args[]) throws Exception {
      Pixel obj = new Pixel();
   }
}*/