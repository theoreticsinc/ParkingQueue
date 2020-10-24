/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.theoretics;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
//import com.theoretics.DataBaseHandler;
import com.theoretics.SystemStatus;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Theoretics Inc
 */
public class NetworkClock implements Runnable {

    int debugMODE = 0;
    //String card, type;
    ArrayList<String> cards;
    ArrayList<String> types;
    //String serverIP = "192.168.1.10";
//    DataBaseHandler dbh = new DataBaseHandler(CONSTANTS.serverIP);
    static Logger log = LogManager.getLogger(NetworkClock.class.getName());
    //String entranceID = "Entry Zone 2";
//    ParkingPanel pp = null;
    int MODE = 0;  //0 = Entry; 1 = Exit
    int OVERRIDE = 0;
    GpioPinDigitalOutput led1;

    ParkingPanel pp = null;
    private FileHandler fh;
    GreetClient gc = new GreetClient();
    int entryCount, exitCount;

    public NetworkClock(ArrayList<String> card, ArrayList<String> type, ParkingPanel p1, int MODE, int OVERRIDE, GpioPinDigitalOutput led1) {
        this.cards = card;
        this.types = type;
        this.MODE = MODE;
        this.OVERRIDE = OVERRIDE;
        this.led1 = led1;
        if (debugMODE == 0) {
            pp = new ParkingPanel();
        } else {
            pp = null;
        }
        this.pp = p1;
        fh = new FileHandler();

        try {
            String n = fh.readFile("entry");
            int entry = Integer.parseInt(n);
            entryCount = entry;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            String n = fh.readFile("exit");
            int exit = Integer.parseInt(n);
            exitCount = exit;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                pp.revalidate();
                pp.repaint();
                SystemStatus ss = new SystemStatus();
                boolean online = ss.checkPING(CONSTANTS.serverIP);//LINUX USE ONLY - also check your root password

                if (cards.isEmpty() == false && types.isEmpty() == false) {
                    String cardFromReader = cards.get(0);
                    String typeFromReader = types.get(0);
                    if (online == true) {
                        System.out.println("ONLINE");
                        System.out.print("`/");
                        boolean isValid = false;
                        if (cardFromReader.compareToIgnoreCase("+") == 0) {
                            if (typeFromReader.compareToIgnoreCase("entering") == 0) {
                                try {
                                    String n = fh.readFile("entry");
                                    int entry = Integer.parseInt(n);
                                    entry++;
                                    entryCount = entry;
                                    fh.writeFile("entry", entry + "");
                                    isValid = true;
                                } catch (Exception exception) {
                                    fh.writeFile("entry", "0");
                                    exception.printStackTrace();
                                }
                            } else if (typeFromReader.compareToIgnoreCase("exiting") == 0) {
                                if (MODE == 1) {
                                    GreetClient gc = new GreetClient();
                                    try {
                                        gc.startConnection(CONSTANTS.serverIP, 7890);
                                        String resp = gc.sendMessage("exit");
                                        System.out.println("Response from Server: " + resp);
                                        isValid = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    String n = fh.readFile("exit");
                                    int exit = Integer.parseInt(n);
                                    exit++;
                                    exitCount = exit;
                                    fh.writeFile("exit", exit + "");
                                    isValid = true;
                                } catch (Exception exception) {
                                    fh.writeFile("exit", "0");
                                    exception.printStackTrace();
                                }
                            }
                        } else if (cardFromReader.compareToIgnoreCase("-") == 0) {
                            if (typeFromReader.compareToIgnoreCase("entering") == 0) {
                                try {
                                    String n = fh.readFile("entry");
                                    int entry = Integer.parseInt(n);
                                    entry--;
                                    entryCount = entry;
                                    fh.writeFile("entry", entry + "");
                                    isValid = true;
                                } catch (Exception exception) {
                                    fh.writeFile("entry", "0");
                                    exception.printStackTrace();
                                }
                            } else if (typeFromReader.compareToIgnoreCase("exiting") == 0) {
                                if (MODE == 1) {
                                    GreetClient gc = new GreetClient();
                                    try {
                                        gc.startConnection(CONSTANTS.serverIP, 7890);
                                        String resp = gc.sendMessage("removeExit");
                                        System.out.println("Response from Server: " + resp);
                                        isValid = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    String n = fh.readFile("exit");
                                    int exit = Integer.parseInt(n);
                                    exit--;
                                    exitCount = exit;
                                    fh.writeFile("exit", exit + "");
                                    isValid = true;
                                } catch (Exception exception) {
                                    fh.writeFile("exit", "0");
                                    exception.printStackTrace();
                                }
                            }
                        }
//                        isValid = dbh.updateQUEUE(types.get(0), cardFromReader);
//                        System.out.println(cardFromReader + " isValid:" + isValid);
                        if (isValid) {
                            cards.remove(0);
                            types.remove(0);
                        }

                    } else if (online == false) {
                        System.out.println("OFFLINE");
                        System.out.print("-");
                    }
                    Thread.sleep(1000);
                    System.out.println("NETWORK");

                    //resetAdmin();
                    //Thread.sleep(2000);
                } else if (MODE == 0 && online == true) {
                    String count = "";
                    try {
                        String n = fh.readFile("entry");
                        entryCount = Integer.parseInt(n);
                        if (debugMODE == 0) {
                            pp.enteringLabel.setText("" + n);
                        }
                        System.out.println("ENTER: " + n);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    try {
//                        String x = exitCount + ""; //fh.readFile("exit");
                        String x = fh.readFile("exit");
                        exitCount = Integer.parseInt(x);
                        if (debugMODE == 0) {
                            pp.exitingLabel.setText("" + x);
                        }
                        System.out.println("EXIT: " + x);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

//                    OVERRIDE = dbh.getOVERRIDE();
                    if (OVERRIDE == 1) {
                        if (debugMODE == 0) {
                            pp.sysMsg1.setText("override relay");
                        }
                        led1.high();
                        OVERRIDE = 0;
//                        dbh.updateOVERRIDE("0");
                    }
//                        } else {

//                        }
                } else if (MODE == 1 && online == true) {
                    Thread.sleep(1000);
                    try {
                        gc.startConnection(CONSTANTS.serverIP, 7890);
                        String n = gc.sendMessage("getEntry");
                        if (debugMODE == 0) {
                            pp.enteringLabel.setText("" + n);
                        }
                        System.out.println("ENTRY from Server: " + n);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (debugMODE == 0) {
                            pp.enteringLabel.setText("" + 0);
                            pp.exitingLabel.setText("" + 0);
                        }
                    }
                    try {
                        gc.startConnection(CONSTANTS.serverIP, 7890);
                        String x = gc.sendMessage("getExit");
                        if (debugMODE == 0) {
                            pp.exitingLabel.setText("" + x);
                        }
                        System.out.println("EXIT from Server: " + x);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (debugMODE == 0) {
                            pp.enteringLabel.setText("" + 0);
                            pp.exitingLabel.setText("" + 0);
                        }
                    }

                }
                pp.revalidate();
                pp.repaint();
                Thread.sleep(1000);
                System.out.println(".");
                if (debugMODE == 0) {
                    pp.sysMsg1.setText("");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
