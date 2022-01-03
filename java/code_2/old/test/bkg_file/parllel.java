import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

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
		
	public parllel(boolean encript, byte[] data, byte[][] keys, int start, int end,serpent_main serpent) 
	{
		this.encript= encript;
		this.data	= data;
		this.keys	= keys;
		this.start	= start;
		this.end	= end;
		this.serpent= serpent;
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
			final parllel left = new parllel(encript, data, keys, start, start + split,serpent);
			left.fork();
			
			final parllel middle = new parllel(encript, data, keys, start + split ,start +split + block_length,serpent);
			middle.fork();
			
			final parllel right = new parllel(encript, data, keys, start +split + block_length,end,serpent);
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
			final parllel left = new parllel(encript, data, keys, start, start +split,serpent);
			
			left.fork();
			
			final parllel right = new parllel(encript, data, keys, start +split,end,serpent);
			right.fork();
			
			byte[] l = left.join();
			byte[] r = right.join();
			byte[] result = new byte[l.length + r.length];
			
			System.arraycopy(l, 0, result, 0 , l.length);
			System.arraycopy(r, 0, result, l.length , r.length);
			
			return result;
		}
	}
	 
	private byte[] computeDirectly() 
	{
		//System.out.println("Direct Compute: "+start+" - "+end);
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
			System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEexception: "+e);
			return null;
			
		}
		
	}

	/*public static void main(String[] args) 
	{

		// create a random data set
		System.out.println("Avalale Pro: "+procc+"\n POOL: "+pool.getParallelism());
		
		final int[] data = new int[1000];

		final Random random = new Random();

		for (int i = 0; i < data.length; i++) 
		{
			data[i] = random.nextInt(100);
		}

		long start = System.currentTimeMillis();

		// submit the task to the pool

		final parllel finder = new parllel(data);

		System.out.println(pool.invoke(finder));
		long end = System.currentTimeMillis();
		// time calculation 
		System.out.println( "--Elapsed enc + dec  time: " + (end-start));	  
	}*/
}