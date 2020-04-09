/**
 * Multithreaded chat server
 * @author Collin Lavergne
 * @version idek
 * ISTE 121
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class MultiServer extends JFrame{
   private String chatRecord = "";
   private ArrayList<Socket> socketList = new ArrayList<Socket>();
   public static void main(String[] args){
      MultiServer main = new MultiServer();
      main.setDefaultCloseOperation(EXIT_ON_CLOSE);
      main.pack();
      main.setVisible(true);
      main.serverStart();
   }
   public MultiServer(){
      setLayout(new BorderLayout());

      JButton refresh = new JButton("Refresh chat record");
      JPanel top = new JPanel();
      JPanel bottom = new JPanel();

      top.setLayout(new FlowLayout());
      bottom.setLayout(new FlowLayout());

      JTextArea chat = new JTextArea(30, 50);
      chat.setEditable(false);
      chat.setLineWrap(true);

      JScrollPane scroll = new JScrollPane (chat, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      top.add(scroll);
      bottom.add(refresh);

      add(top, BorderLayout.CENTER);
      add(bottom, BorderLayout.SOUTH);
      
      refresh.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            chat.setText(chatRecord);
         }
      });
   }

   public void serverStart() {
      ServerSocket ss = null;

      try {
         ss = new ServerSocket(12345);
         Socket cs = null;
         while (true) {
            cs = ss.accept();
            Client ts = new Client(cs);
            ts.start();
         }
      }
      catch (BindException be) {
         System.out.println("something is already running on this port");
      }
      catch (IOException ioe) {
         System.out.println("IO error");
      }
   }
   
}