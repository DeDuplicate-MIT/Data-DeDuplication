package Final;
import java.io.*;
import java.security.MessageDigest;
import java.util.*;
import Final.Connect;
public class Run 
{
	public static final int hconst = 69069; // good hash multiplier for MOD 2^32
	public int mult; // this will hold the p^n value
	int[] buffer; // circular buffer - reading from file stream
	int buffptr;
	InputStream is;
	static ArrayList<Integer> narr = new ArrayList<Integer>();
	static Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
	String string_w = "";
	static Scanner sc = new Scanner(System.in);
	static int counter = 1;
	public static int n=0;	
	public void displayChunks(String filelocation, int n) 
	{
		counter = 1;
		mult = 1;
		buffptr = 0;
		int mask = 1 << 13;
		mask--; // 13 bit of '1's
		File f = new File(filelocation);
		FileInputStream fs;
		try 
		{
			fs = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fs);	// BufferedInputStream is faster to read byte-by-byte from
			this.is = bis;
			long length = bis.available();
			System.out.println("**Size of file:**"+length);
			long curr = length;
			System.out.println("****CURRENT SIZE OF CURR : " + curr);
							//get the initial 1k hash window //
			Integer hash = inithash(1024-1,0);
			narr.add(hash);
			curr -= bis.available();
			while (curr < length) 
			{
				if ((hash & mask) == 0 || curr==length-1)
				{
					// window found - hash it, 
					// compare it to the other file chunk list
					narr.add(hash);
					byte[] wordArray = string_w.getBytes(); 
					String hashIn256=getHash256(wordArray);
					String filename14 = n+"  " + hashIn256;    // remove n + "  " in last
					if(!map.containsKey(hash))
					{
						map.put(hash, new ArrayList<String>(Arrays.asList(hashIn256)));
//						map.put(hash, hashIn256);
//						System.out.println(counter+"=> no hash\tno 256\t" +"  =>  "+hash + "\tsha256\t"+hashIn256);
						createFile(hash,string_w,filename14);
					}
					else if(!map.get(hash).contains(hashIn256))
					{
						map.get(hash).add(hashIn256);
//						map.put(hash, hashIn256);
						System.out.println(counter+"=> ya hash\tno 256\t" +"  =>  "+hash + "\tsha256\t"+hashIn256);
						createFile(hash,string_w,filename14);
					}
					else
					{
						System.out.println(counter+"=> ya hash\tya 256\t" +"  =>  "+hash + "\tsha256\t"+hashIn256);
//						createFile(hash,string_w,hashIn256);
						counter++;
					}
					string_w = "";
				}
				// next window's hash  //
				hash = nexthash(hash);					
				curr++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public int nexthash(int prevhash) throws IOException
	{
		int c = is.read(); // next byte from stream
		string_w += (char) c;
		prevhash -= mult * buffer[buffptr]; // remove the last value
		prevhash *= hconst; // multiply the whole chain with prime
		prevhash += (int)c; // add the new value
		buffer[buffptr] = c; // circular buffer, 1st pos == lastpos
		buffptr++;
		buffptr = buffptr % buffer.length;
		return prevhash;
	}
	public int inithash(int from, int to) throws IOException 
	{
		buffer = new int[from - to + 1]; // create circular buffer
		int hash = 0;
		string_w = "";
		// calculate the hash sum of p^n * a[x]
		for (int i = 0; i <= from - to; i++)
		{
			int c = is.read();
			string_w += (char) c;
			buffer[buffptr] = c; 
			buffptr++;
			buffptr = buffptr % buffer.length;
			hash *= hconst; // multiply the current hash with constant
			hash += c; // add byte to hash
			if(i>0) // calculate the large p^n value for later usage
			{
				mult *= hconst;
			}
		}
		return hash;
	}
	public static void createFile(int hash_val,String word, String hash256val) throws IOException
	{
		String filename = hash256val+".txt";
		FileWriter fw = new FileWriter(filename);
		fw.write(word);
		fw.close();
		counter++;
	}
	
	public static String getHash256(byte[] inputBytes)
	{
		String hash256calc = new String();
		StringBuffer hexDigest = new StringBuffer();
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(inputBytes);
			byte[] digestedBytes = messageDigest.digest();
			for(int i=0; i<digestedBytes.length;i++)
			{
				hexDigest.append(Integer.toString((digestedBytes[i]&0xff)+0x100,16).substring(1));
			}
			hash256calc = hexDigest.toString();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return hash256calc;
	}
	
	
	public static void main(String[] args)
	{
		Run r=new Run();
		String fileLocation1 = "file1.txt";
		String fileLocation2 = "img6.jpg";
//		String fileLocation3 = "img3.jpg";
//		String fileLocation4 = "img4.jpg";
		
		n=1;
		r.displayChunks(fileLocation1,1);
		System.out.println("\n\n\nFILE 1 COMPLETERS\n\n\n");
		
		n=2;
		r.displayChunks(fileLocation2,2);
		System.out.println("\n\n\nFILE 2 COMPLETERS\n\n\n");
//		
//		n=2;
//		r.displayChunks(fileLocation3,3);
//		System.out.println("\n\n\nFILE 3 COMPLETERS\n\n\n");
//
//		n=4;
//		r.displayChunks(fileLocation4,4);
//		System.out.println("\n\n\nFILE 4 COMPLETERS\n\n\n");

		map.keySet().forEach(key -> System.out.println(key + "\t->\t" + map.get(key)));
	}
}