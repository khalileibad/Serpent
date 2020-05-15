import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.JFrame;



//import java.io.*;   // Import the FileWriter class
//import java.io.IOException;  // Import the IOException class to handle errors


public class main_img {
	
	private static long startKEYTime;
	private static long endKEYTime;
	
	private static long startENCTime;
	private static long endENCTime;
	
	private static long startDECTime;
	private static long endDECTime;
	
	private static serpent_main serpent	= new serpent_main();
	private static ForkJoinPool pool 	= new ForkJoinPool();
	private static int block_no 		= 0;
	
	
	private static int type 			= 0;
	private static int key_type 		= 1;
	private static int mode_type 		= 1;
	private static int fun_type 		= 0;
	private static int file_type 		= 3;
	/*
		Args[0] : Type
			0 => SEQ
			1 => PAR
		Args[1] : Key Type
			0 => BKG
			1 => SHAOTIC
			2 => Normal keys
		Args[2] : MODE OF OPERATION
			0 => ECB
			1 => CTR
		Args[3] : Functions
			0 => ENC & DEC
			1 => ENC
			2 => DEC
		Args[3] : File_Type: 
			0 : colored images 4 arrays
			1 : colored images 8 arrays
			2 : Gray images
			3 : images File
	*/
	
	
	
	private static byte[] get_key()
	{
		//read Or generate Key
		String pass = "MyPasswordIsStrong";
		
		//byte[] key1 = new byte[32];//Key Length = 32________________________________
		byte[] key1 = pass.getBytes();//Key Length = 32________________________________
		if(key1.length > 32)
		{
			byte[] key2 = new byte[32];
			System.arraycopy( key1,0,key2,0,32);
			key1 = key2;
		}else if(key1.length < 32)
		{
			byte[] key2 = new byte[32];
			System.arraycopy( key1,0,key2,0,key1.length);
			for(int i = key1.length; i< 32; i++)
			{
				key2[i] = (byte) 0xAB;
			}
			key1 = key2;
		}
		/*
		//initial data for key
		for(int i=0;i<key1.length;i+=2)
		{
			key1[i] = (byte) 0xAB;
			key1[i+1] = (byte) 0x5D;
		}
		*/
		return key1;
	}
	
	private static byte[][] SEQ_ENC(byte[][] in,byte[][] block_keys)
	{
		byte [][] last_in = new byte[in.length][in[0].length];
		byte [] x = new byte[16];
		Object sessionKey;
		
		try{
			startENCTime 	= System.currentTimeMillis();
			
			for(int i=0;i<in.length;i++)
			{
				for(int j=0;j<in[i].length/16; j++)
				{
					sessionKey	= serpent.makeKey(block_keys[j]);
					System.arraycopy(in[i], j * 16, x, 0 , 16);
					x	= serpent.blockEncrypt(x, 0, sessionKey);
					System.arraycopy(x, 0, last_in[i] ,j * 16 , 16);
					
				}
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
	
		byte [][] last_in = new byte[in.length][in[0].length];
		byte [] x = new byte[16];
		Object sessionKey;
		
		try{
			startDECTime 	= System.currentTimeMillis();
			for(int i=0;i<in.length;i++)
			{
				for(int j=0;j<in[i].length/16; j++)
				{
					sessionKey	= serpent.makeKey(block_keys[j]);
					System.arraycopy(in[i], j * 16, x, 0 , 16);
					x	= serpent.blockDecrypt(x, 0, sessionKey);
					System.arraycopy(x, 0, last_in[i] ,j * 16 , 16);
					
				}
			}
			endDECTime 	= System.currentTimeMillis();
		}catch(Exception e)
		{
			System.out.println("exception in SEQ_ENC: "+e);
		}
		return last_in;
	}
	
	private static byte[][] PAR_ENC(byte[][] in ,byte[][] block_keys , int mode)
	{
		startENCTime 	= System.currentTimeMillis();
		byte[][] last_in = new byte[in.length][in[0].length];
		
		for(int i=0;i<in.length;i++)
		{
			parllel Parr_enc_r = new parllel(true, in[i] , block_keys,0,in[i].length , serpent ,mode);
			last_in[i] = pool.invoke(Parr_enc_r);
		}
		
		endENCTime 	= System.currentTimeMillis();
		
		return last_in;
	}
	
	private static byte[][] PAR_DEC(byte[][] in,byte[][] block_keys, int mode)
	{
		startDECTime 	= System.currentTimeMillis();
		byte[][] last_in = new byte[in.length][in[0].length];
		
		for(int i=0;i<in.length;i++)
		{
			parllel Parr_enc_r = new parllel(false, in[i] , block_keys,0,in[i].length , serpent ,mode);
			last_in[i] = pool.invoke(Parr_enc_r);
		}
		
		endDECTime 	= System.currentTimeMillis();
		
		return last_in;
	}
	
	private static void running(byte[] key1 , byte[][] in ,FileWriter myWriter , data_actions da )
	{
		block_no = in[0].length/16; // 16 = block_length
		byte[][] block_keys = new byte[block_no][key1.length];
		serpent_main serpent = new serpent_main();
		
		try{
			myWriter.write("Length, "+in[0].length+",");
			myWriter.write("Blocks, "+block_no+",");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		try{
			
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
				case 2:
					//NORMAL
					for(int i=0;i<block_keys.length; i++)
					{
						block_keys[i] = key1;
					}
				break;
			}
			endKEYTime 	= System.currentTimeMillis();
			
			byte[][] last_in = new byte[in.length][block_no];
			byte[][] last_dec = new byte[in.length][block_no];
		
			if(type == 0)//SEQUENSIAL
			{
				switch(fun_type)
				{
					case 0:
						//ENC & DEC
						last_in = SEQ_ENC(in,block_keys);
						last_dec = SEQ_DEC(last_in,block_keys);
					break;
					case 1:
						//ENC
						last_in = SEQ_ENC(in,block_keys);
					break;
					case 2:
						//DEC
						last_dec = SEQ_DEC(in,block_keys);
					break;
				}
				
			}else
			{//PARALLEL
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
					break;
				}
			}
			
			da.set_data(last_in);
			System.out.println( "DEC: "+ last_dec[0].length);
			
			da.set_data(last_dec);
			myWriter.write("KEY Time, "+(endKEYTime - startKEYTime )+",");
			myWriter.write("ENC Time, "+(endENCTime - startENCTime )+",");
			myWriter.write("DEC Time, "+(endDECTime - startDECTime )+"\n");
			
		}catch(Exception e)
		{
			System.out.println("exception in running: "+e);
		}
	}
	
	public static void main(String args[])
	{
		System.out.println( "Start");
		
		if(args.length == 4)
		{
			type 		= Integer.parseInt(args[0]);
			key_type 	= Integer.parseInt(args[1]);
			mode_type 	= Integer.parseInt(args[2]);
			fun_type 	= Integer.parseInt(args[3]);
		}
		//Data:
		data_actions da = new data_actions(file_type);
		
		byte[][] in = da.get_data();
		byte[] key1 = get_key();
		
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
			}else if(key_type == 1){
				myWriter.write("KEY Type : SHAOTIC \n");
			}else{
				myWriter.write("KEY Type : Normal \n");
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
			running(key1, in, myWriter, da);
			
			myWriter.close();
		} catch (Exception e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}
}