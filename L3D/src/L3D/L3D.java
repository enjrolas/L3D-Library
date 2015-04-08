package L3D;
/**
 * L3D cube library
 * A library for simulating, drawing and streaming data to an L3D cube
 * http://l3dcube.com
 *
 * Fonts and initial text functions by Hape
 * Copyright (c) 2015 Alex Hornstein http://www.lookingglassfactory.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Alex Hornstein http://www.artiswrong.com
 * @modified    4.5.2015
 * @version     2.0.0 (2)
 */

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter; // JAVA says this is conflicting...why?
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This is a template class and can be used to start a new processing library or
 * tool. Make sure you rename this class as well as the name of the example
 * package 'template' to your own library or tool naming convention.
 * 
 * (the tag example followed by the name of an example included in folder
 * 'examples' will automatically include the example in the javadoc.)
 *
 * @example Hello
 */

public class L3D {

	// parent is a reference to the parent sketch
	private static PApplet parent;
	public static Streaming stream;
	public static Spark spark;
	public static Text text;
	public final static String VERSION = "2.0.0";
	private static boolean drawCube;
	public static int side = 8;
	public static int size;
	public static int scale = 20;
	public static double xAngle=-0.15;
	public static double yAngle=0.39;
	public boolean pose=false;
	public static int[][][] cube;
	public static PVector center;
	public static boolean manualUpdate = true;

	/**
	 * This is the simplest constructor for the cube -- it's only used for local
	 * simulation of the cube In order to stream data to the cube, call the
	 * other constructor, L3D(this, "yourSparkAccessToken)
	 * 
	 * @example Sphere
	 */
	public L3D(PApplet _parent) {		
		super();
		welcome();
		parent = _parent;
		parent.registerMethod("draw", this);
		parent.registerMethod("mouseEvent", this);
		enableMulticastStreaming();
		initCubeVariables();
		parent.println(center);
	}

	public L3D(PApplet _parent, int _side) {
		super();
		side=_side;
		scale=parent.height/side/3;
		parent = _parent;
		parent.registerMethod("draw", this);
		parent.registerMethod("mouseEvent", this);
		welcome();
		initCubeVariables();
	}

	/**
	 * This constructor sets up the computer to stream the data it's displaying
	 * to a cube on your local network.
	 * 
	 * @example Streaming
	 * @param accessToken
	 *            -- the access token for your spark account. The library uses
	 *            this to find which of your cores are available, and to get the
	 *            local IP Address of each of your online cores so that it can
	 *            stream data to those cores
	 */
	public L3D(PApplet _parent, String accessToken) {
		super();
		parent = _parent;
		welcome();
		parent.registerMethod("draw", this);
		parent.registerMethod("mouseEvent", this);
		spark = new Spark(accessToken);
		initCubeVariables();
	}

	/**
	 * This constructor uses your spark/cubetube username and password to get an existing access token from your
	 * account, and then streams to your core named <coreName>
	 * 
	 * @example Streaming
	 * @param username
	 * @param password
	 * @param size
	 */
	public L3D(PApplet _parent, String username, String password, int _size) {
		super();
		side=_size;
		parent = _parent;
		scale=parent.height/side/3;
		welcome();
		parent.registerMethod("draw", this);
		parent.registerMethod("mouseEvent", this);
		spark = new Spark(username, password);
		initCubeVariables();
	}
	
	
	/**
	 * This constructor uses your spark/cubetube username and password to get an existing access token from your
	 * account, and then streams to your core named <coreName>
	 * 
	 * @example Streaming
	 * @param username
	 * @param password
	 */
	public L3D(PApplet _parent, String username, String password, String coreName) {
		super();
		parent = _parent;
		welcome();
		parent.registerMethod("draw", this);
		parent.registerMethod("mouseEvent", this);
		spark = new Spark(username, password);
		spark.name=coreName;
		streamToCore(coreName);
		initCubeVariables();
	}

	
	/**
	 * This constructor sets up the computer to stream the data it's displaying
	 * to a cube on your local network.
	 * 
	 * @example Streaming
	 * @param accessToken
	 *            -- the access token for your spark account. The library uses
	 *            this to find which of your cores are available, and to get the
	 *            local IP Address of each of your online cores so that it can
	 *            stream data to those cores
	 */
	public L3D(PApplet _parent, String accessToken, String name) {
		super();
		parent = _parent;
		welcome();
		parent.registerMethod("draw", this);
		parent.registerMethod("mouseEvent", this);
		spark = new Spark(accessToken);
		spark.name=name;
		streamToCore(name);
		initCubeVariables();		
	}	
	
