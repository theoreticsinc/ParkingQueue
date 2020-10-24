/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.theoretics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theoretics
 */
public class GreetServer extends Thread {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running = false;
    NetworkClock nc;
    
    GreetServer(NetworkClock nc1) {
        this.nc = nc1;
    }
    /*
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String greeting = in.readLine();
//            System.out.println(greeting);
            if (greeting.compareTo("hello server") == 0) {
                out.println("hello client");
                System.out.println("client said hello");
            } else {
                System.out.println("unrecognised greeting");
            }

        } catch (IOException ex) {
            Logger.getLogger(GreetServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopconnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(GreetServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
*/
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                System.out.println("Listening for a connection");

                // Call accept() to receive the next connection
                Socket socket = serverSocket.accept();

                // Pass the socket to the RequestHandler thread for processing
                RequestHandler requestHandler = new RequestHandler(nc, socket);
                requestHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FileHandler fh = new FileHandler();
        fh.createFile("entry");
        fh.createFile("exit");
        fh.writeFile("entry", "0");
        fh.writeFile("exit", "0");
        GreetServer server = new GreetServer(null);
        //server.start(7777);

        System.out.println( "Start server on port: " + 7777 );

        //SimpleSocketServer server = new SimpleSocketServer( 7777 );
        server.startServer();

        // Automatically shutdown in 1 minute
//        try
//        {
//            Thread.sleep( 60000 );
//        }
//        catch( Exception e )
//        {
//            e.printStackTrace();
//        }
    }

    void startServer() {
        try
        {
            serverSocket = new ServerSocket( 7890 );
            this.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
