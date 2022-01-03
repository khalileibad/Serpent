import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
public class MaximumFinder extends RecursiveTask<Integer> 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static int procc = Runtime.getRuntime().availableProcessors();
	final static ForkJoinPool pool = new ForkJoinPool(500);

	private static final int SEQUENTIAL_THRESHOLD = 32;
	private final int[] data;
	private final int start;
	private final int end;
		
	public MaximumFinder(int[] data, int start, int end) 
	{
		this.data = data;
		this.start = start;
		this.end = end;
	}
		
	public MaximumFinder(int[] data) 
	{
		this(data, 0, data.length);
	}
	 
	@Override
	protected Integer compute() 
	{
		final int length = end - start;
		if (length < SEQUENTIAL_THRESHOLD) 
		{
			return computeDirectly();
		}
		final int split = length / 2;
		final MaximumFinder left = new MaximumFinder(data, start, start + split);
		left.fork();
		final MaximumFinder right = new MaximumFinder(data, start + split, end);
		right.fork();
		return Math.max(right.join(), left.join());
	}
	 
	private Integer computeDirectly() 
	{
		System.out.println(Thread.currentThread()+"####" + " computing: " + start    + " to " + end);
		System.out.println(pool.getActiveThreadCount());
		int max = Integer.MIN_VALUE;
		for (int i = start; i < end; i++) 
		{
			if (data[i] > max) 
			{
				max = data[i];
			}
		}
		return max;
	}

	public static void main(String[] args) 
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

		final MaximumFinder finder = new MaximumFinder(data);

		System.out.println(pool.invoke(finder));
		long end = System.currentTimeMillis();
		// time calculation 
		System.out.println( "--Elapsed enc + dec  time: " + (end-start));	  
	}
}