	void initCubeVariables()
	{
		drawCube = true;  //draw the cube in the center of the screen by default
		pose=true;  //enable the drag-to-rotate UI by default
		cube = new int[side][side][side];
		size=side;  //compatibility with JS library
		center=new PVector(((float)size-1)/2, ((float)size-1)/2, ((float)size-1)/2);
		background(parent.color(0));
		text=new Text(this);	
	}
	
	// multicast streaming to default port 2222
	public static void enableMulticastStreaming() {
		stream = new Streaming();
		manualUpdate = false;
	}

	// multicast streaming, user specifies the port number
	public static void enableMulticastStreaming(int port) {
		stream = new Streaming(port);
		manualUpdate = false;
	}

	// direct streaming method -- streams to default port, 2222
	public static void streamToCore(String name) {
		String IPAddress = spark.getAddress(name);
		stream = new Streaming(IPAddress);
		System.out.println("streaming to core " + name
				+ " at local IP Address " + IPAddress);
		manualUpdate = false;
	}

	// direct streaming method -- user specifies the port number
	public static void streamToCore(int port, String name) {
		String IPAddress = spark.getAddress(name);
		stream = new Streaming(port, IPAddress);
		manualUpdate = false;
	}

	public static void setManualStreaming() {
		manualUpdate = true;
	}

	public static void setAutomaticStreaming() {
		manualUpdate = false;
	}

	// if we're drawing the cube
	public void draw() {
		parent.pushStyle();
		parent.rectMode(parent.CENTER);
		if(pose)  //translate the cube to the center and rotate it according to the mouse
			poseCube();
		if (drawCube) {
			parent.stroke(255, 255/(side*3));
			for (float x = 0; x < side; x++)
				for (float y = 0; y < side; y++)
					for (float z = 0; z < side; z++) {
						parent.pushMatrix();
						parent.translate(scale*(x-center.x), scale*(((float)size-1-y)-center.y), scale*(z-center.z));					
						if (parent.brightness(cube[(int)x][(int)y][(int)z]) != 0)
							parent.fill(cube[(int)x][(int)y][(int)z]);
						else
							parent.noFill();
						parent.box(scale, scale, scale);
						parent.popMatrix();
					}
			parent.noFill();
			parent.pushMatrix();
			parent.pushStyle();
			parent.stroke(255, 40);
			parent.strokeWeight(5);
			parent.translate(center.x, center.y, center.z);
			parent.box(scale*side);
			parent.popStyle();
			parent.popMatrix();
		}
		parent.popStyle();
		if ((stream != null) && (!manualUpdate))
			stream.sendData(cube);
	}

	// users can call this if they want to control exactly when a cube is
	// updated
	// there is a boolean flag called manualUpdate -- if it's true
	public static void update() {
//		stream.sendData(cube);
	}

	public static void setVoxel(int x, int y, int z, int col) {
		if ((x >= 0) && (x < side))
			if ((y >= 0) && (y < side))
				if ((z >= 0) && (z < side))
					cube[x][y][z] = col;
	}
	
	public static void setVoxel(double x, double y, double z, int col) {
		x = Math.round(x);
		y = Math.round(y);
		z = Math.round(z);
		if ((x >= 0) && (x < side))
			if ((y >= 0) && (y < side))
				if ((z >= 0) && (z < side))
					cube[(int) x][(int) y][(int) z] = col;
	}
	
	public static void setVoxel(int x, int y, int z, int r, int g, int b) {
		if ((x >= 0) && (x < side))
			if ((y >= 0) && (y < side))
				if ((z >= 0) && (z < side))
					cube[x][y][z] = parent.color(r, g, b);
	}

	public static void setVoxel(PVector p, int col) {
		if ((p.x >= 0) && (p.x < side))
			if ((p.y >= 0) && (p.y < side))
				if ((p.z >= 0) && (p.z < side))
					cube[(int) p.x][(int) p.y][(int) p.z] = col;
	}

	public static void setVoxel(PVector p, int r, int g, int b) {
		if ((p.x >= 0) && (p.x < side))
			if ((p.y >= 0) && (p.y < side))
				if ((p.z >= 0) && (p.z < side))
					cube[(int) p.x][(int) p.y][(int) p.z] = parent.color(r, g,
							b);
	}

