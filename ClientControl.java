import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock; 

public class ClientControl extends JFrame {
   private HandManager hMan;
   private JList jlistHand;
   private JButton jbPlayCard, jbTest2;
   private ClientCommunicator cc;
   private DeckPanel dPan;
   private Player p = new Player("Greg");
   private ArrayList<Player> userList;
   private PlayerPanel playerPanelA, playerPanelB, playerPanelC;
   private PlayerListPanel plp;
   public static void main(String[] args) {
      new ClientControl();
   }
   public ClientControl() {
      super("jUNO Client");
      setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
      plp = new PlayerListPanel();
      add(plp);
      //MAIN LAYOUT CONSTRUCT
      JPanel mainLayout = new JPanel(new GridLayout(3,0));
      JPanel jpRow1 = new JPanel(new GridLayout(0,3));
      JPanel jpRow2 = new JPanel(new GridLayout(0,3));
      JPanel jpHand = new JPanel(new GridLayout(0,1));
      //TEST1
      JButton jbTest = new JButton("TEST HMAN");
      //TEST1
      jpRow1.add(jbTest);
      playerPanelB = new PlayerPanel(new Player("PLAYER B"));
      jpRow1.add(playerPanelB);
      jbTest2 = new JButton("TEST2");
      jpRow1.add(jbTest2);
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
      //TEST1
      jbTest.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            ArrayList<Card> testHand = new ArrayList<Card>();
            testHand.add(new Card(-5,'X'));
            testHand.add(new Card(-4,'X'));
            testHand.add(new Card(5,'R'));
            testHand.add(new Card(6,'R'));
            testHand.add(new Card(4,'R'));
            testHand.add(new Card(9,'G'));
            testHand.add(new Card(3,'R'));
            testHand.add(new Card(-5,'X'));
            testHand.add(new Card(-4,'X'));
            testHand.add(new Card(5,'R'));
            testHand.add(new Card(6,'R'));
            testHand.add(new Card(4,'R'));
            testHand.add(new Card(9,'G'));
            testHand.add(new Card(3,'R'));
            testHand.add(new Card(-5,'X'));
            testHand.add(new Card(-4,'X'));
            testHand.add(new Card(5,'R'));
            testHand.add(new Card(6,'R'));
            testHand.add(new Card(4,'R'));
            testHand.add(new Card(9,'G'));
            testHand.add(new Card(3,'R'));
            Player p1 = new Player("Steven");
            Player p2 = new Player("Greg");
            Player p3 = new Player("Tim");
            ArrayList<Player> plist = new ArrayList<Player>();
            plist.add(p);
            plist.add(p1);
            plist.add(p2);
            plist.add(p3);
            plp.setList(plist);
            p.setHand(testHand);
            hMan.setHand(testHand);
         }
      });
      //TEST1
      //Sidebar
      add(new JPanel());
      add(mainLayout);
      setSize(500,300);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setVisible(true);
      try {
         Socket s = new Socket("localhost",12345);
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
         hMan.setAlert("");
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
            playerPanelA = new PlayerPanel();
            playerPanelB = new PlayerPanel();
            playerPanelC = new PlayerPanel();
            break;
         case 2:
            playerPanelA = new PlayerPanel();
            playerPanelB = new PlayerPanel(players.get(1));
            playerPanelC = new PlayerPanel();
            break;
         case 3:
            playerPanelA = new PlayerPanel(players.get(2));
            playerPanelB = new PlayerPanel();
            playerPanelC = new PlayerPanel(players.get(1));
            break;
         case 4:
            playerPanelA = new PlayerPanel(players.get(3));
            playerPanelB = new PlayerPanel(players.get(2));
            playerPanelC = new PlayerPanel(players.get(1));
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
         } catch (IOException ioe) {
            System.out.println("IO error in initiating client");
         } catch (Exception e) {
            System.out.println(e);
         }
         this.socket = socket;
         
         //Activate actions in deck panel
         JButton[] buttons = dPan.getButtons();
         buttons[0].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               sendOut(new Message("DRAW"));
            } 
         });
         buttons[1].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               sendOut(new Message("END"));
               p.setTurn(false);
               hMan.setPlay(false);
            } 
         });
         //Activate things in HMAN
         jbPlayCard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               if(p.getTurn()) {
                  sendOut(new Message("PLAY",jlistHand.getSelectedValue()));
               }
            }
         });
         jbTest2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               sendOut(new Message("HAND"));
            }
         });

      }
            
      public void run() {
         System.out.println("run");
         while (true) {
            System.out.println("Ready for next message");
            Message msg = null;
            try {
               msg = (Message) in.readObject();
            } catch (IOException ioe) {
               System.out.println("IO Error in reading client message");
            } catch (ClassNotFoundException cnfe) {
               System.out.println("Message class not found");
            }
            String command = msg.getCommand();
            System.out.println(command);
            //*** INBOUND COMMANDS ***
            switch(command) {
               case "TURN"://TURN - ASSOCIATED OBJECT: ArrayList<Card>
                  //It's now this client's turn, allow playing/drawingcards
                  //Server delivers hand, in case of any desync
                  //Process object
                  ArrayList<Card> hand = (ArrayList<Card>)msg.getContent();
                  //Execute
                  hMan.setAlert("It's your turn");
                  p.setTurn(true);
                  System.out.println("turnhand"+hand);
                  hMan.setHand(hand);
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
                  char[] colorOrder = new char[]{'R','Y','G','B'};
                  sendOut(new Message("COLORP",colorOrder[response]));
                  break;
               case "UPDATEALL"://RECIEVE UPDATE - ASSOCIATED OBJECT: Player[]???
                  //Update all players, names and card counts
                  ArrayList<Player> updateAllPlayerList = (ArrayList<Player>)msg.getContent();
                  Player me = null;
                  for(int i = 0; i < updateAllPlayerList.size(); i++) {
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
                  try {
                     updatePlayerCards(updateAllPlayerList);
                  } catch (NullPointerException npe) {}//Dont know why it happens
                  break;
               case "PILE"://UPDATE DISCARD PILE - ASSOCIATED OBJECT: Card (top card)
                  //Draw new top card on gui, store card data
                  //Process object
                  Card pileTopCard = (Card)msg.getContent();
                  dPan.refresh(pileTopCard);
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
                  hMan.setAlert(oo);
                  t.schedule(new AlertClearer(),1000);
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
      final DefaultListModel<Card> cardNames = new DefaultListModel<Card>();
      HandManager() {
         setLayout(new BorderLayout());
         
         jlistHand = new JList<>();
         UnoCardRenderer renderer = new UnoCardRenderer();
         jlistHand.setCellRenderer(renderer);
         jlistHand.setLayoutOrientation(JList.HORIZONTAL_WRAP);
         jlistHand.setVisibleRowCount(1);
         add(new JScrollPane(jlistHand,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),"Center");

         jlAlert = new JLabel("Test");
         jlAlert.setHorizontalTextPosition(JLabel.CENTER);
         add(jlAlert,"North");
         
         jbPlayCard = new JButton("Play");
         add(jbPlayCard,"South");
         
         this.hand = hand;
      }
      public void setAlert(String alert) {
         jlAlert.setText(alert);
      }
      public void setPlay(boolean isEnabled) {
         jbPlayCard.setEnabled(isEnabled);
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
               try{sleep(100);}catch(InterruptedException ie){}
               for(int i = 0; i < REFHAND.size(); i++) {
                  cardNames.add(i,REFHAND.get(i));
                  try{sleep(10);}catch(InterruptedException ie){}
               }
               try{sleep(100);}catch(InterruptedException ie){}
               jlistHand.setModel(cardNames);
            } catch(NullPointerException idgaf) {
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