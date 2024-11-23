




import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import sun.awt.image.ToolkitImage;

public class Image_Enhancement extends Frame{
  Image rImg;
  BufferedImage bImage;
  Image modImg;
  int iCols;
  int iRows; 
  int inTop;
  int inLeft;
  String dst = ""; 
   
 static String theProcessing = "Fuzzy_Logic";

 String nam1;
 
 
  //String Img ="C:\\Users\\SUHAS ACHARYA S\\Documents\\NetBeansProjects\\ImageEnhancement\\src\\Gallery\\"+nam1;

  MediaTracker t;
  Image_Enhancement.Display d = new Image_Enhancement.Display();
  Button rB = new Button("Process");
 
  int[][][] threeDP;
  int[][][] threeDPMod;
  int[] oneDP;

  Imageinter imageProcessing;
 
 
 //public static void main(String[] args){
   // Image_Enhancement obj = new Image_Enhancement();
  //}
 
  public Image_Enhancement(String ss){
 
    rImg = Toolkit.getDefaultToolkit().getImage(ss);
    t = new MediaTracker(this);
    t.addImage(rImg,1);

    try{
      if(!t.waitForID(1,10000)){
        System.out.println("Load error.");
        System.exit(1);
      }
    }catch(InterruptedException e){
      e.printStackTrace();
      System.exit(1);
    }
    if((t.statusAll(false)& MediaTracker.ERRORED & MediaTracker.ABORTED) != 0){
      System.out.println("Load errored or aborted");
      System.exit(1);
    }
    iCols = rImg.getWidth(this);
    iRows = rImg.getHeight(this);

    this.setTitle("Image Enhancement");
    this.setBackground(Color.GREEN);
    this.add(d);
    this.add(rB,BorderLayout.SOUTH);
   
    setVisible(true);
    
    inTop = this.getInsets().top;
    inLeft = this.getInsets().left;
    int buttonHeight = rB.getSize().height;
   
    this.setSize(2*inLeft+iCols + 1,inTop + buttonHeight + 2*iRows + 7);
    rB.addActionListener(
      new ActionListener(){
        public void actionPerformed(ActionEvent e){
        
          threeDPMod =imageProcessing.processImg(
                      threeDP,iRows,iCols);
        
          oneDP = convertOneDim(threeDPMod,iCols,iRows);
         
          modImg = createImage(
             new MemoryImageSource(iCols,iRows,oneDP,0,iCols));
          d.repaint();
        }
      }
    );
   
    oneDP = new int[iCols * iRows];
    bImage = new BufferedImage(iCols,iRows,BufferedImage.TYPE_INT_ARGB);

    Graphics g = bImage.getGraphics();
    g.drawImage(rImg, 0, 0, null);

   
    DataBufferInt dataBufferInt =(DataBufferInt)bImage.getRaster().getDataBuffer();
    oneDP = dataBufferInt.getData();
    threeDP = convertThDim(oneDP,iCols,iRows);

    try{
      imageProcessing = (Imageinter)Class.forName(theProcessing).newInstance();

      
      Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new ActionEvent(rB,ActionEvent.ACTION_PERFORMED,"PROCESS"));


    }catch(Exception e){
      System.out.println(e);
    }

    this.setVisible(true);
 
    this.addWindowListener(
      new WindowAdapter(){
        public void windowClosing(WindowEvent e){
          System.exit(0);
        }
      }
    );
   

  }

    Image_Enhancement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
  
 
 
  class Display extends Canvas{
   
    public void paint(Graphics g){
     
      if (t.statusID(1, false) == MediaTracker.COMPLETE){
        if((rImg != null) && (modImg != null)){
          g.drawImage(rImg,0,0,this);
          g.drawImage(modImg,0,iRows + 1,this);
          BufferedImage buffered = ((ToolkitImage) modImg).getBufferedImage();
          saveImage(dst,buffered);
        }
      }
    }
  }

  int[][][] convertThDim(int[] oneDPix,int imgCols,int imgRows){
    
    int[][][] data = new int[imgRows][imgCols][4];

    for(int row = 0;row < imgRows;row++){
     
      int[] aRow = new int[imgCols];
      for(int col = 0; col < imgCols;col++){
        int element = row * imgCols + col;
        aRow[col] = oneDPix[element];
      }

      for(int col = 0;col < imgCols;col++){
       
        data[row][col][0] = (aRow[col] >> 24)
                                          & 0xFF;
       
        data[row][col][1] = (aRow[col] >> 16)
                                          & 0xFF;
       
        data[row][col][2] = (aRow[col] >> 8)
                                          & 0xFF;
        
        data[row][col][3] = (aRow[col])
                                          & 0xFF;
      }
    }
    return data;
  }
 
  int[] convertOneDim(
         int[][][] data,int imgCols,int imgRows){
    
    int[] oneDPix = new int[imgCols * imgRows * 4];
    for(int row = 0,cnt = 0;row < imgRows;row++){
      for(int col = 0;col < imgCols;col++){
        oneDPix[cnt] = ((data[row][col][0] << 24)
                                   & 0xFF000000)
                     | ((data[row][col][1] << 16)
                                   & 0x00FF0000)
                      | ((data[row][col][2] << 8)
                                   & 0x0000FF00)
                           | ((data[row][col][3])
                                   & 0x000000FF);
        cnt++;
      }

    }

    return oneDPix;
  }
  public static void saveImage(String filename,  
            BufferedImage image) { 
        File file = new File(filename); 
        try { 
            ImageIO.write(image, "png", file); 
        } catch (Exception e) { 
            System.out.println(e.toString()+" Image '"+filename 
                                +"' saving failed."); 
        } 
    } 
}

