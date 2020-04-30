import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.io.Serializable;

/**
  * Deck - Manages decks of cards.
  * @author - ngiano
  * @version 4.3.20
  */
public class Deck implements Serializable {
   private ArrayList<Card> cards;
   /**
     * Deck constructor for decks pre-populated with 102 cards
     * @param isFull - true will pre-populate the deck with the standard 102 cards | false will be an empty deck
     */
   public Deck(boolean isFull) {
      cards = new ArrayList<Card>();
      if(isFull) {
         //Generate 1 of each of the 0-value cards in each color
         cards.add(new Card(0,'R'));
         cards.add(new Card(0,'B'));
         cards.add(new Card(0,'G'));
         cards.add(new Card(0,'Y'));
         //Generate 4 wild and wild draw 4 cards
         for(int i = 0; i < 4; i++) {
            cards.add(new Card(-5,'X'));
            cards.add(new Card(-4,'X'));
         }
         //Generate 2 of each 1-9 and special(non-wild) cards in each color
         for(int i = 0; i < 2; i++) {
            for(int j = 1;j < 10;j++) {
               cards.add(new Card(j,'R'));
               cards.add(new Card(j,'B'));
               cards.add(new Card(j,'G'));
               cards.add(new Card(j,'Y'));
            }
            for(int k = -3; k < 0;k++) {
               cards.add(new Card(k,'R'));
               cards.add(new Card(k,'B'));
               cards.add(new Card(k,'G'));
               cards.add(new Card(k,'Y'));
            }
         }
      }
   }
   /**
     * Deck constructor for an empty deck
     */
   public Deck() {
      cards = new ArrayList<Card>();
   }
   /**
     * shuffle - randomizes card order
     * @param saveTop - Save the topmost card, excluding it from the suffle and giving it. Used for recycling the discard pile
     * @return Card - Topmost card of deck
     */
   public Card shuffle(boolean saveTop) {
      Card topCard = null;
      if(saveTop) {
         Random r = new Random();
         try {
            topCard = (Card)cards.get(r.nextInt(cards.size())).clone();
         } catch (CloneNotSupportedException e) {
               e.printStackTrace();
         }
         cards.remove(0);
      }
      Collections.shuffle(cards);
      return topCard;
   }
   public void shuffle() throws CloneNotSupportedException {
      Collections.shuffle(cards);
   }
   /**
     * draw - draw a card from the top of the deck
     * @return Card - Topmost card of deck
     */
   public Card draw() {
      Card topCard = null;
      try {
         topCard = (Card)cards.get(0).clone();
      } catch (CloneNotSupportedException e) {
            e.printStackTrace();
      }
      cards.remove(0);
      return topCard;
   }
   /**
     * verify - Verify if a card can be played on the top of a deck
     */
   public boolean verify(Card card) {
      Card topCard = cards.get(0);
      //Wild card, can always be played
      if(card.getColor() == 'X') {
         return true;
      } else if(card.getColor() == topCard.getColor() || card.getValue() == topCard.getValue()) {
         return true;
      }
      return false;
   }
   /**
     * insert - Insert a new card, adding it to the top of the deck
     */
   public void insert(Card card) {
      cards.add(0, card);
   }
   /**
     * deal - Deal the deck to all players
     * @param players - Array of all players
     */
   public void deal(Player[] players) {
      for(Player p: players) {
         for(int i = 0; i < 7; i++) {
            p.add(this.draw());
         }
      }
   }
   /**
     * deal - Deal the deck to a single player
     * @param Player
     */
   public void deal(Player player) {
      for(int i = 0; i < 7; i++) {
         player.add(this.draw());
      }
   }
   /**
     * getCards  - Return the card deck
     */
   public ArrayList<Card> getCards() {
      return cards;
   }
   /**
     * setCards  - Set the card deck
     */
   public void setCards(ArrayList<Card> cards) {
      this.cards = cards;
   }
  
}