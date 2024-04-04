package necesse.engine.achievements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;
import necesse.engine.GlobalData;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.playerStats.stats.BiomesVisitedStat;
import necesse.engine.playerStats.stats.IncIntPlayerStat;
import necesse.engine.playerStats.stats.ItemsObtainedStat;
import necesse.engine.playerStats.stats.MobKillsStat;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamGameAchievements;
import necesse.engine.util.GameMath;

public class AchievementManager {
   private final ArrayList<Achievement> achievements = new ArrayList();
   public static HashSet<String> GET_PET_ITEMS = new HashSet(Arrays.asList("inefficientfeather", "weticicle", "exoticseeds", "magicstilts"));
   public static HashSet<String> ONE_TAPPED_MOBS = new HashSet(Arrays.asList("zombie", "trapperzombie", "zombiearcher", "crawlingzombie", "swampzombie", "enchantedzombie", "enchantedzombiearcher", "enchantedcrawlingzombie"));
   public final BoolAchievement SPELUNKER = (BoolAchievement)this.add(new BoolAchievement("spelunker", "spelunker", "spelunker_desc"));
   public final BoolAchievement GET_PET;
   public final BoolAchievement START_SETTLEMENT;
   public final BoolAchievement SET_SPAWN;
   public final BoolAchievement ENCHANT_ITEM;
   public final BoolAchievement MAGICAL_DROP;
   public final BoolAchievement VILLAGE_HELPER;
   public final BoolAchievement HOARDER;
   public final BoolAchievement SELF_PROCLAIMED;
   public final BoolAchievement DOUBLE_CATCH;
   public final BoolAchievement COMPLETE_HOST;
   public final BoolAchievement GETTING_HOT;
   public final BoolAchievement MY_JAM;
   public final BoolAchievement CLOUD_NINE;
   public final BoolAchievement ONE_TAPPED;
   public final BoolAchievement TOO_EASY;
   public final BoolAchievement HEADHUNTER;
   public final BoolAchievement REMATCH;
   public final BoolAchievement DEFEAT_PIRATE;
   public final BoolAchievement GRAVE_DIGGER;
   public final BoolAchievement EQUIP_ABILITY;
   public final IntAchievement DEFEAT_BOSS;
   public final IntAchievement OBTAIN_ITEMS;
   public final IntAchievement VISIT_BIOMES;
   public final IntAchievement FISH_UP;
   public final IntAchievement RUN_MARATHON;
   public final IntAchievement PLAY_24H;

   public static String filePath() {
      return GlobalData.cfgPath() + "achievements";
   }

