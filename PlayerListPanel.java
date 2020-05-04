import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class PlayerListPanel extends JPanel {
   private JList playerList;
   final DefaultListModel<Player> playerNames = new DefaultListModel<Player>();
   ArrayList<Player> players;
   private boolean plpBusy = false;
   PlayerListPanel() {
      setLayout(new BorderLayout());
      playerList = new JList();
      playerList.setCellRenderer(new PlayerListRenderer());
      playerList.setLayoutOrientation(JList.VERTICAL);
      add(new JLabel("Player list:"),"North");
      add(playerList,"Center");
      setVisible(true);
   }
   public void setList(ArrayList<Player> players) {
      this.players = players;
      refresh();
   }
   public void refresh() {
      if(!plpBusy) {
         RefreshPLP t = new RefreshPLP(playerList);
         t.start();
         plpBusy = true;
      } else {
         System.out.println("tried to update plp, but it was  busy!!!");
      }
   }
   private class RefreshPLP extends Thread {
      JList playerList;
      public RefreshPLP(JList playerList) {
         this.playerList = playerList;
      }
      public void run() {
         try {
            playerNames.clear();
            try{sleep(100);}catch(InterruptedException ie){}
            for(int i = 0; i < players.size(); i++) {
               playerNames.add(i,players.get(i));
               try{sleep(10);}catch(InterruptedException ie){}
            }
            try{sleep(100);}catch(InterruptedException ie){}
            playerList.setModel(playerNames);
         } catch(NullPointerException npe) {
            System.out.println(npe);
         } finally {
            synchronized("") {
               plpBusy = false;
            }
         }
      }
   }
   public class PlayerListRenderer extends DefaultListCellRenderer {
      
      Font normal = new Font("Calibri",Font.PLAIN,24);
      Font turn = new Font("Calibri",Font.ITALIC,24);
      Font uno = new Font("Calibri",Font.BOLD,24);
            
       public Component getListCellRendererComponent(JList list, Object value, int index,boolean isSelected, boolean cellHasFocus) {
           Player p = (Player)value;
           String uValue = p.getName() + " ("+p.getHandSize()+")";
           JLabel label = (JLabel) super.getListCellRendererComponent(list, uValue, index, isSelected, cellHasFocus);
           label.setHorizontalTextPosition(JLabel.CENTER);
           label.setVerticalTextPosition(JLabel.BOTTOM);
           label.setToolTipText(uValue);
           label.setFont(normal);
           if(p.getTurn()) {
             label.setFont(turn);
           }
           if(p.getUno()) {
             label.setFont(uno);
           }
           return label;
       }
   }
}