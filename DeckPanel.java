import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DeckPanel extends JPanel {
   Deck drawingDeck;
   Card discardTopCard;
   JPanel jpDiscard, jpDrawing;
   JLabel jlDiscard, jlDrawing;
   JButton jbDraw, jbEnd;
   DeckPanel() {
      setLayout(new GridLayout(1,2));
      
      jpDiscard = new JPanel();
      jpDiscard.setLayout(new BorderLayout());
      
      jbEnd = new JButton("End");
      jbEnd.setToolTipText("End your turn");
      
      jlDiscard = new JLabel("DRAW");
      jlDiscard.setIcon(new ImageIcon("resources/r_1.gif"));
      jlDiscard.setHorizontalTextPosition(JLabel.CENTER);
      jlDiscard.setVerticalTextPosition(JLabel.BOTTOM);
      
      jpDiscard.add(jbEnd,"South");
      jpDiscard.add(jlDiscard,"Center");
      
      jpDrawing = new JPanel();
      jpDrawing.setLayout(new BorderLayout());
      
      jbDraw = new JButton("Draw");
      jbDraw.setToolTipText("Draw a card");
      
      jlDrawing = new JLabel("DRAW");
      jlDrawing.setIcon(new ImageIcon("resources/back.gif"));
      jlDrawing.setHorizontalTextPosition(JLabel.CENTER);
      jlDrawing.setVerticalTextPosition(JLabel.BOTTOM);
      
      jpDrawing.add(jbDraw,"South");
      jpDrawing.add(jlDrawing,"Center");
      
      add(jpDiscard);
      add(jpDrawing);
   }
   
   DeckPanel(Deck drawingDeck, Card discardTopCard) {
      this.drawingDeck = drawingDeck;
      this.discardTopCard = discardTopCard;
   }
   
   public void refresh(Deck drawingDeck, Card discardTopCard) {
      this.drawingDeck = drawingDeck;
      this.discardTopCard = discardTopCard;
      jlDrawing.setText("("+drawingDeck.getCards().size()+")");
      jlDiscard.setIcon(new ImageIcon("resources/"+discardTopCard.toFileString()+".png"));
      jlDiscard.setToolTipText(discardTopCard.toString());
   }
   public void refresh(Card discardTopCard) {
      this.discardTopCard = discardTopCard;
      jlDiscard.setIcon(new ImageIcon("resources/"+discardTopCard.toFileString()+".png"));
      jlDiscard.setToolTipText(discardTopCard.toString());
      jlDiscard.setText(discardTopCard.toString());
   }
   
   public JButton[] getButtons() {
      JButton[] buttons = {jbDraw,jbEnd};
      return buttons;
   }
}