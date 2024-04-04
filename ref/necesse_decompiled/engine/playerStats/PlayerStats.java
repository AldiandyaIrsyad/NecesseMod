package necesse.engine.playerStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.stats.BiomesVisitedStat;
import necesse.engine.playerStats.stats.DamageTypesStat;
import necesse.engine.playerStats.stats.IncIntPlayerStat;
import necesse.engine.playerStats.stats.IncursionBiomePlayerStat;
import necesse.engine.playerStats.stats.ItemCountStat;
import necesse.engine.playerStats.stats.ItemsObtainedStat;
import necesse.engine.playerStats.stats.MobKillsStat;
import necesse.engine.playerStats.stats.SetBonusesWornStat;
import necesse.engine.playerStats.stats.TrinketsWornStat;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamGameStats;
import necesse.gfx.gameFont.FontManager;

public class PlayerStats {
   private final ArrayList<PlayerStat> list = new ArrayList();
   private boolean dirty;
   private boolean importantDirty;
   public final boolean controlAchievements;
   public final Mode mode;
   public final IncIntPlayerStat time_played;
   public final IncIntPlayerStat distance_ran;
   public final IncIntPlayerStat distance_ridden;
   public final IncIntPlayerStat deaths;
   public final IncIntPlayerStat damage_dealt;
   public final IncIntPlayerStat damage_taken;
   public final IncIntPlayerStat island_travels;
   public final IncIntPlayerStat objects_mined;
   public final IncIntPlayerStat objects_placed;
   public final IncIntPlayerStat tiles_mined;
   public final IncIntPlayerStat tiles_placed;
   public final IncIntPlayerStat fish_caught;
   public final IncIntPlayerStat quests_completed;
   public final IncIntPlayerStat money_earned;
   public final IncIntPlayerStat items_sold;
   public final IncIntPlayerStat money_spent;
   public final IncIntPlayerStat items_bought;
   public final IncIntPlayerStat items_enchanted;
   public final IncIntPlayerStat items_upgraded;
   public final IncIntPlayerStat items_salvaged;
   public final IncIntPlayerStat ladders_used;
   public final IncIntPlayerStat doors_used;
   public final IncIntPlayerStat plates_triggered;
   public final IncIntPlayerStat levers_flicked;
   public final IncIntPlayerStat homestones_used;
   public final IncIntPlayerStat waystones_used;
   public final IncIntPlayerStat crafted_items;
   public final IncIntPlayerStat crates_broken;
   public final DamageTypesStat type_damage_dealt;
   public final SetBonusesWornStat set_bonuses_worn;
   public final TrinketsWornStat trinkets_worn;
   public final BiomesVisitedStat biomes_visited;
   public final ItemsObtainedStat items_obtained;
   public final MobKillsStat mob_kills;
   public final ItemCountStat food_consumed;
   public final ItemCountStat potions_consumed;
   public final IncursionBiomePlayerStat opened_incursions;
   public final IncursionBiomePlayerStat completed_incursions;

   public static String filePath() {
      return GlobalData.cfgPath() + "stats";
   }

