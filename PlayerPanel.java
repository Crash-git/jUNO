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

   /**
    * hardcodes the hand size pictures
    * @param _handSize size of incomeing hand
    * @return the correct icon
    */
   public ImageIcon getHandGraphic(int _handSize) {
      ImageIcon handSizeIcon = null;

      switch (_handSize) {
         default:
            return handSizeIcon = new ImageIcon(getClass().getResource("resources/handplus.png"));
         case 0:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand0.png"));
            break;
         case 1:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand1.png"));
            break;
         case 2:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand2.png"));
            break;
         case 3:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand3.png"));
            break;
         case 4:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand4.png"));
            break;
         case 5:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand5.png"));
            break;
         case 6:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand6.png"));
            break;
         case 7:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand7.png"));
            break;
         case 8:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand8.png"));
            break;
         case 9:
            handSizeIcon = new ImageIcon(getClass().getResource("resources/hand9.png"));
            break;
      }

      return handSizeIcon;
   }
}