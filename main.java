import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.*;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors


public class main {
	
	private static long startKEYTime;
	private static long endKEYTime;
	
	private static long startENCTime;
	private static long endENCTime;
	
	private static long startDECTime;
	private static long endDECTime;
	
	private static serpent_main serpent= new serpent_main();
	private static ForkJoinPool pool = new ForkJoinPool();
	private static images image_sets = new images();
	
	private static int block_no 		= 0;
	private static boolean has_files = false;
	
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
	
	private static byte[] get_data(int leng)
	{
		//Get Or Generate input data
		byte in[] = image_sets.get_image_byte("img/baboon.jpg","jpg");
		if(in != null && in.length > 50)
		{
			has_files = true;
			
		}else{
			//int block_length = 16;
			in = new byte[leng];//33554432
			
			//initial data for input
			for(int i=0;i<in.length;i++)
			{
				in[i] = (byte) 0x11;
			}
		}
		
		if(in.length % 16 != 0)// 16 = block_length
		{
			int added_pxl = 16 - (in.length % 16);
			image_sets.add = added_pxl;
			byte xx[]=new byte[in.length + added_pxl];// 16 = block_length
			System.arraycopy(in, 0, xx, 0 , in.length);
			for(int i=in.length;i<xx.length;i++)
			{
				xx[i] = (byte) 0x00;
			}
			in = xx;
		}
		
		int block_no = in.length/16; // 16 = block_length
		
		return in;
	}
	
	private static byte[][] SEQ_ENC(byte[][] in,byte[][] block_keys)
	{
		byte [][] last_in = new byte[in.length][16];
		Object sessionKey;
		try{
			startENCTime 	= System.currentTimeMillis();
			for(int i=0;i<block_no;i++)
			{
				sessionKey	= serpent.makeKey(block_keys[i]);
				last_in[i]	= serpent.blockEncrypt(in[i], 0, sessionKey);
			}
			endENCTime 	= System.currentTimeMillis();
		}catch(Exception e)
		{
			System.out.println("exception in SEQ_ENC: "+e);
		}
		return last_in;
	}
	
	private static byte[][] SEQ_DEC(byte[][] in,byte[][] block_keys)
	{
		byte [][] last_in = new byte[in.length][16];
		Object sessionKey;
		try{
			startDECTime 	= System.currentTimeMillis();
			for(int i=0;i<block_no;i++)
			{
				sessionKey	= serpent.makeKey(block_keys[i]);
				last_in[i]	= serpent.blockDecrypt(in[i], 0, sessionKey);
			}
			endDECTime 	= System.currentTimeMillis();
		}catch(Exception e)
		{
			System.out.println("exception AAA: "+e);
		}
		return last_in;
	}
	
	private static byte[] PAR_ENC(byte[] in ,byte[][] block_keys , int mode)
	{
		startENCTime 	= System.currentTimeMillis();
		
		parllel Parr_enc = new parllel(true, in , block_keys,0,in.length , serpent ,mode);
		byte [] last_in = pool.invoke(Parr_enc);
		//byte [] last_in = pool.invoke(new parllel(true, in , block_keys,0,in.length , serpent ,mode));
		endENCTime 	= System.currentTimeMillis();
		System.out.println("Image OUT Length: "+last_in.length);
		
		if(has_files)
		{
			try{
				image_sets.set_image_byte("enc_img/"+System.currentTimeMillis(),last_in);
			} catch (Exception e) {
				System.out.println("An error occurred.\n Write Files Main");
				
			}
		}
		return last_in;
	}
	
	private static byte[] PAR_DEC(byte[] in,byte[][] block_keys, int mode)
	{
		startDECTime 	= System.currentTimeMillis();
		parllel Parr_enc = new parllel(false, in , block_keys,0,in.length , serpent, mode);
		byte [] last_in = pool.invoke(Parr_enc);
		endDECTime 	= System.currentTimeMillis();
		if(has_files)
		{
			try{
				image_sets.set_image_byte("enc_img/"+System.currentTimeMillis(),last_in);
			} catch (Exception e) {
				System.out.println("An error occurred.\n Write Files Main");
				
			}
		}
		return last_in;
	}
	
