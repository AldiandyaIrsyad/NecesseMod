package necesse.entity.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;

public class DeathMessageTable {
   private ArrayList<GameMessage> messages = new ArrayList();

   public DeathMessageTable() {
   }

   public GameMessage getRandomDeathMessage(GameRandom var1) {
      return this.messages.isEmpty() ? null : (GameMessage)this.messages.get(var1.nextInt(this.messages.size()));
   }

   public DeathMessageTable add(GameMessage... var1) {
      this.messages.addAll(Arrays.asList(var1));
      return this;
   }

   public DeathMessageTable add(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.messages.add(new LocalMessage("deaths", var5));
      }

      return this;
   }

   public DeathMessageTable addRange(String var1, int var2, int var3) {
      for(int var4 = var2; var4 <= var3; ++var4) {
         this.messages.add(new LocalMessage("deaths", var1 + var4));
      }

      return this;
   }

   public DeathMessageTable addRange(String var1, int var2) {
      return this.addRange(var1, 1, var2);
   }

   public static DeathMessageTable oneOf(String... var0) {
      DeathMessageTable var1 = new DeathMessageTable();
      var1.add(var0);
      return var1;
   }

   public static DeathMessageTable fromRange(String var0, int var1, int var2) {
      DeathMessageTable var3 = new DeathMessageTable();
      var3.addRange(var0, var1, var2);
      return var3;
   }

   public static DeathMessageTable fromRange(String var0, int var1) {
      DeathMessageTable var2 = new DeathMessageTable();
      var2.addRange(var0, var1);
      return var2;
   }

   public static GameMessage getDeathMessage(Attacker var0, GameMessage var1) {
      Object var2 = null;
      if (var0 != null) {
         DeathMessageTable var3 = var0.getDeathMessages();
         if (var3 != null) {
            var2 = var3.getRandomDeathMessage(GameRandom.globalRandom);
         }
      }

      if (var2 == null) {
         var2 = new LocalMessage("deaths", "default");
      }

      formatDeathMessage((GameMessage)var2, var0, var1);
      return (GameMessage)var2;
   }

   private static void formatDeathMessage(GameMessage var0, Attacker var1, GameMessage var2) {
      if (var0 instanceof LocalMessage) {
         ((LocalMessage)var0).addReplacement("victim", var2);
         if (var1 != null) {
            ((LocalMessage)var0).addReplacement("attacker", var1.getAttackerName());
         }
      }

   }
}
