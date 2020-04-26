import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.FileWriter;   // Import the FileWriter class
import java.awt.*;
import java.awt.color.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.swing.JFrame;


public class data_actions {
	private static int x_length 	= 0;
	private static int y_lenght 	= 0;
	private static int added_pxl 	= 0;
	private static String ImageName = "lena";
	private static String EXT 		= "png";
	private static int type 		= 0;
	private static int width 		= 0;
	private static int height 		= 0;
	
	
	public data_actions(int t)
	{
		/*
		Type: 
		0 : colored images RGBA
		1 : colored images RGB
		2 : Gray images
		3 : images File
		*/
		type = t;
	}
	
	public static byte[][] get_data()
	{
		byte[][] x;
		switch(type)
		{
			case 0:
			default:
				x = get_color();
			break;
			case 1:
				x = get_color_8();
			break;
			case 2:
				x = get_gray();
			break;
			case 3:
				x = get_image_byte();
			break;
		}
		return x;
		
	}
	
	public static void set_data(byte[][] data)
	{
		
		switch(type)
		{
			case 0:
				set_color(data);
			break;
			case 1:
				set_color_8(data);
			break;
			case 2:
				set_gray(data);
			break;
			case 3:
				set_image_byte(data);
			break;
		}
	}
	
	
	private static byte[][] get_color()
	{
		//Get Or Generate input data
		//write image
		try{
			File input = new File("img/"+ImageName+"."+EXT);
			BufferedImage image = ImageIO.read(input);
			
			width = image.getWidth();
			height = image.getHeight();
			
			if((width*height) % 16 != 0)
			{
				added_pxl = 16 - ((width*height) % 16);
			}
			
			int count = 0;
			byte in[][] = new byte[4][(width * height) + added_pxl];
			for(int i=0; i<height; i++) 
			{
				for(int j=0; j<width; j++) 
				{
					Color c = new Color(image.getRGB(j, i));
					if( c.getRed() > 255 || c.getGreen() > 255 || c.getBlue() > 255 || c.getAlpha() > 255 )
					{
						System.out.println( c.getRed()+" --- "+ c.getGreen() +" --- "+ c.getBlue() +" --- "+ c.getAlpha() );
					}						
					in[0][count] = (byte) c.getRed(); //RED
					in[1][count] = (byte) c.getGreen(); //Green
					in[2][count] = (byte) c.getBlue(); //Blue
					in[3][count] = (byte) c.getAlpha(); //Alfa
					count++;
				}
			}
			
			if( added_pxl != 0)
			{
				for(int i=0; i< added_pxl; i++)
				{
					in[0][count] = (byte) 0x00;
					in[1][count] = (byte) 0x00;
					in[2][count] = (byte) 0x00;
					in[3][count] = (byte) 0x00;
					count++;
				}				
			}
			
			return in;
		}catch(Exception e){
			System.out.println("Error in write image: " + e);
			return null;
		}
	}
	
	public static byte[][] get_gray()
	{
		try{
			File input = new File("img/"+ImageName+"."+EXT);
			BufferedImage image = ImageIO.read(input);
			
			width = image.getWidth();
			height = image.getHeight();
			
			if((width*height) % 16 != 0)
			{
				added_pxl = 16 - ((width*height) % 16);
			}
			
			int count = 0;
			int gray = 0,del;
			byte in[][] = new byte[2][(width * height) + added_pxl];
			for(int i=0; i<height; i++) 
			{
				for(int j=0; j<width; j++) 
				{
					Color c = new Color(image.getRGB(j, i));
					if( c.getRed() > 255 || c.getGreen() > 255 || c.getBlue() > 255 || c.getAlpha() > 255 )
					{
						System.out.println( c.getRed()+" --- "+ c.getGreen() +" --- "+ c.getBlue() +" --- "+ c.getAlpha() );
					}
					gray = (c.getRed() + c.getGreen() + c.getBlue()) /3	;				
					if(gray > 127)
					{
						del = gray - 128;
						in[0][count] = (byte) 127;
						in[1][count] = (byte) del;
					}else
					{
						in[0][count] = (byte) gray;
						in[1][count] = (byte) 0;
					}
					count++;
				}
			}
			
			if( added_pxl != 0)
			{
				for(int i=0; i< added_pxl; i++)
				{
					in[0][count] = (byte) 0x00;
					in[1][count] = (byte) 0x00;
					count++;
				}				
			}
			
			return in;
			
		} catch (Exception e) {
			System.out.println("An error occurred..\n Read Files");
			return null;
		}
	}
	
