import L3D.*;

L3D cube;
PVector center;
float radius;
float popRadius;
float speed;
color sphereColor;

void setup()
{
  size(displayWidth, displayHeight, P3D);
  cube=new L3D(this);
  //  cube=new L3D(this, "your@spark.email", "your password", "cube name");
  newSphere();
}

void draw()
{
  background(0);
  cube.background(0);        //set the background of the cube to black for the frame

  cube.sphere(center, radius, sphereColor);
  radius+=speed;
  if(radius>popRadius)
    newSphere();
}

void newSphere()
{
  center=new PVector(random(cube.side),random(cube.side),random(cube.side));  //pick a new center point for a sphere, somewhere in the cube's volume;
  radius=0;
  popRadius=random(cube.side);   //how big the sphere will be when it pops
  speed=0.01+random(0.1);  //how fast the sphere will grow.  
  sphereColor=color(random(255),random(255),random(255));   //pick a random color for the sphere
}
