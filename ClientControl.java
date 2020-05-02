import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock; 

public class ClientControl extends JFrame {
   //GUI
   private JFrame frame;
   private HandManager hMan;
   private JList jlistHand;
   private JButton jbPlayCard, jbUno, jbCallout;
   private PlayerPanel playerPanelA, playerPanelB, playerPanelC;
   private PlayerListPanel plp;
   private DeckPanel dPan;
   //Core
   private ClientCommunicator cc;
   private Player p = new Player("Greg");
   private ArrayList<Player> userList = new ArrayList<Player>();
   //Chat
   private JTabbedPane chatTabs;
   private ArrayList<ChatPanel> chatPanels = new ArrayList<ChatPanel>();
   
   public static void main(String[] args) {
      new ClientControl();
   }
   
   public ClientControl() {
      super("jUNO Client");
      frame = this;
      setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
      JPanel sidebar = new JPanel(new GridLayout(2,1));
      plp = new PlayerListPanel();
      chatTabs = new JTabbedPane();
      sidebar.add(plp);
      sidebar.add(chatTabs);
      sidebar.setMinimumSize(new Dimension(250,100));
      sidebar.setMaximumSize(new Dimension(250,1920));
      sidebar.setPreferredSize(new Dimension(250,600));
      //MAIN LAYOUT CONSTRUCT
      JPanel mainLayout = new JPanel(new GridLayout(3,0));
      JPanel jpRow1 = new JPanel(new GridLayout(0,3));
      JPanel jpRow2 = new JPanel(new GridLayout(0,3));
      JPanel jpHand = new JPanel(new GridLayout(0,1));
      JPanel jpUno = new JPanel();
      jbUno = new JButton("Uno");
      jbUno.setHorizontalAlignment(JLabel.CENTER);
      jpUno.add(jbUno);
      jpRow1.add(jpUno);
      playerPanelB = new PlayerPanel(new Player("PLAYER B"));
      jpRow1.add(playerPanelB);
      JPanel jpCallout = new JPanel();
      jbCallout = new JButton("Callout");
      jbCallout.setHorizontalAlignment(JLabel.CENTER);
      jpCallout.add(jbCallout);
      jpRow1.add(jpCallout);
      playerPanelA = new PlayerPanel(new Player("PLAYER A"));
      jpRow2.add(playerPanelA);
      dPan = new DeckPanel();
      jpRow2.add(dPan);
      playerPanelC = new PlayerPanel(new Player("PLAYER C"));
      jpRow2.add(playerPanelC);
      hMan = new HandManager();
      jpHand.add(hMan);
      mainLayout.add(jpRow1);
      mainLayout.add(jpRow2);
      mainLayout.add(jpHand);


      add(mainLayout);
      add(sidebar);
      pack();
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      setVisible(true);
      try {
         Socket s = new Socket("172.101.242.195",12345);
         cc = new ClientCommunicator(s);
         cc.start();
         System.out.println("Conntected");
      } catch (UnknownHostException uhe) {
         System.err.println("No host found");
         uhe.printStackTrace();
      } catch(ConnectException ce) {
         JOptionPane.showMessageDialog(null, "Could not connect to server. It may be offline");
         System.exit(0);
      } catch (IOException ioe) {
         System.err.println(ioe);
      }
   }
   
   /**
     * helper method
     * clear the alert box after X seconds
     */
   public class AlertClearer extends TimerTask {
      public void run() {
         hMan.clearAlert();
      }
   }
   
   /**
     * helper method
     * update player cards
     */
   public void updatePlayerCards(ArrayList<Player> players) {
      System.out.println(players);
      plp.setList(players);
      switch(players.size()) {
         case 1:
            playerPanelA.setPlayer();
            playerPanelB.setPlayer();
            playerPanelC.setPlayer();
            break;
         case 2:
            playerPanelA.setPlayer();
            playerPanelB.setPlayer(players.get(1));
            playerPanelC.setPlayer();
            break;
         case 3:
            playerPanelA.setPlayer(players.get(1));
            playerPanelB.setPlayer();
            playerPanelC.setPlayer(players.get(2));
            break;
         case 4:
            playerPanelA.setPlayer(players.get(1));
            playerPanelB.setPlayer(players.get(2));
            playerPanelC.setPlayer(players.get(3));
            break;
      }
      playerPanelA.revalidate();
      playerPanelB.revalidate();
      playerPanelC.revalidate();
   }
   
