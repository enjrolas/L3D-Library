class Button {
  int x, y, radius=10;
  color buttonColor;
  boolean selected=false;


  Button(int _x, int _y) {
    x=_x;
    y=_y;
    buttonColor=color(0);
  }

  Button(int _x, int _y, color col) {
    x=_x;
    y=_y;
    buttonColor=col;
  }  

  Button(float _x, float _y, int _diameter, color col) {
    x=(int)_x;
    y=(int)_y;
    radius=(int)_diameter/2;
    buttonColor=col;
  }

  void draw() {
    pushStyle();
    if (selected)
      stroke(255);
    else
      noStroke();
    strokeWeight(3);
    fill(buttonColor);    
    ellipse(x, y, radius*2, radius*2);
    popStyle();
  }

  //returns true if the mouse is inside the button
  boolean isClicked()
  {
    PVector center=new PVector(x, y);
    PVector mouse=new PVector(mouseX, mouseY);
    return(mouse.dist(center)<radius);
  }

  color getColor()
  {
    return buttonColor;
  }
}

class squareButton
{
  int x, y, side=50;
  color col;
  String name;
  PImage img;

  squareButton(PVector p, color _col)
  {
    x=(int)p.x;
    y=(int)p.y;
    col=_col;
  }

  squareButton(float _x, float _y, color _col)
  {
    x=(int)_x;
    y=(int)_y;
    col=_col;
  }
  
  squareButton(float _x, float _y, color _col, String _name)
  {
    x=(int)_x;
    y=(int)_y;
    col=_col;
    name=_name;
  }  
  
  squareButton(float _x, float _y, String imageName)
  {
    x=(int)_x;
    y=(int)_y;
    img=loadImage(imageName);
  }

  void draw()
  {
    stroke(255);
    strokeWeight(2);
    if(img!=null)
      noStroke();
    fill(col);
    rect(x, y, side, side);
    if(name!=null)
    {
      fill(255);
      text(name,x+side/2-textWidth(name)/2, y+side/2+6);
    }
    if(img!=null)
      image(img, x,y,side, side);
  }

  boolean isClicked()
  {
    return((mouseX>x)&&(mouseX<x+side)&&(mouseY>y)&&(mouseY<y+side));
  }

  void setColor(color _col)
  {
    col=_col;
  }

  color getColor()
  {
    return col;
  }
}


