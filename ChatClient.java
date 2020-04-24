
/**
 * Multithreaded chat client
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

public class ChatClient extends JFrame {
   private JTextArea chat;
   private JTextField chatInput;
   private JButton send;
   private JScrollPane scroll;
   private JPanel tabs;
   private JPanel users;
   private JButton global;
   private JPanel chatCombined;

   private String username;
   private Socket s;

   public static void main(String[] args) {
      System.out.print("Enter Username: ");
      Scanner in = new Scanner(System.in);
      String user = in.nextLine();
      in.close();
      ChatClient main = new ChatClient(user);
      main.pack();
      main.setVisible(true);
      main.setDefaultCloseOperation(EXIT_ON_CLOSE);
   }

   public ChatClient(String _username) {
      setLayout(new BorderLayout());

      username = _username;

      send = new JButton("Send");
      tabs = new JPanel();
      users = new JPanel();
      chatCombined = new JPanel();

      send.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            send.setEnabled(false);
            sendMessage(username + ": " + chatInput.getText(), s);

            chatInput.setText("");
            chatInput.requestFocus();
            send.setEnabled(true);
         }
      });

      chatInput = new JTextField(14);
      chat = new JTextArea(30, 50);
      chat.setEditable(false);

      scroll = new JScrollPane(chat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

      users.setLayout(new GridLayout(0, 1));
      users.add(new JTextArea(1, 50));
      users.add(new JTextArea(1, 50));
      users.add(new JTextArea(1, 50));
      users.add(new JTextArea(1, 50));

      tabs.setLayout(new GridLayout(0, 1));
      global = new JButton("Global");
      tabs.add(global);

      chatCombined.setLayout(new BorderLayout(5, 5));
      chatCombined.add(scroll, BorderLayout.NORTH);
      chatCombined.add(chatInput, BorderLayout.CENTER);
      chatCombined.add(send, BorderLayout.SOUTH);

      add(users, BorderLayout.NORTH);
      add(tabs, BorderLayout.CENTER);
      add(chatCombined, BorderLayout.SOUTH);

      try {
         s = new Socket("localhost", 12345);
      }
      catch (UnknownHostException uhe) {
         System.out.println("error with host");
      } 
      catch (IOException ioe) {

      }

      sendMessage(username + " connected", s);
      
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent q) {
            sendMessage(username + " disconnected", s);
            dispose();
         }
      });
   }
   
   public void sendMessage(String input, Socket s) {
      try {
   		OutputStream out = s.getOutputStream();
         PrintWriter pw = new PrintWriter(out);

   		pw.println(input);
         pw.flush();
         
         Listener listen = new Listener(s);
         listen.start();
      }
      catch(UnknownHostException uhe) {
         System.out.println("no host");
      }
      catch(IOException ioe) {
         System.out.println("Error sending message");
      }
      catch(NullPointerException npe) {
         System.out.println("No server running!");
         System.exit(1);
      }
   }

   public String getUsername() {
      return username;
   }

   class Listener extends Thread {
      Socket s;

      public Listener(Socket _s) {
         s = _s;
      }
      
      public void run() {
         try {
            InputStream is = s.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while (true) {
               String readout = "";
               try {
                  readout = br.readLine();
               }
               catch(IOException ioe){

               }
               chat.append("\n" + readout);
            }
         }
         catch(IOException ioe) {
            System.out.print("IO error");
         }
      }
   }
}