   /**
  * ClientCommunicator - main point of communication to/from server
  * @author - ngiano
  * @version - 4.7.20
  */
   class ClientCommunicator extends Thread {
      private Socket socket;
      private ObjectInputStream in = null;
      private ObjectOutputStream out = null;
      private Player p = null;
      private java.util.Timer t;      
      private boolean aborting = false;
      /**
        * ServerClient - Create a new client with a blank player
        * @param socket - Socket for this client
        */
      public ClientCommunicator(Socket socket) {
         t = new java.util.Timer();
         try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            boolean needName = true;
            String name = "Unset";
            hMan.setPlay(false);
            while(needName) {
               name = JOptionPane.showInputDialog(null, "Welcome, please set your player's name");
               sendOut(new Message("SNAME",name));
               Message confirm = (Message)in.readObject();
               if(confirm.getCommand().equals("OK")) {
                  needName = false;
               }
            }
            p = new Player(name);
            frame.setTitle("jUNO Client - "+name);
         } catch (IOException ioe) {
            System.out.println("IO error in initiating client");
         } catch (Exception e) {
            System.out.println(e);
         }
         this.socket = socket;
         
         //Activate actions in deck panel
         JButton button = dPan.getButton();
         button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               sendOut(new Message("DRAW"));
            } 
         });
         //Activate things in HMAN
         jbPlayCard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               if(jlistHand.isSelectionEmpty()) {
                  JOptionPane.showMessageDialog(null,"You need to select a card");
               } else if(p.getTurn()) {
                  hMan.setLastCardPlayed((Card)jlistHand.getSelectedValue());
                  sendOut(new Message("PLAY",jlistHand.getSelectedValue()));
               }
            }
         });
         //Activate uno/callout buttons
         jbUno.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               sendOut(new Message("UNO"));
            }
         });
         jbCallout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               sendOut(new Message("CALLOUT"));
            }
         });
         frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
               sendOut(new Message("ABORT"));
            }
         });
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
            String command = msg.getCommand();
            //*** INBOUND COMMANDS ***
            switch(command) {
               case "CHAT":
                  //Process objects
                  String sendee = (String)msg.getContent();
                  String message = (String)msg.getMoreContent();
                  //Execute
                  System.out.println("chat "+sendee+" "+message);
                  boolean chatMatch = false;
                  for(ChatPanel chatP: chatPanels) {
                     if(chatP.getUsername().equals(sendee)) {
                        //add line to chat panel
                        chatP.updateChat(message);
                        chatMatch = true;
                     }
                  }
                  if(!chatMatch) {
                     //create a new chat panel
                     ChatPanel chP = new ChatPanel(sendee);
                     chP.updateChat("Start of new chat with "+sendee+" ======");
                     chP.updateChat(message);
                     JButton sendButton = chP.getSendReference();
                     if(sendee.equals("GAMELOG")) {
                        sendButton.setEnabled(false);
                     }
                     sendButton.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent ae) {
                           String input = chP.readInput();
                           //Direct message
                           if(input.indexOf("/w ") == 0) {
                              String tempTarget = input.substring(3);
                              String tempInput = tempTarget.substring(tempTarget.indexOf(" ")+1);
                              tempTarget = tempTarget.substring(0,tempTarget.indexOf(" "));
                              if(tempTarget.equals(p.getName())) {
                                 chP.updateChat("You cannot send a message to yourself");
                              } else {
                                 boolean targetFound = false;
                                 for(Player p: userList) {
                                    if(p.getName().equals(tempTarget)) {
                                       sendOut(new Message("CHAT",tempTarget,tempInput));
                                       targetFound = true;
                                    }
                                 }
                                 if(!targetFound) {
                                    chP.updateChat("Failed to send: There is no player by the username "+tempTarget);
                                 }
                              }
                           } else {
                              sendOut(new Message("CHAT",sendee,input));
                           }
                        }
                     });
                     chatPanels.add(chP);
                     chatTabs.addTab(sendee,chP);
                  }
                  break;
               case "TURN"://TURN - ASSOCIATED OBJECT: ArrayList<Card>
                  //It's now this client's turn, allow playing/drawingcards
                  //Server delivers hand, in case of any desync
                  //Execute
                  p.setTurn(true);
                  hMan.setPlay(true);
                  sendOut(new Message("UPDATE"));
                  break;
               case "HAND"://RECIEVE HAND - ASSOCIATED OBJECTS: ArrayList<Card>
                  //Process object
                  ArrayList<Card> hand2 = (ArrayList<Card>)msg.getContent();
                  //Execute
                  System.out.println("handhand"+hand2);
                  hMan.setHand(hand2);
                  //Respond OK
                  sendOut(new Message("OK"));
                  break;
               case "HAND+"://RECIEVE HAND+MSG - ASSOCIATED OBJECTS: ArrayList<Card>, newCard
                  //Process object
                  ArrayList<Card> hand3 = (ArrayList<Card>)msg.getContent();
                  //Process other object
                  String note = (String)msg.getMoreContent();
                  hMan.setAlert(note.toString());
                  t.schedule(new AlertClearer(), 1000);
                  System.out.println("handplus"+hand3);
                  hMan.setHand(hand3);
                  //Respond OK
                  sendOut(new Message("OK"));
                  break;
               case "COLORP"://COLOR PICK - ASSOCIATED OBJECT: char
                  //Server is requesting client to pick a color
                  //Open message box for selecting a color
                  //Send back to server the selected color
                  String[] options = new String[] {"Red", "Yellow", "Green", "Blue"};
                  int response = JOptionPane.showOptionDialog(null, "Select what color", "Played a WILD card",JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
                  while(response == JOptionPane.CLOSED_OPTION) {
                     response = JOptionPane.showOptionDialog(null, "You must select what color", "Played a WILD card",JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,null, options, options[0]);
                  }
                  char[] colorOrder = new char[]{'R','Y','G','B'};
                  sendOut(new Message("COLORP",colorOrder[response]));
                  break;
               case "UPDATEALL"://RECIEVE UPDATE - ASSOCIATED OBJECT: Player[]???
                  //Update all players, names and card counts
                  System.out.println("UPDATEALL TIME!");
                  ArrayList<Player> updateAllPlayerList = (ArrayList<Player>)msg.getContent();
                  userList = updateAllPlayerList;
                  Player me = null;
                  for(int i = 0; i < updateAllPlayerList.size(); i++) {
                     System.out.println(updateAllPlayerList.get(i).getName() + " ("+updateAllPlayerList.get(i).getHandSize()+")");
                     if(updateAllPlayerList.get(i).getName().equals(p.getName())) {
                        me = updateAllPlayerList.get(i);
                     }
                  }
                  //Shift all elements rightward, cycling around until player representing this client is index 0
                  while(updateAllPlayerList.get(0) != me) {
                     //https://stackoverflow.com/questions/32387193/how-to-shift-element-in-an-arraylist-to-the-right-in-java
                     //make temp variable to hold last element
                     Player temp = updateAllPlayerList.get(updateAllPlayerList.size()-1); 
                  
                     //make a loop to run through the array list
                     for(int i = updateAllPlayerList.size()-1; i > 0; i--) {
                          //set the last element to the value of the 2nd to last element
                          updateAllPlayerList.set(i,updateAllPlayerList.get(i-1)); 
                     }
                     //set the first element to be the last element
                     updateAllPlayerList.set(0, temp);  
                  }
                  updatePlayerCards(updateAllPlayerList);
                  break;
               case "PILE"://UPDATE DISCARD PILE - ASSOCIATED OBJECT: Card (top card)
                  //Draw new top card on gui, store card data
                  //Process object
                  Card pileTopCard = (Card)msg.getContent();
                  //Process object
                  Boolean isClockwise = (Boolean)msg.getMoreContent();
                  dPan.refresh(pileTopCard,isClockwise);
                  break;
               case "SMS"://SERVER MESSAGE - ASSOCIATED OBJECT: String
                  //Display a server message on the gui.
                  //Process object
                  String alert = (String)msg.getContent();
                  //Execute
                  hMan.setAlert(alert);
               case "WIN"://WIN - ASSOCIATED OBJECT: null
                  //Show the player that they won
                  JOptionPane.showMessageDialog(null,"You win!");
                  hMan.setAlert("Waiting for server to start new game");
                  break;
               case "LOSE"://LOSE - ASSOCIATED OBJECT: String
                  //Show the player they lost, with a String showing the name of the winner.
                  //Process object
                  String winner = (String)msg.getContent();
                  //Execute
                  JOptionPane.showMessageDialog(null,"You lost\nThis game's winner is "+winner+"!");
                  hMan.setAlert("Waiting for server to start new game");
                  break;
               case "ABORT"://ABORT aka DISCONNECT - ASSOCIATED OBJECT: null
                  //Execute
                  JOptionPane.showMessageDialog(null,"Server is shutting down");
                  System.exit(0);
                  break;
               case "GOODBYE":
                  System.exit(0);
                  break;
               case "FAIL"://OK - this might honestly be it's own thing, but im putting it here so we know to add it later
                  //Process Object
                  String ff = (String)msg.getContent();
                  //Execute
                  JOptionPane.showMessageDialog(null,"FAILURE "+ff);
                  break;
               case "OK":
                  //Process Object
                  String oo = (String)msg.getContent();
                  //Execute
                  System.out.println("ok msg: "+oo);
                  hMan.setAlert(oo);
                  if(oo.equals("New game started!")) {
                     p.setTurn(false);
                     hMan.setPlay(false);
                  }
                  if(oo.equals("Card Played")) {
                     System.out.println("Card played. Remove");
                     hMan.setPlay(false);
                     p.setTurn(false);
                     hMan.removeLastCardPlayed();
                     sendOut(new Message("UPDATE"));
                  }
                  if(oo.equals("Uno called")) {
                     p.setUno(true);
                  }
                  t.schedule(new AlertClearer(),5000);
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
            return false;
         }
         return true;
      }
   }
   /**
     * ===HMAN===
     * HandManager - Manages players hand, sorting, rendering cards and prepping for playing cards
     */
   public class HandManager extends JPanel {
      private ArrayList<Card> hand;
      private JPanel jpHandPanel;
      private JLabel jlAlert;
      private boolean hmanBusy = false;
      private Card lastCard;
      final DefaultListModel<Card> cardNames = new DefaultListModel<Card>();
      HandManager() {
         setLayout(new BorderLayout());
         
         jlistHand = new JList<>();
         UnoCardRenderer renderer = new UnoCardRenderer();
         jlistHand.setCellRenderer(renderer);
         jlistHand.setLayoutOrientation(JList.HORIZONTAL_WRAP);
         jlistHand.setVisibleRowCount(1);
         add(new JScrollPane(jlistHand,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),"Center");

         jlAlert = new JLabel("Hand Manager");
         jlAlert.setHorizontalTextPosition(JLabel.CENTER);
         add(jlAlert,"North");
         
         jbPlayCard = new JButton("Play");
         add(jbPlayCard,"South");
         
         this.hand = hand;
      }
      public void setAlert(String alert) {
         jlAlert.setText(alert);
      }
      public void clearAlert() {
         jlAlert.setText("You have "+hand.size()+" cards in your hand");
      }
      public void setPlay(boolean isEnabled) {
         System.out.println("Play set to "+isEnabled);
         jbPlayCard.setEnabled(isEnabled);
      }
      public void setLastCardPlayed(Card card) {
         lastCard = card;
         System.out.println("Last card set to: "+card.toString());
      }
      public void removeLastCardPlayed() {
         for(int i = 0; i < hand.size(); i++) {
            if(hand.get(i).compareTo(lastCard) == 0) {
               hand.remove(i);
            }
         }
         refresh();
      }
      public void setHand(ArrayList<Card> hand) {
         Collections.sort(hand);
         this.hand = hand;
         refresh();
      }
      public void refresh() {
         if(!hmanBusy) {
            RefreshHMAN t = new RefreshHMAN(jlistHand, hand);
            t.start();
            hmanBusy = true;
         } else {
            System.out.println("tried, but hman was busy!!");
         }
      }
      private class RefreshHMAN extends Thread {
         private JList jlistHand;
         private final ArrayList<Card> REFHAND;
         public RefreshHMAN(JList jlistHand, ArrayList<Card> hand) {
            this.jlistHand = jlistHand;
            this.REFHAND = hand;
         }
         public void run() {
            try {
               cardNames.clear();
               try{sleep(120);}catch(InterruptedException ie){}
               for(int i = 0; i < REFHAND.size(); i++) {
                  cardNames.add(i,REFHAND.get(i));
                  try{sleep(20);}catch(InterruptedException ie){}
               }
               try{sleep(120);}catch(InterruptedException ie){}
               jlistHand.setModel(cardNames);
            } catch(NullPointerException idgaf) {
               System.out.println(idgaf);
            } finally {
               synchronized("") {
                  hmanBusy = false;
               }
            }
         }
      }
      //https://stackoverflow.com/questions/22266506/how-to-add-image-in-jlist
      private class UnoCardRenderer extends DefaultListCellRenderer {         
          public Component getListCellRendererComponent(JList list, Object value, int index,boolean isSelected, boolean cellHasFocus) {
              Card card = (Card)value;
              String uValue = card.toString();
              JLabel label = (JLabel) super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
              label.setIcon(new ImageIcon("resources/"+card.toFileString()+".png"));//"resources/"+card.toFileString()+".gif"
              label.setHorizontalTextPosition(JLabel.CENTER);
              label.setVerticalTextPosition(JLabel.BOTTOM);
              label.setToolTipText(uValue);
              return label;
          }
      }
   }
}