	//adds the color col to the existing voxel color
	public static void addVoxel(double x, double y, double z, int col) {
		x = Math.round(x);
		y = Math.round(y);
		z = Math.round(z);
		if ((x >= 0) && (x < side))
			if ((y >= 0) && (y < side))
				if ((z >= 0) && (z < side))
				{
					int r, g, b;
					int currentColor=cube[(int) x][(int) y][(int) z];
					r=(int)(parent.red(currentColor)+parent.red(col));
					if(r>255)
						r=255;
					g=(int)(parent.green(currentColor)+parent.green(col));
					if(g>255)
						g=255;
					b=(int)(parent.blue(currentColor)+parent.blue(col));
					if(b>255)
						b=255;
					cube[(int) x][(int) y][(int) z] = parent.color(r,g,b);
				}
	}

	/**
	 * This will  a text message across the zPlane of the initialPosition message.
	 * 
	 * @example Text
	 * @param message -- the text string to scroll
	 * @param initialPosition -- a PVector with the position of the first character in the string.  The function will draw the rest
	 * of the string relative to this point
	 * @param col -- the color of the text
	 *            
	 */
	public void scrollText(String message, PVector initialPosition, int col)
	{
		text.scrollText(message, initialPosition, col);
	}
	
	/**
	 * This will  a text message across the zPlane of the initialPosition message, with the letters rotating along their y axes
	 * For more elaborate letter rotations, check out the Text.showChar function
	 * 
	 * @example Text
	 * @param message -- the text string to scroll
	 * @param initialPosition -- a PVector with the position of the first character in the string.  The function will draw the rest
	 * of the string relative to this point
	 * @param col -- the color of the text
	 *            
	 */
	public void scrollSpinningText(String message, PVector initialPosition, int col)
	{
		text.scrollSpinningText(message, initialPosition, col);
	}
	

	
	
	/**
	 * Marches a text marquis across the sides and front of the cube
	 * 
	 * @example Text
	 * @param message -- the text string to scroll
	 * @param initialPosition -- a float indicating how far the message is (in voxels) from the back right corner of the tube.  
	 * The rest of the message will be drawn relative to this position
	 * @param col -- the color of the text
	 *            
	 */
	public void marquis(String message, float initialPosition, int col)
	{
		text.marquis(message, initialPosition, col);
	}
	
	 public static int color(int r, int g, int b) {
		 return((int)(r*Math.pow(2,16)+g*Math.pow(2,8)+b)); }
	 
	// draw the cube to the screen
	public void enableDrawing() {
		drawCube = true;
	}

	// don't draw the cube to the screen
	public void disableDrawing() {
		drawCube = false;
	}

