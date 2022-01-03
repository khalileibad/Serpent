import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.math.BigInteger; 

public class parllel extends RecursiveTask<byte[]> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int SEQUENTIAL_THRESHOLD = 32;
	private final byte[] data;
	private final byte[][] keys;
	private static final int block_length = 16;
	private final int start;
	private final int end;
	private static serpent_main serpent;
	private final boolean encript;
	private final int mode;
		
	public parllel(boolean encript, byte[] data, byte[][] keys, int start, int end,serpent_main serpent , int mode) 
	{
		this.encript= encript;
		this.data	= data;
		this.keys	= keys;
		this.start	= start;
		this.end	= end;
		this.serpent= serpent;
		this.mode	= mode; // 0 => ECB , 1 => CTR
		//System.out.println("Begin: "+start+" - "+end);
	}
	 
	@Override
	protected byte[] compute() 
	{
		//System.out.println("compute: "+start+" - "+end);
		final int length = end - start;
		
		if (length  <= block_length) 
		{
			return computeDirectly();
		}else if((length/block_length) % 2 != 0)
		{
			final int split = (length - block_length) / 2;//ERRRRRRRRRRRRRRRRRRRRRRRRRROOOOOOOOOOOOOOOOOOOOORRRRRRRRRRRR
			final parllel left = new parllel(encript, data, keys, start, start + split,serpent,mode);
			left.fork();
			
			final parllel middle = new parllel(encript, data, keys, start + split ,start +split + block_length,serpent,mode);
			middle.fork();
			
			final parllel right = new parllel(encript, data, keys, start +split + block_length,end,serpent,mode);
			right.fork();
			
			byte[] l = left.join();
			byte[] m = middle.join();
			byte[] r = right.join();
			byte[] result = new byte[l.length + r.length + m.length];
			
			System.arraycopy(l, 0, result, 0 					, l.length);
			System.arraycopy(m, 0, result, l.length 			, m.length);
			System.arraycopy(r, 0, result, l.length + m.length  , r.length);
			
			return result;
		}else
		{
			final int split = length / 2;
			final parllel left = new parllel(encript, data, keys, start, start +split,serpent,mode);
			
			left.fork();
			
			final parllel right = new parllel(encript, data, keys, start +split,end,serpent,mode);
			right.fork();
			
			byte[] l = left.join();
			byte[] r = right.join();
			byte[] result = new byte[l.length + r.length];
			
			System.arraycopy(l, 0, result, 0 , l.length);
			System.arraycopy(r, 0, result, l.length , r.length);
			
			return result;
		}
	}
	
	/*
	@Override
	protected byte[] compute() 
	{
		//System.out.println("compute: "+start+" - "+end);
		final int length = end - start;
		if (length  <= block_length) 
		{
			return computeDirectly();
		}else
		{
			final parllel[] par = new parllel[length/block_length];
			int st = start;
			byte[] result = new byte[length];
			byte[] l;
			for(int i=0; i< par.length;  i++)
			{
				par[i] = new parllel(encript, data, keys, st, st +block_length,serpent,mode);
				par[i].fork();
				l = par[i].join();
				System.arraycopy(l, 0, result, st , l.length);
				st = st + block_length;
			}
			return result;
		}
	}
	 */
	private byte[] computeDirectly() 
	{
		if(mode == 0)
		{
			// ECB
			return ECB_comp();
		}else
		{
			//CTR
			return SRT_comp();
		}
	}
	
	private byte[] ECB_comp()
	{
		try{
			Object sessionKey	= serpent.makeKey(keys[start / block_length]);
			if(encript)
			{
				return serpent.blockEncrypt(data, start, sessionKey);
			}else
			{
				return serpent.blockDecrypt(data, start, sessionKey);
			}
		}catch(Exception e)
		{
			System.out.println("Eexception: IN ECB_comp "+e);
			return null;
		}
	}

	private byte[] SRT_comp()
	{
		try{
			Object sessionKey	= serpent.makeKey(keys[start / block_length]);
			
			byte[] counter = setNonce(start / block_length);
			byte[] enc_dec;
			byte[] ddd_dec = new byte[block_length];
			
			System.arraycopy(data, start, ddd_dec, 0 , block_length);
			
			enc_dec = serpent.blockEncrypt(counter, 0, sessionKey);
			
			return bkg_xor(enc_dec,ddd_dec);
		}catch(Exception e)
		{
			System.out.println("Eexception IN SRT_comp "+e+ "\n\n\n");
			return null;
		}
	}
	
	//XOR
	private static byte[] bkg_xor(byte[] x, byte[] y)
	{
		if(x.length != y.length)
		{
			System.out.println("Eror Length: In bkg_xor: "+x.length +" -- "+ y.length);
			return null;
		}else{
			byte[] result = new byte[x.length];
			for(int i=0;i<result.length;i++)
			{
				result[i] = (byte) (x[i] ^ y[i]);
			}
			return result;
		}
	}
	
	private byte[] setNonce(int counter) 
	{
		/*String nonceString = "12345678123456781234567812345678";
        int len = nonceString.length();
		String x = "";
		if(encript)
		{
			x = "Encrypt";
		}else{
			x = "Dencrypt";
		}
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) 
		{
			data[i / 2] = (byte) ((Character.digit(nonceString.charAt(i), 16) << 4) + Character.digit(nonceString.charAt(i+1), 16));
		}
		
		data[15] = (byte) (data[15]+counter);
		*/
		try{
			byte[] data = new byte[16];
			
			/* 
			System.arraycopy(keys[keys.length -1], 0, data, 0, 16);
			
			data[15] = (byte) (data[15]+counter);
			*/
			
			BigInteger bigInt = BigInteger.valueOf(counter); 
			// create a byte array 
			byte b1[] = bigInt.toByteArray(); 
			int len = 16 - b1.length;
			
			System.arraycopy(keys[keys.length -1], 0, data, 0, len);
			System.arraycopy(b1, 0, data, len -1, b1.length);
			
			
			//public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
			return data;
		}catch(Exception e)
		{
			System.out.println("Eexception IN setNonce "+e+ "\n\n\n");
			return null;
		}
		
		
    }
	
}