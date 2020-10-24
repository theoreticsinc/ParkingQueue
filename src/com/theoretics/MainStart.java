package com.theoretics;

import java.net.*;
import java.io.*;

import com.pi4j.wiringpi.Spi;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.platform.PlatformManager;
import com.pi4j.system.NetworkInfo;
import com.pi4j.system.SystemInfo;
import com.pi4j.wiringpi.Gpio;
import com.theoretics.Convert;
import com.theoretics.DateConversionHandler;
import com.theoretics.NetworkClock;
import com.theoretics.RaspRC522;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainStart {

    String version = "v.3.0.1";
    String entranceID = "EN01";

    String cardFromReader = "";

    ArrayList<String> cards;
    ArrayList<String> types;
    //String card = "", type = "";
    private static Logger log = LogManager.getLogger(MainStart.class.getName());
    DateConversionHandler dch = new DateConversionHandler();
    private Thread ThrNetworkClock;
    private Thread ThrSystemClock;
//    final GpioPinDigitalOutput pin1;

    AudioInputStream welcomeAudioIn = null;
    AudioInputStream thankyouAudioIn = null;
    AudioInputStream pleasewaitAudioIn = null;
    AudioInputStream errorAudioIn = null;
    AudioInputStream beepAudioIn = null;
    AudioInputStream takeCardAudioIn = null;
    AudioInputStream bgAudioIn = null;
    Clip welcomeClip = null;
    Clip pleaseWaitClip = null;
    Clip thankyouClip = null;
    Clip beepClip = null;
    Clip takeCardClip = null;
    Clip errorClip = null;
    Clip bgClip = null;

    String strUID = "";
    String prevUID = "0";

    int MODE = 1;  //0 = Entry; 1 = Exit
    int OVERRIDE = 0;

    int debugMODE = 0;
    ParkingPanel ppUI = new ParkingPanel();

    final GpioController gpio = GpioFactory.getInstance();

    public final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "GREENLED", PinState.LOW);
    public final GpioPinDigitalOutput relay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "GREENLED", PinState.LOW);

    //RASPBERRY PI4
    //GpioPinDigitalInput buttonDOWN1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_DOWN);    
    //GpioPinDigitalInput buttonDOWN2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_23, PinPullResistance.PULL_UP);
    //RASPBERRY PI3
    //final GpioPinDigitalInput buttonDOWN1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN);    
    //final GpioPinDigitalInput buttonDOWN2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, PinPullResistance.PULL_UP);
    GpioPinDigitalInput buttonDOWN1 = null;
    GpioPinDigitalInput buttonDOWN2 = null;
    NetworkClock nc = null;
    FileHandler fh = new FileHandler();

    public void startProgram() {
        System.out.println(entranceID + " PARKING QUEUE Signal " + version);
        if (debugMODE == 0) {
            ppUI = new ParkingPanel();

            ppUI.dispose();
            ppUI.setUndecorated(true);
            ppUI.setVisible(true);
            ppUI.toFront();
            ppUI.requestFocus();
        }
        try {
            welcomeAudioIn = AudioSystem.getAudioInputStream(MainStart.class.getResource("/sounds/takecard.wav"));
            welcomeClip = AudioSystem.getClip();
            welcomeClip.open(welcomeAudioIn);
        } catch (Exception ex) {
            notifyError(ex);
        }
        try {
            pleasewaitAudioIn = AudioSystem.getAudioInputStream(MainStart.class.getResource("/sounds/plswait.wav"));
            pleaseWaitClip = AudioSystem.getClip();
            pleaseWaitClip.open(pleasewaitAudioIn);
        } catch (Exception ex) {
            notifyError(ex);
        }
        try {
            thankyouAudioIn = AudioSystem.getAudioInputStream(MainStart.class.getResource("/sounds/thankyou.wav"));
            thankyouClip = AudioSystem.getClip();
            thankyouClip.open(thankyouAudioIn);
        } catch (Exception ex) {
            notifyError(ex);
        }
        try {
            beepAudioIn = AudioSystem.getAudioInputStream(MainStart.class.getResource("/sounds/beep.wav"));
            beepClip = AudioSystem.getClip();
            beepClip.open(beepAudioIn);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        try {
            takeCardAudioIn = AudioSystem.getAudioInputStream(MainStart.class.getResource("/sounds/takecard.wav"));
            takeCardClip = AudioSystem.getClip();
            takeCardClip.open(takeCardAudioIn);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        try {
            errorAudioIn = AudioSystem.getAudioInputStream(MainStart.class.getResource("/sounds/beep.wav"));
            errorClip = AudioSystem.getClip();
            errorClip.open(errorAudioIn);
        } catch (Exception ex) {
            notifyError(ex);
        }

        try {
            bgAudioIn = AudioSystem.getAudioInputStream(MainStart.class.getResource("/sounds/bgmusic.wav"));
            bgClip = AudioSystem.getClip();
            bgClip.open(bgAudioIn);
        } catch (Exception ex) {
            notifyError(ex);
        }

        try {
            if (welcomeClip.isActive() == false) {
                welcomeClip.setFramePosition(0);
                welcomeClip.start();
                System.out.println("Welcome Message OK");
            }
        } catch (Exception ex) {
            notifyError(ex);
        }

        this.cards = new ArrayList<String>();
        this.types = new ArrayList<String>();

        //CONSTANTS.updateData();
        nc = new NetworkClock(this.cards, this.types, ppUI, MODE, OVERRIDE, led1);

        ThrNetworkClock = new Thread(nc);
        ThrNetworkClock.start();

        System.out.println("Reader Ready!");
        led1.high();

        if (MODE == 0) {
//            FileHandler fh = new FileHandler();
//            fh.createFile("entry");
//            fh.createFile("exit");
//            fh.writeFile("entry", "5");
//            fh.writeFile("exit", "8");
            GreetServer srvr = new GreetServer(nc);
            srvr.startServer();

        }
    }

    private void notifyError(Exception ex) {
        System.out.println(ex.getMessage());
        try {
            if (errorClip.isActive() == false) {
                //haltButton = false;
                errorClip.setFramePosition(0);
                errorClip.start();
            }
        } catch (Exception ex2) {
            System.out.println(ex2.getMessage());
        }
    }

    public void setupLED() {
        if (MODE == 0) {
            buttonDOWN1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, PinPullResistance.PULL_UP);
            buttonDOWN2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_UP);
        } else {
            buttonDOWN1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, PinPullResistance.PULL_UP);
            buttonDOWN2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_UP);
        }
        System.out.println("Setting Up GPIO!");
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println(" ==>> GPIO SETUP FAILED");
            return;
        }

        led1.setShutdownOptions(true, PinState.HIGH);
        relay.setShutdownOptions(true, PinState.HIGH);

        buttonDOWN1.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                if (event.getState() == PinState.LOW) {
                    System.out.println("RED WAS PRESSED DOWN");
                    try {
                        Thread.yield();
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(MainStart.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (debugMODE == 0) {
                        ppUI.sysMsg1.setText("RED WAS PRESSED DOWN");
                    }

                    if (MODE == 0) {
                        cards.add("-");
                        types.add("entering");
                        led1.high();
                        relay.high();
                        
                        //RESET ENTERING SCENARIO
                        try {
                            fh.writeFile("entry", "0");
                            ppUI.enteringLabel.setText(0 + "");
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        /*try {
                            String n = fh.readFile("entry");
                            int entry = Integer.parseInt(n);
                            entry--;
                            ppUI.enteringLabel.setText(entry + "");
                            fh.writeFile("entry", entry + "");
                        } catch (Exception exception) {
                            fh.writeFile("entry", "0");
                            exception.printStackTrace();
                        }*/
 /*
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                            @Override
                            public void run() {                                
//                                DataBaseHandler dbh = new DataBaseHandler(CONSTANTS.serverIP);
//                                dbh.setQUEUE("entering", "0");
                            }
                        },
                                1000
                        );*/
                    } else {
//                        cards.add("-");
//                        types.add("exiting");
                        GreetClient gc = new GreetClient();
                        try {
                            gc.startConnection(CONSTANTS.serverIP, 7890);
                            String resp = gc.sendMessage("removeExit");
                            System.out.println("Response from Server: " + resp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        led1.low();
//                        relay.low();
                        /*try {
                            String n = fh.readFile("exit");
                            int exit = Integer.parseInt(n);
                            exit--;
                            ppUI.exitingLabel.setText(exit + "");
                            fh.writeFile("exit", exit + "");
                        } catch (Exception exception) {
                            fh.writeFile("exit", "0");
                            exception.printStackTrace();
                        }*/
                    }
                } else if (event.getState() == PinState.HIGH) {
                    System.out.println("RED WAS PRESSED UP");
                    if (debugMODE == 0) {
                        ppUI.sysMsg1.setText("RED WAS PRESSED UP");
                    }
                }
                Thread.yield();
            }
        });

        buttonDOWN2.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                if (event.getState() == PinState.LOW) {
                    System.out.println("GREEN WAS PRESSED DOWN");
                    try {
                        Thread.yield();
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(MainStart.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (debugMODE == 0) {
                        ppUI.sysMsg1.setText("GREEN WAS PRESSED DOWN");
                    }

                    if (MODE == 0) {
                        cards.add("+");
                        types.add("entering");
                        led1.low();
                        relay.low();
                        //RESET EXITING SCENARIO
                        try {
                            fh.writeFile("exit", "0");
                            ppUI.exitingLabel.setText(0 + "");
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                    } else {
//                        cards.add("+");
//                        types.add("exiting");
                        GreetClient gc = new GreetClient();
                        try {
                            gc.startConnection(CONSTANTS.serverIP, 7890);
                            String resp = gc.sendMessage("exit");
                            gc.startConnection(CONSTANTS.serverIP, 7890);
                            resp = gc.sendMessage("override");
                            System.out.println("Response from Server: " + resp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /*                        
                        ppUI.exitingLabel.setText("+1");
//                        DataBaseHandler dbh = new DataBaseHandler(CONSTANTS.serverIP);
//                        String count = dbh.getQUEUE("entering");
                        if (debugMODE == 0) {
                            ppUI.exitingLabel.setText("count");
                        }*/
//                        led1.high();
//                        relay.low();
                        /*try {
                            String n = fh.readFile("exit");
                            int exit = Integer.parseInt(n);
                            exit++;
                            ppUI.exitingLabel.setText(exit + "");
                            fh.writeFile("exit", exit + "");
                        } catch (Exception exception) {
                            fh.writeFile("exit", "0");
                            exception.printStackTrace();
                        }*/
                    }
                } else if (event.getState() == PinState.HIGH) {
                    System.out.println("GREEN WAS PRESSED UP");
                    if (debugMODE == 0) {
                        ppUI.sysMsg1.setText("GREEN WAS PRESSED UP");
                    }
                }
                Thread.yield();
            }
        });
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main(String[] args) throws InterruptedException {
        MainStart m = new MainStart();
        m.setupLED();
//        InfoClass i = new InfoClass();
//        i.showInfo();
        m.startProgram();
//        while (true) {
//            Thread.sleep(5000L);
//        }
//
    }

}
