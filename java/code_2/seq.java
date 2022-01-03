
public class seq {
	
	
	private static byte[] get_key()
	{
		//read Or generate Key
		
		byte[] key1 = new byte[32];//Key Length = 32________________________________
		//initial data for key
		for(int i=0;i<key1.length;i+=2)
		{
			key1[i] = (byte) 0xAB;
			key1[i+1] = (byte) 0x5D;
		}
		
		return key1;
	}
	
	private static byte[] get_data()
	{
		//Get Or Generate input data
		//int block_length = 16;
		byte in[]=new byte[33554432];//33554432
		
		//initial data for input
		for(int i=0;i<in.length;i++)
		{
			in[i] = (byte) 0x11;
		}
		
		int block_no = in.length/16; // 16 = block_length
		
		if(in.length % 16 != 0)// 16 = block_length
		{
			block_no += 1;
			byte xx[]=new byte[block_no * 16];// 16 = block_length
			System.arraycopy(in, 0, xx, 0 , in.length);
			for(int i=in.length;i<xx.length;i++)
			{
				xx[i] = (byte) 0x00;
			}
			in = xx;
		}
		
		return in;
	}
	
	public static void main(String args[])
	{
		byte[] key1 = get_key();
		byte[] in = get_data();
		
		int block_no = in.length/16; // 16 = block_length
		
		//Start Counting time___________________________________________________
		long startTime = System.currentTimeMillis();
		
		serpent_main serpent = new serpent_main();
		Object sessionKey;
		try{
			if(block_no != 1)
			{
				//long bkgTime = System.currentTimeMillis();
		
				byte [][] last_in = new byte[block_no][16];
				//byte [][] last_out = new byte[block_no][16];
			
				for(int i=0;i<block_no;i++)
				{
					sessionKey	= serpent.makeKey(key1);
					last_in[i]	= serpent.blockEncrypt(in, (i*16), sessionKey);
				}
			}else{
				byte [] last_in = new byte[16];
				byte [] last_out = new byte[16];
			
				sessionKey	= serpent.makeKey(key1);
				last_in		= serpent.blockEncrypt(in, 0, sessionKey);
				last_out	= serpent.blockDecrypt(last_in, 0, sessionKey);
			}
		}catch(Exception e)
		{
			System.out.println("exception: "+e);
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("SEQ + BKG: Blocks: "+block_no+"\nTotal: "+totalTime+" ms");
	}
}