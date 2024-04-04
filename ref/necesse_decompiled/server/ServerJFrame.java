package necesse.server;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ServerJFrame extends JFrame {
   private JTextArea console;
   private JScrollPane consoleScroll;
   private JTextField input;
   private JButton enter;
   private JLabel infoLabel;
   private int lastMessage;
   private ArrayList<String> commands;
   private ServerWrapper serverWrapper;
   private Server server;
   private List<AutoComplete> autoCompleteList;
   private int autoCompleteIndex;
   private String autocompleteStart;

   public ServerJFrame(ServerWrapper var1) throws HeadlessException {
      super("Necessenecesse/server");
      this.serverWrapper = var1;
      JPanel var2;
      this.add(var2 = new JPanel());
      var2.setLayout(new BoxLayout(var2, 2));
      var2.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      JPanel var3;
      var2.add(var3 = new JPanel());
      var3.setLayout(new BoxLayout(var3, 3));
      var3.setPreferredSize(new Dimension(250, 32767));
      var3.setMinimumSize(new Dimension(200, -32768));
      var3.setMaximumSize(new Dimension(250, 32767));
      var3.add(this.infoLabel = new JLabel());
      JPanel var4;
      var2.add(var4 = new JPanel());
      var4.setLayout(new BoxLayout(var4, 3));
      this.console = new JTextArea();
      this.console.setEditable(false);
      var4.add(this.consoleScroll = new JScrollPane(this.console));
      var4.setPreferredSize(new Dimension(400, 200));
      JPanel var5 = new JPanel();
      var5.setLayout(new BoxLayout(var5, 2));
      var5.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
      var5.setMaximumSize(new Dimension(32767, 0));
      var5.add(Box.createHorizontalGlue());
      var5.add(this.input = new JTextField());
      var5.add(Box.createRigidArea(new Dimension(10, 0)));
      var5.add(this.enter = new JButton("Enter"));
      var4.add(var5);
      this.setContentPane(var2);
      this.setPreferredSize(new Dimension(600, 450));
      this.setDefaultCloseOperation(0);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            if (ServerJFrame.this.server != null) {
               ServerJFrame.this.server.stop((var0) -> {
                  try {
                     Thread.sleep(2000L);
                  } catch (InterruptedException var2) {
                     var2.printStackTrace();
                  }

                  System.exit(0);
               });
            } else {
               System.exit(0);
            }

         }
      });
      this.enter.addActionListener((var1x) -> {
         this.submitCommand(this.input.getText());
      });
      this.input.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent var1) {
            if (var1.getKeyCode() == 10) {
               ServerJFrame.this.submitCommand(ServerJFrame.this.input.getText());
               ServerJFrame.this.autoCompleteIndex = -1;
            } else if (var1.getKeyCode() == 38) {
               if (ServerJFrame.this.lastMessage < ServerJFrame.this.commands.size() - 1) {
                  ServerJFrame.this.lastMessage++;
                  ServerJFrame.this.input.setText((String)ServerJFrame.this.commands.get(ServerJFrame.this.lastMessage));
                  ServerJFrame.this.input.moveCaretPosition(ServerJFrame.this.input.getText().length());
                  var1.consume();
                  ServerJFrame.this.autoCompleteIndex = -1;
               }
            } else if (var1.getKeyCode() == 40) {
               if (ServerJFrame.this.lastMessage >= 0) {
                  ServerJFrame.this.lastMessage--;
                  if (ServerJFrame.this.lastMessage >= 0) {
                     ServerJFrame.this.input.setText((String)ServerJFrame.this.commands.get(ServerJFrame.this.lastMessage));
                  } else {
                     ServerJFrame.this.input.setText("");
                  }

                  ServerJFrame.this.input.moveCaretPosition(ServerJFrame.this.input.getText().length());
                  var1.consume();
                  ServerJFrame.this.autoCompleteIndex = -1;
               }
            } else if (var1.getKeyCode() == 9) {
               if (ServerJFrame.this.server != null) {
                  String var2 = ServerJFrame.this.input.getText();
                  boolean var3 = var2.startsWith("/");
                  if (var3) {
                     var2 = var2.substring(1);
                  }

                  int var4 = var2.indexOf(" ", ServerJFrame.this.input.getCaretPosition() - (var3 ? 1 : 0));
                  if (ServerJFrame.this.autoCompleteIndex < 0) {
                     ServerJFrame.this.autocompleteStart = var2.substring(0, var4 == -1 ? var2.length() : var4);
                     ServerJFrame.this.autoCompleteList = ServerJFrame.this.server.commandsManager.autocomplete(new ParsedCommand(ServerJFrame.this.autocompleteStart), (ServerClient)null);
                     ServerJFrame.this.autoCompleteIndex = 0;
                  } else {
                     ServerJFrame.this.autoCompleteIndex++;
                  }

                  if (!ServerJFrame.this.autoCompleteList.isEmpty()) {
                     String var5 = ((AutoComplete)ServerJFrame.this.autoCompleteList.get(ServerJFrame.this.autoCompleteIndex % ServerJFrame.this.autoCompleteList.size())).getFullCommand(ServerJFrame.this.autocompleteStart);
                     String var6 = var2.substring(var4 == -1 ? var2.length() : var4);
                     ServerJFrame.this.input.setText((var3 ? "/" : "") + var5 + var6);
                     ServerJFrame.this.input.setCaretPosition(var5.length() + (var3 ? 1 : 0));
                  }

                  var1.consume();
               }
            } else {
               ServerJFrame.this.autoCompleteIndex = -1;
            }

         }
      });
      this.pack();
      this.setVisible(true);
      this.setInfoLines(new String[0]);
      this.input.requestFocus();
      this.input.setFocusTraversalKeysEnabled(false);
      this.lastMessage = -1;
      this.commands = new ArrayList();
   }

   public void setServer(Server var1) {
      this.server = var1;
   }

   public void setInfoLines(String[] var1) {
      this.infoLabel.setText("<html><span>---------- Server info ---------<br>" + String.join("<br>", var1) + "</span>");
   }

   public void setInfoLines(List<String> var1) {
      this.setInfoLines((String[])var1.toArray(new String[0]));
   }

   public void submitCommand(String var1) {
      if (!var1.equals("")) {
         this.commands.add(0, var1);
      }

      this.input.setText("");
      this.input.requestFocus();
      this.lastMessage = -1;
      if (var1.equals("clear")) {
         this.clearConsole();
      } else {
         this.serverWrapper.submitCommand(this.server, var1);
      }

   }

   public void clearConsole() {
      this.console.setText((String)null);
      this.console.setCaretPosition(this.console.getText().length());
   }

   public void writeConsole(String var1) {
      this.console.append(var1 + "\n");
      this.console.setCaretPosition(this.console.getText().length());
   }
}
