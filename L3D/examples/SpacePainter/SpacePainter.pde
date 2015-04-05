import L3D.*;

PVector selected;
int boxSide=50;
PImage logo;
PImage front;
String accessToken="XXXXXXXXXXX";  //set this to your spark access token
String coreName="XXXXXXXXXX";  //change this name to your core's name
int layer=0;
L3D cube;
boolean updated;
color[][][] cubeColors;
color arrowCol=color(213, 25, 125);
int blinkSpeed=15;
float rotX, rotY;
boolean currentBlink, lastBlink;

void setup()
{
  size(displayWidth, displayHeight, P3D);
  selected=new PVector(0, 0);
  logo=loadImage("logo.png");
  front=loadImage( "front-rotated.png");
  println("cube library loaded");

  //for multicast streaming on a network
  //your target cube must be running the Listener firmware
  cube=new L3D(this);

  //for direct streaming to a specific core -- more reliable and better frame rate
  //  cube=new L3D(this, "your@spark.email", "your password", "cube name");

  cube.disableDrawing();
  cube.setManualStreaming();  //update the cube only when there's a change -- that way we're not dumping tons of UDP packets everywhere
  textFont(loadFont("QuicksandBold-Regular-48.vlw"));
  initUI();
  cubeColors=new color[cube.side][cube.side][cube.side];
  cubeBackground(color(0));
}

void draw()
{
  background(0); 
  pushMatrix();
  translate(0, height/2);
  rotateX(PI);
  translate(cube.side*cube.scale, -cube.side*cube.scale/2);//, -cube.side*cube.scale/2);
  rotateX(rotX);
  rotateY(rotY);


  rotateX(-PI/8);
  rotateY(-PI/6);
  pushMatrix();
  rotateX(-PI/2);
  fill(arrowCol);
  stroke(arrowCol);
  textSize(48);
  translate(-15, 48, -cube.scale/2);
  arrow(new PVector(textWidth("FRONT")/2, 20), new PVector(textWidth("FRONT")/2, 80), 0, cube.scale/2, true);
  text("FRONT", 0, 0);
  popMatrix();
  for (int x=0; x<cube.side; x++)  
    for (int y=0; y<cube.side; y++)  
      for (int z=0; z<cube.side; z++)  
      {
        cube.setVoxel(x, y, z, cubeColors[x][y][z]);
        pushMatrix();
        if (y==selected.y)
        {
          stroke(255, 50);
          if ((x==selected.x)&&(z==selected.z))
          {
            if ((frameCount%blinkSpeed)<blinkSpeed/2)
            {
              cube.setVoxel(x, y, z, currentSwatch.getColor());
              currentBlink=true;
            } else
            {
              currentBlink=false;
              cube.setVoxel(x, y, z, getVoxel(new PVector(x, y, z)));
            }
            if (logicalXOR(currentBlink, lastBlink))
            {
              color col=getVoxel(new PVector(x, y, z));
              col=cube.getVoxel(new PVector(x, y, z));
              updated=true;
            }
            stroke(255);
            strokeWeight(5);
          } else
            strokeWeight(1);
        } else
        {
          strokeWeight(1);
          stroke(255, 5);
        }

        translate(x*cube.scale, y*cube.scale, (cube.side-1-z)*cube.scale);
        if (brightness(cube.cube[x][y][z])!=0)
        {
          color col=cube.cube[x][y][z];
          fill(color(red(col), green(col), blue(col), 125  ));  //fill the cube color, but with transparency
        } else
          noFill();
        box(cube.scale, cube.scale, cube.scale);
        popMatrix();
      }
  lastBlink=currentBlink;
  noFill();
  stroke(255);
  strokeWeight(3);
  translate(cube.scale*(cube.side-1)/2, cube.scale*(cube.side-1)/2, cube.scale*(cube.side-1)/2);
  box(cube.scale*cube.side, cube.scale*cube.side, cube.scale*cube.side);
  if (updated)
  {
    updated=false;
    cube.update();
  }
  popMatrix();
  drawUI();
}

// returns the color (represented as an int) at the integer location closest
// to the PVector point
color getVoxel(PVector p) {
  return cubeColors[(int) p.x][(int) p.y][(int) p.z];
}

void setVoxel(PVector p, color col)
{
  if ((p.x >= 0) && (p.x < cube.side))
    if ((p.y >= 0) && (p.y < cube.side))
      if ((p.z >= 0) && (p.z < cube.side))
        cubeColors[(int) p.x][(int) p.y][(int) p.z] = col;
}

void cubeBackground(color col)
{
  for (int x=0; x<cube.side; x++)
    for (int y=0; y<cube.side; y++)
      for (int z=0; z<cube.side; z++)
        cubeColors[x][y][z]=col;
}

boolean logicalXOR(boolean x, boolean y) {
  return ( ( x || y ) && ! ( x && y ) );
}

