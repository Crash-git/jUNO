
/**
 * Chat client to be pasted into a JFrame
 * @author Collin Lavergne
 * @version 1.3.0
 * ISTE 121
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Chat extends JPanel {
   private JTextArea chat;
   private JTextField chatInput;
   private JButton send;
   private JScrollPane scroll;
   private JPanel chatCombined;

   private String username;

   public Chat(String _username) {
        setLayout(new BorderLayout());

        username = _username;

        send = new JButton("Send");
        chatCombined = new JPanel();

        // send.addActionListener(new ActionListener() {
        //     public void actionPerformed(ActionEvent ae) {
        //     send.setEnabled(false);
        //     sendMessage(username + ": " + chatInput.getText());

        //     chatInput.setText("");
        //     chatInput.requestFocus();
        //     send.setEnabled(true);
        //     }
        // });

        chatInput = new JTextField(14);
        chat = new JTextArea(30, 50);
        chat.setEditable(false);

        scroll = new JScrollPane(chat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        chatCombined.setLayout(new BorderLayout(5, 5));
        chatCombined.add(scroll, BorderLayout.NORTH);
        chatCombined.add(chatInput, BorderLayout.CENTER);
        chatCombined.add(send, BorderLayout.SOUTH);

        add(chatCombined, BorderLayout.CENTER);

        // sendMessage(username + " connected");
    }
   
    // public void sendMessage(String input) {
        
    // }

    public void updateChat(String readout) {
        chat.append("\n" + readout);
    }

    public void clearChat() {
        chat.setText("");
    }

    public String getUsername() {
        return username;
    }

    public JButton getSendReference() {
        return send;
    }

    public String readInput() {
        String input = chatInput.getText();
        chatInput.setText("");
        chatInput.requestFocus();
        return input;
    }
}