
color selectedColor;
Button[] swatches;
squareButton currentSwatch, clearButton, saveButton, openButton, upButton, downButton;
PVector UIOrigin;
color currentColor;
int blinkRate=1000;
float mouseScale=22;

void initUI()
{
  UIOrigin=new PVector(displayWidth-(cube.side+3)*boxSide, boxSide, 0);
  swatches=new Button[10];
  for (int i=0; i<swatches.length; i++)
    swatches[i]=new Button(UIOrigin.x+i*cube.side*boxSide/swatches.length, UIOrigin.y+(cube.side+2)*boxSide, 35, cube.colorMap(i, 0, swatches.length));
  swatches[0].selected=true;
  selectedColor=swatches[0].getColor();  
  currentSwatch=new squareButton(UIOrigin.x+cube.side*boxSide/4, UIOrigin.y+(cube.side+3)*boxSide, swatches[0].getColor());
  currentSwatch.x-=currentSwatch.side/2;
  currentSwatch.setColor(selectedColor);
  saveButton=new squareButton(UIOrigin.x+(cube.side+3)*boxSide/2, UIOrigin.y+(cube.side+3)*boxSide, "save.png");
  openButton=new squareButton(UIOrigin.x+(cube.side+6)*boxSide/2, UIOrigin.y+(cube.side+3)*boxSide, "open.png");
  clearButton=new squareButton(UIOrigin.x+(cube.side+9)*boxSide/2, UIOrigin.y+(cube.side+3)*boxSide, "clear.png");
  upButton=new squareButton(UIOrigin.x-300, UIOrigin.y+4*boxSide, "up.png");
  downButton=new squareButton(UIOrigin.x-300, UIOrigin.y+(7)*boxSide, "down.png");
  //  saveButton=new squareButton(UIOrigin.x+(cube.side+3)*boxSide/2, UIOrigin.y+(cube.side+3)*boxSide, color(0,80,180),"save");
  //  clearButton=new squareButton(UIOrigin.x+(cube.side+6)*boxSide/2, UIOrigin.y+(cube.side+3)*boxSide, color(0,255,0), "clear");
}

void drawUI()
{
  image(logo, 0, 0);
  textSize(12);
  String []info= {
    "Make your own 3D paintings layer by layer,", 
    "Select any color from the pallette, and then click in a square to color that voxel", 
    "Change layers by clicking the up and down arrows", 
    "Click again on a color to pull a Winehouse and toggle it back to black.", 
    "You can also just use your keyboard:  the arrow keys move your selected key inside your layer,", 
    "     and the 'q' and 'a' keys to move up or down layers", 
    "Select a color from the swatches, and hit the space key to toggle that color in your selected point", 
    "Click the disk icon to save your drawing as a .L3D file into the program's directory, or press the 's' key", 
    "Click the sweep icon to clear your drawing, or press 'c'", 
    "Finally, you can drag and drop any .L3D file over the program, and it will load the image into the editor",
  };  
  HUDText(info, new PVector(0, 100, 0));
  pushMatrix();
  translate(UIOrigin.x, UIOrigin.y);
  drawLayer();
  popMatrix();
  for (int i=0; i<swatches.length; i++)
    swatches[i].draw();
  currentSwatch.draw();
  openButton.draw();
  saveButton.draw();
  clearButton.draw();
  upButton.draw();
  downButton.draw();

}

void HUDText(String[] info, PVector origin)
{
  fill(255);
  for (int i=0; i<info.length; i++)
    text(info[i], origin.x, origin.y+15*i, origin.z);
}

void drawLayer()
{
  strokeWeight(5);
  stroke(100);
  pushMatrix();
  for (int x=0; x<cube.side; x++)
    for (int z=0; z<cube.side; z++)
    {        
      pushMatrix();
      translate(x*boxSide, z*boxSide);
      fill(cube.cube[x][(int)selected.y][z]);
      rect(0, 0, boxSide, boxSide);
      popMatrix();
    }
  pushMatrix();
  pushStyle();
  stroke(255);
  translate(selected.x*boxSide, selected.z*boxSide);
  noFill();
  rect(0, 0, boxSide, boxSide);
  popMatrix();
  popStyle();
  popMatrix();
}

