/**
  * Message - Communication format between a server and client.
  * @author - ngiano
  * @version 4.6.20
  */
import java.util.ArrayList;
import java.io.Serializable;
public class Message implements Serializable {
   String command;
   Object content, moreContent;
   /**
     * Message - Construct a new message
     * @param command - Command of the message. "CHAT", "DRAW", "PLAY", "CONFIRM" etc.
     */
   public Message(String command) {
      this.command = command;
      this.content = null;
      System.out.println("MESSAGE "+command);
   }
   /**
     * Message - Construct a new message
     * @param command - Command of the message. "CHAT", "DRAW", "PLAY", "CONFIRM" etc.
     * @param content - Content of the message, can be blank. "Hello", null, Card, etc.
     */
   public Message(String command, Object content) {
      this.command = command;
      if(content instanceof String) {
         String s = (String)content;
         s.concat("");
         this.content = s;
         System.out.println(s);
      }
      else if(content instanceof Deck) {
         Deck d = (Deck)content;
         try {
            this.content = d.clone();
         } catch(CloneNotSupportedException cnse) {}
      }
      else if(content instanceof Card) {
         Card c = (Card)content;
         try {
            this.content = c.clone();
         } catch(CloneNotSupportedException cnse) {}
      }
      else if(content instanceof ArrayList) {
         ArrayList c = (ArrayList)content;
         this.content = c.clone();
      } else {
         this.content = content;
      }
      System.out.println("MESSAGE "+command+" "+content.getClass());
   }
   
   /**
     * Message - Construct a new message
     * @param command - Command of the message. "CHAT", "DRAW", "PLAY", "CONFIRM" etc.
     * @param content - Content of the message, can be blank. "Hello", null, Card, etc.
     * @param moreContent - Content of the message, when 1 isnt enough
     */
   public Message(String command, Object content, Object moreContent) {
      this.command = command;
      if(content instanceof String) {
         String s = (String)content;
         s.concat("");
         this.content = s;
      }
      else if(content instanceof Deck) {
         Deck d = (Deck)content;
         try {
            this.content = d.clone();
         } catch(CloneNotSupportedException cnse) {}
      }
      else if(content instanceof Card) {
         Card c = (Card)content;
         try {
            this.content = c.clone();
         } catch(CloneNotSupportedException cnse) {}
      }
      else if(content instanceof ArrayList) {
         ArrayList c = (ArrayList)content;
         this.content = c.clone();
      } else {
         this.content = content;
      }
      this.moreContent = moreContent;
      System.out.println("MESSAGE "+command+" "+content.getClass());
   }
   
   public String getCommand() {
      return command;
   }
   
   public Object getContent() {
      return content;
   }
   
   public Object getMoreContent() {
      return moreContent;
   }
   
}