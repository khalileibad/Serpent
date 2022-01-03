
public class ssi {
	
	public static void main(String[] args) 
	{
		byte y;
		String s1,s2,b1,b2;
		for(int i=0;i<128;i++)
		{
			y = (byte) i;
			s1 = String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0');
			s2 = String.format("%8s", Integer.toBinaryString(i)).replace(' ', '0');
			b1 = String.format("%8s", Integer.toBinaryString(y & 0xFF)).replace(' ', '0');
			b2 = String.format("%8s", Integer.toBinaryString(y)).replace(' ', '0');
			
			System.out.println(i+"\n"+s1+" ---- "+s2+"\n"+b1+" ---- "+b2+"\n\n");
			
		}
	}
	
}
