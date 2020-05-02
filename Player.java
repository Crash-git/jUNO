import java.util.ArrayList;
import java.io.Serializable;

/**
  * Player - manages a player, and their hand
  * @author - ngiano
  * @version 4.6.20
  */

public class Player implements Serializable {
   private String name;
   private ArrayList<Card> hand;
   private boolean isTurn = false;
   private boolean isUno = false;
   
   /**
     * Player - construct a blank player
     */
   public Player() {
      this.name = "Unset";
      this.hand = new ArrayList<Card>();
   }
   
   /**
     * Player - construct a player with a name, and an empty hand
     * @param name - Name
     */
   public Player(String name) {
      this.name = name;
      this.hand = new ArrayList<Card>();
   }
   
   /**
     * Player - construct a player with a name, and a hand
     * @param name - Name
     * @param hand - Hand of cards, ArrayList<Card>
     */
   public Player(String name, ArrayList<Card> hand) {
      this.name = name;
      this.hand = hand;
   }
   
   /** Player - constructs a player with a name, and a hand
     * @param name - Name 
     * @param hand - Hand of cards, Card[] (which is converted to ArrayList)
     */
   public Player(String name, Card[] hand) {
      this.name = name;
      this.hand = new ArrayList<Card>();
      for(Card c:hand) {
         this.hand.add(c);
      }
   }
   
   /**
     * setTurn - Sets if it's this player's turn or not
     * @param boolean - isTurn
     */
   public void setTurn(boolean isTurn) {
      this.isTurn = isTurn;
   }
   
   /**
     * setUno - Sets if the player has pressed the Uno button
     * @param boolean - isUno
     */
   public void setUno(boolean isUno) {
      this.isUno = isUno;
   }
   
   /**
     * setName - Sets player display name
     * @param string - name
     */
   public void setName(String name) {
      this.name = name;
   }
   
   /**
     * getTurn - Sees if it's this player's turn or not
     * @returns boolean - isTurn
     */
   public boolean getTurn() {
      return isTurn;
   }
   
   /**
     * getUno - Sees if the player has pressed the Uno button
     * @returns boolean - isUno
     */
   public boolean getUno() {
      return isUno;
   }
   
   /**
     * getName - Gets player display name
     * @returns string - name
     */
   public String getName() {
      return name;
   }
   
   /**
     * getHandSize - Get the size (remaining cards) of a player's hand
     * @returns int - Size of hand
     */
   public int getHandSize() {
      return hand.size();
   }
   
   /**
     * getHand - Get the player's hand
     * @returns Cards - hand
     */
   public ArrayList<Card> getHand() {
      return hand;
   }
   
   /**
     * setHand - Set the player's hand
     * @param Cards - hand
     */
   public void setHand(ArrayList<Card> hand) {
      this.hand = hand;
      if(isUno && hand.size() > 1) {
         isUno = false;
      }
   }
   public void setHand() {
      this.hand = new ArrayList<Card>();
   }
   
   /**
     * play - Play a card, removing it from the player's hand and returning it
     * @param index - Index of the card in the hand
     * @returns Card - Card selected
     */
   public Card play(int index) {
       Card playedCard = null;
       try {
         playedCard = (Card)hand.get(index).clone();
       } catch (CloneNotSupportedException e) {
            e.printStackTrace();
       }
       hand.remove(index);
       return playedCard;
   }
   
   /**
     * add - Add a card to the player's hand
     * @param card - Card to be added
     */
   public void add(Card card) {
      hand.add(card);
      if(isUno && hand.size() > 1) {
         isUno = false;
      }
   }
   
}