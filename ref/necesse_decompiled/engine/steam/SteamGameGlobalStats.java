package necesse.engine.steam;

import com.codedisaster.steamworks.SteamUserStats;
import java.util.HashMap;

public class SteamGameGlobalStats {
   public final long time_played;
   public final long distance_ran;
   public final long distance_ridden;
   public final long deaths;
   public final long mob_kills;
   public final long boss_kills;
   public final long damage_dealt;
   public final long normal_damage_dealt;
   public final long true_damage_dealt;
   public final long melee_damage_dealt;
   public final long ranged_damage_dealt;
   public final long magic_damage_dealt;
   public final long summon_damage_dealt;
   public final long damage_taken;
   public final long island_travels;
   public final long islands_discovered;
   public final long islands_visited;
   public final long objects_mined;
   public final long objects_placed;
   public final long tiles_mined;
   public final long tiles_placed;
   public final long food_consumed;
   public final long potions_consumed;
   public final long fish_caught;
   public final long quests_completed;
   public final long money_earned;
   public final long items_sold;
   public final long money_spent;
   public final long items_bought;
   public final long items_enchanted;
   public final long items_upgraded;
   public final long items_salvaged;
   public final long ladders_used;
   public final long doors_used;
   public final long plates_triggered;
   public final long levers_flicked;
   public final long homestones_used;
   public final long waystones_used;
   public final long crafted_items;
   public final long crates_broken;
   public final long opened_incursions;
   public final long completed_incursions;
   private final HashMap<String, Long> loadedStats = new HashMap();

   public SteamGameGlobalStats(SteamUserStats var1) {
      this.time_played = this.getStat(var1, "time_played");
      this.distance_ran = this.getStat(var1, "distance_ran");
      this.distance_ridden = this.getStat(var1, "distance_ridden");
      this.deaths = this.getStat(var1, "deaths");
      this.mob_kills = this.getStat(var1, "mob_kills");
      this.boss_kills = this.getStat(var1, "boss_kills");
      this.damage_dealt = this.getStat(var1, "damage_dealt");
      this.normal_damage_dealt = this.getStat(var1, "normal_damage_dealt");
      this.true_damage_dealt = this.getStat(var1, "true_damage_dealt");
      this.melee_damage_dealt = this.getStat(var1, "melee_damage_dealt");
      this.ranged_damage_dealt = this.getStat(var1, "ranged_damage_dealt");
      this.magic_damage_dealt = this.getStat(var1, "magic_damage_dealt");
      this.summon_damage_dealt = this.getStat(var1, "summon_damage_dealt");
      this.damage_taken = this.getStat(var1, "damage_taken");
      this.island_travels = this.getStat(var1, "island_travels");
      this.islands_discovered = this.getStat(var1, "islands_discovered");
      this.islands_visited = this.getStat(var1, "islands_visited");
      this.objects_mined = this.getStat(var1, "objects_mined");
      this.objects_placed = this.getStat(var1, "objects_placed");
      this.tiles_mined = this.getStat(var1, "tiles_mined");
      this.tiles_placed = this.getStat(var1, "tiles_placed");
      this.food_consumed = this.getStat(var1, "food_consumed");
      this.potions_consumed = this.getStat(var1, "potions_consumed");
      this.fish_caught = this.getStat(var1, "fish_caught");
      this.quests_completed = this.getStat(var1, "quests_completed");
      this.money_earned = this.getStat(var1, "money_earned");
      this.items_sold = this.getStat(var1, "items_sold");
      this.money_spent = this.getStat(var1, "money_spent");
      this.items_bought = this.getStat(var1, "items_bought");
      this.items_enchanted = this.getStat(var1, "items_enchanted");
      this.items_upgraded = this.getStat(var1, "items_upgraded");
      this.items_salvaged = this.getStat(var1, "items_salvaged");
      this.ladders_used = this.getStat(var1, "ladders_used");
      this.doors_used = this.getStat(var1, "doors_used");
      this.plates_triggered = this.getStat(var1, "plates_triggered");
      this.levers_flicked = this.getStat(var1, "levers_flicked");
      this.homestones_used = this.getStat(var1, "homestones_used");
      this.waystones_used = this.getStat(var1, "waystones_used");
      this.crafted_items = this.getStat(var1, "crafted_items");
      this.crates_broken = this.getStat(var1, "crates_broken");
      this.opened_incursions = this.getStat(var1, "opened_incursions");
      this.completed_incursions = this.getStat(var1, "completed_incursions");
   }

   private long getStat(SteamUserStats var1, String var2) {
      long var3 = var1 == null ? 0L : var1.getGlobalStat(var2, 0L);
      this.loadedStats.put(var2, var3);
      return var3;
   }

   public long getStatByName(String var1, long var2) {
      return (Long)this.loadedStats.getOrDefault(var1, var2);
   }
}
