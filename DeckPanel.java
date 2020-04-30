import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DeckPanel extends JPanel {
   Deck drawingDeck;
   Card discardTopCard;
   JPanel jpDiscard, jpDrawing;
   JLabel jlDiscard, jlDrawing, jlTurnOrder;
   JButton jbDraw;
   DeckPanel() {
      setLayout(new BorderLayout());
      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new GridLayout(1,2));
      
      jpDiscard = new JPanel();
      jpDiscard.setLayout(new BorderLayout());
      
      
      jlDiscard = new JLabel("DRAW");
      jlDiscard.setIcon(new ImageIcon("resources/r_1.gif"));
      jlDiscard.setHorizontalTextPosition(JLabel.CENTER);
      jlDiscard.setVerticalTextPosition(JLabel.BOTTOM);
      
      jpDiscard.add(jlDiscard,"Center");
      
      jpDrawing = new JPanel();
      jpDrawing.setLayout(new BorderLayout());
      
      jbDraw = new JButton("Draw");
      jbDraw.setToolTipText("Draw a card");
      
      jlDrawing = new JLabel("");
      jlDrawing.setIcon(new ImageIcon("resources/back.gif"));
      jlDrawing.setHorizontalTextPosition(JLabel.CENTER);
      jlDrawing.setVerticalTextPosition(JLabel.BOTTOM);
      
      jpDrawing.add(jbDraw,"South");
      jpDrawing.add(jlDrawing,"Center");
      
      mainPanel.add(jpDiscard);
      mainPanel.add(jpDrawing);
      
      jlTurnOrder = new JLabel("Turn order: Clockwise");
      jlTurnOrder.setIcon(new ImageIcon("resources/clockwise.png"));
      jlTurnOrder.setHorizontalAlignment(JLabel.CENTER);
      jlTurnOrder.setHorizontalTextPosition(JLabel.RIGHT);
      jlTurnOrder.setVerticalTextPosition(JLabel.CENTER);
      
      add(jlTurnOrder,"North");
      add(mainPanel,"Center");
   }
   
   DeckPanel(Deck drawingDeck, Card discardTopCard) {
      this.drawingDeck = drawingDeck;
      this.discardTopCard = discardTopCard;
   }
   
   public void refresh(Card discardTopCard, boolean isClockwise) {
      this.discardTopCard = discardTopCard;
      jlDiscard.setIcon(new ImageIcon("resources/"+discardTopCard.toFileString()+".png"));
      jlDiscard.setToolTipText(discardTopCard.toString());
      jlDiscard.setText(discardTopCard.toString());
      if(isClockwise) {
         jlTurnOrder.setText("Turn order: Clockwise");
         jlTurnOrder.setIcon(new ImageIcon("resources/clockwise.png"));
      } else {
         jlTurnOrder.setText("Turn order: Counter-Clockwise");
         jlTurnOrder.setIcon(new ImageIcon("resources/counterclockwise.png"));
      }
   }
   
   public JButton getButton() {
      return jbDraw;
   }
}