//thanks to Gael Beaunée for the arrow function
//(slightly modified from the original at http://gaelbn.com/wpblog/wp-content/files/drawArrow.pde)
void arrow(PVector start, PVector end, float beginHeadSize, float endHeadSize, boolean filled) {

  PVector d = new PVector(end.x - start.x, end.y - start.y);
  d.normalize();

  float coeff = 1.5;

  strokeCap(SQUARE);

  line(start.x+d.x*beginHeadSize*coeff/(filled?1.0f:1.75f), 
  start.y+d.y*beginHeadSize*coeff/(filled?1.0f:1.75f), 
  end.x-d.x*endHeadSize*coeff/(filled?1.0f:1.75f), 
  end.y-d.y*endHeadSize*coeff/(filled?1.0f:1.75f));

  float angle = atan2(d.y, d.x);

  if (filled) {
    // begin head
    pushMatrix();
    translate(start.x, start.y);
    rotate(angle+PI);
    triangle(-beginHeadSize*coeff, -beginHeadSize, -beginHeadSize*coeff, beginHeadSize, 0, 0);
    popMatrix();
    // end head
    pushMatrix();
    translate(end.x, end.y);
    rotate(angle);
    triangle(-endHeadSize*coeff, -endHeadSize, -endHeadSize*coeff, endHeadSize, 0, 0);
    popMatrix();
  } else {
    // begin head
    pushMatrix();
    translate(start.x, start.y);
    rotate(angle+PI);
    strokeCap(ROUND);
    line(-beginHeadSize*coeff, -beginHeadSize, 0, 0);
    line(-beginHeadSize*coeff, beginHeadSize, 0, 0);
    popMatrix();
    // end head
    pushMatrix();
    translate(end.x, end.y);
    rotate(angle);
    strokeCap(ROUND);
    line(-endHeadSize*coeff, -endHeadSize, 0, 0);
    line(-endHeadSize*coeff, endHeadSize, 0, 0);
    popMatrix();
  }
}



void mouseClicked()
{
  //if we clicked in the 2D layer box
  if ((mouseX>UIOrigin.x)&&(mouseX<UIOrigin.x+cube.side*boxSide)&&(mouseY>UIOrigin.y)&&(mouseY<UIOrigin.y+cube.side*boxSide))
  {
    selected.x=(int)((mouseX-UIOrigin.x)/boxSide);
    selected.z=(int)((mouseY-UIOrigin.y)/boxSide);
    println(selected);
    if (getVoxel(selected)==selectedColor)
    {
      setVoxel(selected, color(0));
      currentColor=color(0);
    } else
    {
      setVoxel(selected, selectedColor);
      currentColor=selectedColor;
    }
  }

  boolean clickedSwatch=false;
  for (int i=0; i<swatches.length; i++)
    clickedSwatch|=swatches[i].isClicked();
  if (clickedSwatch)
    for (int i=0; i<swatches.length; i++)
      if (swatches[i].isClicked())
      {
        selectedColor=swatches[i].buttonColor;
        currentSwatch.setColor(selectedColor);
        swatches[i].selected=true;
      } else
        swatches[i].selected=false;

  if (saveButton.isClicked())
    cube.saveFile();

  if (openButton.isClicked())
    selectInput("Select a .L3D file to open", "openFile", new File(dataPath("")));

  if (clearButton.isClicked())
    cubeBackground(0);

  if (upButton.isClicked())
  { 
    selected.y++;
    if (selected.y>=cube.side-1)
      selected.y=cube.side-1;
  }
  if (downButton.isClicked())
  {
    selected.y--;
    if (selected.y<0)
      selected.y=0;
  }

  cube.update();
}

void keyPressed()
{
  cube.setVoxel(selected, selectedColor);
  if (key==CODED) {
    if (keyCode==LEFT)
    {
      selected.x--;
      if (selected.x<0)
        selected.x=0;
    }
    if (keyCode==RIGHT)
    {
      selected.x++;
      if (selected.x>=cube.side-1)
        selected.x=cube.side-1;
    }
    if (keyCode==UP)
    {
      selected.z--;
      if (selected.z<0)
        selected.z=0;
    }
    if (keyCode==DOWN)
    {
      selected.z++;
      if (selected.z>=cube.side-1)
        selected.z=cube.side-1;
    }
  }
  if ((key=='Q')||(key=='q'))
  {
    selected.y++;
    if (selected.y>=cube.side-1)
      selected.y=cube.side-1;
  }
  if ((key=='A')||(key=='a'))
  {
    selected.y--;
    if (selected.y<0)
      selected.y=0;
  }

  //toggle selected voxel in current layer between selected color and black
  if (key==' ')
  {
    if (getVoxel(selected)==selectedColor)
    {
      setVoxel(selected, color(0));
      currentColor=color(0);
    } else
    {
      setVoxel(selected, selectedColor);
      currentColor=selectedColor;
    }
  }

  //clear the cube
  if (Character.toLowerCase(key)=='c')
  {
    cubeBackground(color(0));  //set all the buffer's voxels to black
    cube.background(color(0));   //set the cube object's voxels to black
  }

  //save the current design to an '.L3D' file, autonamed based on the current time
  if (Character.toLowerCase(key)=='s')
    cube.saveFile();

  cube.update();
}


//TODO -- error-check the file, just so we're not loading non-.L3D files,
//and give some feedback if it doesn't load correctly.

void openFile(File file) {
  cube.loadFile(file);
  for (int x=0; x<cube.side; x++)
    for (int y=0; y<cube.side; y++)
      for (int z=0; z<cube.side; z++)
        setVoxel(new PVector(x, y, z), cube.getVoxel(x, y, z));
}

void mouseDragged()
{
  rotX+=(float)(mouseY-pmouseY)/mouseScale;
  rotY+=(float)(mouseX-pmouseX)/mouseScale;
}

