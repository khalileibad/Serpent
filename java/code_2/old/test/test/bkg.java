import java.security.MessageDigest;
//import java.io.PrintWriter;
//import java.security.InvalidKeyException;
//import java.util.*;
//import java.math.*;
//import java.lang.*;

class bkg
{
	public static byte[][] keys;
	public static boolean error = false;
	public static String error_text = "";
	
	
	public bkg(byte[] main_key, int no_keys)
	{
		keys = new byte[no_keys][main_key.length];
		
		block_key_generation(main_key,no_keys);
		
	}
	
	private static void block_key_generation(byte[] key,int block_no)
	{
		byte[] curr_key = key;
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			for(int i=0;i<block_no;i++)
			{
				//System.out.println("BKG: Key "+i+" :\n before: "+String.valueOf(get_char_bit(curr_key))+"\n len: "+curr_key.length);
				curr_key = bkg_prumitation(curr_key);
				//System.out.println("Prum: "+String.valueOf(get_char_bit(curr_key))+"\n len: "+curr_key.length);
				curr_key = bkg_xor(key,curr_key);
				//System.out.println("XOR: "+String.valueOf(get_char_bit(curr_key))+"\n len: "+curr_key.length);
				curr_key = digest.digest(curr_key);
				//System.out.println("BKG "+i+" : "+String.valueOf(get_char_bit(curr_key))+" - len: "+curr_key.length);
				//System.out.println("\n\n");
				keys[i] = curr_key;
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			error = true;
			error_text += "\n"+ex.getMessage();
        }
	}
	
	// prumitation
	private static byte[] bkg_prumitation(byte[] key)
	{
		byte[] left		= new byte[key.length /2];
		byte[] right	= new byte[key.length /2];
		byte[] result	= new byte[key.length];
		result = key;
		System.arraycopy(key, 0, left, 0 , key.length /2);
		System.arraycopy(key, key.length /2, right, 0 , key.length /2);
		
		left 	= bkg_shift(left,4);
		right 	= bkg_nibble(right);
		left	= bkg_xor(left,right);
		
		result = bkg_concate(right,left);
		return result;
	}
	
	// SHIFT
	private static byte[] bkg_shift(byte[] y, int Move)
	{
		char[] L = get_char_bit(y);
		char[] M = new char[L.length];
		
		int x;
		for(int i= 0; i<L.length;i++)
		{
			x = (i+ Move)% L.length;
			M[i] = L[x];
		}
		
		return get_byte_bit(M);
	}
	
	//nibble
	private static byte[] bkg_nibble(byte[] input)
	{
		char[] L = get_char_bit(input);
		char[] M = new char[L.length];
		
		if(L.length % 8 == 0)
		{
			for(int i=0;i<L.length; i+= 8)
			{
				M[i		] = L[i + 4];
				M[i + 1 ] = L[i + 5];
				M[i + 2 ] = L[i + 6];
				M[i + 3 ] = L[i + 7];
				
				M[i + 4 ] = L[i + 0];
				M[i + 5 ] = L[i + 1];
				M[i + 6 ] = L[i + 2];
				M[i + 7 ] = L[i + 3];
			}
			return get_byte_bit(M);
		}else{
			System.out.println("EEEEEEEEERRRRRRRRRRRRRRRRRROOOOOOOOOOOOOOOOORRRRRRRRR --- "+L.length);
			return null;
		}
	}
	
	//XOR
	private static byte[] bkg_xor(byte[] x, byte[] y)
	{
		if(x.length != y.length)
		{
			System.out.println("Eeeeeeeeerrrrrrrrrrrrroooooooooooorrrrrrrrr Length: "+x.length +" -- "+ y.length);
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
	
	//concate
	private static byte[] bkg_concate(byte[] L,byte[] R)
	{
		byte[] result = new byte[L.length + R.length];
		
		System.arraycopy(L, 0, result, 0 , L.length);
		System.arraycopy(R, 0, result, L.length , R.length);
		
		return result;
	}
	
	
	
	public static char[] get_char_bit(byte[] input)
	{
		String res = "";
		for (byte b : input) {
			res += Integer.toBinaryString(b & 255 | 256).substring(1);
		}
		return res.toCharArray();
	}
	
	public static byte[] get_byte_bit(char[] input)
	{
		String s = String.valueOf(input);
		
		/*int len = input.length / 8;
		
		if(input.length % 8 != 0)
		{
			len ++;
		}
		byte[] result = new byte[len];
		
		int i = 0;
		int j = 0;
		try{
			while(i+8 <= input.length)
			{
				result[j] = Byte.valueOf(s.substring(i, i+8), 2);
				i+=8;
				j++;
			}
			result[j] = Byte.valueOf(s.substring(i, input.length));
		}catch(Exception ex) {
			System.out.println(ex.getMessage()+"\n kkk i: "+i+" -- j: "+j);
        }
		
		return result;*/
		
		int splitSize = 8;

		if(s.length() % splitSize == 0)
		{
			int index = 0;
			int position = 0;

			byte[] resultByteArray = new byte[s.length()/splitSize];
			StringBuilder text = new StringBuilder(s);

			while (index < text.length()) 
			{
				String binaryStringChunk = text.substring(index, Math.min(index + splitSize, text.length()));
				Integer byteAsInt = Integer.parseInt(binaryStringChunk, 2);
				resultByteArray[position] = byteAsInt.byteValue();
				index += splitSize;
				position ++;
			}
			return resultByteArray;
		}
		else{
			System.out.println("Cannot convert binary string to byte[], because of the input length. '" +s+"' % 8 != 0");
			return null;
		}
	}
	
	
	
	

}