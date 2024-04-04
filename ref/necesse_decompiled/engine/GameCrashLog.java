package necesse.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModRuntimeException;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.reports.CrashJFrame;
import necesse.reports.CrashReportData;
import necesse.reports.ModCrashJFrame;
import necesse.reports.NoticeJFrame;

public class GameCrashLog {
   private static final String LOG_PATH = "latest-crash.log";

   public GameCrashLog() {
   }

   public static void printCrashLog(Exception var0, Client var1, Server var2, String var3, boolean var4) {
      printCrashLog(Collections.singletonList(var0), var1, var2, var3, var4);
   }

   public static void printCrashLog(List<Throwable> var0, Client var1, Server var2, String var3, boolean var4) {
      File var5 = new File("latest-crash.log");
      System.err.println("Printing crash log to " + var5.getAbsolutePath());

      try {
         CrashReportData var6 = new CrashReportData(var0, var1, var2, var3);
         FileOutputStream var7 = new FileOutputStream(var5);
         PrintStream var8 = new PrintStream(var7);
         var6.printFullReport(var8, var5);
         var7.close();
         if (var1 != null) {
            var1.serverCrashReport = var6;
         }

         if (var4) {
            openCrashFrame(var6);
            Screen.dispose();
         }
      } catch (IOException var9) {
         var9.printStackTrace();
      }

   }

   public static void openCrashFrame(CrashReportData var0) {
      ModRuntimeException[] var1 = (ModRuntimeException[])var0.errors.stream().filter((var0x) -> {
         return var0x instanceof ModRuntimeException;
      }).map((var0x) -> {
         return (ModRuntimeException)var0x;
      }).toArray((var0x) -> {
         return new ModRuntimeException[var0x];
      });
      if (var1.length > 0) {
         ModCrashJFrame var2 = new ModCrashJFrame(Collections.singletonList(var1[0].mod), var1);
         var2.setVisible(true);
         var2.requestFocus();
      } else {
         ArrayList var4 = ModLoader.getResponsibleMods(var0.errors, true);
         if (!var4.isEmpty()) {
            ModCrashJFrame var6 = new ModCrashJFrame(var4, (Throwable[])var0.errors.toArray(new Exception[0]));
            var6.setVisible(true);
            var6.requestFocus();
            return;
         }

         if (checkAnyCause((Iterable)var0.errors, (var0x) -> {
            return var0x instanceof OutOfMemoryError;
         })) {
            NoticeJFrame var3 = new NoticeJFrame(400, Localization.translate("misc", "outofmemory"));
            var3.setVisible(true);
            var3.requestFocus();
         } else {
            CrashJFrame var5 = new CrashJFrame(var0);
            var5.setVisible(true);
            var5.requestFocus();
         }
      }

   }

   public static boolean checkAnyCause(Iterable<Throwable> var0, Predicate<Throwable> var1) {
      Iterator var2 = var0.iterator();

      Throwable var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Throwable)var2.next();
      } while(!checkAnyCause(var3, var1));

      return true;
   }

   public static boolean checkAnyCause(Throwable var0, Predicate<Throwable> var1) {
      if (var1.test(var0)) {
         return true;
      } else {
         Throwable var2 = var0.getCause();
         return var2 != null ? checkAnyCause(var2, var1) : false;
      }
   }
}
