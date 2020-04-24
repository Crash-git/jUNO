/**
 * Multithreaded chat server
 * @author Collin Lavergne
 * @version 1.2.0
 * ISTE 121
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer extends JFrame {
   private String chatRecord = "";
   private ArrayList<Socket> socketList = new ArrayList<Socket>();
   private JTextArea chat = null;
   private ArrayList<String> userList = new ArrayList<String>();
   private boolean firstMessage = true;
   public static void main(String[] args) {
      ChatServer main = new ChatServer();
      main.setDefaultCloseOperation(EXIT_ON_CLOSE);
      main.pack();
      main.setVisible(true);
      main.serverStart();
   }

   public ChatServer() {
      setLayout(new BorderLayout());

      JButton refresh = new JButton("Refresh chat record");
      JPanel top = new JPanel();
      JPanel bottom = new JPanel();

      top.setLayout(new FlowLayout());
      bottom.setLayout(new FlowLayout());

      chat = new JTextArea(30, 50);
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
            ThreadServer ts = new ThreadServer(cs);
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

   public String[] getUserList() {
      String[] data = new String[userList.size()];
      for (int i = 0; i < userList.size(); i++) data[i] = userList.get(i);

      return data;
   }
   
   class ThreadServer extends Thread {
      private Socket cs;

      public ThreadServer(Socket _cs) {
         cs = _cs;
         socketList.add(cs);
         firstMessage = true;
      }
      
      public void run() {
         BufferedReader br;
         PrintWriter pw;
         String clientMsg;
         
         while (true) {
            try {
               br = new BufferedReader( new InputStreamReader(cs.getInputStream()));

               while (true) {
                  clientMsg = br.readLine();

                  if (clientMsg != null) {
                     System.out.println("Server read:" + clientMsg);

                     for (int i = 0; i < socketList.size(); i++) {
                        pw = new PrintWriter(new OutputStreamWriter(socketList.get(i).getOutputStream()));
                        pw.println(clientMsg);
                        pw.flush();
                        //pw.close();
                     }

                     chatRecord += ("\n" + clientMsg);
                     String[] currentUser = clientMsg.split(" ");
                     if (firstMessage) {
                        userList.add(currentUser[0]);
                        firstMessage = false;
                     }

                     //for tests
                     for (int i = 0; i < userList.size(); i++) System.out.println(userList.get(i));
                  }
               }
            }
            catch (IOException ioe) {
               System.out.println("IO error, killing thread");
               break;
            }
            catch (NullPointerException npe) {
               //System.out.println("npe woo");
            }
         }
      }
   }
}