   public AchievementManager(PlayerStats var1) {
      MobKillsStat var10007 = var1.mob_kills;
      Objects.requireNonNull(var10007);
      this.DEFEAT_BOSS = (IntAchievement)this.add(new IntAchievement("defeat_boss", "defeat_boss", "defeat_boss_desc", var10007::getBossKills, 0, 1, IntAchievement.DrawMode.BOOL));
      this.GET_PET = (BoolAchievement)this.add(new BoolAchievement("get_pet", "get_pet", "get_pet_desc"));
      this.START_SETTLEMENT = (BoolAchievement)this.add(new BoolAchievement("start_settlement", "start_settlement", "start_settlement_desc"));
      this.SET_SPAWN = (BoolAchievement)this.add(new BoolAchievement("set_spawn", "set_spawn", "set_spawn_desc"));
      this.ENCHANT_ITEM = (BoolAchievement)this.add(new BoolAchievement("enchant_item", "enchant_item", "enchant_item_desc"));
      this.MAGICAL_DROP = (BoolAchievement)this.add(new BoolAchievement("magical_drop", "magical_drop", "magical_drop_desc"));
      this.VILLAGE_HELPER = (BoolAchievement)this.add(new BoolAchievement("village_helper", "village_helper", "village_helper_desc"));
      this.HOARDER = (BoolAchievement)this.add(new BoolAchievement("hoarder", "hoarder", "hoarder_desc"));
      this.SELF_PROCLAIMED = (BoolAchievement)this.add(new BoolAchievement("self_proclaimed", "self_proclaimed", "self_proclaimed_desc"));
      this.DOUBLE_CATCH = (BoolAchievement)this.add(new BoolAchievement("double_catch", "double_catch", "double_catch_desc"));
      this.COMPLETE_HOST = (BoolAchievement)this.add(new BoolAchievement("complete_host", "complete_host", "complete_host_desc"));
      this.GETTING_HOT = (BoolAchievement)this.add(new BoolAchievement("getting_hot", "getting_hot", "getting_hot_desc"));
      this.MY_JAM = (BoolAchievement)this.add(new BoolAchievement("my_jam", "my_jam", "my_jam_desc"));
      this.CLOUD_NINE = (BoolAchievement)this.add(new BoolAchievement("cloud_nine", "cloud_nine", "cloud_nine_desc"));
      this.ONE_TAPPED = (BoolAchievement)this.add(new BoolAchievement("one_tapped", "one_tapped", "one_tapped_desc"));
      this.TOO_EASY = (BoolAchievement)this.add(new BoolAchievement("too_easy", "too_easy", "too_easy_desc"));
      this.HEADHUNTER = (BoolAchievement)this.add(new BoolAchievement("headhunter", "headhunter", "headhunter_desc"));
      this.REMATCH = (BoolAchievement)this.add(new BoolAchievement("rematch", "rematch", "rematch_desc"));
      this.DEFEAT_PIRATE = (BoolAchievement)this.add(new BoolAchievement("defeat_pirate", "defeat_pirate", "defeat_pirate_desc"));
      this.GRAVE_DIGGER = (BoolAchievement)this.add(new BoolAchievement("grave_digger", "grave_digger", "grave_digger_desc"));
      this.EQUIP_ABILITY = (BoolAchievement)this.add(new BoolAchievement("equip_ability", "equip_ability", "equip_ability_desc"));
      ItemsObtainedStat var2 = var1.items_obtained;
      Objects.requireNonNull(var2);
      this.OBTAIN_ITEMS = (IntAchievement)this.add(new IntAchievement("obtain_items", "obtain_items", "obtain_items_desc", var2::getTotalStatItems, 0, ItemRegistry.getTotalStatItemsObtainable(), IntAchievement.DrawMode.NORMAL));
      BiomesVisitedStat var3 = var1.biomes_visited;
      Objects.requireNonNull(var3);
      this.VISIT_BIOMES = (IntAchievement)this.add(new IntAchievement("visit_biomes", "visit_biomes", "visit_biomes_desc", var3::getTotalBiomeTypes, 0, BiomeRegistry.getTotalDiscoverableBiomesTypes(), IntAchievement.DrawMode.NORMAL));
      IncIntPlayerStat var4 = var1.fish_caught;
      Objects.requireNonNull(var4);
      this.FISH_UP = (IntAchievement)this.add(new IntAchievement("fish_up", "fish_up", "fish_up_desc", var4::get, 0, 500, IntAchievement.DrawMode.NORMAL));
      var4 = var1.distance_ran;
      Objects.requireNonNull(var4);
      this.RUN_MARATHON = (IntAchievement)this.add(new IntAchievement("run_marathon", "run_marathon", "run_marathon_desc", var4::get, 0, GameMath.metersToPixels(42195.0F), IntAchievement.DrawMode.PERCENT));
      var4 = var1.time_played;
      Objects.requireNonNull(var4);
      this.PLAY_24H = (IntAchievement)this.add(new IntAchievement("play_24h", "play_24h", "play_24h_desc", var4::get, 0, 86400, IntAchievement.DrawMode.PERCENT));
   }

   public void runStatsUpdate(ServerClient var1) {
      this.achievements.forEach((var1x) -> {
         var1x.runStatsUpdate(var1);
      });
   }