	private static void running(int leng ,byte[] key1 , int type, int key_type, int mode_type, int fun_type,FileWriter myWriter)
	{
		byte[] in = get_data(leng);
		block_no = in.length/16; // 16 = block_length
		byte[][] block_keys = new byte[block_no][key1.length];
		serpent_main serpent = new serpent_main();
		
		try{
			myWriter.write("Length, "+leng+",");
			myWriter.write("Blocks, "+block_no+",");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		try{
			if(block_no != 1)
			{
				//Keys
				startKEYTime 	= System.currentTimeMillis();
				switch(key_type)
				{
					case 0:
						//BKG
						bkg bkg = new bkg(key1,block_no+1);
						block_keys = bkg.keys;
					break;
					case 1:
						//SHAOTIC
						shaotic b = new shaotic(key1,block_no+1);
						block_keys = b.keys;
					break;
				}
				endKEYTime 	= System.currentTimeMillis();
				
				if(type == 0)//SEQUENSIAL
				{
					byte[][] in_seq = new byte[block_no][16];
					
					byte[][] last_in = new byte[block_no][16];
					byte[][] last_dec = new byte[block_no][16];
		
					for(int i = 0;i< block_no;i++)
					{
						System.arraycopy(in, i * 16, in_seq[i], 0 , 16);
						//public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
					}
					
					switch(fun_type)
					{
						case 0:
							//ENC & DEC
							last_in = SEQ_ENC(in_seq,block_keys);
							last_dec = SEQ_DEC(last_in,block_keys);
						break;
						case 1:
							//ENC
							last_in = SEQ_ENC(in_seq,block_keys);
						break;
						case 2:
							//DEC
							last_dec = SEQ_DEC(in_seq,block_keys);
						break;
					}
					
				}else
				{//PARALLEL
			
					parllel Parr_enc = new parllel(true, in , block_keys,0,in.length , serpent ,mode_type);
					byte [] last_a = pool.invoke(Parr_enc);
		
					byte[] last_in = new byte[block_no];
					byte[] last_dec = new byte[block_no];
		
					switch(fun_type)
					{
						case 0:
							//ENC & DEC
							last_in	= PAR_ENC(in 		,block_keys	,mode_type);
							last_dec= PAR_DEC(last_in	,block_keys ,mode_type);
						break;
						case 1:
							//ENC
							last_in = PAR_ENC(in 		,block_keys	,mode_type);
						break;
						case 2:
							//DEC
							last_dec= PAR_DEC(in 		,block_keys	,mode_type);
						break;
						case 4:
							//ENC & DEC
							last_dec= PAR_DEC(in		,block_keys ,mode_type);
							last_in	= PAR_ENC(last_in	,block_keys	,mode_type);
						break;
						
					}
					
				}
			}else{//one block
				Object sessionKey	= serpent.makeKey(key1);
				byte[] last_in = new byte[block_no];
				byte[] last_dec = new byte[block_no];
		
				switch(fun_type)
				{
					case 0:
						//ENC & DEC
						last_in		= serpent.blockEncrypt(in		, 0, sessionKey);
						last_dec	= serpent.blockDecrypt(last_in	, 0, sessionKey);
					break;
					case 1:
						//ENC
						last_in		= serpent.blockEncrypt(in		, 0, sessionKey);
					break;
					case 2:
						//DEC
						last_dec	= serpent.blockDecrypt(in		, 0, sessionKey);
					break;
				}
			}
			try{
				myWriter.write("KEY Time, "+(endKEYTime - startKEYTime )+",");
				myWriter.write("ENC Time, "+(endENCTime - startENCTime )+",");
				myWriter.write("DEC Time, "+(endDECTime - startDECTime )+"\n");
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}catch(Exception e)
		{
			System.out.println("exception: "+e);
		}
	}
	
	private static void main_running(int[] lengs, byte[] key1, int type,int key_type, int mode_type, int fun_type)
	{
		try{
			FileWriter myWriter = new FileWriter("Results/"+System.currentTimeMillis()+".csv");
			if(type == 0)
			{
				myWriter.write("Execution Type : SEQ \n");
			}else{
				myWriter.write("Execution Type : PAR \n");
			}
			if(key_type == 0)
			{
				myWriter.write("KEY Type : BKG \n");
			}else{
				myWriter.write("KEY Type : SHAOTIC \n");
			}
			if(mode_type == 0)
			{
				myWriter.write("MODE OF OPERATION : ECB \n");
			}else{
				myWriter.write("MODE OF OPERATION : CTR \n");
			}
			if(fun_type == 0)
			{
				myWriter.write("Functions : ENC & DEC \n");
			}else if(fun_type == 1)
			{
				myWriter.write("Functions : ENC \n");
			}else{
				myWriter.write("Functions : DEC \n");
			}
			System.out.println("Total: "+lengs.length);
			
			running(0 ,key1, type, key_type, mode_type, fun_type,myWriter);
			/*for(int i = 0;i < lengs.length; i++) //lengs.length
			{
				running(lengs[i] ,key1, type, key_type, mode_type, fun_type,myWriter);
				System.out.println(i+" -- 1");
				running(lengs[i] ,key1, type, key_type, mode_type, fun_type,myWriter);
				System.out.println(i+" -- 2");
				running(lengs[i] ,key1, type, key_type, mode_type, fun_type,myWriter);
				System.out.println(i+" -- 3");
				running(lengs[i] ,key1, type, key_type, mode_type, fun_type,myWriter);
				System.out.println(i+" -- 4");
				running(lengs[i] ,key1, type, key_type, mode_type, fun_type,myWriter);
				myWriter.write("\n");
				System.out.println(i+" -- 5");
			}*/
			myWriter.close();
		} catch (Exception e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		/*
		Args[0] : Type
			0 => SEQ
			1 => PAR
		
		Args[1] : Key Type
			0 => BKG
			1 => SHAOTIC
		
		Args[2] : MODE OF OPERATION
			0 => ECB
			1 => CTR
		
		Args[3] : Functions
			0 => ENC & DEC
			1 => ENC
			2 => DEC
		*/
		int type 		= 1;
		int key_type 	= 1;
		int mode_type 	= 1;
		int fun_type 	= 1;
		
		if(args.length == 4)
		{
			type 		= Integer.parseInt(args[0]);
			key_type 	= Integer.parseInt(args[1]);
			mode_type 	= Integer.parseInt(args[2]);
			fun_type 	= Integer.parseInt(args[3]);
		}
		//Data:
		
		int[] lengs = {16,160,800,1600,8000,16000,400000,800000,1600000,4000000,8000000,16000000,24000000,32000000,33554432};
		byte[] key1 = get_key();
		main_running(lengs, key1, type, key_type, mode_type, fun_type);
	}
}