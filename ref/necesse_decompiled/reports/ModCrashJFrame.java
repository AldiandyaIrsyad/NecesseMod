package necesse.reports;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import necesse.engine.util.GameUtils;

public class ModCrashJFrame extends JFrame {
   public static void main(String[] var0) {
      new ModCrashJFrame((List)null, new Throwable[]{new RuntimeException()});
   }

   public ModCrashJFrame(List<LoadedMod> var1, Throwable... var2) {
      super("");
      String var3 = var1 != null && !var1.isEmpty() ? GameUtils.join((LoadedMod[])var1.toArray(new LoadedMod[0]), LoadedMod::getModNameString, ", ", " & ") : "N/A";
      this.setTitle((var1 != null && var1.size() > 1 ? var3 : "Mod") + " crash");
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
      String var7 = getLocale(() -> {
         return Localization.translate("ui", "modrunerror", "mod", var3);
      }, "An error occurred while running mods: " + var3 + "\nYou can try to contact the author with the below log, or disable the mod.");
      var6.setText("<html><div>" + var7 + "</div></html>");
      JPanel var8;
      var4.add(var8 = new JPanel());
      var8.setLayout(new BoxLayout(var8, 3));
      JTextArea var9 = new JTextArea();
      var9.setEditable(false);
      var8.add(new JScrollPane(var9));
      var8.setPreferredSize(new Dimension(400, 200));

      try {
         StringWriter var10 = new StringWriter();

         try {
            var10.write("Mods: " + var3 + "\n\n");
            Throwable[] var11 = var2;
            int var12 = var2.length;
            int var13 = 0;

            while(true) {
               if (var13 >= var12) {
                  var10.flush();
                  var9.setText(var10.toString());
                  break;
               }

               Throwable var14 = var11[var13];
               var14.printStackTrace(new PrintWriter(var10));
               ++var13;
            }
         } catch (Throwable var16) {
            try {
               var10.close();
            } catch (Throwable var15) {
               var16.addSuppressed(var15);
            }

            throw var16;
         }

         var10.close();
      } catch (IOException var17) {
         var9.setText("Error writing error log");
      }

      JPanel var18 = new JPanel();
      var18.setLayout(new BoxLayout(var18, 2));
      var18.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
      var18.setMaximumSize(new Dimension(32767, 0));
      JButton var19;
      if (var1 != null && !var1.isEmpty()) {
         var18.add(var19 = new JButton(getLocale("ui", "moderrordisablethis", "Disable the mod")));
         var18.add(Box.createRigidArea(new Dimension(5, 0)));
         var19.addActionListener((var2x) -> {
            ArrayList var3 = new ArrayList();

            ModListData var6;
            for(Iterator var4 = ModLoader.getAllMods().iterator(); var4.hasNext(); var3.add(var6)) {
               LoadedMod var5 = (LoadedMod)var4.next();
               var6 = new ModListData(var5);
               if (var1.stream().anyMatch((var1x) -> {
                  return var5 == var1x;
               })) {
                  var6.enabled = false;
               }
            }

            ModLoader.saveModListSettings(var3);
            this.dispose();
         });
      }

      var18.add(var19 = new JButton(getLocale("ui", "moderrordisableall", "Disable all mods")));
      var18.add(Box.createRigidArea(new Dimension(5, 0)));
      var19.addActionListener((var1x) -> {
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
      JButton var20;
      var18.add(var20 = new JButton(getLocale("ui", "closebutton", "Close")));
      var8.add(var18);
      var20.addActionListener((var1x) -> {
         this.dispose();
      });
      this.setContentPane(var4);
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
