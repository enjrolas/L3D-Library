import L3D.*;
L3D cube;
int side=0;
int inc=1;
int mode=0;
color cubeCol;
void setup()
{
  size(displayWidth, displayHeight, P3D);
  cube=new L3D(this);
//  cube=new L3D(this, "your@spark.email", "your password", "cube name");
}

void draw()
{
  background(0);
  cube.background(0);
  PVector topLeft=new PVector(0, 0, 0);
  switch(mode) {
    case(0):
    topLeft=new PVector(0, 0, 0);
    cubeCol=color(255, 0, 0);
    break;
    case(1):
    topLeft=new PVector(cube.side-1-side, 0, 0);
    cubeCol=color(255, 255, 0);
    break;
    case(2):
    topLeft=new PVector(cube.side-1-side, cube.side-1-side, 0);
    cubeCol=color(0, 255, 0);
    break;
    case(3):
    topLeft=new PVector(0, cube.side-1-side, 0);
    cubeCol=color(0, 0, 255);
    break;
    case(4):
    topLeft=new PVector(0, 0, cube.side-1-side);
    cubeCol=color(255, 0, 255);
    break;
    case(5):
    topLeft=new PVector(cube.side-1-side, 0, cube.side-1-side);
    cubeCol=color(0, 255, 255);
    break;
    case(6):
    topLeft=new PVector(cube.side-1-side, cube.side-1-side, cube.side-1-side);
    cubeCol=color(255, 255, 255);
    break;
    case(7):
    topLeft=new PVector(0, cube.side-1-side, cube.side-1-side);
    cubeCol=color(0, 180, 130);
    break;
  }
  drawCube(topLeft, side, cubeCol);
  if (frameCount%5==0)
    cubeInc();
}

void drawCube(PVector topLeft, int side, color col)
{
  PVector[] topPoints=new PVector[4];
  PVector[] bottomPoints=new PVector[4];
  topPoints[0]=topLeft;
  topPoints[1]=new PVector(topLeft.x+side, topLeft.y, topLeft.z);
  topPoints[2]=new PVector(topLeft.x+side, topLeft.y+side, topLeft.z);
  topPoints[3]=new PVector(topLeft.x, topLeft.y+side, topLeft.z);
  PVector bottomLeft=new PVector(topLeft.x, topLeft.y, topLeft.z+side);
  bottomPoints[0]=bottomLeft;
  bottomPoints[1]=new PVector(bottomLeft.x+side, bottomLeft.y, bottomLeft.z);
  bottomPoints[2]=new PVector(bottomLeft.x+side, bottomLeft.y+side, bottomLeft.z);
  bottomPoints[3]=new PVector(bottomLeft.x, bottomLeft.y+side, bottomLeft.z);
  for (int i=0; i<4; i++)
  {
    drawLine(topPoints[i], bottomPoints[i], col);
    drawLine(topPoints[i], topPoints[(i+1)%4], col);
    drawLine(bottomPoints[i], bottomPoints[(i+1)%4], col);
  }
  color complement=complement(col);
  for (int i=0; i<4; i++)
  {
    cube.setVoxel(topPoints[i], complement);
    cube.setVoxel(bottomPoints[i], complement);
  }
}

// draws a line from point p1 to p2 and colors each of the points according
// to the col parameter
// p1 and p2 can be outside of the cube, but it will only draw the parts of
// the line that fall
// inside the cube
void drawLine(PVector p1, PVector p2, color col) {
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
      mixVoxel(currentPoint, col);
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
      mixVoxel(currentPoint, col);
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
      mixVoxel(currentPoint, col);
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

  mixVoxel(currentPoint, col);
}


void mixVoxel(PVector currentPoint, color col)
{
  color currentCol=cube.getVoxel(currentPoint);
  color newCol=color(red(currentCol)+red(col), green(currentCol)+green(col), blue(currentCol)+blue(col));
  cube.setVoxel(currentPoint, newCol);
}

void keyPressed()
{
  cubeInc();
}

void cubeInc()
{
  side+=inc;
  if ((side==cube.side-1)||(side==0))
    inc*=-1;
  if (side==0)
    mode++;
  if (mode>7)
    mode=0;
}  

color complement(color original)
{
  float R = red(original);
  float G = green(original);
  float B = blue(original);
  float minRGB = min(R, min(G, B));
  float maxRGB = max(R, max(G, B));
  float minPlusMax = minRGB + maxRGB;
  color complement = color(minPlusMax-R, minPlusMax-G, minPlusMax-B);
  return complement;
}

