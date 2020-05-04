import java.awt.*;
import javax.swing.*;

/**
  * PlayerPanel - Displays a player, their hand, and card count
  * @author - ngiano
  * iste121
  */
public class PlayerPanel extends JPanel {
   private Player p;
   private JLabel jlName;
   /**
     * PlayerPanel constructs an empty panel
     */
   PlayerPanel() {
   }
   /**
     * PlayerPanel constructs a panel with a player, showing card counds and name
     * @param p - Player object
     */
   PlayerPanel(Player p) {
      this.p = p;
      setLayout(new FlowLayout());
      jlName = new JLabel(p.getName());
      jlName.setIcon(new ImageIcon(getClass().getResource("resources/hand0.png")));
      jlName.setHorizontalAlignment(JLabel.CENTER);
      jlName.setHorizontalTextPosition(JLabel.CENTER);
      jlName.setVerticalTextPosition(JLabel.TOP);
      add(jlName);
   }
   /**
     * refresh - Refresh UI
     */
   public void refresh() {
      jlName.setText(p.getName()+" | Hand: "+p.getHandSize());
      if(p.getHandSize() < 10) {
         jlName.setIcon(new ImageIcon(getClass().getResource("resources/hand"+p.getHandSize()+".png")));
      } else {
         jlName.setIcon(new ImageIcon(getClass().getResource("resources/handplus.png")));
      }
   }
   /**
     * setPlayer - Update player
     * @param p Player to set
     */
   public void setPlayer(Player p) {
      jlName.setText(p.getName()+" | Hand: "+p.getHandSize());
      if(p.getHandSize() < 10) {
         jlName.setIcon(new ImageIcon(getClass().getResource("resources/hand"+p.getHandSize()+".png")));
      } else {
         jlName.setIcon(new ImageIcon(getClass().getResource("resources/handplus.png")));
      }
   }
   /**
     * setPlayer - Clear player panel
     */
   public void setPlayer() {
      jlName.setText("");
   }
}