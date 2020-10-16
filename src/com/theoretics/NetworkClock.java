/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.theoretics;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.theoretics.DataBaseHandler;
import com.theoretics.SystemStatus;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Theoretics Inc
 */
public class NetworkClock implements Runnable {

    //String card, type;
    ArrayList<String> cards;
    ArrayList<String> types;
    //String serverIP = "192.168.1.10";
    DataBaseHandler dbh = new DataBaseHandler(CONSTANTS.serverIP);
    static Logger log = LogManager.getLogger(NetworkClock.class.getName());
    //String entranceID = "Entry Zone 2";
//    ParkingPanel pp = null;
    int MODE, OVERRIDE;
    GpioPinDigitalOutput led1;
    ParkingPanel pp = new ParkingPanel();
    
    public NetworkClock(ArrayList<String> card, ArrayList<String> type, ParkingPanel p1, int MODE, int OVERRIDE, GpioPinDigitalOutput led1) {
        this.cards = card;
        this.types = type;
        this.pp = p1;
        this.MODE = MODE;
        this.OVERRIDE = OVERRIDE;
        this.led1 = led1;
    }

    @Override
    public void run() {
        while (true) {
            try {
                SystemStatus ss = new SystemStatus();
                boolean online = ss.checkPING(CONSTANTS.serverIP);//LINUX USE ONLY - also check your root password
                if (cards.isEmpty() == false) {
                    String cardFromReader = cards.get(0);
                    if (online == true) {
                        System.out.println("ONLINE");
                        System.out.print("`/");

                        //SAVE Card to DATABASE
                        boolean isValid = false;
                        boolean isUpdated = false;
                        
                            isValid = dbh.updateQUEUE(types.get(0), cardFromReader);
                            System.out.println(cardFromReader + " isValid:" + isValid);
                            if (isValid) {
                                cards.remove(0);
                            }
                        

                    } else if (online == false) {
                        System.out.println("OFFLINE");
                        System.out.print("-");
                    }
                    Thread.sleep(100);
                    System.out.println("NETWORK");
                    
                    //resetAdmin();
                    //Thread.sleep(2000);
                } else {
                    if (online == true) {
                        String count = "";
                        int override = 0;
//                        if (MODE == 0) {
                            count = dbh.getQUEUE("entering");
                            pp.enteringLabel.setText(count);                    
                            OVERRIDE = dbh.getOVERRIDE();
                            if (OVERRIDE == 1) {
                                pp.sysMsg1.setText("override relay");
                                led1.high();
                                dbh.updateOVERRIDE("0");
                            }
//                        } else {
                            count = dbh.getQUEUE("exiting");
                            pp.exitingLabel.setText(count);    
//                        }
                        
                    }
                }
                Thread.sleep(100);
                System.out.println(".");
                pp.sysMsg1.setText("");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
