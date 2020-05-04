/**
  * Card - Core structure class for a card. Contains its properties
  * @author - ngiano
  * @version 4.3.20
  */
import java.util.*;
import java.io.Serializable;

public class Card implements Cloneable, Comparable, Serializable {
   int value;
   char color; // (R)ed (B)lue (G)reen (Y)ellow (X)Wild
   /**
     * Construct a card
     * @param value Value of the card 0-9 or special: -1 reverse -2 draw2 -3 skip -4 draw 4
     * @param color Color of the card, (R)ed (B)lue (G)reen (Y)ellow (X)Wild
     */
   public Card(int value, char color) {
      this.value = value;
      this.color = color;
   }
   /**
     * setColor - Set the card's color (In case of wild)
     */
   public void setColor(char color) {
      this.color = color;
   }
   /**
     * getColor - Get the card's color
     */
   public char getColor() {
      return color;
   }
   /**
     * getValue - Get the card's value
     */
   public int getValue() {
      return value;
   }
   /**
     * toString - Get the card's human-readable name
     */
   public String toString() {
      String retStr = "";
      switch(color) {
         case 'R':
            retStr+="Red ";
            break;
         case 'G':
            retStr+="Green ";
            break;
         case 'B':
            retStr+="Blue ";
            break;
         case 'Y':
            retStr+="Yellow ";
            break;
         case 'X':
            retStr+="Wild ";
            break;
         default:
            retStr+="Unknown ";
      }
      if(value < 0) {
         switch(value) {
            case -1:
               retStr+="Reverse";
               break;
            case -2:
               retStr+="Draw 2";
               break;
            case -3:
               retStr+="Skip";
               break;
            case -4:
               retStr+="Draw 4";
               break;
            case -5:
               retStr+="(Wild)";
            default:
               break;
         }
      } else {
         retStr+=(""+value);
      }
      return retStr;
   }
   /**
     * toFileString - Get computer-friendly name of the card
     */
   public String toFileString() {
      String retStr = ""+color;
      switch(value) {
         case -1:
            retStr+="_r";
            break;
         case -2:
            retStr+="_d2";
            break;
         case -3:
            retStr+="_s";
            break;
         case -4:
            retStr+="_d4";
            break;
         case -5:
            break;
         default:
            retStr+=("_"+value);
            break;
      }
      return retStr;
   }
   /**
     * compareTo - Used for sorting
     */
   public int compareTo(Object comparableO) {
      Card comparable = (Card)comparableO;
      ArrayList<Character> colorOrder = new ArrayList<Character>();
      colorOrder.add('R');
      colorOrder.add('Y');
      colorOrder.add('G');
      colorOrder.add('B');
      colorOrder.add('X');
      if(colorOrder.indexOf(comparable.getColor()) < colorOrder.indexOf(this.getColor())) {
         return 1;
      } else if (colorOrder.indexOf(comparable.getColor()) > colorOrder.indexOf(this.getColor())) {
         return -1;
      } else {
         if(comparable.getValue() < this.getValue()) {
            return 1;
         } else if (comparable.getValue() > this.getValue()) {
            return -1;
         } else {
            return 0;
         }
      }
   }
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   } 
}