import java.io.*;
import java.net.*;
/**
  * ChatThread - Chat processing on clientside
  * @author - ngiano
  * @version - 4.7.20
  */
class ChatThread extends Thread {
      private Socket socket;
      private ObjectInputStream in = null;
      private ObjectOutputStream out = null;
      
      /**
        * ChatThread - Create a new thread chat processor
        * @param socket - Socket for this client
        */
      public ChatThread(Socket socket) {
         try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
         } catch (IOException ioe) {
            System.out.println("IO error in initiating serverclient");
         } 
         this.socket = socket;
      }
            
      public void run() {
         while (true) {
            Message msg = null;
            try {
               msg = (Message) in.readObject();
            } catch (IOException ioe) {
               System.out.println("IO Error in reading client CHAT message");
            } catch (ClassNotFoundException cnfe) {
               System.out.println("Message class not found");
            }
            String command = msg.command;
            //*** INBOUND COMMANDS ***
            switch(command) {
               case "GCHAT"://GLOBAL CHAT - ASSOCIATED OBEJCT: Message(String origin, String message)
                  //Global Chat message, origin is whoever sent it
               case "PCHAT"://PRIVATE CHAT - ASSOCIATED OBEJCT: Message(String origin, String message)
                  //Private Chat message, origin is whoever sent it
               case "ABORT"://ABORT aka DISCONNECT - ASSOCIATED OBJECT: null
            }
         }
      }
      /**
        * sendOut - Send out a message to the client on the other end
        * @param msg - Message object containing the message = Message("GCHAT/PCHAT",Message(SENDER,TEXT));
        * @returns boolean - Successfully sent message
        */
      public boolean sendOut(Message msg) {
         try {
               out.writeObject(msg);
         } catch (IOException ioe) {
            System.out.println("IO Error in sending client CHAT message");
            return false;
         } catch (ClassNotFoundException cnfe) {
            System.out.println("ChatMessage class not found");
            return false;
         }
         return true;
      }
   }
