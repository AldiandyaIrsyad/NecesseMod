package necesse.engine.commands;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairType;

public class CommandLog {
   private final Client client;
   private final ServerClient serverClient;
   private List<Runnable> logs;
   private boolean printed = false;

   public CommandLog(Client var1, ServerClient var2) {
      this.client = var1;
      this.serverClient = var2;
      this.logs = new ArrayList();
   }

   public void add(FairType var1) {
      this.add(() -> {
         print(this.client, this.serverClient, var1);
      });
   }

   public void add(GameMessage var1) {
      this.add(() -> {
         print(this.client, this.serverClient, var1);
      });
   }

   public void add(String var1) {
      this.add((GameMessage)(new StaticMessage(var1)));
   }

   public void addConsole(String var1) {
      this.addConsole((GameMessage)(new StaticMessage(var1)));
   }

   public void addConsole(GameMessage var1) {
      this.add(() -> {
         System.out.println(GameColor.stripCodes(var1.translate()));
      });
   }

   public void addClient(String var1, ServerClient var2) {
      this.addClient((GameMessage)(new StaticMessage(var1)), var2);
   }

   public void addClient(GameMessage var1, ServerClient var2) {
      this.add(() -> {
         var2.sendChatMessage(var1);
      });
   }

   protected synchronized void add(Runnable var1) {
      if (this.printed) {
         var1.run();
      } else {
         this.logs.add(var1);
      }

   }

   public synchronized void printLog() {
      this.logs.forEach(Runnable::run);
      this.printed = true;
   }

   public static void print(Client var0, ServerClient var1, FairType var2) {
      if (var0 != null) {
         var0.chat.addMessage(var2);
      } else if (var1 != null) {
         Server var3 = var1.getServer();
         Client var4 = var3.getLocalClient();
         if (var4 != null && var3.getLocalServerClient() == var1) {
            var4.chat.addMessage(var2);
         } else {
            var1.sendChatMessage(var2.getParseString());
         }
      } else {
         System.out.println(GameColor.stripCodes(var2.getParseString()));
      }

   }

   public static void print(Client var0, ServerClient var1, GameMessage var2) {
      if (var0 != null) {
         var0.chat.addMessage(var2.translate());
      } else if (var1 != null) {
         var1.sendChatMessage(var2);
      } else {
         System.out.println(GameColor.stripCodes(var2.translate()));
      }

   }

   public static void print(Client var0, ServerClient var1, String var2) {
      print(var0, var1, (GameMessage)(new StaticMessage(var2)));
   }
}
