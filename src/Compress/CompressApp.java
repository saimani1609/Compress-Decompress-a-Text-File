package Compress;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;


public class CompressApp extends JApplet implements ActionListener{
  
  private static final long serialVersionUID = 123456789L;
  private JRadioButton zipButton = new JRadioButton( "Zip", true );
  private JRadioButton gzipButton = new JRadioButton( "GZip", false );
  private ButtonGroup  zipButtonGroup = new ButtonGroup();
  private String stringZIP = "ZIP";
  private String stringGZIP = "GZIP";
  private boolean zipBool = true;
  private JLabel compressFormat = new JLabel("Compression Format: ");
  private JLabel compressDecompress = new JLabel("Compression Mode: ");
  private JRadioButton compButton = new JRadioButton("Compress", true);
  private JRadioButton dcompButton = new JRadioButton("Decompress", false);
  private ButtonGroup compButtonGroup  = new ButtonGroup();
  private String stringCOMP = "COMPRESS";
  private String stringDCOMP = "DECOMPRESS";
  private boolean compBool = true;
  private boolean zipProcessBool = true;
  private boolean compProcessBool = true;

  private static final String newline = "\n";
  private JButton selectFileButton = new JButton("Select Source File");
  private JButton processFileButton = new JButton("Process Source File");
  private JTextArea log = new JTextArea(5,30);
  private JScrollPane logScrollPane = new JScrollPane(log);

  private String sourceFile = "";
  private String sourceDir = "";
  private String sourcePath = "";
  private String targetFile = "";
  private String targetPath = "";
  private String sourceFileExt = "";

  static final int BUFFER = 2048;
  private BufferedInputStream origin;

  public void init(){
    setLayout( new FlowLayout() ); // set frame layout

    //create COMPRESS/DECOMPRESS file mode radio buttons
    add(compressDecompress);
    add(compButton);
    add(dcompButton);
    compButtonGroup.add(compButton);
    compButtonGroup.add(dcompButton);
    compButton.addItemListener(new RadioButtonHandler(stringCOMP));
    dcompButton.addItemListener(new RadioButtonHandler(stringDCOMP));

    // create ZIP FORMAT radio buttons
    add(compressFormat);
    add(zipButton);
    add(gzipButton);
    zipButtonGroup.add(zipButton); // add zip to group
    zipButtonGroup.add(gzipButton); // add gzip to group
    zipButton.addItemListener(new RadioButtonHandler(stringZIP));
    gzipButton.addItemListener(new RadioButtonHandler(stringGZIP));

    //Show Dialog for file selection
    log.setMargin(new Insets(5,5,5,5));
    log.setEditable(false);
    log.setText("Compress a text file (.txt file) " + newline +
        "or decompress a compressed text file " + newline +
            "(.zip or .gzip format).");
    
    //selectFileButton and processFileButton Prep
    selectFileButton.addActionListener(this);
    processFileButton.addActionListener(this);

    //put the buttons in a separate panel
    //use FlowLayout
    JPanel buttonPanel = new JPanel(); 
    buttonPanel.add(selectFileButton);
    buttonPanel.add(processFileButton);

    //Add the buttons and the log to this panel.
    add(buttonPanel, BorderLayout.PAGE_START);
    add(logScrollPane, BorderLayout.CENTER);

    setSize( 350, 350 ); // set applet size
    setVisible( true ); // display applet
  }//end init

  // private inner class to handle radio button events
  private class RadioButtonHandler implements ItemListener 
  {
    private String stringType; // font associated with this listener

    public RadioButtonHandler(String s1)
    {
      stringType = s1; // set the font of this listener
    } // end constructor RadioButtonHandler

    // handle radio button events
    public void itemStateChanged(ItemEvent event)
    {
      if("ZIP".equals(stringType)||"GZIP".equals(stringType)){
        if("ZIP".equals(stringType)){
          zipBool = true;
        }else{
          zipBool = false;
        }
      }else{
        if("COMPRESS".equals(stringType)){
          compBool = true;
        }else{
          compBool = false;
        }
      }//if ZIP || GZIP

    } // end method itemStateChanged
  } // end private inner class RadioButtonHandler

