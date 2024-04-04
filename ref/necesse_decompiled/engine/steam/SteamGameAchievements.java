package necesse.engine.steam;

import com.codedisaster.steamworks.SteamAchievementUnlocked;
import com.codedisaster.steamworks.SteamUserStats;
import java.util.HashMap;

public class SteamGameAchievements {
   public final SteamAchievementUnlocked spelunker;
   public final SteamAchievementUnlocked defeat_boss;
   public final SteamAchievementUnlocked get_pet;
   public final SteamAchievementUnlocked start_settlement;
   public final SteamAchievementUnlocked set_spawn;
   public final SteamAchievementUnlocked enchant_item;
   public final SteamAchievementUnlocked obtain_items;
   public final SteamAchievementUnlocked visit_biomes;
   public final SteamAchievementUnlocked fish_up;
   public final SteamAchievementUnlocked run_marathon;
   public final SteamAchievementUnlocked play_24h;
   public final SteamAchievementUnlocked magical_drop;
   public final SteamAchievementUnlocked village_helper;
   public final SteamAchievementUnlocked hoarder;
   public final SteamAchievementUnlocked self_proclaimed;
   public final SteamAchievementUnlocked double_catch;
   public final SteamAchievementUnlocked complete_host;
   public final SteamAchievementUnlocked getting_hot;
   public final SteamAchievementUnlocked my_jam;
   public final SteamAchievementUnlocked cloud_nine;
   public final SteamAchievementUnlocked one_tapped;
   public final SteamAchievementUnlocked too_easy;
   public final SteamAchievementUnlocked headhunter;
   public final SteamAchievementUnlocked rematch;
   public final SteamAchievementUnlocked defeat_pirate;
   public final SteamAchievementUnlocked grave_digger;
   public final SteamAchievementUnlocked equip_ability;
   private final HashMap<String, SteamAchievementUnlocked> loadedAchievements = new HashMap();

   public SteamGameAchievements(SteamUserStats var1) {
      this.spelunker = this.getAchieved(var1, "spelunker");
      this.defeat_boss = this.getAchieved(var1, "defeat_boss");
      this.get_pet = this.getAchieved(var1, "get_pet");
      this.start_settlement = this.getAchieved(var1, "start_settlement");
      this.set_spawn = this.getAchieved(var1, "set_spawn");
      this.enchant_item = this.getAchieved(var1, "enchant_item");
      this.obtain_items = this.getAchieved(var1, "obtain_items");
      this.visit_biomes = this.getAchieved(var1, "visit_biomes");
      this.fish_up = this.getAchieved(var1, "fish_up");
      this.run_marathon = this.getAchieved(var1, "run_marathon");
      this.play_24h = this.getAchieved(var1, "play_24h");
      this.magical_drop = this.getAchieved(var1, "magical_drop");
      this.village_helper = this.getAchieved(var1, "village_helper");
      this.hoarder = this.getAchieved(var1, "hoarder");
      this.self_proclaimed = this.getAchieved(var1, "self_proclaimed");
      this.double_catch = this.getAchieved(var1, "double_catch");
      this.complete_host = this.getAchieved(var1, "complete_host");
      this.getting_hot = this.getAchieved(var1, "getting_hot");
      this.my_jam = this.getAchieved(var1, "my_jam");
      this.cloud_nine = this.getAchieved(var1, "cloud_nine");
      this.one_tapped = this.getAchieved(var1, "one_tapped");
      this.too_easy = this.getAchieved(var1, "too_easy");
      this.headhunter = this.getAchieved(var1, "headhunter");
      this.rematch = this.getAchieved(var1, "rematch");
      this.defeat_pirate = this.getAchieved(var1, "defeat_pirate");
      this.grave_digger = this.getAchieved(var1, "grave_digger");
      this.equip_ability = this.getAchieved(var1, "equip_ability");
   }

   private SteamAchievementUnlocked getAchieved(SteamUserStats var1, String var2) {
      SteamAchievementUnlocked var3 = var1.getAchievementAndUnlockTime(var2);
      this.loadedAchievements.put(var2, var3);
      return var3;
   }

   public SteamAchievementUnlocked getAchievementByName(String var1) {
      return (SteamAchievementUnlocked)this.loadedAchievements.get(var1);
   }
}
