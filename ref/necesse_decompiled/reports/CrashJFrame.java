package necesse.reports;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

public class CrashJFrame extends JFrame {
   private JTextArea console;
   private JScrollPane consoleScroll;
   private JButton dontSendReport;
   private JButton sendReport;

   public static void main(String[] var0) {
      new CrashJFrame(new CrashReportData(Collections.singletonList(new Exception()), (Client)null, (Server)null, "TestCrash"));
   }

   public CrashJFrame(CrashReportData var1) {
      super("Necesse crash report");
      JPanel var2;
      this.add(var2 = new JPanel());
      var2.setLayout(new BoxLayout(var2, 1));
      var2.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      JPanel var3;
      var2.add(var3 = new JPanel());
      var3.setLayout(new BoxLayout(var3, 2));
      var3.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
      JLabel var4;
      var3.add(var4 = new JLabel());
      String var5 = getLocale("ui", "crashsorry", "Sorry! Looks like the game crashed :(\nPlease help the development by sending the crash report!");
      var4.setText("<html><div>" + var5 + "</div></html>");
      JPanel var6;
      var2.add(var6 = new JPanel());
      var6.setLayout(new BoxLayout(var6, 3));
      this.console = new JTextArea();
      this.console.setEditable(false);
      var6.add(this.consoleScroll = new JScrollPane(this.console));
      var6.setPreferredSize(new Dimension(400, 200));
      this.console.setText(var1.getFullReport((File)null));
      JPanel var7 = new JPanel();
      var7.setLayout(new BoxLayout(var7, 2));
      var7.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
      var7.setMaximumSize(new Dimension(32767, 0));
      var7.add(this.sendReport = new JButton(getLocale("ui", "sendreport", "Send report")));
      var7.add(Box.createRigidArea(new Dimension(5, 0)));
      var7.add(this.dontSendReport = new JButton(getLocale("ui", "dontsendreport", "Don't send report")));
      var6.add(var7);
      this.sendReport.addActionListener((var2x) -> {
         DetailsFrame var3 = new DetailsFrame(this, var1, getLocale("ui", "crashdetailshere", "Type details here..."));
         var3.setVisible(true);
         var3.requestFocus();
         this.dispose();
      });
      this.dontSendReport.addActionListener((var1x) -> {
         this.dispose();
      });
      this.setContentPane(var2);
      this.setPreferredSize(new Dimension(600, 450));
      this.setDefaultCloseOperation(2);
      this.pack();
      this.setLocationRelativeTo((Component)null);
      this.setVisible(true);
      this.requestFocus();
   }

   private static String getLocale(String var0, String var1, String var2) {
      try {
         return Localization.translate(var0, var1).replace("\n", "<br>");
      } catch (Exception var4) {
         return var2.replace("\n", "<br>");
      }
   }

   private static class DetailsFrame extends JFrame {
      private JTextArea console;
      private JScrollPane consoleScroll;
      private JButton sendReport;

      public DetailsFrame(Frame var1, CrashReportData var2, String var3) {
         super("Necesse crash report");
         JPanel var4;
         this.add(var4 = new JPanel());
         var4.setLayout(new BoxLayout(var4, 1));
         var4.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
         JPanel var5;
         var4.add(var5 = new JPanel());
         var5.setLayout(new BoxLayout(var5, 2));
         var5.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
         JLabel var6;
         var5.add(var6 = new JLabel());
         String var7 = CrashJFrame.getLocale("ui", "crashgivedetails", "Please give details about what you were doing when the crash happened or any other details you think might be helpful!");
         var6.setText("<html><span>" + var7 + "</span>");
         JPanel var8;
         var4.add(var8 = new JPanel());
         var8.setLayout(new BoxLayout(var8, 3));
         this.console = new JTextArea();
         this.console.setEditable(true);
         var8.add(this.consoleScroll = new JScrollPane(this.console));
         var8.setPreferredSize(new Dimension(400, 200));
         DefaultStyledDocument var9 = new DefaultStyledDocument();
         var9.setDocumentFilter(new DocumentSizeFilter(500));
         this.console.setDocument(var9);
         this.console.setText(var3);
         JPanel var10 = new JPanel();
         var10.setLayout(new BoxLayout(var10, 2));
         var10.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
         var10.setMaximumSize(new Dimension(32767, 0));
         var10.add(this.sendReport = new JButton(CrashJFrame.getLocale("ui", "sendreport", "Send report")));
         var8.add(var10);
         this.sendReport.addActionListener((var2x) -> {
            SendFrame var3 = new SendFrame(this, var2, this.console.getText());
            var3.setVisible(true);
            var3.requestFocus();
            this.dispose();
         });
         this.setContentPane(var4);
         this.setPreferredSize(new Dimension(400, 300));
         this.setDefaultCloseOperation(2);
         this.pack();
         this.setLocationRelativeTo(var1);
      }
   }

   private static class DocumentSizeFilter extends DocumentFilter {
      public int maxChars;

      public DocumentSizeFilter(int var1) {
         this.maxChars = var1;
      }

