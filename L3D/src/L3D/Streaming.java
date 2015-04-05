package L3D;

//import UDP library

public class Streaming {
	public static L3D_UDP udp;  // define the UDP object
	boolean multicast;
	public static String address;
	private static int port=2222;

	//multicast constructor
	public Streaming()
	{
	  multicast=true;
	  address       = "224.0.0.1";  // this is the multicast IP -- the router reserves this address,
	  								//and forwards any packets it receives at this address to all hosts on the LAN
	  udp = new L3D_UDP( this, port, address );
	}	
	
	public Streaming(int _port)
	{
	  multicast=true;
	  port=_port;
	  address       = "224.0.0.1";  // this is the multicast IP -- the router reserves this address,
	  								//and forwards any packets it receives at this address to all hosts on the LAN
	  udp = new L3D_UDP( this, port, address );
	}
	
	//direct streaming constructor
	public Streaming(String ip)
	{
	  multicast=false;
	  address=ip;
	  System.out.println("IP address is: "+address);
	  udp = new L3D_UDP( this );
	}
	
	//direct streaming constructor
	public Streaming(int _port, String ip)
	{
	  multicast=false;
	  address=ip;
	  port=_port;
	  udp = new L3D_UDP( this );
	}
	
	void sendData(int[][][] cube)
	{
		byte[] data=serializeCube(cube);
	  if (multicast)
	  {
		    udp.send( data);
		    //System.out.println("sent multicast data");
	  }
	  else
		    udp.send( data, address, port);
	}

	public static byte[] serializeCube(int[][][] cube)
	{
		  int index=0;
		  byte[] data=new byte[(int)Math.pow(cube.length, 3)];
		  for (int z=0; z<cube[0][0].length; z++)
		    for (int y=0; y<cube[0].length; y++)
		      for (int x=0; x<cube.length; x++)
		      {
		        index=z*64+y*8+x;
		        data[index]=colorByte(cube[x][y][z]);
		      }
		 return data;
		
	}
	
	public static void sendData(String _address, byte[] data)
	{
		udp.send(data, _address,port);
	}
	
	public static byte colorByte(int col)
	{
	  return (byte)(red(col)&224 | (green(col)&224)>>3 | (blue(col)&192)>>6);
	}
	
	public static int red(int col)
	{
		return((col>>16)&255);
	}

	public static int green(int col)
	{
		return((col>>8)&255);
	}

	public static int blue(int col)
	{
		return(col&255);
	}
	
	public static void setPort(int _port)
	{
		port=_port;
	}

}
