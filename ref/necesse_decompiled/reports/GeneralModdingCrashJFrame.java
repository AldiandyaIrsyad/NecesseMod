package necesse.reports;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

public class GeneralModdingCrashJFrame extends JFrame {
   public static void main(String[] var0) {
      new GeneralModdingCrashJFrame(new CrashReportData(Collections.singletonList(new RuntimeException()), (Client)null, (Server)null, "Test"));
   }

   public GeneralModdingCrashJFrame(CrashReportData var1) {
      super("Necesse modded crash report");
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
      String var5 = getLocale("ui", "moddederror", "An error occurred while running mods.");
      var4.setText("<html><div>" + var5 + "</div></html>");
      JPanel var6;
      var2.add(var6 = new JPanel());
      var6.setLayout(new BoxLayout(var6, 3));
      JTextArea var7 = new JTextArea();
      var7.setEditable(false);
      var6.add(new JScrollPane(var7));
      var6.setPreferredSize(new Dimension(400, 200));
      var7.setText(var1.getFullReport((File)null));
      JPanel var8 = new JPanel();
      var8.setLayout(new BoxLayout(var8, 2));
      var8.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
      var8.setMaximumSize(new Dimension(32767, 0));
      JButton var9;
      var8.add(var9 = new JButton(getLocale("ui", "moderrordisableall", "Disable all mods")));
      var8.add(Box.createRigidArea(new Dimension(5, 0)));
      var9.addActionListener((var1x) -> {
         ArrayList var2 = new ArrayList();
         Iterator var3 = ModLoader.getAllMods().iterator();

         while(var3.hasNext()) {
            LoadedMod var4 = (LoadedMod)var3.next();
            ModListData var5 = new ModListData(var4);
            var5.enabled = false;
            var2.add(var5);
         }

         ModLoader.saveModListSettings(var2);
         this.dispose();
      });
      JButton var10;
      var8.add(var10 = new JButton(getLocale("ui", "closebutton", "Close")));
      var6.add(var8);
      var10.addActionListener((var1x) -> {
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

   private static String getLocale(Supplier<String> var0, String var1) {
      try {
         return ((String)var0.get()).replace("\n", "<br>");
      } catch (Exception var3) {
         return var1.replace("\n", "<br>");
      }
   }

   private static String getLocale(String var0, String var1, String var2) {
      return getLocale(() -> {
         return Localization.translate(var0, var1);
      }, var2);
   }
}
