import java.io.*;
import java.net.*;
/**
  * ClientCommunicator - main point of communication to/from server
  * @author - ngiano
  * @version - 4.7.20
  */
class ClientCommunicator extends Thread {
      private Socket socket;
      private ObjectInputStream in = null;
      private ObjectOutputStream out = null;
      private Player p = null;
      
      /**
        * ServerClient - Create a new client with a blank player
        * @param socket - Socket for this client
        */
      public ClientCommunicator(Socket socket) {
         try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            p = new Player();
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
               case "TURN"://TURN - ASSOCIATED OBJECT: ArrayList<Card>
                  //It's now this client's turn, allow playing/drawingcards
                  //Server delivers hand, in case of any desync
               case "HAND"://RECIEVE HAND - ASSOCIATED OBJECT: ArrayList<Card>
                  //Recieve updated hand (Occurs on card draw (self inflicted, and +2 +4), and inital deal)
               case "COLORP"://COLOR PICK - ASSOCIATED OBJECT: char
                  //Server is requesting client to pick a color
                  //Open message box for selecting a color
                  //Send back to server the selected color
               case "UPDATEALL"://RECIEVE UPDATE - ASSOCIATED OBJECT: Player[]???
                  //Update all players, names and card counts
               case "PILE"://UPDATE DISCARD PILE - ASSOCIATED OBJECT: Card (top card)
                  //Draw new top card on gui, store card data
               case "SMS"://SERVER MESSAGE - ASSOCIATED OBJECT: String
                  //Display a server message on the gui.
               case "WIN"://WIN - ASSOCIATED OBJECT: null
                  //Show the player that they won
               case "LOSE"://LOSE - ASSOCIATED OBJECT: String
                  //Show the player they lost, with a String showing the name of the winner.
               case "ABORT"://ABORT aka DISCONNECT - ASSOCIATED OBJECT: null
               case "OK"://OK - this might honestly be it's own thing, but im putting it here so we know to add it later
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
         }
         return true;
      }
   }
