import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
/**
  * ServerControl - Main control unit for server, builds GUI, handles messages from clients
  * @author Collin Lavergne / ngiano
  * iste121
  */
public class ServerControl extends JFrame {
   //Game variables
   private Deck drawingDeck = new Deck();
   private Deck discardDeck = new Deck();
   private boolean isClockwise = true;
   //GUI variables
   private PlayerListPanel plp;
   private JLabel jlDiscard, jlDraw;
   private DeckPanel dPan;
   private ChatPanel chatGlobal, chatGameLog;
   private JFrame frame;
   //Core variables
   private ArrayList<ServerClient> userList = new ArrayList<ServerClient>();
   private ServerSocket ss = null;

   
   public static void main(String[] args) {
      new ServerControl();
   }
   /**
     * ServerControl - builds server gui and starts message/connection handlers
     */
   public ServerControl() {
      super("jUNO Server");
      frame = this;
      setLayout(new GridLayout(1,0));
      //Create server
      try {
         ss = new ServerSocket(12345);
      } catch (BindException be) {
         System.out.println("something is already running on this port");
      } catch (IOException ioe) {
         System.out.println("IOE In creating server");
      }
      
      //START BUTTON
      JButton jbStart = new JButton("New Game");
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
              
               userList.get(0).broadcast(new Message("OK","New game started!"));
               userList.get(0).broadcast(new Message("CHAT","GAMELOG","New game started!"));
               selectedTurn.broadcast(new Message("UPDATEALL",getAllPlayers()),true);
               selectedTurn.sendOut(new Message("TURN"));
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
      dPan = new DeckPanel();
      add(dPan);
      //Chat stuff
      JTabbedPane chatTabs = new JTabbedPane();
      chatGlobal = new ChatPanel();
      chatGameLog = new ChatPanel();
      chatTabs.addTab("GLOBAL CHAT",chatGlobal);
      chatTabs.addTab("GAME LOG",chatGameLog);
      add(chatTabs);
      //Run timer to update GameStatus panel
      java.util.Timer t = new java.util.Timer();
      t.schedule(new GameStatus(), 0, 1000);

      //Finalize frame
      pack();
      setVisible(true);
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent we) {
            if(userList.size() > 0) {
               userList.get(0).broadcast(new Message("ABORT"));
            }
            System.exit(0);
         }
      });
      
      //Start connection manager
      final ConnectionManager CMAN = new ConnectionManager(ss);
      CMAN.start();
   }
   /**
     * GameStatus - update GUI components to display whats going on in the game
     */
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
         if(discardDeck.getCards().size() > 0) {
            dPan.refresh(discardDeck.getCards().get(0),isClockwise);
         }
      }
   }
   /**
     * ==CMAN== ConnectionManager - Core program for handling new connections
     */
   class ConnectionManager extends Thread {
      private ServerSocket ss;
      /**
        * ConnectionManager
        * @param ss - ServerSocket the server socket for bringing in new sockets
        */
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
     * @return array list of all connected players
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
     * @param current - Current turn
     * @return next - Next turn
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
         boolean connection = true;
         while (connection) {
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
                     //Check if player name already exists
                     if(snamePlayer.getName().equals(s)) {
                        sendOut(new Message("FAIL","SOMEONE ALREADY HAS THIS NAME"));
                     //Server critical message points are reserved, so you cannont be named them
                     } else if (snamePlayer.getName().equals("GLOBAL") || snamePlayer.getName().equals("GAMELOG")) {
                        sendOut(new Message("FAIL","THIS NAME IS RESERVED"));
                     //Checks pass, set name and tell everyone else to display that name
                     } else {
                        p.setName(s);
                        //Return OK
                        sendOut(new Message("OK","Username set to "+s));
                        broadcast(new Message("CHAT","GLOBAL","Welcome to the chatroom, "+s));
                        chatGlobal.updateChat(s+" joined");
                        updatePlayerList();
                        broadcast(new Message("UPDATEALL",getAllPlayers()));
                        break;
                     }
                  }
                  break;
               case "DRAW"://DRAW CARD - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  //Check if it's the player's turn
                  if(p.getTurn()) {
                     //Draw card from drawingDeck
                     Card drawnCard = drawingDeck.draw();
                     //Add to hand
                     p.add(drawnCard);
                     //Send to client
                     sendOut(new Message("HAND+",p.getHand(),"You drew a "+drawnCard.toString()),true);
                     //broadcast(new Message("UPDATEALL",getAllPlayers(),true));
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" drew a card"));
                     chatGameLog.updateChat(p.getName()+" drew a card");
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
                  //Response to server asking what color the wild should be
                  //Process object
                  char color = (Character)msg.getContent();
                  //Execute
                  //Get the top card
                  ArrayList<Card> cards = discardDeck.getCards();
                  Card topCard = cards.get(0);
                  //Remove it
                  cards.remove(0);
                  //Play the top card as a colored wild card
                  playCard(new Card(topCard.getValue(),color));
                  break;
               case "PLAY"://PLAY CARD - ASSOCIATED OBJECT: Card
                  //Process object
                  Card card = (Card)msg.getContent();
                  //Execute
                  //Check if card can be played, number/color matches
                  if(!discardDeck.verify(card)) {
                     sendOut(new Message("FAIL","CARD DOES NOT MATCH"));
                  } else {
                     //Check if card exists in player's hand
                     int cardIndex = -1;
                     ArrayList<Card> playHand = p.getHand();
                     for(int i = 0; i < playHand.size(); i++) {
                        if(playHand.get(i).compareTo(card) == 0) {
                           cardIndex = i;
                        }
                     }
                     //If it doesn't, send the player what their hand is supposed to be
                     if(cardIndex == -1) {
                        sendOut(new Message("FAIL","CARD NOT IN YOUR HAND"));
                        sendOut(new Message("HAND",p.getHand()),true);
                     } else {
                        //Check if it's the players turn
                        if(!p.getTurn()) {
                           sendOut(new Message("FAIL","NOT YOUR TURN"));
                        } else {
                           //All tests pass, play the card
                           playCard(card);
                        }
                     }
                  }
                  break;
               case "CHAT"://SEND CHAT - ASSOCIATED OBJECT: Message(String destination, String message)
                  //Process objects
                  String target = (String)msg.getContent();
                  String message = (String)msg.getMoreContent();
                  String sendee = p.getName();
                  //Send to global chat
                  if(target.equals("GLOBAL")) {
                     broadcast(new Message("CHAT",target,sendee+": "+message));
                     chatGlobal.updateChat(sendee+": "+message);
                  //Bounce to other client
                  } else {
                     for(ServerClient chSc: userList) {
                        Player chSendP = chSc.getPlayer();
                        if(chSendP.getName().equals(target)) {
                           //Send to orignal sender, as a reciept
                           sendOut(new Message("CHAT",target,sendee+": "+message));
                           //Send to recipient
                           chSc.sendOut(new Message("CHAT",sendee,sendee+": "+message));
                        }
                     }
                  }
                  break;
               case "UNO"://DECLARE UNO - ASSOCIATED OBJECT: null
                  //Process object
                  //Check if uno can be called
                  if(p.getHandSize() <= 2) {
                     p.setUno(true);
                     sendOut(new Message("OK","You have declared you have UNO!"));
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" declared UNO!"));
                     chatGameLog.updateChat(p.getName()+" declared UNO!");
                  } else {
                     sendOut(new Message("FAIL","NOT ONE CARD LEFT"));
                  }
                  break;
               case "CALLOUT"://CALL SOMEONE OUT FOR NOT CALLING UNO - ASSOCIATED OBJECT: null
                  //No object to process
                  //Search all players
                  boolean correctCallOut = false;
                  //Scan players to see if anyone has 1 card, and hasn't called uno
                  for(ServerClient z: userList) {
                     Player q = z.getPlayer();
                     //Someone didn't say uno!
                     if(q.getHandSize() == 1 && !q.getName().equals(p.getName())) {
                        if(!q.getUno()) {
                           //PENALTY FOR NOT CALLING UNO
                           correctCallOut = true;
                           //Draw 2 cards
                           Card cp1 = drawingDeck.draw();
                           Card cp2 = drawingDeck.draw();
                           q.add(cp1);
                           q.add(cp2);
                           //Tell client at fault, as well as the gamelog
                           z.sendOut(new Message("HAND+",q.getHand(),p.getName()+" called you out for not saying UNO! Penalty: "+cp1.toString()+", "+cp2.toString()),true);
                           broadcast(new Message("CHAT","GAMELOG",p.getName()+" called "+q.getName()+" for not saying UNO! 2 card penalty"));
                           chatGameLog.updateChat(p.getName()+" called "+q.getName()+" for not saying UNO! 2 card penalty");
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
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" called out someone for not saying UNO, but nobody was at fault! 2 card penalty"));
                     chatGameLog.updateChat(p.getName()+" called out someone for not saying UNO, but nobody was at fault! 2 card penalty");
                  }
                  break;
               case "UPDATE"://REQUEST UPDATE - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  //Send the player their hand
                  sendOut(new Message("HAND",p.getHand()));
                  //Send the player all other player's objects
                  sendOut(new Message("UPDATEALL",getAllPlayers()),true);
                  //Send the player the top card in the pile
                  ArrayList<Card> cardL = discardDeck.getCards();
                  Card topCard2 = cardL.get(0);
                  sendOut(new Message("PILE",topCard2,isClockwise));
                  break;
               case "ABORT"://ABORT aka DISCONNECT - ASSOCIATED OBJECT: null
                  //No object to process
                  //Execute
                  connection = false;
                  userList.remove(userList.indexOf(this));
                  //Tell everyone the player is leaving
                  broadcast(new Message("UPDATEALL",getAllPlayers()),true);
                  broadcast(new Message("CHAT","GAMELOG",p.getName()+" has disconnected"));
                  chatGameLog.updateChat(p.getName()+" has disconnected");
                  //If only 1 player left, they win!
                  if(userList.size() == 1) {
                     userList.get(0).sendOut(new Message("WIN"));
                  }
                  //Send OK
                  sendOut(new Message("GOODBYE"));
                  break;
            }
         }
      }
      /**
        * sendOut - Send out a message to the client on the other end
        * @param msg - Message object containing the message
        * @return boolean - Successfully sent message
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
       /**
        * sendOut - Send out a message to the client on the other end
        * @param msg - Message object containing the message
        * @param shouldReset - Reset the object stream after sending, because of caching
        * @return boolean - Successfully sent message
        */
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
        * @return boolean - Successfully sent message
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
        * @param excludeSelf - Exclude self switch
        * @return boolean - Successfully sent message
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
        * @return p - The player
        */
      public Player getPlayer() {
         return p;
      }
      /**
        * getSocket - Get Socket
        * @return socket The socket
        */
      public Socket getSocket() {
         return socket;
      }
      /**
        * helper method
        * playCard - Play a card, add to top discard pile, and update server accordingly
        * @param card - Card to play
        */
      public void playCard(Card card) {
         //Find card in player's hand, and remove it
         if(card.getValue() == -5 ||  card.getValue() == -4) {
            Card indSearch = new Card(card.getValue(),'X');
            ArrayList<Card> hand = p.getHand();
            boolean found = false;
            for(int i = 0; i < hand.size(); i++) {
               if(hand.get(i).compareTo(indSearch) == 0 && !found) {
                  p.play(i);
                  found = true;
               }
            }
         } else {
            ArrayList<Card> hand = p.getHand();
            boolean found = false;
            for(int i = 0; i < hand.size(); i++) {
               if(hand.get(i).compareTo(card) == 0 && !found) {
                  p.play(i);
                  found = true;
               }
            }
         }
         //Add to top of discard deck
         discardDeck.insert(card);
         broadcast(new Message("PILE",card,isClockwise));
         //if that was their last card...
         if(p.getHandSize() == 0) {
            //tell the client they won
            broadcast(new Message("CHAT","GAMELOG",p.getName()+" won the game!"));
            chatGameLog.updateChat(p.getName()+" won the game!");
            sendOut(new Message("WIN"));
            //... and tell everyone else they lost
            broadcast(new Message("LOSE",p.getName()),true);
         } else {
            //If card was black wild, send request to pick color
            if(card.getColor() == 'X') {
               sendOut(new Message("COLORP"));
            } else {
               //Card played
               sendOut(new Message("OK","Card Played"));
               broadcast(new Message("CHAT","GAMELOG",p.getName()+" played a "+card.toString()));
               chatGameLog.updateChat(p.getName()+" played a "+card.toString());
               p.setTurn(false);
               ServerClient next = getNextTurn(this);
               Player nextP = next.getPlayer();
               //Handle special cards
               switch(card.getValue()) {
                  case -1: //reverse
                     isClockwise = !isClockwise;
                     String direction = (isClockwise ? "Clockwise" : "Counter-Clockwise");
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" made the turn order "+direction));
                     chatGameLog.updateChat(p.getName()+" made the turn order "+direction);
                     next = getNextTurn(this);
                     break;
                  case -2://draw 2
                     Card d2card1 = drawingDeck.draw();
                     Card d2card2 = drawingDeck.draw();
                     nextP.add(d2card1);
                     nextP.add(d2card2);
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" made "+nextP.getName()+" draw 2 cards!"));
                     chatGameLog.updateChat(p.getName()+" made "+nextP.getName()+" draw 2 cards!");
                     next.sendOut(new Message("OK",p.getName()+" made you draw 2 cards! "+d2card1.toString()+" and "+d2card2.toString()));
                     break;
                  case -3://skip
                     broadcast(new Message("CHAT","GAMELOG",p.getName()+" skipped "+nextP.getName()+"'s turn!"));
                     chatGameLog.updateChat(p.getName()+" skipped "+nextP.getName()+"'s turn!");
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
                     chatGameLog.updateChat(p.getName()+" made "+nextP.getName()+" draw 4 cards! ");
                     next.sendOut(new Message("OK",p.getName()+" made you draw 4 cards! "+d4card1.toString()+", "+d4card2.toString()+", "+d4card3.toString()+" and "+d4card4.toString()));
                     break;      
               }
               //Set next player's turn
               next.getPlayer().setTurn(true);
               next.sendOut(new Message("TURN"));
               updatePlayerList();
            }
         }
      }
      
   }

}