      public void insertString(DocumentFilter.FilterBypass var1, int var2, String var3, AttributeSet var4) throws BadLocationException {
         int var5 = var1.getDocument().getLength();
         if (var5 + var3.length() <= this.maxChars) {
            super.insertString(var1, var2, var3, var4);
         } else {
            int var6 = this.maxChars - var5;
            super.insertString(var1, var2, var3.substring(0, var6), var4);
            Toolkit.getDefaultToolkit().beep();
         }

      }

      public void replace(DocumentFilter.FilterBypass var1, int var2, int var3, String var4, AttributeSet var5) throws BadLocationException {
         int var6 = var1.getDocument().getLength();
         if (var6 + var4.length() - var3 <= this.maxChars) {
            super.replace(var1, var2, var3, var4, var5);
         } else {
            int var7 = this.maxChars - var6;
            String var8 = var4.substring(0, var7);
            super.replace(var1, var2, var3, var8, var5);
            Toolkit.getDefaultToolkit().beep();
         }

      }
   }

   private static class ThankYouFrame extends JFrame {
      public ThankYouFrame(Frame var1) {
         super("Necesse crash report");
         JPanel var2 = new JPanel();
         var2.setLayout(new BoxLayout(var2, 2));
         var2.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
         JLabel var3;
         var2.add(var3 = new JLabel());
         var3.setHorizontalAlignment(0);
         String var4 = CrashJFrame.getLocale("ui", "sendreportthanks", "Thank you!");
         var3.setText("<html><span>" + var4 + "</span>");
         JPanel var5 = new JPanel();
         var5.setLayout(new BoxLayout(var5, 2));
         var5.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
         var5.setMaximumSize(new Dimension(32767, 0));
         JButton var6;
         var5.add(var6 = new JButton(CrashJFrame.getLocale("ui", "closebutton", "Close")));
         var6.addActionListener((var1x) -> {
            this.dispose();
         });
         var6.setHorizontalAlignment(0);
         this.getContentPane().add(var2, "Center");
         this.getContentPane().add(var5, "Last");
         this.setPreferredSize(new Dimension(300, 150));
         this.setDefaultCloseOperation(2);
         this.pack();
         this.setLocationRelativeTo(var1);
      }
   }

   private static class RetryFrame extends JFrame {
      public RetryFrame(Frame var1, String var2, CrashReportData var3, String var4) {
         super("Necesse crash report");
         JPanel var5 = new JPanel();
         var5.setLayout(new BoxLayout(var5, 2));
         var5.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
         JLabel var6;
         var5.add(var6 = new JLabel());
         var6.setHorizontalAlignment(0);
         var6.setText("<html><span>" + var2 + "</span>");
         JPanel var7 = new JPanel();
         var7.setLayout(new BoxLayout(var7, 2));
         var7.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
         var7.setMaximumSize(new Dimension(32767, 0));
         JButton var8;
         var7.add(var8 = new JButton(CrashJFrame.getLocale("ui", "sendreportretry", "Retry")));
         var8.addActionListener((var3x) -> {
            SendFrame var4x = new SendFrame(this, var3, var4);
            var4x.setVisible(true);
            var4x.requestFocus();
            this.dispose();
         });
         var7.add(Box.createRigidArea(new Dimension(10, 0)));
         JButton var9;
         var7.add(var9 = new JButton(CrashJFrame.getLocale("ui", "closebutton", "Close")));
         var9.addActionListener((var1x) -> {
            this.dispose();
         });
         this.getContentPane().add(var5, "Center");
         this.getContentPane().add(var7, "Last");
         this.setPreferredSize(new Dimension(300, 200));
         this.setDefaultCloseOperation(2);
         this.pack();
         this.setLocationRelativeTo(var1);
      }
   }

   private static class SendFrame extends JFrame {
      public SendFrame(Frame var1, CrashReportData var2, String var3) {
         super("Necesse crash report");
         JLabel var4 = new JLabel();
         var4.setHorizontalAlignment(0);
         var4.setVerticalAlignment(0);
         var4.setText("<html><span>Sending report...</span>");
         this.getContentPane().add(var4, "Center");
         this.setPreferredSize(new Dimension(300, 100));
         final AtomicBoolean var5 = new AtomicBoolean(false);
         (new Thread(() -> {
            String var4 = ReportUtils.sendCrashReport(var2, var3);
            if (!var5.get()) {
               if (var4 != null) {
                  var4 = var4.replace("\n", "</br>");
                  RetryFrame var5x = new RetryFrame(this, var4, var2, var3);
                  var5x.setVisible(true);
                  var5x.requestFocus();
               } else {
                  ThankYouFrame var6 = new ThankYouFrame(this);
                  var6.setVisible(true);
                  var6.requestFocus();
               }
            }

            this.dispose();
         })).start();
         this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent var1) {
               var5.set(true);
            }
         });
         this.setDefaultCloseOperation(2);
         this.pack();
         this.setLocationRelativeTo(var1);
      }
   }
}
