import java.awt.*;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;

class Fuzzy_Logic extends Frame implements Imageinter{

  TextField pixelintensity;
  TextField imageintensity;
  Panel input;
   int[] origHistogram = new int[256];
  int[] newHistogram = new int[256];
  Fuzzy_Logic.OrigfuzzyHistPanel ofp;
  Fuzzy_Logic.NewfuzzyHistPanel nfp;
 
  Fuzzy_Logic(){
   
    Box aBox = new Box(BoxLayout.Y_AXIS);
    this.add(aBox,BorderLayout.CENTER);
    input = new Panel();
    Panel cPanel = new Panel();
    cPanel.add(new Label("Pixel Intensity"));
    pixelintensity = new TextField("1.0",5);
    cPanel.add(pixelintensity);
    input.add(cPanel);

    Panel bPanel = new Panel();
    bPanel.add(new Label("Image Intensity"));
    imageintensity = new TextField("1.0",5);
    bPanel.add(imageintensity);
    input.add(bPanel);

    input.add(new Label(""));

    input.setBackground(Color.RED);
    aBox.add(input);
    ofp = new Fuzzy_Logic.OrigfuzzyHistPanel();
    ofp.setBackground(Color.BLUE);
    aBox.add(ofp);
    nfp = new Fuzzy_Logic.NewfuzzyHistPanel();
    nfp.setBackground(Color.CYAN);
    aBox.add(nfp);
    setTitle("Image Enhancement");
    setBounds(400,0,275,400);
    setVisible(true);
  }
  class OrigfuzzyHistPanel extends Panel{
    public void paint(Graphics g){
     
      final int flip = 110;
      final int shift = 5;
      g.drawLine(0 + shift,flip,255 + shift,flip);
        for(int cnt = 0;cnt < origHistogram.length;cnt++){
   g.drawLine(cnt + shift,flip - 0, cnt + shift,flip - origHistogram[cnt]);
      }
    }
  }
  class NewfuzzyHistPanel extends Panel{
    public void paint(Graphics g){
      final int flip = 110;
     final int shift = 5;
      g.drawLine(0 + shift,flip,255 + shift,flip); 
      for(int cnt = 0;cnt < newHistogram.length;cnt++){
        g.drawLine(cnt + shift,flip - 0,
                    cnt + shift,
                      flip - newHistogram[cnt]);
      }
    }
  }
 
  public int[][][] processImg(int[][][] tDPix,int iRows,int iCols){

    System.out.println("W = " + iCols);
    System.out.println("H = " + iRows);

   int[][][] output3D =new int[iRows][iCols][4];
    double c = Double.parseDouble(pixelintensity.getText());
    double b = Double.parseDouble(imageintensity.getText());
    if(c<1||b<1||c>2||b>2)
    {
     JOptionPane.showMessageDialog(pixelintensity, "Enter Pixel and Image Intensity from 1.0 to 2.0");
    }
    else{
    
    for(int row = 0;row < iRows;row++){
      for(int col = 0;col < iCols;col++){
        output3D[row][col][0] = tDPix[row][col][0];
        output3D[row][col][1] = tDPix[row][col][1];
        output3D[row][col][2] = tDPix[row][col][2];
        output3D[row][col][3] = tDPix[row][col][3];
      }
    }
    
    int mean = getMean(output3D,iRows,iCols);
    
    removeMean(output3D,iRows,iCols,mean);
    int rms = getRms(output3D,iRows,iCols);
    scale(output3D,iRows,iCols,c);
   
    shiftMean(output3D,iRows,iCols,(int)(b*mean));
   
    clip(output3D,iRows,iCols);
   

    origHistogram = getHistogram(tDPix,iRows,iCols);
    ofp.repaint();

    newHistogram = getHistogram(output3D,iRows,iCols);
    nfp.repaint();
    }
    return output3D;
    
  }
  int[] getHistogram(int[][][] data3D,int imgRows,int imgCols){
    int[] hist = new int[256];
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        hist[data3D[row][col][1]]++;
        hist[data3D[row][col][2]]++;
        hist[data3D[row][col][3]]++;
      }
    }
    int max = 0;
    for(int cnt = 1;cnt < hist.length - 1;cnt++){
      if(hist[cnt] > max){
        max = hist[cnt];
      }
    }
    for(int cnt = 0;cnt < hist.length;cnt++){
      hist[cnt] = 100 * hist[cnt]/max;
    }
    return hist;
  }

  void removeMean(int[][][] data3D,int imgRows,int imgCols,int mean){
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        data3D[row][col][1] -= mean;
        data3D[row][col][2] -= mean;
        data3D[row][col][3] -= mean;
      }
    }
  }
  
  int getRms(int[][][] data3D,int imgRows,int imgCols){
    int pixelCntr = 0;
    long accum = 0;
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        accum += data3D[row][col][1] *
                             data3D[row][col][1];
        accum += data3D[row][col][2] *
                             data3D[row][col][2];
        accum += data3D[row][col][3] *
                             data3D[row][col][3];
        pixelCntr += 3;
      }
    }
    int meanSquare = (int)(accum/pixelCntr);
    int rms = (int)(Math.sqrt(meanSquare));
    return rms;
  }
 
   int getMean(int[][][] data3D,int imgRows,int imgCols){

    int pixelCntr = 0;
    long accum = 0;
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        accum += data3D[row][col][1];
        accum += data3D[row][col][2];
        accum += data3D[row][col][3];
        pixelCntr += 3;
      }
    }

    return (int)(accum/pixelCntr);

  }
  void scale(int[][][] data3D,int imgRows,int imgCols,double scale){
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        data3D[row][col][1] *= scale;
        data3D[row][col][2] *= scale;
        data3D[row][col][3] *= scale;
      }
    }
  }
 


  void shiftMean(int[][][] data3D,int imgRows, int imgCols,int newMean){
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        data3D[row][col][1] += newMean;
        data3D[row][col][2] += newMean;
        data3D[row][col][3] += newMean;
      }
    }
  }
 
  void clip(int[][][] data3D,int imgRows,int imgCols){
    for(int row = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        if(data3D[row][col][1] < 0)
          data3D[row][col][1] = 0;
        if(data3D[row][col][1] > 255)
          data3D[row][col][1] = 255;

        if(data3D[row][col][2] < 0)
          data3D[row][col][2] = 0;
        if(data3D[row][col][2] > 255)
          data3D[row][col][2] = 255;

        if(data3D[row][col][3] < 0)
          data3D[row][col][3] = 0;
        if(data3D[row][col][3] > 255)
          data3D[row][col][3] = 255;

      }
    }
  }

}