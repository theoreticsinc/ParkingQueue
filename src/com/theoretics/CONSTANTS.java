/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.theoretics;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theoretics Inc
 */
public class CONSTANTS {
//    public static String serverIP = "127.0.0.1";
    public static String serverIP = "192.168.1.80";
    public static String entranceID = "Entry Zone 1";  //BOOTH 
    
    //public static String entranceID = "Entry Zone 2";  //BOOTH B
    
    public static String CAMipaddress1 = "192.168.100.221"; //Entry Camera Booth A
    public static String CAMipaddress2 = "192.168.100.221"; //Entry Camera Booth B
    public static String CAMusername = "admin";
    public static String CAMpassword = "user1234";
    public static String USERNAME = "base";   //root
    public static String PASSWORD = "theoreticsinc";     //sa
    
    public static void updateData() {
        XMLreader xr = new XMLreader();
        try {
            serverIP = xr.getElementValue("/home/pi/net.xml", "main1");
        } catch (Exception ex) {
            Logger.getLogger(CONSTANTS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            CAMipaddress1 = xr.getElementValue("/home/pi/net.xml", "entryCam1");
        } catch (Exception ex) {
            Logger.getLogger(CONSTANTS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            CAMipaddress2 = xr.getElementValue("/home/pi/net.xml", "entryCam2");
        } catch (Exception ex) {
            Logger.getLogger(CONSTANTS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