   public void setupContentPacket(PacketWriter var1) {
      Iterator var2 = this.achievements.iterator();

      while(var2.hasNext()) {
         Achievement var3 = (Achievement)var2.next();
         var3.setupContentPacket(var1);
      }

   }

   public void applyContentPacket(PacketReader var1) {
      Iterator var2 = this.achievements.iterator();

      while(var2.hasNext()) {
         Achievement var3 = (Achievement)var2.next();
         var3.applyContentPacket(var1);
      }

   }

   public int getCompleted() {
      return (int)this.achievements.stream().filter(Achievement::isCompleted).count();
   }

   public int getTotal() {
      return this.achievements.size();
   }

   private <T extends Achievement> T add(T var1) {
      if (this.achievements.stream().anyMatch((var1x) -> {
         return var1x.stringID.equals(var1.stringID);
      })) {
         throw new IllegalArgumentException("Achievement stringID " + var1.stringID + " already taken.");
      } else {
         int var2 = this.achievements.size();
         this.achievements.add(var1);
         var1.setDataID(var2);
         return var1;
      }
   }

   public Achievement getAchievement(int var1) {
      return (Achievement)this.achievements.get(var1);
   }

   public List<Achievement> getAchievements() {
      return this.achievements;
   }

   public void saveAchievementsFile() {
      try {
         SaveData var1 = new SaveData("");
         Iterator var2 = this.achievements.iterator();

         while(var2.hasNext()) {
            Achievement var3 = (Achievement)var2.next();
            SaveData var4 = new SaveData(var3.stringID);
            var3.addSaveData(var4);
            if (var3.isCompleted()) {
               var3.addCompletedTimeSave(var4);
            }

            if (!var4.isEmpty()) {
               var1.addSaveData(var4);
            }
         }

         var1.saveScriptRaw(new File(filePath()), true);
         if (GlobalData.isDevMode()) {
            var1.saveScript(new File(filePath() + "-debug"));
         }
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public void saveAchievementsFileSafe() {
      try {
         this.saveAchievementsFile();
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public void loadAchievementsFile() {
      try {
         LoadData var1 = LoadData.newRaw(new File(filePath()), true);
         File var2 = new File(filePath() + "-debug");
         if (GlobalData.isDevMode() && var2.exists()) {
            var1 = LoadData.newRaw(var2, false);
         }

         Iterator var3 = var1.getLoadData().iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();
            String var5 = var4.getName();
            Iterator var6 = this.achievements.iterator();

            while(var6.hasNext()) {
               Achievement var7 = (Achievement)var6.next();
               if (var7.stringID.equals(var5)) {
                  var7.applyLoadData(var4);
                  if (var7.isCompleted()) {
                     var7.applyCompletedTimeSave(var4);
                  }
                  break;
               }
            }
         }
      } catch (FileNotFoundException var9) {
         System.out.println("Couldn't find achievements file, does not exist. Creating new.");

         try {
            this.saveAchievementsFile();
         } catch (Exception var8) {
            System.err.println("Error saving achievement file: " + var8.getMessage());
            var8.printStackTrace();
         }
      } catch (DataFormatException | IOException var10) {
         var10.printStackTrace();
      }

   }

   public void loadTextures() {
      this.loadTextures((AchievementManager)null);
   }

   public void loadTextures(AchievementManager var1) {
      if (var1 == null) {
         this.achievements.forEach((var0) -> {
            var0.loadTextures((Achievement)null);
         });
      } else {
         for(int var2 = 0; var2 < this.achievements.size(); ++var2) {
            ((Achievement)this.achievements.get(var2)).loadTextures((Achievement)var1.achievements.get(var2));
         }
      }

   }

   public void loadSteam(SteamGameAchievements var1) {
      this.achievements.forEach((var1x) -> {
         var1x.loadSteam(var1);
      });
   }
}
