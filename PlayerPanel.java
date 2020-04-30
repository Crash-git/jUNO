import java.awt.*;
import javax.swing.*;

public class PlayerPanel extends JPanel {
   Player p;
   JLabel jlName, jlHandSize;
   PlayerPanel() {
   }
   PlayerPanel(Player p) {
      this.p = p;
      setLayout(new FlowLayout());
      jlName = new JLabel(p.getName());
      add(jlName);
      jlHandSize = new JLabel("Hand: "+p.getHandSize());
      add(jlHandSize);
   }
   public void refresh() {
      jlName.setText(p.getName());
      jlHandSize.setText("Hand: "+p.getHandSize());
   }
   public void setPlayer(Player p) {
      jlName.setText(p.getName());
      jlHandSize.setText("Hand: "+p.getHandSize());
   }
   public void setPlayer() {
      jlName.setText("");
      jlHandSize.setText("");
   }
}