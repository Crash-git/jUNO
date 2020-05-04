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
   }
   /**
     * Message - Construct a new message
     * @param command - Command of the message. "CHAT", "DRAW", "PLAY", "CONFIRM" etc.
     * @param content - Content of the message, can be blank. "Hello", null, Card, etc.
     */
   public Message(String command, Object content) {
      this.command = command;
      this.content = content;
   }
   
   /**
     * Message - Construct a new message
     * @param command - Command of the message. "CHAT", "DRAW", "PLAY", "CONFIRM" etc.
     * @param content - Content of the message, can be blank. "Hello", null, Card, etc.
     * @param moreContent - Content of the message, when 1 isnt enough
     */
   public Message(String command, Object content, Object moreContent) {
      this.command = command;
      this.content = content;
      this.moreContent = moreContent;
   }
   
   /**
     * getCommand - Get the command of the message
     * @return command - Message command
     */
   public String getCommand() {
      return command;
   }
   /**
     * getContent - Get the content of the message
     * @return content - Message content
     */
   public Object getContent() {
      return content;
   }
   /**
     * getMoreContent - Get the additional content of the message
     * @return moreContent - More message content
     */
   public Object getMoreContent() {
      return moreContent;
   }
   
}