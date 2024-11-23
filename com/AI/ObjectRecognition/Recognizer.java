package com.AI.ObjectRecognition;

import com.AI.tablelayout.swing.Table;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import voice.speech.VoiceSpeech;
import voice.speech.demojsapi;

/**
 *
 * 
 * Kindly: Don't remove this header
 * Download the pre-trained inception model from here: https://storage.googleapis.com/download.tensorflow.org/models/inception_dec_2015.zip 
 */
public class Recognizer extends JFrame implements ActionListener {


    private Table table;
    private JButton predict;
    private JTextField result;
    private JTextField imgpth;
    
    private String imagepath1;
    
    private boolean modelselected = false;
    private byte[] graphDef;
    private List<String> labels;

    public Recognizer(String s) {
        modelselected = true;
        graphDef = readAllBytesOrExit(Paths.get("", "C:\\Users\\rakes\\Desktop\\Project\\inception_dec_2015\\tensorflow_inception_graph.pb"));
        labels = readAllLinesOrExit(Paths.get("", "C:\\Users\\rakes\\Desktop\\Project\\inception_dec_2015\\imagenet_comp_graph_label_strings.txt"));
        imagepath1=s;
        
        setTitle("Depth Object Recognition - Suhas.com");
        setSize(500, 500);
        table = new Table();
        
        predict = new JButton("Predict");
        predict.setEnabled(true);
       
        predict.addActionListener(this);
        result=new JTextField();
       
        imgpth=new JTextField();
        imgpth.setText("Inception Object Recognition using DCNN");
        
        imgpth.setEditable(false);
      
        getContentPane().add(table);
       
        table.row();
        table.addCell(imgpth).width(250);
        

        table.row();
        table.row();
        table.addCell(predict).colspan(2);
        table.row();
        table.addCell(result).width(300).colspan(2);
       
       
        setLocationRelativeTo(null);

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

         /*if (e.getSource() == img) {
            int returnVal = imgch.showOpenDialog(Recognizer.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = imgch.getSelectedFile();
                    imagepath = file.getAbsolutePath();
                    imgpth.setText(imagepath);
                    System.out.println("Image Path: " + imagepath1);
                    //imagepath1=imagepath;
                    Image img = ImageIO.read(file);

                    viewer.setIcon(new ImageIcon(img.getScaledInstance(200, 200, 200)));
                    if (modelselected) {
                        predict.setEnabled(true);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Recognizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("Process was cancelled by user.");
            }*/
         if (e.getSource() == predict) {
            byte[] imageBytes = readAllBytesOrExit(Paths.get(imagepath1));

            try (Tensor image = Tensor.create(imageBytes)) {
                demojsapi v = new demojsapi();
                float[] labelProbabilities = executeInceptionGraph(graphDef, image);
                int bestLabelIdx = maxIndex(labelProbabilities);
                result.setText("");
                result.setText(String.format(
                                "MATCH: %s (%.2f%% likely)",
                                labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f));
                System.out.println(
                        String.format(
                                "MATCH: %s (%.2f%% likely)",
                                labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f));
                v.dospeak(String.format(
                                "MATCH: %s (%.2f%% likely)",
                                labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f),"kevin16");
            }

        }
    }
    
     public String prediction() {

            byte[] imageBytes = readAllBytesOrExit(Paths.get(imagepath1));
            float[] labelProbabilities;
                    int bestLabelIdx;
            try (Tensor image = Tensor.create(imageBytes)) {
                demojsapi v = new demojsapi();
                labelProbabilities = executeInceptionGraph(graphDef, image);
                bestLabelIdx = maxIndex(labelProbabilities);
                result.setText("");
                result.setText(String.format(
                                "MATCH: %s (%.2f%% likely)",
                                labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f));
                System.out.println(
                        String.format(
                                "MATCH: %s (%.2f%% likely)",
                                labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f));
                v.dospeak(String.format(
                                "MATCH: %s (%.2f%% likely)",
                                labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f),"kevin16");
            }
            return String.format("MATCH: %s (%.2f%% likely)",
                                labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f);
        
    }

    ///
    private static float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                    Tensor result = s.runner().feed("DecodeJpeg/contents", image).fetch("softmax").run().get(0)) {
                final long[] rshape = result.shape();
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)));
                }
                int nlabels = (int) rshape[1];
                return result.copyTo(new float[1][nlabels])[0];
            }
        }
    }

    private static int maxIndex(float[] probabilities) {
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }

    private static byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }

    // In the fullness of time, equivalents of the methods of this class should be auto-generated from
    // the OpDefs linked into libtensorflow_jni.so. That would match what is done in other languages
    // like Python, C++ and Go.
    static class GraphBuilder {

        GraphBuilder(Graph g) {
            this.g = g;
        }

        Output div(Output x, Output y) {
            return binaryOp("Div", x, y);
        }

        Output sub(Output x, Output y) {
            return binaryOp("Sub", x, y);
        }

        Output resizeBilinear(Output images, Output size) {
            return binaryOp("ResizeBilinear", images, size);
        }

        Output expandDims(Output input, Output dim) {
            return binaryOp("ExpandDims", input, dim);
        }

        Output cast(Output value, DataType dtype) {
            return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
        }

        Output decodeJpeg(Output contents, long channels) {
            return g.opBuilder("DecodeJpeg", "DecodeJpeg")
                    .addInput(contents)
                    .setAttr("channels", channels)
                    .build()
                    .output(0);
        }

        Output constant(String name, Object value) {
            try (Tensor t = Tensor.create(value)) {
                return g.opBuilder("Const", name)
                        .setAttr("dtype", t.dataType())
                        .setAttr("value", t)
                        .build()
                        .output(0);
            }
        }

        private Output binaryOp(String type, Output in1, Output in2) {
            return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
        }

        private Graph g;
    }
    ////////////

    //public void rec(String s) {
        //imagepath1=s;
        //SwingUtilities.invokeLater(new Runnable() {
            //public void run() {
                //new Recognizer().setVisible(true);

            //}
        //});
   // }
   

}
