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

public class tt {
	public static byte[] get_image_byte(String ImageName,String EXT)
	{
		try{
			File input = new File("img/"+ImageName+"."+EXT);
			BufferedImage image = ImageIO.read(input);
			byte[][] imageInByte;
			
			// convert BufferedImage to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, EXT, baos);
			baos.flush();
			byte[] i = baos.toByteArray();
			baos.close();
			
			return i;
			
		} catch (Exception e) {
			System.out.println("An error occurred..\n Read Files \n"+ImageName+"\n"+e);
			return null;
		}
	}
	
	public static void main(String args[])
	{
		byte [] a = get_image_byte("a","png");
		byte [] b = get_image_byte("b","png");
		byte [] c = get_image_byte("baboon","png");
		byte [] d = get_image_byte("Lena","png");
		
		int max = a.length;
		
		int ai,bi,ci,di;
		if(b.length > max)
		{
			max = b.length;
		}
		if(c.length > max)
		{
			max = c.length;
		}
		if(d.length > max)
		{
			max = d.length;
		}
		
		try{
			
			FileWriter myWriter = new FileWriter("kk.csv");
			for(int i=0;i<max;i++)
			{
				if(i >= a.length)
				{
					myWriter.write("--,");
					ai =0;
				}else
				{
					ai = (int) a[i];
					myWriter.write(ai+",");
				}
				if(i >= b.length)
				{
					myWriter.write("--,");
					bi = 0;
				}else
				{
					bi = (int) b[i];
					myWriter.write(bi+",");
				}
				if(i >= c.length)
				{
					myWriter.write("--,");
					ci = 0;
				}else
				{
					ci = (int) c[i];
					myWriter.write(ci+",");
				}
				if(i >= d.length)
				{
					myWriter.write("--,");
					di = 0;
				}else
				{
					di = (int) d[i];
					myWriter.write(di+",");
				}
				if(ai == bi && ai == ci && ai == di)
				{
					myWriter.write("MATCH");
				}
				myWriter.write("\n");
			}
			
			myWriter.write("Execution Type : SEQ \n");
			
			myWriter.close();
		} catch (Exception e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}