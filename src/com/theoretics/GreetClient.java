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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Theoretics
 */
public class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
 
    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
        } catch (Exception ex) {
            Logger.getLogger(GreetClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    public String sendMessage(String msg) {
        String resp = "";
        try {
            if (null != out) {
            out.println(msg);
            resp = in.readLine();
            }
            return resp;
        } catch (Exception ex) {
            Logger.getLogger(GreetClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return resp;
    }
 
    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(GreetClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {        
        GreetClient gc = new GreetClient();
        try {
            gc.startConnection(CONSTANTS.serverIP, 7890);
            String resp = gc.sendMessage("entry");
            System.out.println("Response from Server: " + resp);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        while(true) {
            
        }
//        gc.stopConnection();
    }
}
