/**
 * Chat client to be pasted into a JPanel
 * @author Collin Lavergne
 * @version 1.3.0
 * ISTE 121
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ChatPanel extends JPanel {
   private JTextArea chat;
   private JTextField chatInput;
   private JButton send;
   private JScrollPane scroll;
   private JPanel chatCombined;

   private String username;
   public static void main(String[]args) {
      new ChatPanel("T");
   }
   /**
     * ChatPanel - Create a chat panel with stored username
     * @param _username - Username
     */
   public ChatPanel(String _username) {
        setLayout(new BorderLayout());

        username = _username;

        send = new JButton("Send");
        chatCombined = new JPanel();

        JPanel sendBar = new JPanel();
        sendBar.setLayout(new BoxLayout(sendBar,BoxLayout.X_AXIS));
        chatInput = new JTextField(15);
        sendBar.add(chatInput);
        sendBar.add(send);
        
        chat = new JTextArea();
        chat.setEditable(false);

        scroll = new JScrollPane(chat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        chatCombined.setLayout(new BorderLayout(5, 5));
        chatCombined.add(scroll, BorderLayout.CENTER);
        chatCombined.add(sendBar, BorderLayout.SOUTH);

        add(chatCombined, BorderLayout.CENTER);
        // sendMessage(username + " connected");
    }
   /**
     * ChatPanel - Construct a read-only panel
     */
   public ChatPanel() {
        setLayout(new BorderLayout());

        username = "None";

        send = new JButton("Send");
        
        chat = new JTextArea();
        chat.setEditable(false);

        scroll = new JScrollPane(chat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scroll, BorderLayout.CENTER);
    }
    
    /**
      * updateChat - Add a line to the chat
      * @param readout - Line to add
      */
    public void updateChat(String readout) {
        chat.append("\n" + readout);
    }
    /**
      * clearChat - Clears the chat
      */
    public void clearChat() {
        chat.setText("");
    }
   /**
     * getUsername
     * @return username - Username
     */
    public String getUsername() {
        return username;
    }
   /**
     * getSendReference - Get reference to the send button, for adding action events
     * @return send - JButton for sending
     */
    public JButton getSendReference() {
        return send;
    }
   /**
     * readInput - Read the input that will be sent out
     * @return input - Message to be sent out
     */
    public String readInput() {
        String input = chatInput.getText();
        chatInput.setText("");
        chatInput.requestFocus();
        return input;
    }
}