  public void actionPerformed(ActionEvent e) {
    //Handle Select Source File button action.
    if (e.getSource() == selectFileButton) {
      Frame openDialogFrame = new Frame();
      FileDialog openDialog = new FileDialog(openDialogFrame, "Select a file:", FileDialog.LOAD);
      openDialog.setVisible(true);

      sourceFile = openDialog.getFile();
      sourceDir = openDialog.getDirectory();
      sourcePath = sourceDir + sourceFile;
       
      if (!sourceFile.isEmpty()){   
        int i = sourceFile.lastIndexOf('.');

        if (i > 0 &&  i < sourceFile.length() - 1) {
          sourceFileExt = sourceFile.substring(i+1).toLowerCase();
        }

        if( compBool && "txt".equals(sourceFileExt) && zipBool){
          //compress file to zip format
          compProcessBool = true;
          targetFile = sourceFile.substring(0, sourceFile.length()-3) + "zip"; 
          targetPath = sourcePath.substring(0, sourcePath.length()-3) + "zip";
          zipProcessBool = true;
          log.setText("Selected File: " + sourcePath + newline + 
              "Target File: " + targetPath);
        } else if (compBool && "txt".equals(sourceFileExt) && !zipBool ){
          //compress file to gzip format
          compProcessBool = true;
          targetFile = sourceFile.substring(0, sourceFile.length()-3) + "gzip";
          targetPath = sourcePath.substring(0, sourcePath.length()-3) + "gzip";
          zipProcessBool = false;
          log.setText("Selected File: " + sourcePath + newline + 
              "Target File: " + targetPath);
        }else if(!compBool && "zip".equals(sourceFileExt) && zipBool){
          //decompress zip file
          compProcessBool = false;
          zipProcessBool = true;
          targetFile = sourceFile.substring(0, sourceFile.length()-3)+ "txt";
          targetPath = sourcePath.substring(0, sourcePath.length()-3) + "txt";
          log.setText("Selected File: " + sourcePath + newline + 
              "Target File: " + targetPath);
        }else if(!compBool && "gzip".equals(sourceFileExt) && !zipBool ){
          //decompress gzip file
          compProcessBool = false;
          zipProcessBool = false;
          targetFile = sourceFile.substring(0, sourceFile.length()-4) + "txt"; 
          targetPath = sourcePath.substring(0, sourcePath.length()-4) + "txt";
          log.setText("Selected File: " + sourcePath + newline + 
              "Target File: " + targetPath);
        }else{
          log.setText("Selected File: " + sourcePath + newline + 
              "not proper file type for compression mode" + newline +
          "or compresson format.");
          targetFile = "";
          targetPath = "";
        }//if compBool && "txt".equals(sourceFileExt)
      } else {
        log.setText("Select command cancelled by user." + newline +
            "Compress a text file (.txt file) " + newline +
            "or decompress a compressed text file " + newline +
        "(.zip or .gzip format).");
      }


    //Handle Process Source File button action.
    } else if (e.getSource() == processFileButton) {
      if(!sourceFile.isEmpty() && !targetFile.isEmpty()){
        if( compProcessBool ){
          //compress file
          if(zipProcessBool){             
            compressFile(sourceFile, sourcePath, targetPath);
            log.setText("Zip Compression of " + newline + 
                sourcePath + newline + "is complete.");
          }else{
            gcompressFile(sourceFile, sourcePath, targetPath);
            log.setText("GZip Compression of " + newline + 
                sourceFile + newline + "is complete.");
          }//if(zipProcessBool)
        }else{
          //decompress file
          if(zipProcessBool){
            dcompressFile(sourceFile, sourcePath, sourceDir, targetFile);
            log.setText("Zip Decompression of " + newline + 
                sourceFile + newline + "is complete.");
          }else{
            gdcompressFile(sourceFile,sourcePath, sourceDir, targetFile);
            log.setText("GZip Decompression of " + newline + 
                sourceFile + newline + "is complete.");
          }//if(zipProcessBool)
        }
      }else{
        log.setText("Proper Source File not selected.  No Processing Performed." + 
            newline +
            "Compress a text file (.txt file) " + newline +
            "or decompress a compressed text file " + newline +
                "(.zip or .gzip format).");
      }
    }// end if e.getSource()

  }//end void actionPerformed
  
