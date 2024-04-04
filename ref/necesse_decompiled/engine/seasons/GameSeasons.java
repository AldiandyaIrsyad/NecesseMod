package necesse.engine.seasons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import necesse.engine.GameLaunch;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItemList;

public class GameSeasons {
   private static int month;
   private static int day;
   private static boolean aprilFools;
   private static boolean halloween;
   private static boolean christmas;
   private static boolean newYear;
   private static ArrayList<SeasonalHat> hats = new ArrayList();
   private static ArrayList<SeasonCrate> crates = new ArrayList();
   private static ArrayList<SeasonalMobLootTable> mobLoot = new ArrayList();
   public static ArrayList<SeasonCrate> activeCrates = new ArrayList();

   public GameSeasons() {
   }

   public static void loadSeasons() {
      boolean var0 = GameLaunch.launchOptions != null && GameLaunch.launchOptions.containsKey("ignoreseasons");
      if (var0) {
         System.out.println("Ignoring seasonal content with -ignoreseasons launch option");
         aprilFools = false;
         halloween = false;
         christmas = false;
         newYear = false;
      } else {
         Calendar var1 = Calendar.getInstance(Locale.ENGLISH);
         month = var1.get(2) + 1;
         day = var1.get(5);
         aprilFools = isBetween(1, 4, 2, 4);
         halloween = isBetween(18, 10, 7, 11);
         christmas = isBetween(1, 12, 28, 12);
         newYear = isBetween(28, 12, 7, 1);
      }

      refreshActive();
   }

   public static void loadResources() {
      Iterator var0 = hats.iterator();

      while(var0.hasNext()) {
         SeasonalHat var1 = (SeasonalHat)var0.next();
         var1.loadTextures();
      }

      var0 = crates.iterator();

      while(var0.hasNext()) {
         SeasonCrate var2 = (SeasonCrate)var0.next();
         var2.loadTextures();
      }

   }

   public static void writeSeasons(PacketWriter var0) {
      var0.putNextBoolean(aprilFools);
      var0.putNextBoolean(halloween);
      var0.putNextBoolean(christmas);
      var0.putNextBoolean(newYear);
   }

   public static void readSeasons(PacketReader var0) {
      aprilFools = var0.getNextBoolean();
      halloween = var0.getNextBoolean();
      christmas = var0.getNextBoolean();
      newYear = var0.getNextBoolean();
      refreshActive();
   }

   private static void refreshActive() {
      activeCrates.clear();
      Iterator var0 = crates.iterator();

      while(var0.hasNext()) {
         SeasonCrate var1 = (SeasonCrate)var0.next();
         if ((Boolean)var1.isActive.get()) {
            activeCrates.add(var1);
         }
      }

   }

   public static boolean isBetween(int var0, int var1, int var2, int var3) {
      boolean var4 = var1 == var3 ? var2 < var0 : var3 < var1;
      if (var4) {
         if (month == var1 && month == var3) {
            return day >= var0 || day <= var2;
         } else if (month == var1) {
            return day >= var0;
         } else if (month == var3) {
            return day <= var2;
         } else {
            return month > var1 || month < var3;
         }
      } else if (month == var1 && month == var3) {
         return day >= var0 && day <= var2;
      } else if (month == var1) {
         return day >= var0;
      } else if (month == var3) {
         return day <= var2;
      } else {
         return month > var1 && month < var3;
      }
   }

   public static boolean isAprilFools() {
      return aprilFools;
   }

   public static boolean isHalloween() {
      return halloween;
   }

   public static boolean isChristmas() {
      return christmas;
   }

   public static boolean isNewYear() {
      return newYear;
   }

   public static SeasonalHat getHat(GameRandom var0) {
      Iterator var1 = hats.iterator();

      SeasonalHat var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (SeasonalHat)var1.next();
      } while(!(Boolean)var2.isActive.get() || !var0.getChance(var2.mobWearChance));

      return var2;
   }

   public static SeasonCrate getCrate(GameRandom var0) {
      if (activeCrates.isEmpty()) {
         return null;
      } else {
         Iterator var1 = activeCrates.iterator();

         SeasonCrate var2;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            var2 = (SeasonCrate)var1.next();
         } while(!var0.getChance(var2.crateChance));

         return var2;
      }
   }

   public static void addMobDrops(Mob var0, ArrayList<InventoryItem> var1, GameRandom var2, float var3) {
      Iterator var4 = mobLoot.iterator();

      while(var4.hasNext()) {
         SeasonalMobLootTable var5 = (SeasonalMobLootTable)var4.next();
         if ((Boolean)var5.isActive.get()) {
            var5.addDrops(var0, var1, var2, var3);
         }
      }

   }

   static {
      hats.add(new SeasonalHatLight(GameSeasons::isHalloween, 0.16666667F, "pumpkinmask", 0.1F, "pumpkinmask"));
      hats.add(new SeasonalHat(GameSeasons::isChristmas, 0.16666667F, "christmashat", 0.1F, "christmashat"));
      hats.add(new SeasonalHat(GameSeasons::isNewYear, 0.16666667F, "partyhat", 0.1F, "partyhat"));
      crates.add(new SeasonCrate(GameSeasons::isChristmas, 0.2F, "christmascrates"));
      mobLoot.add(new SeasonalMobLootTable(GameSeasons::isChristmas, new LootTable(new LootItemInterface[]{new ConditionLootItemList((var0, var1) -> {
         Mob var2 = (Mob)LootTable.expectExtra(Mob.class, var1, 0);
         return var2 != null && var2.isHostile && !var2.isSummoned;
      }, new LootItemInterface[]{new ChanceLootItem(0.04F, "christmaspresent")})})));
   }
}
