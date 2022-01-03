import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class main {
	
	//length in bytes
	private static int key_length = 32;
	private static int block_length = 16;
	
	//For Pool
	final static ForkJoinPool pool = new ForkJoinPool();

	
	public static void main(String args[])
	{
		byte[] key1=new byte[32];
		byte in[]=new byte[33554432];
		int block_no = 1;
		
		//initial data for input
		for(int i=0;i<in.length;i+=2)
		{
			in[i] = (byte) 0xA9;
			in[i+1] = (byte) 0xC2;
		}
		//initial data for key
		for(int i=0;i<key1.length;i+=2)
		{
			key1[i] = (byte) 0xAB;
			key1[i+1] = (byte) 0x5D;
		}
		
		//Start Counting time___________________________________________________
		long startTime = System.currentTimeMillis();
		
		//check Input blocks
		block_no = in.length/block_length;
		if(in.length % block_length != 0)
		{
			block_no += 1;
			byte xx[]=new byte[block_no * block_length];
			System.arraycopy(in, 0, xx, 0 , in.length);
			for(int i=in.length;i<xx.length;i++)
			{
				xx[i] = (byte) 0x00;
			}
			in = xx;
		}
		
		//check Key Size;
		if(key1.length > key_length)
		{
			byte xx[] = new byte[key_length];
			System.arraycopy(key1, 0, xx, 0 , key_length);
			key1 = xx;
		}else if(key1.length < key_length)
		{
			byte xx[] = new byte[key_length];
			System.arraycopy(key1, 0, xx, 0 , key1.length);
			xx[key1.length] = (byte) 0x80;
			for(int i= key1.length +1;i< key_length;i++)
			{
				xx[i] = (byte) 0x00;
			}
			key1 = xx;
		}
		
		serpent_main serpent = new serpent_main();
		Object sessionKey;
		try{
			if(block_no != 1)
			{
				//Multiple Blocks
				//bkg b = new bkg(key1,block_no);
				//byte[][] block_keys = b.keys;
			
				//End BKG  time___________________________________________________
				long bkgTime = System.currentTimeMillis();
				//System.out.println("Start: ");
				
				parllel Parr_enc = new parllel(true, in , key1,0,in.length , serpent);
				byte [] last_in = pool.invoke(Parr_enc);
				
				
				
				//System.out.println("Enc finsh, Dec Start: ");
				
				
				//parllel Parr_dec = new parllel(false, last_in , block_keys,0,in.length , serpent);
				//byte [] last_out = pool.invoke(Parr_dec);
				
				//System.out.println("Main Text: \n"+String.valueOf(b.get_char_bit(in))+"\n\n");
				//System.out.println("Main Enc: \n"+String.valueOf(b.get_char_bit(last_in))+"\n\n");
				//System.out.println("Main Dec: \n"+String.valueOf(b.get_char_bit(last_out))+"\n\n");
				
				
			}else{
				byte [] last_in = new byte[16];
				//byte [] last_out = new byte[16];
			
				sessionKey	= serpent.makeKey(key1);
				last_in		= serpent.blockEncrypt(in, 0, sessionKey);
				//last_out	= serpent.blockDecrypt(last_in, 0, sessionKey);
			}
		}catch(Exception e)
		{
			System.out.println("exception: "+e);
			
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("PARLLEL - No BKG\nNo Of Blocks: "+block_no+"\nStart: "+startTime+"\n End: "+endTime+"\n Totla: "+totalTime+"ms");
	}
}