	private static byte[][] get_color_8()
	{
		//Get Or Generate input data
		//write image
		try{
			File input = new File("img/"+ImageName+"."+EXT);
			BufferedImage image = ImageIO.read(input);
			
			width = image.getWidth();
			height = image.getHeight();
			
			if((width*height) % 16 != 0)
			{
				added_pxl = 16 - ((width*height) % 16);
			}
			
			int count = 0;
			byte in[][] = new byte[8][(width * height) + added_pxl];
			
			for(int i=0; i<height; i++) 
			{
				for(int j=0; j<width; j++) 
				{
					Color c = new Color(image.getRGB(j, i));
					int r = c.getRed(); //RED
					int g = c.getGreen(); //Green
					int b = c.getBlue(); //Blue
					int a = c.getAlpha(); //Alfa
					int del = 0;
					//RED
					if(r > 127)
					{
						del = r - 128;
						in[0][count] = (byte) 127;
						in[4][count] = (byte) del;
					}else
					{
						in[0][count] = (byte) r;
						in[4][count] = (byte) 0;
					}
					del = 0;
					//Green
					if(g > 127)
					{
						del = g - 128;
						in[1][count] = (byte) 127;
						in[5][count] = (byte) del;
					}else
					{
						in[1][count] = (byte) g;
						in[5][count] = (byte) 0;
					}
					del = 0;
					//Blue
					if(b > 127)
					{
						del = b - 128;
						in[2][count] = (byte) 127;
						in[6][count] = (byte) del;
					}else
					{
						in[2][count] = (byte) b;
						in[6][count] = (byte) 0;
					}
					del = 0;
					//Alfa
					if(a > 127)
					{
						del = a - 127;
						in[3][count] = (byte) 127;
						in[7][count] = (byte) del;
					}else
					{
						in[3][count] = (byte) a;
						in[7][count] = (byte) 0;
					}
					count++;
				}
			}
			
			if( added_pxl != 0)
			{
				for(int i=0; i< added_pxl; i++)
				{
					in[0][count] = (byte) 0x00;
					in[1][count] = (byte) 0x00;
					in[2][count] = (byte) 0x00;
					in[3][count] = (byte) 0x00;
					in[4][count] = (byte) 0x00;
					in[5][count] = (byte) 0x00;
					in[6][count] = (byte) 0x00;
					in[7][count] = (byte) 0x00;
					count++;
				}				
			}
			
			return in;
		}catch(Exception e){
			System.out.println("Error in write image: " + e);
			return null;
		}
	}
	
	public static byte[][] get_image_byte()
	{
		try{
			File input = new File("img/"+ImageName+"."+EXT);
			BufferedImage image = ImageIO.read(input);
			
			
			byte[][] imageInByte;
			BufferedImage originalImage = ImageIO.read(new File(ImageName));

			// convert BufferedImage to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, EXT, baos);
			baos.flush();
			byte[] i = baos.toByteArray();
			baos.close();
			
			int x = i.length % 16;
			if(x != 0)
			{
				added_pxl = 16 - x;
				imageInByte = new byte[1][i.length + added_pxl];
				System.arraycopy( i,0,imageInByte,0,i.length);
			}else
			{
				imageInByte = new byte[1][i.length];
				imageInByte[0] = i;
			}
			return imageInByte;
			
		} catch (Exception e) {
			System.out.println("An error occurred..\n Read Files");
			return null;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static void set_color(byte[][] data)
	{
		//write image
		try{
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			
			//create random image pixel by pixel
			int count = 0;
			int a,r,g,b;
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					a = data[3][count] & 0xFF;
					r = data[0][count] & 0xFF;
					g = data[1][count] & 0xFF;
					b = data[2][count] & 0xFF;
					int p = (a<<24) |(r<<16) | (g<<8) | b; //pixel
					img.setRGB(x, y, p);
					count++;
				}
			}
			
			File f = new File("enc_img/"+ImageName+"_"+System.currentTimeMillis()+"."+EXT);
			ImageIO.write(img, EXT, f);
		}catch(Exception e){
			System.out.println("Error in write image: " + e);
		}
	}
	
	private static void set_color_8(byte[][] data)
	{
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		//create random image pixel by pixel
		int count = 0;
		int r,g,b,a;
		
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				r = (int)data[0][count] + (int)data[4][count];
				g = (int)data[1][count] + (int)data[5][count];
				b = (int)data[2][count] + (int)data[6][count];
				a = (int)data[3][count] + (int)data[7][count];
				
				int p = (a<<24) |(r<<16) | (g<<8) | b; //pixel
				img.setRGB(x, y, p);
				count++;
			}
		}
		
		//write image
		try{
			File f = new File("enc_img/"+ImageName+"_"+System.currentTimeMillis()+"."+EXT);
			ImageIO.write(img, EXT, f);
		}catch(Exception e){
			System.out.println("Error in write image: " + e);
		}
	}
	
	private static void set_gray(byte[][] data)
	{
		int[] ndata = new int[data[0].length - added_pxl];
		int a,b;
		try{
			
			for(int i=0;i<ndata.length;i++)
			{
				a = (int) data[0][i] & 0xFF;
				b = (int) data[1][i] & 0xFF;
				ndata[i] = a + b ;
			}
			
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			int[] nBits = { 8 };
			ColorModel cm = new ComponentColorModel(cs, nBits, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
			
			SampleModel sm = cm.createCompatibleSampleModel(width, height);
			DataBufferInt db = new DataBufferInt(ndata, width * height);
			WritableRaster raster = Raster.createWritableRaster(sm, db, null);
			BufferedImage img = new BufferedImage(cm, raster, false, null);
			
			File f = new File("enc_img/"+ImageName+"_"+System.currentTimeMillis()+"."+EXT);
			ImageIO.write(img, EXT, f);
			
		} catch (Exception e) {
			System.out.println("An error occurred.\n Write Files"+e);
			
		}
	}
	
	public static void set_image_byte( byte[][] data)
	{
		if(added_pxl != 0)
		{
			byte[] ndata = new byte[data.length - added_pxl];
			System.arraycopy(data[0], 0, ndata, 0, data[0].length - added_pxl);
			data[0] = ndata;
		}
		System.out.println("Files: "+ data.length+"\n Name: "+ImageName+"\nLength: ");
		
		try{
			// convert byte array back to BufferedImage
			ByteArrayInputStream in = new ByteArrayInputStream(data[0]);
			BufferedImage bImageFromConvert = ImageIO.read(in);

//BufferedImage imageFromGrayScale = createImage(grayScaleValues, imageWidth, imageHeight);

			ImageIO.write(bImageFromConvert, EXT, new File(ImageName));
			
		} catch (Exception e) {
			System.out.println("An error occurred.\n Write Files"+e);
			
		}
	}
}