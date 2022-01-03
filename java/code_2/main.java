import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.Arrays;

public class main {
	
	//length in bytes
	//private static int key_length = 32;
	//private static int block_length = 16;
	
	//For Pool
	//final static ForkJoinPool pool = new ForkJoinPool();

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
		
		/*/check Key Size;
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
		}*/
		return key1;
	}
	
	private static byte[] get_data()
	{
		//Get Or Generate input data
		//int block_length = 16;
		byte in[]=new byte[160];//33554432
		
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
	
	private static byte[] get_data_input()
	{
		String txt = "Hello World in Incryption Area good luck But Must be carfule for coding ";
		byte in[] = txt.getBytes();
		
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
	
	private static void MSE(byte[] in, byte[] out)
	{
		int length = in.length;
		
		double x = 0;
		/*
		InputImage=imread(‘Input.jpg’);
		ReconstructedImage=imread(‘recon.jpg’);
		n=size(InputImage);
		M=n(1);
		N=n(2);
		MSE = sum(sum((InputImage-ReconstructedImage).^2))/(M*N);
		PSNR = 10*log10(256*256/MSE);
		fprintf('\nMSE: %7.2f ', MSE);
		fprintf('\nPSNR: %9.7f dB', PSNR);
		*/
	}
	
	public static void main(String args[])
	{
		byte[] key1 = get_key();
		byte[] in = get_data_input();
		
		byte [] last_in = new byte[16];
		byte [] last_out = new byte[16];
		
		System.out.println("Length: "+in.length);
		int block_no = in.length/16; // 16 = block_length
		
		//Start Counting time___________________________________________________
		long startTime = System.currentTimeMillis();
		long endKEYTime = System.currentTimeMillis();
		long endENCTime = System.currentTimeMillis();
		long endDECTime = System.currentTimeMillis();
		
		serpent_main serpent = new serpent_main();
		
		try{
			if(block_no != 1)
			{
				//Multiple Blocks
				System.out.println("Mutiplle bloks: "+block_no+" \n\n");
				ForkJoinPool pool = new ForkJoinPool();
				bkg b = new bkg(key1,block_no);
				
				//shaotic b = new shaotic(key1,block_no);
				
				startTime = System.currentTimeMillis();
				byte[][] block_keys = b.keys;
				endKEYTime = System.currentTimeMillis();
				
				parllel Parr_enc = new parllel(true, in , block_keys,0,in.length , serpent);
				last_in = pool.invoke(Parr_enc);
				
				endENCTime = System.currentTimeMillis();
				
				parllel Parr_dec = new parllel(false, last_in , block_keys,0,in.length , serpent);
				last_out = pool.invoke(Parr_dec);
				
				endDECTime = System.currentTimeMillis();
				
				String M = new String(in, "UTF-8");
				System.out.println("Main Text: \n"+M+"\n\n");
				
				String LI = new String(last_in, "UTF-8");
				System.out.println("Main Enc: \n"+LI+"\n\n");
				
				String LO = new String(last_out, "UTF-8");
				System.out.println("Main Dec: \n"+LO+"\n\n");
				
				//System.out.println("Parllel + BKG:\nNo of Blocks: "+block_no+"\nTotal: "+(endTime - startTime)+" ms");
				
				System.out.println("Start Time: "+startTime+"\n\n");
				System.out.println("KEY End Time: "+endKEYTime+" DiFF: "+(endKEYTime - startTime )+"\n\n");
				System.out.println("ENC End Time: "+endENCTime+" DiFF: "+(endENCTime - endKEYTime)+"\n\n");
				System.out.println("DEC End Time: "+endDECTime+" DiFF: "+(endDECTime - endENCTime)+"\n\n");
				
				
			}else{
				
				startTime = System.currentTimeMillis();
				Object sessionKey;
				sessionKey	= serpent.makeKey(key1);
				last_in		= serpent.blockEncrypt(in, 0, sessionKey);
				last_out	= serpent.blockDecrypt(last_in, 0, sessionKey);
			}
		}catch(Exception e)
		{
			System.out.println("exception: "+e);
			
		}
		long endTime   = System.currentTimeMillis();
		//System.out.println("No of Blocks: "+block_no+"\nStart: "+startTime+"\n End: "+endTime+"\n Totla: "+(endTime - startTime)+"ms \nStart BKG: "+startBKG+"\n EndBKG: "+endBKG+"\n TotalBKG: "+(endBKG - startBKG)+"ms \n");
		
		
	}
}