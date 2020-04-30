/**
 * Multithreaded Uno Server
 * @author Collin Lavergne / ngiano
 * @version 
 * ISTE 121
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class ServerControl extends JFrame {
   private Deck drawingDeck = new Deck();
   private Deck discardDeck = new Deck();
   private boolean isClockwise = true;
   private String chatRecord = "";
   private PlayerListPanel plp;
   private ArrayList<ServerClient> userList = new ArrayList<ServerClient>();
   private ServerSocket ss = null;
   private JLabel jlDiscard, jlDraw;
   public static void main(String[] args) {
      new ServerControl();
   }
   public ServerControl() {
      super("jUNO Server");
      setLayout(new FlowLayout());
      try {
         ss = new ServerSocket(12345);
      } catch (BindException be) {
         System.out.println("something is already running on this port");
      } catch (IOException ioe) {
         System.out.println("IOE In creating server");
      }
      
      //START BUTTON
      JButton jbStart = new JButton("Start Game");
      jbStart.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            if(userList.size() < 2) {
               JOptionPane.showMessageDialog(null,"Not enough players to start.","Player Error",JOptionPane.ERROR_MESSAGE);
            } else {
               //Initialize Game!
               //Make decks
               drawingDeck = new Deck(true);
               discardDeck = new Deck();
               //Reset other game variables
               isClockwise = true;
               //Shuffle deck, and 'flop' the starter card
               discardDeck.insert(drawingDeck.shuffle(true));
               antiWildFlop();
               //Tell everyone what the card is
               Card c = discardDeck.getCards().get(0);
               userList.get(0).broadcast(new Message("PILE",c,isClockwise));
               //Deal cards
               for(ServerClient z: userList) {
                  Player q = z.getPlayer();
                  q.setHand();
                  q.setTurn(false);
                  q.setUno(false);
                  drawingDeck.deal(q);
                  //Give them their hand
                  z.sendOut(new Message("HAND",q.getHand()));
               }
               //Establish turn order, pick a random player to have the first turn
               Random rand = new Random();
               ServerClient selectedTurn = userList.get(rand.nextInt(userList.size()));
               Player selectedPlayer = selectedTurn.getPlayer();
               selectedPlayer.setTurn(true);
               selectedTurn.sendOut(new Message("TURN"));
               //Tell all players the states of all other players
               ArrayList<Player> pList = new ArrayList<Player>();
               for(ServerClient z : userList) {
                  pList.add(z.getPlayer());
               }
               userList.get(0).broadcast(new Message("OK","New game started!"));
               userList.get(0).broadcast(new Message("UPDATEALL",pList));
               }            
         }
      });
      add(jbStart);      
      //Game status panel:
      JPanel jpStatus = new JPanel();
      plp = new PlayerListPanel();
      jlDiscard = new JLabel("Init");
      jlDraw = new JLabel("Init");
      jpStatus.add(jlDiscard);
      jpStatus.add(jlDraw);
      jpStatus.add(plp);
      add(jpStatus);
      //Run timer to update GameStatus panel
      java.util.Timer t = new java.util.Timer();
      t.schedule(new GameStatus(), 0, 1000);

      //Finalize frame
      pack();
      setVisible(true);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      
      final ConnectionManager CMAN = new ConnectionManager(ss);
      CMAN.start();
   }
   class GameStatus extends TimerTask {
      public void run() {
         int discDeckSize = discardDeck.getCards().size();
         String labelString = "Discard Deck ("+discDeckSize+")";
         if(discDeckSize > 0) {
            labelString+=" - Top: "+discardDeck.getCards().get(0).toString();
         }
         jlDiscard.setText(labelString);
         discDeckSize = drawingDeck.getCards().size();
         labelString = "Draw Deck ("+discDeckSize+")";
         if(discDeckSize > 0) {
            labelString+=" - Top: "+drawingDeck.getCards().get(0).toString();
         }
         jlDraw.setText(labelString);
      }
   }
   /**
     * ==CMAN== ConnectionManager - Core program for handling new connections
     */
   class ConnectionManager extends Thread {
      private ServerSocket ss;
      ConnectionManager(ServerSocket ss) {
         this.ss = ss;
      }
      public void run() {
         try {
            while (userList.size() < 4) {
               Socket cs = ss.accept();
               System.out.println("New user accepted");
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
   /**
     * recursive helper method
     * antiWildFlop - keep flopping until a non-wild card is down
     */
   public void antiWildFlop() {
      if(discardDeck.getCards().get(0).getColor() == 'X') {
         discardDeck.insert(drawingDeck.draw());
         antiWildFlop();
      }
   }
   /**
     * helper method
     * getAllPlayers - get all the player objects from each connected component
     */
   public ArrayList<Player> getAllPlayers() {
      ArrayList<Player> pList = new ArrayList<Player>();
      for(ServerClient z : userList) {
         Player zz = z.getPlayer();
         pList.add(zz);
      }
      return pList;
   }
   /**
     * helper method
     * getNextTurn - get the next player in the turn order
     */
   public ServerClient getNextTurn(ServerClient current) {
      int playerCount = userList.size();
      int turnIndex = userList.indexOf(current);
      if(isClockwise) {
         turnIndex++;
      } else {
         turnIndex--;
      }
      if(turnIndex > (playerCount-1)) {
         turnIndex = 0;
      } else if(turnIndex < 0) {
         turnIndex = (playerCount-1);
      }
      return userList.get(turnIndex);
   }
   /**
     * helper method
     * updatePlayerList
     */
  public void updatePlayerList() {
      try {
         ArrayList<Player> gameStatusPList = new ArrayList<Player>();
         for(ServerClient z: userList) {
            gameStatusPList.add(z.getPlayer());
         }
         plp.setList(gameStatusPList);
      } catch(NullPointerException npe) {}//I dont know why it happens
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
      private int pCardStorage = -1;
      /**
        * ServerClient - Create a new client with a blank player
        * @param socket - Socket for this client
        */
      public ServerClient(Socket socket) {
         try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
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
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            p = new Player(pname);
         } catch (IOException ioe) {
            System.out.println("IO error in initiating serverclient");
         } 
         this.socket = socket;
         sendOut(new Message("CHAT","GLOBAL","Welcome to the chatroom, use /w <username> <message> to send a private message"));
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
                  s = s.replace(" ","");
                  //Execute
                  for(ServerClient z: userList) {
                     Player snamePlayer = z.getPlayer();
                     if(snamePlayer.getName().equals(s)) {
                        sendOut(new Message("FAIL","SOMEONE ALREADY HAS THIS NAME"));
                     } else if (snamePlayer.getName().equals("GLOBAL") || snamePlayer.getName().equals("GAMELOG")) {
                        sendOut(new Message("FAIL","THIS NAME IS RESERVED"));
                     } else {
                        p.setName(s);
                        //Return OK
                        sendOut(new Message("OK","Username set to "+s));
                        broadcast(new Message("CHAT","GLOBAL","Welcome to the chatroom, "+s));
                        updatePlayerList();
                        broadcast(new Message("UPDATEALL",getAllPlayers()));
                        break;
                     }
                  }
                  break;
               case "DRAW"://DRAW CARD - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  if(p.getTurn()) {
                     Card drawnCard = drawingDeck.draw();
                     p.add(drawnCard);
                     sendOut(new Message("HAND+",p.getHand(),"You drew a "+drawnCard.toString()),true);
                     updatePlayerList();
                  } else {
                     sendOut(new Message("FAIL","NOT YOUR TURN"));
                  }
                  break;
               case "HAND"://REQUEST HAND - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  sendOut(new Message("HAND",p.getHand()));
                  //Return OK
                  sendOut(new Message("OK","Hand updated"));
                  break;
               case "COLORP"://COLOR PICK - ASSOCIATED OBJECT: char
                  //Process object
                  char color = (Character)msg.getContent();
                  //Execute
                  ArrayList<Card> cards = discardDeck.getCards();
                  Card topCard = cards.get(0);
                  cards.remove(0);
                  playCard(new Card(topCard.getValue(),color), pCardStorage);
                  break;
               case "PLAY"://PLAY CARD - ASSOCIATED OBJECT: Card
                  //Process object
                  Card card = (Card)msg.getContent();
                  //Execute
                  System.out.println("play "+card.toString());
                  if(!discardDeck.verify(card)) {
                     sendOut(new Message("FAIL","CARD DOES NOT MATCH"));
                  } else {
                     int cardIndex = -1;
                     ArrayList<Card> playHand = p.getHand();
                     for(int i = 0; i < playHand.size(); i++) {
                        if(playHand.get(i).compareTo(card) == 0) {
                           cardIndex = i;
                        }
                     }
                     if(cardIndex == -1) {
                        sendOut(new Message("FAIL","CARD NOT IN YOUR HAND"));
                        sendOut(new Message("HAND",p.getHand()),true);
                     } else {
                        if(!p.getTurn()) {
                           sendOut(new Message("FAIL","NOT YOUR TURN"));
                        } else {
                           playCard(card, cardIndex);
                        }
                     }
                  }
                  break;
               case "CHAT"://SEND CHAT - ASSOCIATED OBJECT: Message(String destination, String message)
                  //Process objects
                  String target = (String)msg.getContent();
                  String message = (String)msg.getMoreContent();
                  String sendee = p.getName();
                  if(target.equals("GLOBAL")) {
                     broadcast(new Message("CHAT",target,sendee+": "+message));
                  } else {
                     for(ServerClient chSc: userList) {
                        Player chSendP = chSc.getPlayer();
                        if(chSendP.getName().equals(target)) {
                           sendOut(new Message("CHAT",target,sendee+": "+message));
                           chSc.sendOut(new Message("CHAT",sendee,sendee+": "+message));
                        }
                     }
                  }
                  break;
               case "UNO"://DECLARE UNO - ASSOCIATED OBJECT: ArrayList<Card>
                  //Process object
                  if(p.getHandSize() == 1) {
                     p.setUno(true);
                     sendOut(new Message("OK","You have declared you have UNO!"));
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
                           Card cp1 = drawingDeck.draw();
                           Card cp2 = drawingDeck.draw();
                           q.add(cp1);
                           q.add(cp2);
                           z.sendOut(new Message("HAND+",q.getHand(),p.getName()+" called you out for not saying UNO! Penalty: "+cp1.toString()+", "+cp2.toString()),true);
                        }
                     }
                  }
                  //Nobody was at fault, penalize the person who tried calling them out
                  if(!correctCallOut) {
                     Card c1 = drawingDeck.draw();
                     p.add(c1);
                     Card c2 = drawingDeck.draw();
                     p.add(c2);
                     sendOut(new Message("HAND+",p.getHand(),"Nobody is at fault. Penalty of 2 cards: "+c1.toString()+", "+c2.toString()),true);
                  }
                  break;
               case "UPDATE"://REQUEST UPDATE - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  sendOut(new Message("HAND",p.getHand()));
                  sendOut(new Message("UPDATEALL",getAllPlayers()),true);
                  ArrayList<Card> cardL = discardDeck.getCards();
                  Card topCard2 = cardL.get(0);
                  sendOut(new Message("PILE",topCard2,isClockwise));
                  break;
               case "ABORT"://ABORT aka DISCONNECT - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  userList.remove(userList.indexOf(this));
                  broadcast(new Message("UPDATEALL",getAllPlayers()),true);
                  //Send OK
                  sendOut(new Message("OK","Client has been aborted"));
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
               out.flush();

         } catch (IOException ioe) {
            System.out.println("IO Error in sending client message");
            ioe.printStackTrace();
            return false;
         }
         return true;
      }
      public boolean sendOut(Message msg, boolean shouldReset) {
         try {
               out.writeObject(msg);
               out.flush();
               if(shouldReset) {
                  out.reset();
               }
         } catch (IOException ioe) {
            System.out.println("IO Error in sending client message");
            ioe.printStackTrace();
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
               boolean noTrouble = sc.sendOut(msg);
               if(!noTrouble) {
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
      
      public void playCard(Card card, int cardIndex) {
         p.play(cardIndex);
         pCardStorage = -1;
         discardDeck.insert(card);
         broadcast(new Message("PILE",card,isClockwise));
         //if that was their last card...
         if(p.getHandSize() == 0) {
            //tell the client they won
            broadcast(new Message("CHAT","GAMELOG",p.getName()+" won the game!"));
            sendOut(new Message("WIN"));
            //... and tell everyone else they lost
            broadcast(new Message("LOSE",p.getName()),true);
         } else {
            if(card.getColor() == 'X') {
               System.out.println("pcardstorage set to "+cardIndex);
               pCardStorage = cardIndex;
               sendOut(new Message("COLORP"));
            } else {
               sendOut(new Message("OK","Card Played"));
               broadcast(new Message("CHAT","GAMELOG",p.getName()+" played a "+card.toString()));
               p.setTurn(false);
               ServerClient next = getNextTurn(this);
               Player nextP = next.getPlayer();
               switch(card.getValue()) {
                  case -1: //reverse
                     isClockwise = !isClockwise;
                     String direction = (isClockwise ? "Clockwise" : "Counter-Clockwise");
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" made the turn order "+direction));
                     next = getNextTurn(this);
                     break;
                  case -2://draw 2
                     Card d2card1 = drawingDeck.draw();
                     Card d2card2 = drawingDeck.draw();
                     nextP.add(d2card1);
                     nextP.add(d2card2);
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" made "+nextP.getName()+" draw 2 cards!"));
                     next.sendOut(new Message("OK",p.getName()+" made you draw 2 cards! "+d2card1.toString()+" and "+d2card2.toString()));
                     break;
                  case -3://skip
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" skipped "+nextP.getName()+"'s turn!"));
                     next.sendOut(new Message("OK",p.getName()+" skipped your turn!"));
                     next = getNextTurn(next);
                     break;
                  case -4://draw 4
                     Card d4card1 = drawingDeck.draw();
                     Card d4card2 = drawingDeck.draw();
                     Card d4card3 = drawingDeck.draw();
                     Card d4card4 = drawingDeck.draw();
                     nextP.add(d4card1);
                     nextP.add(d4card2);
                     nextP.add(d4card3);
                     nextP.add(d4card4);
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" made "+nextP.getName()+" draw 4 cards! "));
                     next.sendOut(new Message("OK",p.getName()+" made you draw 4 cards! "+d4card1.toString()+", "+d4card2.toString()+", "+d4card3.toString()+" and "+d4card4.toString()));
                     break;      
               }
               
               next.getPlayer().setTurn(true);
               next.sendOut(new Message("TURN"));
               updatePlayerList();
            }
         }
      }
      
   }

}