  private void compressFile(String source, String sourcePath, String targetPath){
    ZipOutputStream out = null;
    try { 

      FileOutputStream dest = new FileOutputStream(targetPath);
      out = new ZipOutputStream(new BufferedOutputStream(dest));

      File fil = new File(sourcePath);
      FileInputStream fis = new FileInputStream(fil);

      origin = new BufferedInputStream(fis, BUFFER);

      ZipEntry entry = new ZipEntry(source);
      out.putNextEntry(entry);


      int size = 0;            
      byte[] buffer = new byte[BUFFER]; 

      // read data to the end of the source file and write it to the zip 
      //output stream. 
      while ((size = origin.read(buffer, 0, BUFFER)) > 0) {            
        out.write(buffer, 0, size); 
      } 

      out.flush();

    } catch (IOException e) { 
      e.printStackTrace(); 
    }finally{
      try {
        origin.close();
        out.close();
      } catch (IOException e) {}  
    }
  }//end compressFile
  
  private void gcompressFile(String source, String sourcePath, String targetPath){
    GZIPOutputStream out = null;
    try {
        // Open files to read and write      
      FileOutputStream dest = new FileOutputStream(targetPath);
      out = new GZIPOutputStream(new BufferedOutputStream(dest));

      File fil = new File(sourcePath);
      FileInputStream fis = new FileInputStream(fil);
      
      origin = new BufferedInputStream(fis, BUFFER);
      
        //read data to the end of the source file and write it to the 
      //output stream.  
        int size = 0;
        byte[] buffer = new byte[BUFFER];
        
        while ((size= origin.read(buffer, 0, BUFFER)) > 0) {            
        out.write(buffer, 0, size); 
      } 
        
        out.flush();    
    } catch (IOException e) {
      e.printStackTrace();
    }finally{
      try {
        origin.close();
        out.close();
      } catch (IOException e) {}  
    }
  }//end gcompressFile
  
  
  private void dcompressFile(String source, String sourcePath, String sourceDir, String target){
    BufferedOutputStream dest = null;
        try {
            FileInputStream fis = new FileInputStream(sourcePath);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;


            // Read each entry from the ZipInputStream until no more entry found
            // indicated by a null return value of the getNextEntry() method.

            while ((entry = zis.getNextEntry()) != null) {
                int size;
                byte[] buffer = new byte[BUFFER];
               
                FileOutputStream fos = new FileOutputStream(sourceDir + entry.getName());
                dest = new BufferedOutputStream(fos, BUFFER);

                while ((size = zis.read(buffer, 0, BUFFER)) > 0) {
                    dest.write(buffer, 0, size);
                }//while size
                dest.flush();
                dest.close();
            }//while entry

            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
  }//end dcompress
  
  private void gdcompressFile(String source, String sourcePath, String sourceDir, String target){
    BufferedOutputStream dest = null;
    try{ 
           FileInputStream fis = new FileInputStream(sourcePath);
           GZIPInputStream gzis = new GZIPInputStream(new BufferedInputStream(fis));
           
           FileOutputStream fos = new FileOutputStream(sourceDir + target);
           dest = new BufferedOutputStream(fos, BUFFER);
           
           int size;
           byte[] buffer = new byte[BUFFER]; 
           while ((size = gzis.read(buffer,0,BUFFER)) > 0) {
             dest.write(buffer, 0, size);
           }
           dest.flush();
           dest.close();
           
           gzis.close();
           fos.close();
      }
      catch(IOException e){
        e.printStackTrace();
      }    
  }//end gdcompress

}//end ComppressApp
