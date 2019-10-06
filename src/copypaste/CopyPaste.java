/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copypaste;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JLabel;

/**
 *
 * @author DoctorOne
 */
public class CopyPaste implements Runnable {

    /**
     * @param args the command line arguments
     */
    private int secretCounter = 0;
    private int timer = 0;
    private int counter = 0;
    private File source;
    private File dest;
    private long timeInMilis;
    private JLabel counterLabel;

    public CopyPaste(File source, JLabel counterLabel, int timer) {
        this.source = source;
        timeInMilis = Calendar.getInstance().getTimeInMillis();
        this.counterLabel = counterLabel;
        this.timer = timer;
    }
    
    public void setTimer (int set) {
        timeInMilis = Calendar.getInstance().getTimeInMillis();
        timer = set;
        secretCounter = 0;
    }
    
    public int getCounter() {
        return counter;
    }
    
    @Override
    public void run() {
        while(true) {
            if(timeInMilis + secretCounter * timer < Calendar.getInstance().getTimeInMillis()) {
                counter++;
                secretCounter++;
                File folder = new File("backups");
                if(!folder.exists()){
                    System.out.println("Folder created.");
                    folder.mkdir();
                }
                dest = new File("backups/" + source.getName() + new SimpleDateFormat("dd-M-yyyy hh-mm-ss").format(new Date(timeInMilis + (counter * timer) - timer)));
                System.out.println("Copying file.");
                copyFile(source, dest);
                System.out.println("File coppied : " + dest.getAbsolutePath());
            }
            counterLabel.setText("Counter : " + counter);
        }
    }
    
    private void copyFile(File source, File dest) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            if(!source.isDirectory()) {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } else {
                if (!dest.exists()) {
                        dest.mkdir();
                    }
                for(File file : source.listFiles()) {
                    if(file.isDirectory()) {
                        copyFile(file, new File(dest.getAbsolutePath().replace("\\", "/") + "/" + file.getName() + "/"));
                    }
                    else
                        copyFile(file, new File(dest.getAbsolutePath().replace("\\", "/") + "/" + file.getName()));
                }
                return;
            }
           } catch (FileNotFoundException ex) {
               ex.printStackTrace();
               counterLabel.setText("File not found.");
            } catch (IOException ex) {
                counterLabel.setText("Something went wrong, IO Exception.");
            }finally{
                try {
                    if(sourceChannel != null && sourceChannel.isOpen())
                        sourceChannel.close();
                    if(destChannel != null && destChannel.isOpen())
                        destChannel.close();
                } catch (IOException ex) {
                    counterLabel.setText("IO Exception while closing streams.");
                } catch (NullPointerException ex) {
                    System.out.println("Null Pointer ex.");
                }
            }
     }
    
}
