import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

/**
  * DeckPanel - Display discard deck's top card graphic, as well as drawing
  */
public class DeckPanel extends JPanel {
   Deck drawingDeck;
   Card discardTopCard;
   JPanel jpDiscard, jpDrawing;
   JLabel jlDiscard, jlDrawing, jlTurnOrder;
   JButton jbDraw;
   java.util.Timer t = new java.util.Timer();
   /**
     * Constructs a deckpanel
     */
   DeckPanel() {
      setLayout(new BorderLayout());
      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new GridLayout(1,2));
      
      jpDiscard = new JPanel();
      jpDiscard.setLayout(new BorderLayout());
      
      
      jlDiscard = new JLabel("DRAW");
      jlDiscard.setIcon(new ImageIcon(getClass().getResource("resources/r_1.gif")));
      jlDiscard.setHorizontalTextPosition(JLabel.CENTER);
      jlDiscard.setVerticalTextPosition(JLabel.BOTTOM);
      
      jpDiscard.add(jlDiscard,"Center");
      
      jpDrawing = new JPanel();
      jpDrawing.setLayout(new BorderLayout());
      
      jbDraw = new JButton("Draw");
      jbDraw.setToolTipText("Draw a card");
      
      jlDrawing = new JLabel("");
      jlDrawing.setIcon(new ImageIcon(getClass().getResource("resources/back.gif")));
      jlDrawing.setHorizontalTextPosition(JLabel.CENTER);
      jlDrawing.setVerticalTextPosition(JLabel.BOTTOM);
      
      jpDrawing.add(jbDraw,"South");
      jpDrawing.add(jlDrawing,"Center");
      
      mainPanel.add(jpDiscard);
      mainPanel.add(jpDrawing);
      
      jlTurnOrder = new JLabel("Turn order: Clockwise");
      jlTurnOrder.setIcon(new ImageIcon(getClass().getResource("resources/clockwise.png")));
      jlTurnOrder.setHorizontalAlignment(JLabel.CENTER);
      jlTurnOrder.setHorizontalTextPosition(JLabel.RIGHT);
      jlTurnOrder.setVerticalTextPosition(JLabel.CENTER);
      
      jbDraw.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            jbDraw.setEnabled(false);
            t.schedule(new ReactivateButton(),2000);
         }
      });
      
      add(jlTurnOrder,"North");
      add(mainPanel,"Center");
   }
   
   /**
     * refresh - Update deckpanel with new data
     * @param discardTopCard - Top card to display
     * @param isClockwise - Turn direction
     */
   public void refresh(Card discardTopCard, boolean isClockwise) {
      this.discardTopCard = discardTopCard;
      jlDiscard.setIcon(new ImageIcon(getClass().getResource("resources/"+discardTopCard.toFileString()+".png")));
      jlDiscard.setToolTipText(discardTopCard.toString());
      jlDiscard.setText(discardTopCard.toString());
      if(isClockwise) {
         jlTurnOrder.setText("Turn order: Clockwise");
         jlTurnOrder.setIcon(new ImageIcon(getClass().getResource("resources/clockwise.png")));
      } else {
         jlTurnOrder.setText("Turn order: Counter-Clockwise");
         jlTurnOrder.setIcon(new ImageIcon(getClass().getResource("resources/counterclockwise.png")));
      }
   }
   
   /** 
     * getButton - Get the button object, for adding event listeners
     * @return JButton for adding event listeners
     */
   public JButton getButton() {
      return jbDraw;
   }
   
   /**
     * ReactivateButton - TimerTask that resets the button to be clicked again
     */
   public class ReactivateButton extends TimerTask {
      public void run() {
         jbDraw.setEnabled(true);
      }
   }
}