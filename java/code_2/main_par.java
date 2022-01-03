import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class main_par {
	
	//length in bytes
	//private static int key_length = 32;
	//private static int block_length = 16;
	
	//For Pool
	//final static ForkJoinPool pool = new ForkJoinPool();

	private static byte[][] get_key(int length)
	{
		//read Or generate Key
		
		byte[][] keys = new byte[length][32];//Key Length = 32________________________________
		//initial data for key
		for(int w = 0;w<length;w++)
		{
			for(int i=0;i<32;i+=2)
			{
				keys[w][i] = (byte) 0xAB;
				keys[w][i+1] = (byte) 0x5D;
			}
		}	
		return keys;
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
		byte[] in = get_data();
		int block_no = in.length/16; // 16 = block_length
		byte[][] block_keys = get_key(block_no);
		
		//Start Counting time___________________________________________________
		long startTime = System.currentTimeMillis();
		
		serpent_main serpent = new serpent_main();
		
		try{
			if(block_no != 1)
			{
				//System.out.println("Start: ");
				ForkJoinPool pool = new ForkJoinPool();
				parllel Parr_enc = new parllel(true, in , block_keys,0,in.length , serpent);
				
				byte [] last_in = pool.invoke(Parr_enc);
				
				
				
				//System.out.println("Enc finsh, Dec Start: ");
				
				
				//parllel Parr_dec = new parllel(false, last_in , block_keys,0,in.length , serpent);
				//byte [] last_out = pool.invoke(Parr_dec);
				
				//System.out.println("Main Text: \n"+String.valueOf(b.get_char_bit(in))+"\n\n");
				//System.out.println("Main Enc: \n"+String.valueOf(b.get_char_bit(last_in))+"\n\n");
				//System.out.println("Main Dec: \n"+String.valueOf(b.get_char_bit(last_out))+"\n\n");
				
				
			}else{
				byte [] last_in = new byte[16];
				byte [] last_out = new byte[16];
				
				Object sessionKey;
				sessionKey	= serpent.makeKey(block_keys[0]);
				last_in		= serpent.blockEncrypt(in, 0, sessionKey);
				last_out	= serpent.blockDecrypt(last_in, 0, sessionKey);
			}
		}catch(Exception e)
		{
			System.out.println("exception: "+e);
			
		}
		long endTime   = System.currentTimeMillis();
		//System.out.println("No of Blocks: "+block_no+"\nStart: "+startTime+"\n End: "+endTime+"\n Totla: "+(endTime - startTime)+"ms \nStart BKG: "+startBKG+"\n EndBKG: "+endBKG+"\n TotalBKG: "+(endBKG - startBKG)+"ms \n");
		System.out.println("Parllel + BKG:\nNo of Blocks: "+block_no+"\nTotal: "+(endTime - startTime)+" ms");
		
	}
}