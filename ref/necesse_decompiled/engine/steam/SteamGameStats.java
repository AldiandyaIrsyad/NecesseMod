package necesse.engine.steam;

import com.codedisaster.steamworks.SteamUserStats;
import java.util.HashMap;

public class SteamGameStats {
   public final int time_played;
   public final int distance_ran;
   public final int distance_ridden;
   public final int deaths;
   public final int damage_dealt;
   public final int normal_damage_dealt;
   public final int true_damage_dealt;
   public final int melee_damage_dealt;
   public final int ranged_damage_dealt;
   public final int magic_damage_dealt;
   public final int summon_damage_dealt;
   public final int damage_taken;
   public final int island_travels;
   public final int islands_discovered;
   public final int islands_visited;
   public final int objects_mined;
   public final int objects_placed;
   public final int tiles_mined;
   public final int tiles_placed;
   public final int food_consumed;
   public final int potions_consumed;
   public final int fish_caught;
   public final int quests_completed;
   public final int money_earned;
   public final int items_sold;
   public final int money_spent;
   public final int items_bought;
   public final int items_enchanted;
   public final int items_upgraded;
   public final int items_salvaged;
   public final int ladders_used;
   public final int doors_used;
   public final int plates_triggered;
   public final int levers_flicked;
   public final int crafted_items;
   public final int crates_broken;
   public final int items_obtained;
   public final int homestones_used;
   public final int waystones_used;
   public final int mob_kills;
   public final int boss_kills;
   public final int opened_incursions;
   public final int completed_incursions;
   private final HashMap<String, Integer> loadedStats = new HashMap();

   public SteamGameStats(SteamUserStats var1) {
      this.time_played = this.getStat(var1, "time_played");
      this.distance_ran = this.getStat(var1, "distance_ran");
      this.distance_ridden = this.getStat(var1, "distance_ridden");
      this.deaths = this.getStat(var1, "deaths");
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
      this.crafted_items = this.getStat(var1, "crafted_items");
      this.crates_broken = this.getStat(var1, "crates_broken");
      this.items_obtained = this.getStat(var1, "items_obtained");
      this.homestones_used = this.getStat(var1, "homestones_used");
      this.waystones_used = this.getStat(var1, "waystones_used");
      this.mob_kills = this.getStat(var1, "mob_kills");
      this.boss_kills = this.getStat(var1, "boss_kills");
      this.opened_incursions = this.getStat(var1, "opened_incursions");
      this.completed_incursions = this.getStat(var1, "completed_incursions");
   }

   private int getStat(SteamUserStats var1, String var2) {
      int var3 = var1.getStatI(var2, 0);
      this.loadedStats.put(var2, var3);
      return var3;
   }

   public int getStatByName(String var1, int var2) {
      return (Integer)this.loadedStats.getOrDefault(var1, var2);
   }
}
