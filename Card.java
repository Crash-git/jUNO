import java.util.*;
import javax.swing.ImageIcon;
import java.io.Serializable;
/**
  * Card - Core structure class for a card. Contains its properties
  * @author - ngiano
  * @version 4.3.20
  */
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
     * @param color - Card color
     */
   public void setColor(char color) {
      this.color = color;
   }
   /**
     * getColor - Get the card's color
     * @return color - Card color
     */
   public char getColor() {
      return color;
   }
   /**
     * getValue - Get the card's value
     * @return value - Card value
     */
   public int getValue() {
      return value;
   }
   /**
     * toString - Get the card's human-readable name
     * @return str - Card readable name
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
     * @return str - File-readable string
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
     * @param comparableO - Card to compare
     * @return int - Compare (-1, 0, 1) for sorting
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
   /**
     * get referenced card graphics for each card. Needed for jar compilation
     * @param c Card to get graphic of
     * @return ImageIcon graphic of the card
     */
   public static ImageIcon getCardGraphic(Card c) {
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      switch(c.getColor()) {
         case 'X':
            switch(c.getValue()) {
               case -5:
                  return new ImageIcon(classLoader.getResource("resources/x.png"));
               case -4:
                  return new ImageIcon(classLoader.getResource("resources/x_d4.png"));
            }
         case 'R':
            switch(c.getValue()) {
               case 0:
                  return new ImageIcon(classLoader.getResource("resources/r_0.png"));
               case 1:
                  return new ImageIcon(classLoader.getResource("resources/r_1.png"));
               case 2:
                  return new ImageIcon(classLoader.getResource("resources/r_2.png"));
               case 3:
                  return new ImageIcon(classLoader.getResource("resources/r_3.png"));
               case 4:
                  return new ImageIcon(classLoader.getResource("resources/r_4.png"));
               case 5:
                  return new ImageIcon(classLoader.getResource("resources/r_5.png"));
               case 6:
                  return new ImageIcon(classLoader.getResource("resources/r_6.png"));
               case 7:
                  return new ImageIcon(classLoader.getResource("resources/r_7.png"));
               case 8:
                  return new ImageIcon(classLoader.getResource("resources/r_8.png"));
               case 9:
                  return new ImageIcon(classLoader.getResource("resources/r_9.png"));
               case -1:
                  return new ImageIcon(classLoader.getResource("resources/r_r.png"));
               case -2:
                  return new ImageIcon(classLoader.getResource("resources/r_d2.png"));
               case -3:
                  return new ImageIcon(classLoader.getResource("resources/r_s.png"));
               case -4:
                  return new ImageIcon(classLoader.getResource("resources/r_d4.png"));
               case -5:
                  return new ImageIcon(classLoader.getResource("resources/r.png"));
            }
         case 'B':
            switch(c.getValue()) {
               case 0:
                  return new ImageIcon(classLoader.getResource("resources/b_0.png"));
               case 1:
                  return new ImageIcon(classLoader.getResource("resources/b_1.png"));
               case 2:
                  return new ImageIcon(classLoader.getResource("resources/b_2.png"));
               case 3:
                  return new ImageIcon(classLoader.getResource("resources/b_3.png"));
               case 4:
                  return new ImageIcon(classLoader.getResource("resources/b_4.png"));
               case 5:
                  return new ImageIcon(classLoader.getResource("resources/b_5.png"));
               case 6:
                  return new ImageIcon(classLoader.getResource("resources/b_6.png"));
               case 7:
                  return new ImageIcon(classLoader.getResource("resources/b_7.png"));
               case 8:
                  return new ImageIcon(classLoader.getResource("resources/b_8.png"));
               case 9:
                  return new ImageIcon(classLoader.getResource("resources/b_9.png"));
               case -1:
                  return new ImageIcon(classLoader.getResource("resources/b_r.png"));
               case -2:
                  return new ImageIcon(classLoader.getResource("resources/b_d2.png"));
               case -3:
                  return new ImageIcon(classLoader.getResource("resources/b_s.png"));
               case -4:
                  return new ImageIcon(classLoader.getResource("resources/b_d4.png"));
               case -5:
                  return new ImageIcon(classLoader.getResource("resources/b.png"));
            }
         case 'Y':
            switch(c.getValue()) {
               case 0:
                  return new ImageIcon(classLoader.getResource("resources/y_0.png"));
               case 1:
                  return new ImageIcon(classLoader.getResource("resources/y_1.png"));
               case 2:
                  return new ImageIcon(classLoader.getResource("resources/y_2.png"));
               case 3:
                  return new ImageIcon(classLoader.getResource("resources/y_3.png"));
               case 4:
                  return new ImageIcon(classLoader.getResource("resources/y_4.png"));
               case 5:
                  return new ImageIcon(classLoader.getResource("resources/y_5.png"));
               case 6:
                  return new ImageIcon(classLoader.getResource("resources/y_6.png"));
               case 7:
                  return new ImageIcon(classLoader.getResource("resources/y_7.png"));
               case 8:
                  return new ImageIcon(classLoader.getResource("resources/y_8.png"));
               case 9:
                  return new ImageIcon(classLoader.getResource("resources/y_9.png"));
               case -1:
                  return new ImageIcon(classLoader.getResource("resources/y_r.png"));
               case -2:
                  return new ImageIcon(classLoader.getResource("resources/y_d2.png"));
               case -3:
                  return new ImageIcon(classLoader.getResource("resources/y_s.png"));
               case -4:
                  return new ImageIcon(classLoader.getResource("resources/y_d4.png"));
               case -5:
                  return new ImageIcon(classLoader.getResource("resources/y.png"));
            }
         case 'G':
            switch(c.getValue()) {
               case 0:
                  return new ImageIcon(classLoader.getResource("resources/g_0.png"));
               case 1:
                  return new ImageIcon(classLoader.getResource("resources/g_1.png"));
               case 2:
                  return new ImageIcon(classLoader.getResource("resources/g_2.png"));
               case 3:
                  return new ImageIcon(classLoader.getResource("resources/g_3.png"));
               case 4:
                  return new ImageIcon(classLoader.getResource("resources/g_4.png"));
               case 5:
                  return new ImageIcon(classLoader.getResource("resources/g_5.png"));
               case 6:
                  return new ImageIcon(classLoader.getResource("resources/g_6.png"));
               case 7:
                  return new ImageIcon(classLoader.getResource("resources/g_7.png"));
               case 8:
                  return new ImageIcon(classLoader.getResource("resources/g_8.png"));
               case 9:
                  return new ImageIcon(classLoader.getResource("resources/g_9.png"));
               case -1:
                  return new ImageIcon(classLoader.getResource("resources/g_r.png"));
               case -2:
                  return new ImageIcon(classLoader.getResource("resources/g_d2.png"));
               case -3:
                  return new ImageIcon(classLoader.getResource("resources/g_s.png"));
               case -4:
                  return new ImageIcon(classLoader.getResource("resources/g_d4.png"));
               case -5:
                  return new ImageIcon(classLoader.getResource("resources/g.png"));
            }
      }
      return new ImageIcon(classLoader.getResource("resources/back.gif"));
   }
   /**
     * clone
     * @return clone
     */
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   } 
}