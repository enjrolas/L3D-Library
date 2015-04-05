import L3D.*;
L3D cube;
float radius=3.5, lineAngle;
PVector center;

void setup()
{
  size(500,500, P3D);
  cube=new L3D(this);
  //  cube=new L3D(this, "your@spark.email", "your password", "cube name");
}

void draw()
{
  background(0);
  cube.background(0);

  for (float theta=0; theta<2*PI; theta+=PI/3)
  {
    PVector start=new PVector(cube.center.x+radius*cos(theta), 0, cube.center.z+radius*sin(theta));
    PVector end=new PVector(cube.center.x+radius*cos(theta+lineAngle), cube.side-1, cube.center.z+radius*sin(theta+lineAngle));
    color col=cube.colorMap(theta%(2*PI), 0, 2*PI);
    cube.line(start, end, col);
  }
  lineAngle+=.05;

}


