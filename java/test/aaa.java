import java.math.BigInteger; 
import java.io.FileWriter;   // Import the FileWriter class
public class aaa
{
	public static void main(String args[])
	{
		byte a[];
		int b[],c[];
		try{
			//FileWriter myWriter = new FileWriter("Results_no.csv");
			//myWriter.write("INT MAIN , BYTE , AND BYTE , INT BYTE \n");
			for(int i = 128 ;i< 150;i++)
			{
				a[i] = (byte) i;
				b[i] = i;
				c[i] = (int)a;
				//myWriter.write(i +" , "+ a +" , "+ b+ " , "+c+" \n");
			}
			//myWriter.close();
		} catch (Exception e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		BigInteger bigInt = BigInteger.valueOf(1000000); 
  
        // create a byte array 
        byte b1[] = bigInt.toByteArray(); 
		System.out.println("LEngth: "+b1.length);
		
		$as[] = bkg_xor[]
		
		
	}//main() ends here
	
	private static byte[] bkg_xor(byte[] x, byte[] y)
	{
		if(x.length != y.length)
		{
			System.out.println("Eror Length: In bkg_xor: "+x.length +" -- "+ y.length);
			return null;
		}else{
			byte[] result = new byte[x.length];
			for(int i=0;i<result.length;i++)
			{
				result[i] = (byte) ((int)x[i] ^ (int)y[i]);
			}
			return result;
		}
	}
}//class ends here