package necesse.reports;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NoticeJFrame extends JFrame {
   public static void main(String[] var0) {
      new NoticeJFrame(300, "This is just a test\nSome next line text");
   }

   public NoticeJFrame(int var1, String var2) {
      super("Necesse notice");
      JPanel var3;
      this.add(var3 = new JPanel());
      var3.setLayout(new BoxLayout(var3, 1));
      var3.setBorder(BorderFactory.createEmptyBorder(16, 8, 16, 8));
      JPanel var4;
      var3.add(var4 = new JPanel());
      var4.setLayout(new BoxLayout(var4, 2));
      var4.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
      JLabel var5;
      var4.add(var5 = new JLabel());
      var5.setHorizontalAlignment(0);
      String var6 = var2.replace("\n", "<br>");
      var5.setText("<html><body style=\"width: " + (var1 - 120) + "px\"><div style=\"text-align: center\">" + var6 + "</div></body></html>");
      JPanel var7 = new JPanel();
      var7.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
      var7.setMaximumSize(new Dimension(32767, 50));
      JButton var8;
      var7.add(var8 = new JButton("Close"));
      var8.addActionListener((var1x) -> {
         this.dispose();
      });
      var3.add(var7);
      this.setContentPane(var3);
      this.setDefaultCloseOperation(2);
      this.pack();
      this.setPreferredSize(new Dimension(var1, var5.getHeight() + 160));
      this.setLocationRelativeTo((Component)null);
      this.setVisible(true);
      this.requestFocus();
   }
}
