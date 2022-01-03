import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.io.FileWriter;   // Import the FileWriter class

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
	
	private static serpent_main serpent= new serpent_main();
	private static ForkJoinPool pool = new ForkJoinPool();
	
	private static int block_no 		= 0;
	private static int x_length 		= 0;
	private static int y_lenght 		= 0;
	private static int added_pxl 		= 0;
	
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
	
	private static byte[][] get_data()
	{
		//Get Or Generate input data
		//write image
		try{
			File input = new File("img/Lena.png");
			BufferedImage image = ImageIO.read(input);
			
			x_length = image.getWidth();
			y_lenght = image.getHeight();
			
			if((x_length*y_lenght) % 16 != 0)
			{
				added_pxl = 16 - ((x_length*y_lenght) % 16);
			}
			
			int count = 0;
			byte in[][] = new byte[4][(x_length * y_lenght) + added_pxl];
			for(int i=0; i<y_lenght; i++) 
			{
				for(int j=0; j<x_length; j++) 
				{
					Color c = new Color(image.getRGB(j, i));
					if( c.getRed() > 255 || c.getGreen() > 255 || c.getBlue() > 255 || c.getAlpha() > 255 )
					{
						System.out.println( c.getRed()+" --- "+ c.getGreen() +" --- "+ c.getBlue() +" --- "+ c.getAlpha() );
					}						
					in[0][count] = (byte) c.getRed(); //RED
					in[1][count] = (byte) c.getGreen(); //Green
					in[2][count] = (byte) c.getBlue(); //Blue
					in[3][count] = (byte) c.getAlpha(); //Alfa
					count++;
				}
			}
			
			if( added_pxl != 0)
			{
				for(int i=0; i< added_pxl; i++)
				{
					in[0][count] = (byte) 0x00;
					in[1][count] = (byte) 0x00;
					in[2][count] = (byte) 0x00;
					in[3][count] = (byte) 0x00;
					count++;
				}				
			}
			
			return in;
		}catch(Exception e){
			System.out.println("Error in write image: " + e);
			return null;
		}
	}
	
	private static void set_data(byte[][] data)
	{
		BufferedImage img = new BufferedImage(x_length, y_lenght, BufferedImage.TYPE_INT_ARGB);
		//file object
		File f = null;
		
		//create random image pixel by pixel
		int count = 0;
		int a,r,g,b;
		for(int y = 0; y < y_lenght; y++)
		{
			for(int x = 0; x < x_length; x++)
			{
				a = data[3][count] & 0xFF;
				r = data[0][count] & 0xFF;
				g = data[1][count] & 0xFF;
				b = data[2][count] & 0xFF;
				int p = (a<<24) |(r<<16) | (g<<8) | b; //pixel
				img.setRGB(x, y, p);
				count++;
			}
		}
		
		//write image
		try{
			f = new File("enc_img/lena_new_"+System.currentTimeMillis()+".png");
			ImageIO.write(img, "png", f);
		}catch(Exception e){
			System.out.println("Error in write image: " + e);
		}
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
		byte[][] last_in = new byte[4][in[0].length];
		
		//RED
		parllel Parr_enc_r = new parllel(true, in[0] , block_keys,0,in[0].length , serpent ,mode);
		last_in[0] = pool.invoke(Parr_enc_r);
		
		//GREEN
		parllel Parr_enc_g = new parllel(true, in[1] , block_keys,0,in[1].length , serpent ,mode);
		last_in[1] = pool.invoke(Parr_enc_g);
		
		//BLUE
		parllel Parr_enc_b = new parllel(true, in[2] , block_keys,0,in[2].length , serpent ,mode);
		last_in[2] = pool.invoke(Parr_enc_b);
		
		//ALFA
		parllel Parr_enc_a = new parllel(true, in[3] , block_keys,0,in[3].length , serpent ,mode);
		last_in[3] = pool.invoke(Parr_enc_a);
		
		endENCTime 	= System.currentTimeMillis();
		
		return last_in;
	}
	
	private static byte[][] PAR_DEC(byte[][] in,byte[][] block_keys, int mode)
	{
		startDECTime 	= System.currentTimeMillis();
		byte[][] last_in = new byte[in.length][in[0].length];
		//RED
		parllel Parr_enc_r = new parllel(false, in[0] , block_keys,0,in[0].length , serpent ,mode);
		last_in[0] = pool.invoke(Parr_enc_r);
		
		//GREEN
		parllel Parr_enc_g = new parllel(false, in[1] , block_keys,0,in[0].length , serpent ,mode);
		last_in[1] = pool.invoke(Parr_enc_g);
		
		//BLUE
		parllel Parr_enc_b = new parllel(false, in[2] , block_keys,0,in[2].length , serpent ,mode);
		last_in[2] = pool.invoke(Parr_enc_b);
		
		//ALFA
		parllel Parr_enc_a = new parllel(false, in[3] , block_keys,0,in[3].length , serpent ,mode);
		last_in[3] = pool.invoke(Parr_enc_a);
		
		endDECTime 	= System.currentTimeMillis();
		
		return last_in;
	}
	
	private static void running(byte[] key1 , int type, int key_type, int mode_type, int fun_type,FileWriter myWriter)
	{
		byte[][] in = get_data();
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
			
			byte[][] last_in = new byte[4][block_no];
			byte[][] last_dec = new byte[4][block_no];
		
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
			set_data(last_in);
			set_data(last_dec);
			myWriter.write("KEY Time, "+(endKEYTime - startKEYTime )+",");
			myWriter.write("ENC Time, "+(endENCTime - startENCTime )+",");
			myWriter.write("DEC Time, "+(endDECTime - startDECTime )+"\n");
			
		}catch(Exception e)
		{
			System.out.println("exception in running: "+e);
		}
	}
	
	private static void main_running( byte[] key1, int type,int key_type, int mode_type, int fun_type)
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
			running(key1, type, key_type, mode_type, fun_type,myWriter);
			
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
			2 => Normal keys
		
		Args[2] : MODE OF OPERATION
			0 => ECB
			1 => CTR
		
		Args[3] : Functions
			0 => ENC & DEC
			1 => ENC
			2 => DEC
		*/
		int type 		= 1;
		int key_type 	= 2;
		int mode_type 	= 1;
		int fun_type 	= 0;
		System.out.println( "ssss");
		if(args.length == 4)
		{
			type 		= Integer.parseInt(args[0]);
			key_type 	= Integer.parseInt(args[1]);
			mode_type 	= Integer.parseInt(args[2]);
			fun_type 	= Integer.parseInt(args[3]);
		}
		//Data:
		
		byte[] key1 = get_key();
		main_running(key1, type, key_type, mode_type, fun_type);
	}
}