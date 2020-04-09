import java.util.ArrayList;

/**
  * Player - manages a player, and their hand
  * @author - ngiano
  * @version 4.6.20
  */
public class Player {
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
     * getHandSize - Get the size (remaining cards) of a player's hand
     * @returns int - Size of hand
     */
   public int getHandSize() {
      return hand.size();
   }
   
   /**
     * play - Play a card, removing it from the player's hand and returning it
     * @param index - Index of the card in the hand
     * @returns Card - Card selected
     */
   public Card play(int index) throws CloneNotSupportedException {
       Card playedCard = (Card)hand.get(index).clone();
       hand.remove(index);
       return playedCard;
   }
   
   /**
     * add - Add a card to the player's hand
     * @param card - Card to be added
     */
   public void add(Card card) {
      hand.add(card);
   }
}