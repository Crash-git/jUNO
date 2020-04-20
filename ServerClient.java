import java.io.*;
import java.net.*;
/**
  * ServerClient - main thread task for each client in the server
  * @author - ngiano
  * @version - 4.7.20
  */
class ServerClient extends Thread {
      private Socket socket;
      private ObjectInputStream in = null;
      private ObjectOutputStream out = null;
      private Player p = null;
      
      /**
        * ServerClient - Create a new client with a blank player
        * @param socket - Socket for this client
        */
      public ServerClient(Socket socket) {
         try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            p = new Player();
         } catch (IOException ioe) {
            System.out.println("IO error in initiating serverclient");
         } 
         this.socket = socket;
      }
      
      /**
        * ServerClient - Create a new client with a named player
        * @param socket - Socket for this client
        * @param pname - Name for the player
        */
      public ServerClient(Socket socket, String pname) {
         try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            p = new Player(pname);
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
               System.out.println("IO Error in reading client message");
            } catch (ClassNotFoundException cnfe) {
               System.out.println("Message class not found");
            }
            String command = msg.command;
            //*** INBOUND COMMANDS ***
            switch(command) {
               case "SNAME"://SET NAME - ASSOCIATED OBEJCT: String
                  //Process object
                  String s = (String)msg.context;
                  //Execute
                  p.setName(s);
                  //Return OK
                  sendOut(new Message("OK",null));
               case "DRAW"://DRAW CARD - ASSOCIATED OBJECT: null
               case "HAND"://REQUEST HAND - ASSOCIATED OBJECT: null
               case "COLORP"://COLOR PICK - ASSOCIATED OBJECT: char
               case "PLAY"://PLAY CARD - ASSOCIATED OBJECT: Card
               case "CHAT"://SEND CHAT - ASSOCIATED OBJECT: Message(String destination, String message)
               case "UNO"://DECLARE UNO - ASSOCIATED OBJECT: ArrayList<Card>
               case "UPDATE"://REQUEST UPDATE - ASSOCIATED OBJECT: null
               case "ABORT"://ABORT aka DISCONNECT - ASSOCIATED OBJECT: null
            }
         }
      }
      /**
        * sendOut - Send out a message to the client on the other end
        * @param msg - Message object containing the message
        * @returns boolean - Successfully sent message
        */
      public boolean sendOut(Message msg) {
         try {
               out.writeObject(msg);
         } catch (IOException ioe) {
            System.out.println("IO Error in sending client message");
            return false;
         } catch (ClassNotFoundException cnfe) {
            System.out.println("Message class not found");
            return false;
         }
         return true;
      }
      
      /**
        * getPlayer - Get player
        */
      public Player getPlayer() {
         return p;
      }
}
