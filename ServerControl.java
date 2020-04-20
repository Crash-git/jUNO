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

public class ServerControl {
   private Deck drawingDeck;
   private Deck discardDeck;
   private String chatRecord = "";
   private ArrayList<ServerClient> userList = new ArrayList<ServerClient>();
   private ServerSocket ss = null;
   public static void main(String[] args) {
      ServerControl main = new ServerControl();
   }
   public ServerControl() {
      try {
         ss = new ServerSocket(12345);
      } catch (BindException be) {
         System.out.println("something is already running on this port");
      } catch (IOException ioe) {
         System.out.println("IOE In creating server");
      }
   }
   
   class ConnectionManager extends Thread {
      private ServerSocket ss;
      ConnectionManager(ServerSocket ss) {
         this.ss = ss;
      }
      public void run() {
         try {
            while (true) {
               Socket cs = ss.accept();
               ServerClient sct = new ServerClient(cs);
               userList.add(sct);
               sct.start();
            }
         }
         catch (IOException ioe) {
            System.out.println("IO error");
         }
      }
   }
   public void serverStart() {
      ServerSocket ss = null;

   }
   /**
  * ServerClient - main thread task for each client in the server
  * @author - ngiano
  * @version - 4.7.20
  */
   class ServerClient extends Thread {
      private Socket socket;
      private ObjectInputStream in = null;
      private ObjectOutputStream out = null;
      private Player p = null;
      
      /**
        * ServerClient - Create a new client with a blank player
        * @param socket - Socket for this client
        */
      public ServerClient(Socket socket) {
         try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            p = new Player();
         } catch (IOException ioe) {
            System.out.println("IO error in initiating serverclient");
         } 
         this.socket = socket;
      }
      
