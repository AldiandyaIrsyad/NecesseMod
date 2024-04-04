package necesse.engine.network.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.tickManager.PerformanceTimer;
import necesse.engine.tickManager.PerformanceTimerUtils;
import necesse.engine.tickManager.PerformanceTotal;
import necesse.engine.util.GameUtils;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;

public class ClientPerformanceDumpCache {
   public final Client client;
   private HashMap<Integer, CachedText> cache = new HashMap();
   private LinkedList<CachedText> nextTimeouts = new LinkedList();

   public ClientPerformanceDumpCache(Client var1) {
      this.client = var1;
   }

   public void tickTimeouts() {
      while(true) {
         if (!this.nextTimeouts.isEmpty()) {
            CachedText var1 = (CachedText)this.nextTimeouts.getFirst();
            if (var1.timeout <= System.currentTimeMillis()) {
               CachedText var2 = (CachedText)this.cache.get(var1.uniqueID);
               if (var2 == var1) {
                  this.cache.remove(var1.uniqueID);
               }

               this.nextTimeouts.removeFirst();
               this.client.chat.addMessage("Timed out performance recording of " + var2.seconds + " seconds");
               continue;
            }
         }

         return;
      }
   }

   public void submitClientDump(int var1, String var2, boolean var3) {
      CachedText var4 = this.ensureExists(var1, "N/A", System.currentTimeMillis() + 10000L, (String)null);
      var4.clientText = var2;
      this.tickDone(var4, var3);
   }

   public void submitServerDump(int var1, String var2) {
      CachedText var3 = this.ensureExists(var1, "N/A", System.currentTimeMillis() + 10000L, (String)null);
      var3.serverText = var2;
      this.tickDone(var3, false);
   }

   public void submitIncomingDump(int var1, long var2, int var4, String var5) {
      this.ensureExists(var1, Integer.toString(var4), var2, var5);
   }

   private CachedText ensureExists(int var1, String var2, long var3, String var5) {
      CachedText var6 = (CachedText)this.cache.get(var1);
      if (var6 == null) {
         var6 = new CachedText(var1, var2, var3, var5);
         this.cache.put(var1, var6);
         GameUtils.insertSortedList((List)this.nextTimeouts, var6, Comparator.comparingLong((var0) -> {
            return var0.timeout;
         }));
      }

      return var6;
   }

   private boolean tickDone(CachedText var1, boolean var2) {
      if (var1.clientText != null) {
         if (var2 && var1.serverText == null) {
            return false;
         } else {
            String var3 = (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date());
            File var4 = new File((var1.fileName == null ? "performance " + var3 : var1.fileName) + ".txt");
            StringBuilder var5 = new StringBuilder();
            if (var1.serverText != null) {
               var5.append(var1.serverText);
               var5.append("\n\n");
            }

            var5.append(var1.clientText);

            try {
               GameUtils.saveByteFile(var5.toString().getBytes(), var4);
               this.client.chat.addMessage((new FairType()).append(ChatMessage.fontOptions, "Printed performance to file: ").append(FairCharacterGlyph.fromStringToOpenFile(ChatMessage.fontOptions, var4.getName(), var4)));
            } catch (IOException var7) {
               this.client.chat.addMessage("Error printing performance file: " + var7);
            }

            this.cache.remove(var1.uniqueID);
            this.nextTimeouts.remove(var1);
            return true;
         }
      } else {
         return false;
      }
   }

   public static String getText(LinkedList<PerformanceTimer> var0) {
      PerformanceTotal var1 = PerformanceTimerUtils.combineTimers(var0);
      if (var1 != null) {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         var1.print(new PrintStream(var2));
         return var2.toString();
      } else {
         return "";
      }
   }

   private static class CachedText {
      public final int uniqueID;
      public final long timeout;
      public final String seconds;
      public final String fileName;
      public String serverText;
      public String clientText;

      public CachedText(int var1, String var2, long var3, String var5) {
         this.uniqueID = var1;
         this.seconds = var2;
         this.timeout = var3;
         this.fileName = var5;
      }
   }
}