	public void saveFile() {
		BufferedWriter writer = null;
		try {
			// create a temporary file
			String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(Calendar.getInstance().getTime());
			timeLog = parent.sketchPath + "/" + timeLog + ".L3D";
			File logFile = new File(timeLog);

			// This will output the full path where the file will be written
			// to...
			System.out.println("saving to " + logFile.getCanonicalPath());

			writer = new BufferedWriter(new FileWriter(logFile));
			writer.write(side + "X" + side + "X" + side + "\n");
			writer.write("format:ascii\n");
			for (int x = 0; x < side; x++)
				for (int y = 0; y < side; y++)
					for (int z = 0; z < side; z++)
						writer.write((int) parent.red(cube[x][y][z]) + ","
								+ (int) parent.green(cube[x][y][z]) + ","
								+ (int) parent.blue(cube[x][y][z]) + ":");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}

	public void saveFile(boolean binaryFormat) {
		if (binaryFormat) {
			BufferedWriter writer = null;
			try {
				// create a temporary file
				String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss")
						.format(Calendar.getInstance().getTime());
				timeLog = parent.sketchPath + "/" + timeLog + "-bin.L3D";
				File logFile = new File(timeLog);

				// This will output the full path where the file will be written
				// to...
				System.out.println("saving to " + logFile.getCanonicalPath());

				writer = new BufferedWriter(new FileWriter(logFile));
				writer.write(side + "X" + side + "X" + side + "\n");
				writer.write("format:binary\n");
				for (int x = 0; x < side; x++)
					for (int y = 0; y < side; y++)
						for (int z = 0; z < side; z++) {
							byte[] colorBytes = colorBytes(cube[x][y][z]);
							for (int i = 0; i < 3; i++)
								writer.write(colorBytes[i]);
						}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					// Close the writer regardless of what happens...
					writer.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public void loadFile(File file) {
		try {
			FileReader fileReader = new FileReader(file);

			BufferedReader br = new BufferedReader(fileReader);

			String line = null;
			boolean dimension = false, format = false, ascii = true;

			// if no more lines the readLine() returns null
			while ((line = br.readLine()) != null) {
				// reading lines until the end of the file
				if (!dimension) // if we haven't parsed out a dimension yet
				{
					String[] sides = line.split("X");
					System.out.println(sides);
					//todo -- make sure the side length matches the cube's side length
					dimension = true;
				}
				else if(!format)
				{
					format=true;
					String[] formatStrings=line.split(":");
					if(formatStrings[1].equals("ascii"))
					{
						ascii=true;
						System.out.println("ascii format");
					}
					else
					{
						ascii=false;
						System.out.println("binary format");
					}
					
				}
				else
				{
					String[] voxels=line.split(":");
					System.out.println(voxels.length+" voxels");
					int x=0, y=0, z=0;
					for(int i=0;i<voxels.length;i++)
					{
						String[] colorStrings=voxels[i].split(",");
						cube[x][y][z]=parent.color(Integer.parseInt(colorStrings[0]), Integer.parseInt(colorStrings[1]), Integer.parseInt(colorStrings[2]));
						x++;
						if(x>=side)
						{
							y++;
							x=0;
							if(y>=side)
							{
								z++;
								y=0;
							}
						}
					}
				}

			}
		} catch (Exception e) {
			System.out.println("couldn't load that file");
			System.out.println(e);
		}

	}

	/*
	 * returns a byte array of three bytes representing the red, green, and blue
	 * values of the color
	 * 
	 * @param col -- an RGB color value, created with processing's color()
	 * function
	 */
	byte[] colorBytes(int col) {
		byte[] array = new byte[3];
		for (int i = 0; i < 3; i++)
			array[i] = (byte) ((col >> (8 * (2 - i))) & 255); // array[0] (red)=
																// col>>16 & 255
		// array[1] (green)=col>>8 & 255
		// array[2] (blue) = col &255
		return array;
	}

	// draws a line from point p1 to p2 and colors each of the points according
	// to the col parameter
	// p1 and p2 can be outside of the cube, but it will only draw the parts of
	// the line that fall
	// inside the cube
	public static void line(PVector p1, PVector p2, int col) {
		// thanks to Anthony Thyssen for the original write of Bresenham's line
		// algorithm in 3D
		// http://www.ict.griffith.edu.au/anthony/info/graphics/bresenham.procs

		float dx, dy, dz, l, m, n, dx2, dy2, dz2, i, x_inc, y_inc, z_inc, err_1, err_2;
		PVector currentPoint = new PVector(p1.x, p1.y, p1.z);
		dx = p2.x - p1.x;
		dy = p2.y - p1.y;
		dz = p2.z - p1.z;
		x_inc = (dx < 0) ? -1 : 1;
		l = Math.abs(dx);
		y_inc = (dy < 0) ? -1 : 1;
		m = Math.abs(dy);
		z_inc = (dz < 0) ? -1 : 1;
		n = Math.abs(dz);
		dx2 = l * 2;
		dy2 = m * 2;
		dz2 = n * 2;

		if ((l >= m) && (l >= n)) {
			err_1 = dy2 - l;
			err_2 = dz2 - l;
			for (i = 0; i < l; i++) {
				setVoxel(currentPoint, col);
				if (err_1 > 0) {
					currentPoint.y += y_inc;
					err_1 -= dx2;
				}
				if (err_2 > 0) {
					currentPoint.z += z_inc;
					err_2 -= dx2;
				}
				err_1 += dy2;
				err_2 += dz2;
				currentPoint.x += x_inc;
			}
		} else if ((m >= l) && (m >= n)) {
			err_1 = dx2 - m;
			err_2 = dz2 - m;
			for (i = 0; i < m; i++) {
				setVoxel(currentPoint, col);
				if (err_1 > 0) {
					currentPoint.x += x_inc;
					err_1 -= dy2;
				}
				if (err_2 > 0) {
					currentPoint.z += z_inc;
					err_2 -= dy2;
				}
				err_1 += dx2;
				err_2 += dz2;
				currentPoint.y += y_inc;
			}
		} else {
			err_1 = dy2 - n;
			err_2 = dx2 - n;
			for (i = 0; i < n; i++) {
				setVoxel(currentPoint, col);
				if (err_1 > 0) {
					currentPoint.y += y_inc;
					err_1 -= dz2;
				}
				if (err_2 > 0) {
					currentPoint.x += x_inc;
					err_2 -= dz2;
				}
				err_1 += dy2;
				err_2 += dx2;
				currentPoint.z += z_inc;
			}
		}

		setVoxel(currentPoint, col);
	}

	// draws a hollow  centered around the 'center' PVector, with radius
	// radius and color col
	public static void sphere(PVector center, float radius, int col) {
		float res = 30;
		for (float m = 0; m < res; m++)
			for (float n = 0; n < res; n++)
				setVoxel(
						center.x + radius * Math.sin((float) Math.PI * m / res)
								* Math.cos((float) 2 * Math.PI * n / res),
						center.y + radius * Math.sin((float) Math.PI * m / res)
								* Math.sin((float) 2 * Math.PI * n / res),
						center.z + radius * Math.cos((float) Math.PI * m / res),
						col);
	}

	public static void background(int col) {
		for (int x = 0; x < side; x++)
			for (int y = 0; y < side; y++)
				for (int z = 0; z < side; z++)
					cube[x][y][z] = col;
	}

	// returns the color (represented as an int) at the integer location closest
	// to the PVector point
	public static int getVoxel(PVector p) {
		return cube[(int) p.x][(int) p.y][(int) p.z];
	}

	// returns the color (represented as an int) at the x,y,z location
	public static int getVoxel(int x, int y, int z) {
		return cube[x][y][z];
	}
	
	public void enablePoseCube()
	{
		pose=true;
	}

	public void disablePoseCube()
	{
		pose=false;
	}
	
	public void poseCube()
	{
		parent.translate(parent.width/2, parent.height/2);
		parent.rotateY((float)xAngle);
		parent.rotateX((float)-yAngle);
	}
	
	  // This method is called automatically every loop
	  // becasue we have registerMouseEvent(this object)
	  public void mouseEvent(MouseEvent event){

		  switch (event.getAction()) {
		    case MouseEvent.PRESS:
		      // do something for the mouse being pressed
		      break;
		    case MouseEvent.RELEASE:
		      // do something for mouse released
		      break;
		    case MouseEvent.CLICK:
		      // do something for mouse clicked
		      break;
		    case MouseEvent.DRAG:
		    	xAngle+=(float)(parent.mouseX-parent.pmouseX)/100;
		    	yAngle+=(float)(parent.mouseY-parent.pmouseY)/100;
				
		      // do something for mouse dragged
		      break;
		  }
	  }
	    
	  
	public static int colorMap(float val, float min, float max) {
		float range = 1024;
		val = parent.map(val, min, max, 0, range);
		int colors[] = new int[6];
		colors[0] = parent.color(0, 0, 255);
		colors[1] = parent.color(0, 255, 255);
		colors[2] = parent.color(0, 255, 0);
		colors[3] = parent.color(255, 255, 0);
		colors[4] = parent.color(255, 0, 0);
		colors[5] = parent.color(255, 0, 255);
		if (val <= range / 6) {
			return (parent.lerpColor(colors[0], colors[1], val / (range / 6)));
		} else if (val <= 2 * range / 6)
			return (parent.lerpColor(colors[1], colors[2],
					(val / (range / 6)) - 1));
		else if (val <= 3 * range / 6)
			return (parent.lerpColor(colors[2], colors[3],
					(val / (range / 6)) - 2));
		else if (val <= 4 * range / 6)
			return (parent.lerpColor(colors[3], colors[4],
					(val / (range / 6)) - 3));
		else if (val <= 5 * range / 6)
			return (parent.lerpColor(colors[4], colors[5],
					(val / (range / 6)) - 4));
		else
			return (parent.lerpColor(colors[5], colors[0],
					(val / (range / 6)) - 5));
	}

	private void welcome() {
		System.out
				.println("L3D cube library 2.0.0 by Alex Hornstein http://www.artiswrong.com");
	}

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

}