      /**
        * ServerClient - Create a new client with a named player
        * @param socket - Socket for this client
        * @param pname - Name for the player
        */
      public ServerClient(Socket socket, String pname) {
         try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            p = new Player(pname);
         } catch (IOException ioe) {
            System.out.println("IO error in initiating serverclient");
         } 
         this.socket = socket;
      }
      
      public void run() {
         while (true) {
            Message msg = null;
            try {
               msg = (Message) in.readObject();
            } catch (IOException ioe) {
               System.out.println("IO Error in reading client message");
            } catch (ClassNotFoundException cnfe) {
               System.out.println("Message class not found");
            }
            String command = msg.command;
            //*** INBOUND COMMANDS ***
            switch(command) {
               case "SNAME"://SET NAME - ASSOCIATED OBEJCT: String
                  //Process object
                  String s = (String)msg.getContent();
                  //Execute
                  p.setName(s);
                  //Return OK
                  sendOut(new Message("OK",null));
                  break;
               case "DRAW"://DRAW CARD - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  if(p.getTurn()) {
                     p.add(drawingDeck.draw());
                     sendOut(new Message("HAND",p.getHand()));
                     //Return OK
                     sendOut(new Message("OK",null));
                  } else {
                     sendOut(new Message("FAIL","NOT YOUR TURN"));
                  }
                  break;
               case "HAND"://REQUEST HAND - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  sendOut(new Message("HAND",p.getHand()));
                  //Return OK
                  sendOut(new Message("OK",null));
                  break;
               case "COLORP"://COLOR PICK - ASSOCIATED OBJECT: char
                  //Process object
                  char color = (Character)msg.getContent();
                  //Execute
                  ArrayList<Card> cards = discardDeck.getCards();
                  Card topCard = cards.get(0);
                  topCard.setColor(color);
                  cards.set(0,topCard);
                  discardDeck.setCards(cards);
                  //Recieving PILE on requesting client will act as OK
                  broadcast(new Message("PILE",topCard));
                  break;
               case "PLAY"://PLAY CARD - ASSOCIATED OBJECT: Card
                  //Process object
                  Card card = (Card)msg.getContent();
                  //Execute
                  if(discardDeck.verify(card)) {
                     if(p.getHand().indexOf(card) > -1) {
                        if(p.getTurn()) {
                           ArrayList<Card> hand = p.getHand();
                           p.play(hand.indexOf(card));
                           discardDeck.insert(card);
                           broadcast(new Message("PILE",card));
                           //if that was their last card...
                           if(p.getHandSize() == 0) {
                              //tell the client they won
                              sendOut(new Message("WIN",null));
                              //... and tell everyone else they lost
                              broadcast(new Message("LOSE",p.getName()),true);
                           } else {
                              sendOut(new Message("OK",null));
                           }
                        } else {
                           sendOut(new Message("FAIL","NOT YOUR TURN"));
                        }
                     } else {
                        sendOut(new Message("FAIL","CARD NOT IN YOUR HAND"));
                        sendOut(new Message("HAND",p.getHand()));
                     }
                  } else {
                     sendOut(new Message("FAIL","CARD DOES NOT MATCH"));
                  }
                  break;
               case "CHAT"://SEND CHAT - ASSOCIATED OBJECT: Message(String destination, String message)
               case "UNO"://DECLARE UNO - ASSOCIATED OBJECT: ArrayList<Card>
                  //Process object
                  if(p.getHandSize() == 1) {
                     p.setUno(true);
                     sendOut(new Message("OK",null));
                  } else {
                     sendOut(new Message("FAIL","NOT ONE CARD LEFT"));
                  }
                  break;
               case "CALLOUT"://CALL SOMEONE OUT FOR NOT CALLING UNO - ASSOCIATED OBJECT: null
                  //No object to process
                  //Search all players
                  boolean correctCallOut = false;
                  for(ServerClient z: userList) {
                     Player q = z.getPlayer();
                     if(q.getHandSize() == 1) {
                        if(!q.getUno()) {
                           //PENALTY FOR NOT CALLING UNO
                           correctCallOut = true;
                           q.add(drawingDeck.draw());
                           q.add(drawingDeck.draw());
                           z.sendOut(new Message("HAND",q.getHand()));
                        }
                     }
                  }
                  //Nobody was at fault, penalize the person who tried calling them out
                  if(!correctCallOut) {
                     p.add(drawingDeck.draw());
                     p.add(drawingDeck.draw());
                     sendOut(new Message("HAND",p.getHand()));
                  }
                  sendOut(new Message("OK",null));
                  break;
               case "UPDATE"://REQUEST UPDATE - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  sendOut(new Message("HAND",p.getHand()));
                  ArrayList<Player> pList = new ArrayList<Player>();
                  for(ServerClient z : userList) {
                     pList.add(z.getPlayer());
                  }
                  sendOut(new Message("UPDATEALL",pList));
                  ArrayList<Card> cardL = discardDeck.getCards();
                  Card topCard2 = cardL.get(0);
                  sendOut(new Message("PILE",topCard2));
                  //Send OK
                  sendOut(new Message("OK",null));
                  break;
               case "ABORT"://ABORT aka DISCONNECT - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  userList.remove(userList.indexOf(this));
                  ArrayList<Player> pList2 = new ArrayList<Player>();
                  for(ServerClient z : userList) {
                     pList2.add(z.getPlayer());
                  }
                  broadcast(new Message("UPDATEALL",pList2),true);
                  //Send OK
                  sendOut(new Message("OK",null));
                  break;
            }
         }
      }
      /**
        * sendOut - Send out a message to the client on the other end
        * @param msg - Message object containing the message
        * @returns boolean - Successfully sent message
        */
      public boolean sendOut(Message msg) {
         try {
               out.writeObject(msg);
         } catch (IOException ioe) {
            System.out.println("IO Error in sending client message");
            return false;
         }
         return true;
      }
      
      /**
        * broadcast - Send out to all clients
        * @param msg - Message object containing the message
        * @returns boolean - Successfully sent message
        */
      public boolean broadcast(Message msg) {
         for(ServerClient sc: userList) {
            if(!sc.sendOut(msg)) {
               System.err.println("Error sending broadcast "+msg.getCommand()+" to client: "+sc.getName());
               return false;
            }
         }
         return true;
      }
      
      /**
        * broadcast,true - Send out to all clients EXCEPT self
        * @param msg - Message object containing the message
        * @returns boolean - Successfully sent message
        */
      public boolean broadcast(Message msg, boolean excludeSelf) {
         for(ServerClient sc: userList) {
            if(sc != this) {
               if(!sc.sendOut(msg)) {
                  System.err.println("Error sending broadcast "+msg.getCommand()+" to client: "+sc.getName());
                  return false;
               }
            }
         }
         return true;
      }
      
      /**
        * getPlayer - Get player
        */
      public Player getPlayer() {
         return p;
      }
      /**
        * getSocket - Get Socket
        */
      public Socket getSocket() {
         return socket;
      }
      
   }

}