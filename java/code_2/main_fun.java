
public class seq {
	
	private long startKEYTime 	= System.currentTimeMillis();
	private long endKEYTime 	= System.currentTimeMillis();
	private long startENCTime 	= System.currentTimeMillis();
	private long endENCTime 	= System.currentTimeMillis();
	private long startDECTime 	= System.currentTimeMillis();
	private long endDECTime 	= System.currentTimeMillis();
	private serpent_main serpent= new serpent_main();
	
	private int block_no 		= 0;
	
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
		/*byte in[]=new byte[33554432];//33554432
		
		//initial data for input
		for(int i=0;i<in.length;i++)
		{
			in[i] = (byte) 0x11;
		}*/ 
		String M = "My Name Is Khalil Taha Mohamed Obeid iam programmer i want to test kkkkk Serpent algorithm";
		byte[] in = M.getBytes();
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
	
	private static void SEQ_ENC(byte[][] in)
	{
		byte [][] last_in = new byte[this.block_no][16];
		
		this.startENCTime 	= System.currentTimeMillis();
		
		for(int i=0;i<this.block_no;i++)
		{
			sessionKey	= serpent.makeKey(key1);
			last_in[i]	= serpent.blockEncrypt(in, (i*16), sessionKey);
		}
		endENCTime 	= System.currentTimeMillis();
		return last_in;
	}
	
	private static void SEQ_DEC(byte[][] in)
	{
		byte [][] last_in = new byte[this.block_no][16];
		
		this.startDECTime 	= System.currentTimeMillis();
		
		for(int i=0;i<this.block_no;i++)
		{
			sessionKey	= serpent.makeKey(key1);
			last_in[i]	= serpent.blockDecrypt(in, (i*16), sessionKey);
		}
		endDECTime 	= System.currentTimeMillis();
		return last_in;
	}
	
	private static void ECB_ENC(byte[][] in ,byte[][] block_keys)
	{
		byte [][] last_in = new byte[block_no][16];
		
		this.startENCTime 	= System.currentTimeMillis();
		
		parllel Parr_enc = new parllel(true, in , block_keys,0,in.length , this.serpent);
		last_in = pool.invoke(Parr_enc);
		endENCTime 	= System.currentTimeMillis();
		return last_in;
	}
	
	private static void ECB_DEC(byte[][] in,byte[][] block_keys)
	{
		byte [][] last_in = new byte[block_no][16];
		
		this.startDECTime 	= System.currentTimeMillis();
		parllel Parr_enc = new parllel(false, in , block_keys,0,in.length , this.serpent);
		last_in = pool.invoke(Parr_enc);
		this.endDECTime 	= System.currentTimeMillis();
		
		return last_in;
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
		
		
		
		//Data:
		byte[] key1 = get_key();
		byte[] in = get_data();
		byte[] last_in = new byte[in.length];
		byte[] last_dec = new byte[in.length];
		
		this.block_no = in.length/16; // 16 = block_length
		serpent_main serpent = new serpent_main();
		Object sessionKey;
		
		try{
			if(this.block_no != 1)
			{
				//Keys
				this.startKEYTime 	= System.currentTimeMillis();
				switch(args[1])
				{
					case 0:
						//BKG
						bkg b = new bkg(key1,block_no);
					break;
					case 1:
						//SHAOTIC
						shaotic b = new shaotic(key1,block_no);
					break;
				}
				this.endKEYTime 	= System.currentTimeMillis();
				
				if(args[0] == 0)//SEQUENSIAL
				{
					switch(args[3])
					{
						case 0:
							//ENC & DEC
							last_in = SEQ_ENC(in);
							last_dec = SEQ_DEC(last_in);
						break;
						case 1:
							//ENC
							last_in = SEQ_ENC(in);
						break;
						case 2:
							//DEC
							last_dec = SEQ_DEC(in);
						break;
					}
				}else
				{//PARALLEL
					switch(args[2]) //MODE
					{
						case 0:
							//ECB
							switch(args[3])
							{
								case 0:
									//ENC & DEC
									last_in = ECB_ENC(in);
									last_dec = ECB_DEC(last_in);
								break;
								case 1:
									//ENC
									last_in = ECB_ENC(in);
								break;
								case 2:
									//DEC
									last_dec = ECB_DEC(in);
								break;
							}
						break;
						case 1:
							//CTR
							switch(args[3])
							{
								case 0:
									//ENC & DEC
									last_in = CTR_ENC(in);
									last_dec = CTR_DEC(last_in);
								break;
								case 1:
									//ENC
									last_in = CTR_ENC(in);
								break;
								case 2:
									//DEC
									last_dec = CTR_DEC(in);
								break;
							}
						break;
					}
				}
			}else{
				sessionKey	= serpent.makeKey(key1);
				
				switch(args[3])
				{
					case 0:
						//ENC & DEC
						last_in		= serpent.blockEncrypt(in, 0, sessionKey);
						last_dec	= serpent.blockDecrypt(last_in, 0, sessionKey);
					break;
					case 1:
						//ENC
						last_in		= serpent.blockEncrypt(in, 0, sessionKey);
					break;
					case 2:
						//DEC
						last_decs	= serpent.blockDecrypt(in, 0, sessionKey);
					break;
				}
			}
		}catch(Exception e)
		{
			System.out.println("exception: "+e);
		}
		
		System.out.println("Blocks: "+this.block_no);
		if(Args[0] == 0)
		{
			System.out.println("Execution Type : SEQ");
		}else{
			System.out.println("Execution Type : PAR");
		}
		if(Args[1] == 0)
		{
			System.out.println("KEY Type : BKG");
		}else{
			System.out.println("KEY Type : SHAOTIC");
		}
		if(Args[2] == 0)
		{
			System.out.println("MODE OF OPERATION : ECB");
		}else{
			System.out.println("MODE OF OPERATION : CTR");
		}
		
		if(Args[3] == 0)
		{
			System.out.println("Functions : ENC & DEC");
		}else if(Args[3] == 1)
		{
			System.out.println("Functions : ENC");
		}else{
			System.out.println("Functions : DEC");
		}
		
		System.out.println("KEY Time From : "+this.startKEYTime+" - TO : "+this.endKEYTime+" DiFF: "+(this.endKEYTime - this.startKEYTime )+"\n\n");
		System.out.println("ENC Time From : "+this.startENCTime+" - TO : "+this.endENCTime+" DiFF: "+(this.endENCTime - this.startENCTime)+"\n\n");
		System.out.println("DEC Time From : "+this.startDECTime+" - TO : "+this.endDECTime+" DiFF: "+(this.endDECTime - this.startDECTime)+"\n\n");
		
		String M = new String(in, "UTF-8");
		System.out.println("Main Text: \n"+M+"\n\n");
		
		String LI = new String(last_in, "UTF-8");
		System.out.println("Main Enc: \n"+LI+"\n\n");
		
		String LO = new String(last_out, "UTF-8");
		System.out.println("Main Dec: \n"+LO+"\n\n");
	}
}