/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.theoretics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theoretics
 */
public class FileHandler {

    public void createFile(String filename) {
        try {
            File file = new File(filename);
            file.createNewFile(); // if file already exists will do nothing
            FileOutputStream oFile = new FileOutputStream(file, false);
        } catch (Exception ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeFile(String filename, String data) {
    
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filename);
            byte[] strToBytes = data.getBytes();
            outputStream.write(strToBytes);    
            outputStream.close();
        } catch (Exception ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (Exception ex) {
                Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public String readFile(String filename) {
        FileReader fr = null;
        try {
            fr = new FileReader(filename); //reads the file
        } catch (Exception ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
        //StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
        String line = "";
        /*try {
            while ((line = br.readLine()) != null) {
            sb.append(line);      //appends line to string buffer
            sb.append("\n");     //line feed
            }
            } catch (Exception ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        
        try {            
            line = br.readLine();
        } catch (Exception ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            fr.close();    //closes the stream and release the resources  
        } catch (Exception ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Contents of File: ");
        //System.out.println(sb.toString());   //returns a string that textually represents the object  
        return line;
    }
}
