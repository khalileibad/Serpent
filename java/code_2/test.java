public class test {
	
	//length in bytes
	private static int key_length = 32;
	private static int block_length = 16;
	
	public static void main(String args[])
	{
		byte[] key1=new byte[32];
		
		//initial data for key
		for(int i=0;i<key1.length;i+=4)
		{
			key1[i] = (byte) 0x00;
			key1[i+1] = (byte) 0x44;
			key1[i+2] = (byte) 0xAA;
			key1[i+3] = (byte) 0xFF;
		}
		int w,total;
		int N = key1.length;
		byte m;
		for(int i=0;i<key1.length;i++)
		{
			w = (int)key1[i];
			m = (byte)w;
			System.out.println(key1[i]+":--------:"+w+":--------------:"+m);
		}
		
		System.out.println();
		System.out.println();
		System.out.println();
		
		total = ((int)key1[1] - (int)key1[N-2]) * (int)key1[N-1] - (int)key1[0];
		System.out.println("N: "+total);
		total = total % 255;
		m = (byte)total;
		System.out.println("Mod: "+total);
		System.out.println("byte: "+m);
		
	}
}