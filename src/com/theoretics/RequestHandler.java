/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.theoretics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Theoretics
 */
public class RequestHandler extends Thread {

    private Socket socket;
    private FileHandler fh;
    NetworkClock nc;

    RequestHandler(NetworkClock nc1, Socket socket) {
        this.socket = socket;
        this.nc = nc1;
        fh = new FileHandler();
    }

    @Override
    public void run() {
        try {
            System.out.println("Received a connection");

            // Get input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String greeting = in.readLine();
            if (greeting.compareTo("entry") == 0) {
                System.out.println("client said ENTERING");
                try {
                    nc.cards.add("+");
                    nc.types.add("entering");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                out.println("ENTRY sent +");

            } else if (greeting.compareTo("removeEntry") == 0) {
                System.out.println("client said BACK ENTERING");
                try {
                    nc.cards.add("-");
                    nc.types.add("entering");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                out.println("ENTRY sent -");

            } else if (greeting.compareTo("exit") == 0) {
                System.out.println("client said EXITING");
                try {
//                    String n = fh.readFile("exit");
//                    int exit = Integer.parseInt(n);
//                    exit--;
//                    fh.writeFile("exit", exit + "");
                    nc.cards.add("+");
                    nc.types.add("exiting");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                out.println("EXIT client sent +");

            } else if (greeting.compareTo("removeExit") == 0) {
                System.out.println("client said BACK EXITING");
                try {
//                    String n = fh.readFile("exit");
//                    int exit = Integer.parseInt(n);
//                    exit--;
//                    fh.writeFile("exit", exit + "");
                    nc.cards.add("-");
                    nc.types.add("exiting");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                out.println("hello EXIT client");

            } else if (greeting.compareTo("getEntry") == 0) {

                System.out.println("client said I need entry");
                try {
//                    String n = fh.readFile("entry");
//                    int entry = Integer.parseInt(n);
                    int entry = nc.entryCount;
                    out.println("" + entry);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            } else if (greeting.compareTo("getExit") == 0) {

                System.out.println("client said I need Exit");
                try {
//                    String n = fh.readFile("exit");
//                    int exit = Integer.parseInt(n);
                    int exit = nc.exitCount;
                    out.println("" + exit);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            } else if (greeting.compareTo("override") == 0) {
                System.out.println("OVERRIDE waiting...");
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                    @Override
                    public void run() {
                        nc.OVERRIDE = 1;
                        out.println("OVERRIDE successful");
                    }
                }, 10000);

            } else {
                System.out.println("unrecognised greeting");
            }
            // Write out our header to the client
//            out.println("Echo Server 1.0");
            out.flush();

            // Echo lines back to the client until the client closes the connection or we receive an empty line
            String line = in.readLine();
            while (line != null && line.length() > 0) {
                out.println("Echo: " + line);
                out.flush();
                line = in.readLine();
            }

            // Close our connection
            in.close();
            out.close();
            socket.close();

            System.out.println("Connection closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
