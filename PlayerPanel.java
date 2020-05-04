import java.awt.*;
import javax.swing.*;

public class PlayerPanel extends JPanel {
   private Player p;
   private JLabel jlName;
   PlayerPanel() {
   }
   PlayerPanel(Player p) {
      this.p = p;
      setLayout(new FlowLayout());
      jlName = new JLabel(p.getName());
      jlName.setIcon(new ImageIcon("resources/hand0.png"));
      jlName.setHorizontalAlignment(JLabel.CENTER);
      jlName.setHorizontalTextPosition(JLabel.CENTER);
      jlName.setVerticalTextPosition(JLabel.TOP);
      add(jlName);
   }
   public void refresh() {
      jlName.setText(p.getName()+" | Hand: "+p.getHandSize());
      if(p.getHandSize() < 10) {
         jlName.setIcon(new ImageIcon("resources/hand"+p.getHandSize()+".png"));
      } else {
         jlName.setIcon(new ImageIcon("resources/handplus.png"));
      }
   }
   public void setPlayer(Player p) {
      jlName.setText(p.getName()+" | Hand: "+p.getHandSize());
      if(p.getHandSize() < 10) {
         jlName.setIcon(new ImageIcon("resources/hand"+p.getHandSize()+".png"));
      } else {
         jlName.setIcon(new ImageIcon("resources/handplus.png"));
      }
   }
   public void setPlayer() {
      jlName.setText("");
   }
}