   public PlayerStats(boolean var1, Mode var2) {
      this.controlAchievements = var1;
      this.mode = var2;
      this.time_played = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "time_played", false));
      this.distance_ran = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "distance_ran", false));
      this.distance_ridden = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "distance_ridden", false));
      this.damage_dealt = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "damage_dealt", false));
      this.damage_taken = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "damage_taken", false));
      this.deaths = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "deaths", true));
      this.island_travels = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "island_travels", true));
      this.objects_mined = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "objects_mined", true));
      this.objects_placed = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "objects_placed", true));
      this.tiles_mined = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "tiles_mined", true));
      this.tiles_placed = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "tiles_placed", true));
      this.fish_caught = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "fish_caught", true));
      this.quests_completed = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "quests_completed", true));
      this.money_earned = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "money_earned", true));
      this.items_sold = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "items_sold", true));
      this.money_spent = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "money_spent", true));
      this.items_bought = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "items_bought", true));
      this.items_enchanted = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "items_enchanted", true));
      this.items_upgraded = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "items_upgraded", true));
      this.items_salvaged = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "items_salvaged", true));
      this.ladders_used = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "ladders_used", true));
      this.doors_used = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "doors_used", true));
      this.plates_triggered = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "plates_triggered", true));
      this.levers_flicked = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "levers_flicked", true));
      this.homestones_used = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "homestones_used", true));
      this.waystones_used = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "waystones_used", true));
      this.crafted_items = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "crafted_items", true));
      this.crates_broken = (IncIntPlayerStat)this.addStat(new IncIntPlayerStat(this, "crates_broken", true));
      this.type_damage_dealt = (DamageTypesStat)this.addStat(new DamageTypesStat(this, "type_damage_dealt"));
      this.set_bonuses_worn = (SetBonusesWornStat)this.addStat(new SetBonusesWornStat(this, "set_bonuses_worn"));
      this.trinkets_worn = (TrinketsWornStat)this.addStat(new TrinketsWornStat(this, "trinkets_worn"));
      this.biomes_visited = (BiomesVisitedStat)this.addStat(new BiomesVisitedStat(this, "biomes_known"));
      this.items_obtained = (ItemsObtainedStat)this.addStat(new ItemsObtainedStat(this, "items_obtained"));
      this.mob_kills = (MobKillsStat)this.addStat(new MobKillsStat(this, "mob_kills"));
      this.food_consumed = (ItemCountStat)this.addStat(new ItemCountStat(this, "food_consumed"));
      this.potions_consumed = (ItemCountStat)this.addStat(new ItemCountStat(this, "potions_consumed"));
      this.opened_incursions = (IncursionBiomePlayerStat)this.addStat(new IncursionBiomePlayerStat(this, "opened_incursions"));
      this.completed_incursions = (IncursionBiomePlayerStat)this.addStat(new IncursionBiomePlayerStat(this, "completed_incursions"));
   }

   private <T extends PlayerStat> T addStat(T var1) {
      if (this.list.stream().anyMatch((var1x) -> {
         return var1x.stringID.equals(var1.stringID);
      })) {
         throw new IllegalArgumentException("Cannot add stat with duplicate stringID: " + var1.stringID);
      } else {
         this.list.add(var1);
         return var1;
      }
   }

   public Stream<PlayerStat> streamStats() {
      return this.list.stream();
   }

   public void combine(PlayerStats var1) {
      for(int var2 = 0; var2 < this.list.size(); ++var2) {
         ((PlayerStat)this.list.get(var2)).combine((PlayerStat)var1.list.get(var2));
      }

   }

   public void combineDirty(PlayerStats var1) {
      for(int var2 = 0; var2 < this.list.size(); ++var2) {
         if (((PlayerStat)var1.list.get(var2)).isDirty()) {
            ((PlayerStat)this.list.get(var2)).combine((PlayerStat)var1.list.get(var2));
         }
      }

   }

   public void resetCombine() {
      this.list.forEach(PlayerStat::resetCombine);
   }

   public void loadSteam(SteamGameStats var1) {
      this.list.forEach((var1x) -> {
         var1x.loadSteam(var1);
      });
   }

   public PlayerStat getStat(String var1) {
      return (PlayerStat)this.list.stream().filter((var1x) -> {
         return var1x.stringID.equals(var1);
      }).findFirst().orElse((Object)null);
   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.list.iterator();

      while(var2.hasNext()) {
         PlayerStat var3 = (PlayerStat)var2.next();
         SaveData var4 = new SaveData(var3.stringID);
         var3.addSaveData(var4);
         if (!var4.isEmpty()) {
            var1.addSaveData(var4);
         }
      }

   }

   public void applyLoadData(LoadData var1) {
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();
         PlayerStat var4 = this.getStat(var3.getName());
         if (var4 != null) {
            var4.applyLoadData(var3);
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      Iterator var2 = this.list.iterator();

      while(var2.hasNext()) {
         PlayerStat var3 = (PlayerStat)var2.next();
         var3.setupContentPacket(var1);
      }

   }

   public void applyContentPacket(PacketReader var1) {
      Iterator var2 = this.list.iterator();

      while(var2.hasNext()) {
         PlayerStat var3 = (PlayerStat)var2.next();
         var3.applyContentPacket(var1);
      }

   }

   public void markDirty() {
      this.dirty = true;
   }

   public boolean isDirty() {
      return this.dirty || this.importantDirty;
   }

   public void cleanAll() {
      this.list.forEach(PlayerStat::clean);
      this.dirty = false;
      this.importantDirty = false;
   }

   public void markImportantDirty() {
      this.importantDirty = true;
   }

   public boolean isImportantDirty() {
      return this.importantDirty;
   }

   public void setupDirtyPacket(PacketWriter var1) {
      for(int var2 = 0; var2 < this.list.size(); ++var2) {
         PlayerStat var3 = (PlayerStat)this.list.get(var2);
         if (var3.isDirty()) {
            var1.putNextShortUnsigned(var2);
            var3.setupDirtyPacket(var1);
         }
      }

      var1.putNextShortUnsigned(32767);
   }

   public void applyDirtyPacket(PacketReader var1) {
      while(true) {
         int var2 = var1.getNextShortUnsigned();
         if (var2 == 32767) {
            return;
         }

         PlayerStat var3 = (PlayerStat)this.list.get(var2);
         var3.applyDirtyPacket(var1);
      }
   }

   public void saveStatsFile() {
      try {
         SaveData var1 = new SaveData("");
         this.addSaveData(var1);
         var1.saveScriptRaw(new File(filePath()), true);
         if (GlobalData.isDevMode()) {
            var1.saveScript(new File(filePath() + "-debug"));
         }
      } catch (IllegalArgumentException var3) {
         if (FontManager.isLoaded()) {
            GameMessageBuilder var2 = (new GameMessageBuilder()).append("misc", "statssaveeerror");
            if (var3.getCause() != null) {
               var2.append("\n\n").append(var3.getCause().getMessage());
            } else {
               var2.append("\n\n").append(var3.getMessage());
            }

            Screen.addLoadingNotice("statserrornotice", var2);
         }

         System.err.println("Error creating folder to save stats. Stats may not be saved.");
         var3.printStackTrace();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public void loadStatsFile() {
      try {
         LoadData var1 = LoadData.newRaw(new File(filePath()), true);
         File var2 = new File(filePath() + "-debug");
         if (GlobalData.isDevMode() && var2.exists()) {
            var1 = LoadData.newRaw(var2, false);
         }

         this.applyLoadData(var1);
      } catch (FileNotFoundException var3) {
         System.out.println("Could not find stats file, does not exist. Creating new.");
         this.saveStatsFile();
      } catch (DataFormatException | IOException var4) {
         var4.printStackTrace();
      }

   }

   public static enum Mode {
      READ_ONLY,
      WRITE_ONLY,
      READ_AND_WRITE;

      private Mode() {
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{READ_ONLY, WRITE_ONLY, READ_AND_WRITE};
      }
   }
}
