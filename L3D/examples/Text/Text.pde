import L3D.*;

L3D cube;

float pos=0;
float posInc=.1;
int mode=0;
String message=" ";
void setup()
{
  size(500, 500, P3D);
  cube=new L3D(this);
//  cube=new L3D(this, "your@spark.email", "your password", "cube name");
}

void draw()
{
  background(0);
  cube.background(0);
  switch(mode) {

    case(0):    
    cube.text.showChar('L', new PVector(3.5, 0, 3.5), new PVector(0, pos), cube.colorMap(frameCount%1000, 0, 1000));
    break;  
    case(1):
    cube.text.showChar('3', new PVector(1, 3.5, 3.5), new PVector(0, 3.5), new PVector(pos, 0), cube.colorMap(frameCount%1000, 0, 1000));
    break;  
    case(2):
    cube.text.showChar('D', new PVector(1.5, 0, 3.5), new PVector(0, 0), new PVector(0, -pos), cube.colorMap(frameCount%1000, 0, 1000));
    break;  
    case(3):
    message="L3D cube!";
    cube.marquis(message, pos, cube.colorMap(frameCount%1000, 0, 1000));
    break;  
    case(4):
    message="cubism";
    cube.scrollText(message, new PVector(pos, 0, 4), cube.colorMap(frameCount%1000, 0, 1000));
    break;  
    case(5):
    message="1^3";
    cube.scrollSpinningText(message, new PVector(pos, 0, 3), cube.colorMap(frameCount%1000, 0, 1000));
    break;
  }

  pos+=posInc;
  if (pos>(message.length()+1)*8)
  {
    pos=0;
    mode++;
    if(mode>5)
      mode=0;
  }
}

