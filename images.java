import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

class images
{
	private static int length = 0;
	public static int add = 0;
	private static String EXT = "jpg";
	public images()
	{
		
	}
	
	public static byte[] get_image_byte(String ImageName,String Exten)
	{
		EXT = Exten;
		try{
			byte[] imageInByte;
			BufferedImage originalImage = ImageIO.read(new File(ImageName));

			// convert BufferedImage to byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, Exten, baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
			
		} catch (IOException e) {
			System.out.println("An error occurred..\n Read Files");
			return null;
		}
	}
	
	public static void set_image_byte(String ImageName , byte[] data)
	{
		
		if(add != 0)
		{
			byte[] ndata = new byte[data.length - add];
			System.arraycopy(data, 0, ndata, 0, data.length - add);
			data = ndata;
		}
		System.out.println("Files: "+ data.length+"\n Name: "+ImageName+"\nLength: "+add);
		
		try{
			// convert byte array back to BufferedImage
			InputStream in = new ByteArrayInputStream(data);
			BufferedImage bImageFromConvert = ImageIO.read(in);

			ImageIO.write(bImageFromConvert, EXT, new File(ImageName+"."+EXT ));
			
		} catch (Exception e) {
			System.out.println("An error occurred.\n Write Files"+e);
			
		}
	}
	
}