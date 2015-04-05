/**
Scrolling fourier landscape for the L3D cube
Based around Dan Ellis' LiveSpectrum example, from http://www.ee.columbia.edu/~dpwe/resources/Processing/LiveSpectrum.pde
 
By Alex Hornstein, Fall 2014
 */

import ddf.minim.analysis.*;
import ddf.minim.*;
import L3D.*;

Minim minim;
AudioInput in;
FFT fft;
L3D cube;
float[][] peaks;

int peak_hold_time = 1;  // how long before peak decays
float[] slowPeaks;
int[] peak_age;  // tracks how long peak has been stable, before decaying

// how wide each 'peak' band is, in fft bins
int binsperband = 40;
int peaksize; // how many individual peak bands we have (dep. binsperband)
float gain = 20; // in dB
float dB_scale = 2.0;  // pixels per dB

int buffer_size = 1024;  // also sets FFT size (frequency resolution)
float sample_rate = 44100;

int spectrum_height = 200; // determines range of dB shown
int legend_height = 20;
int spectrum_width = 512; // determines how much of spectrum we see
int legend_width = 40;

float max=0;

void setup()
{
  size(displayWidth, displayHeight, P3D);
  cube=new L3D(this);
  //  cube=new L3D(this, "your@spark.email", "your password", "cube name");
  textFont(createFont("SanSerif", 12));

  minim = new Minim(this);
  in = minim.getLineIn(Minim.MONO, buffer_size, sample_rate);

  // create an FFT object that has a time-domain buffer 
  // the same size as line-in's sample buffer
  fft = new FFT(in.bufferSize(), in.sampleRate());
  // Tapered window important for log-domain display
  fft.window(FFT.HAMMING);

  peaks = new float[cube.side][cube.side];
  // initialize peak-hold structures
  peaksize = 1+Math.round(fft.specSize()/binsperband);
  slowPeaks = new float[peaksize];
  peak_age = new int[peaksize];
}


void draw()
{
  // clear window
  background(0);
  cube.background(0);
  // perform a forward FFT on the samples in input buffer
  fft.forward(in.mix);

  pushMatrix();
  translate(0, 400);

  // draw peak bars
  noStroke();
  fill(0, 128, 144); // dim cyan
  int index=0;
  for(int i = 0; i < spectrum_width/4; i+=spectrum_width/cube.side/4)  {
    // draw the line for frequency band i using dB scale
    float val = dB_scale*(20*((float)Math.log10(fft.getBand(i))) + gain);
    if (fft.getBand(i) == 0) {   val = -200;   }  // avoid log(0)
    int peak=Math.round(val);
    //keep track of the max amplitude for the autoscaling
    if (peak>max)
      max=peak;

    peaks[index][0]=map(peak,0,max,0,cube.side);
    index++;
  }


  // now draw current spectrum in brighter blue
  stroke(64, 192, 255);
  noFill();
  for (int i = 0; i < spectrum_width; i++) {
    // draw the line for frequency band i using dB scale
    float val = dB_scale*(20*((float)Math.log10(fft.getBand(i))) + gain);
    if (fft.getBand(i) == 0) {   
      val = -200;
    }  // avoid log(0)
    int y = spectrum_height - Math.round(val);
    if (y > spectrum_height) { 
      y = spectrum_height;
    }
    line(legend_width+i, spectrum_height, legend_width+i, y);
    // update the peak record
    // which peak bin are we in?
    int peaksi = i/binsperband;
    if (val > slowPeaks[peaksi]) {
      slowPeaks[peaksi] = val;
      // reset peak age counter
      peak_age[peaksi] = 0;
    }
  }

  // add legend
  // frequency axis
  fill(255);
  stroke(255);
  int y = spectrum_height;
  line(legend_width, y, legend_width+spectrum_width, y); // horizontal line
  // x,y address of text is immediately to the left of the middle of the letters 
  textAlign(CENTER, TOP);
  for (float freq = 0.0; freq < in.sampleRate ()/2; freq += 2000.0) {
    int x = legend_width+fft.freqToIndex(freq); // which bin holds this frequency
    line(x, y, x, y+4); // tick mark
    text(Math.round(freq/1000) +"kHz", x, y+5); // add text label
  }

  // level axis
  int x = legend_width;
  line(x, 0, x, spectrum_height); // vertictal line
  textAlign(RIGHT, CENTER);
  for (float level = -100.0; level < 100.0; level += 20.0) {
    y = spectrum_height - (int)(dB_scale * (level+gain));
    line(x, y, x-3, y);
    text((int)level+" dB", x-5, y);
  }
  int z;
  popMatrix();

  for (x=0; x<cube.side; x++)
    for (z=0; z<cube.side; z++)
      for (y=0; y<peaks[x][z]; y++)
        cube.setVoxel(x, y, z, cube.colorMap(y, 0, cube.side-1));

  for(z=cube.side-1;z>0;z--)
    for(x=0;x<cube.side;x++)
      peaks[x][z]=peaks[x][z-1];
  
}

void keyReleased()
{
  // +/- used to adjust gain on the fly
  if (key == '+' || key == '=') {
    gain = gain + 5.0;
  } else if (key == '-' || key == '_') {
    gain = gain - 5.0;
  }
}

void stop()
{
  // always close Minim audio classes when you finish with them
  in.close();
  minim.stop();

  super.stop();
}

