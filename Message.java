/**
  * Message - Communication format between a server and client.
  * @author - ngiano
  * @version 4.6.20
  */
public class Message {
   String command;
   Object content;
   
   /**
     * Message - Construct a new message
     * @param command - Command of the message. "CHAT", "DRAW", "PLAY", "CONFIRM" etc.
     * @param content - Content of the message, can be blank. "Hello", null, Card, etc.
     */
   public Message(String command, Object content) {
      this.command = command;
      this.content = content;
   }
   
   public String getCommand() {
      return command;
   }
   
   public Object getContent() {
      return content;
   }
   
}