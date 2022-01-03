class shaotic
{
	public static byte[][] keys;
	public static boolean error = false;
	public static String error_text = "";
	
	
	public shaotic(byte[] main_key, int no_keys)
	{
		keys = new byte[no_keys][main_key.length];
		
		block_key_generation(main_key,no_keys);
		
	}
	
	private static void block_key_generation(byte[] key,int block_no)
	{
		byte[] curr_key = key;
		for(int i=0;i< block_no;i++)
		{
			curr_key = key_gen(curr_key);
			keys[i] = curr_key;
		}
	}
	
	private static byte[] key_gen(byte[] key)
	{
		//Lorenz 96 model - Shoatic Map
		//https://en.wikipedia.org/wiki/Lorenz_96_model
		
		int N = key.length;
		byte[] result = new byte[N];
		
		int total,x1,x2,x3,x4;
		
		x1 = (int)key[1];
		x2 = (int)key[N-2];
		x3 = (int)key[N-1];
		x4 = (int)key[0];
		total = (x1 - x2) * x3 - x4;
		result[0] = (byte)total;
		
		x1 = (int)key[2];
		x2 = (int)key[N-1];
		x3 = (int)key[0];
		x4 = (int)key[1];
		total = (x1 - x2) * x3 - x4;
		result[1] = (byte)total;
		
		x1 = (int)key[0];
		x2 = (int)key[N-3];
		x3 = (int)key[N-2];
		x4 = (int)key[N-1];
		total = (x1 - x2) * x3 - x4;
		result[N-1] = (byte)total;
		
		for(int i=2;i<N-1;i++)
		{
			x1 = (int)key[i+1];
			x2 = (int)key[i-2];
			x3 = (int)key[i-1];
			x4 = (int)key[i];
			total = (x1 - x2) * x3 - x4;
			result[i] = (byte)total;
		}
		
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