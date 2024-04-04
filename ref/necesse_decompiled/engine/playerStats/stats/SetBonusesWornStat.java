package necesse.engine.playerStats.stats;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStat;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamGameStats;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;

public class SetBonusesWornStat extends PlayerStat {
   protected HashSet<String> dirtySets = new HashSet();
   protected HashSet<String> sets = new HashSet();

   public SetBonusesWornStat(PlayerStats var1, String var2) {
      super(var1, var2);
   }

   public void clean() {
      super.clean();
      this.dirtySets.clear();
   }

   protected void addSetBonusWorn(String var1, boolean var2) {
      if (!this.sets.contains(var1)) {
         try {
            int var3 = BuffRegistry.getBuffIDRaw(var1);
            Buff var4 = BuffRegistry.getBuff(var3);
            if (var4 instanceof SetBonusBuff) {
               this.sets.add(var1);
               if (var2) {
                  this.updateSteam();
               }

               this.dirtySets.add(var1);
               this.markImportantDirty();
            }
         } catch (NoSuchElementException var5) {
         }

      }
   }

   public void addSetBonusWorn(SetBonusBuff var1) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else {
         this.addSetBonusWorn(var1.getStringID(), true);
      }
   }

   public boolean isSetBonusWorn(String var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.sets.contains(var1);
      }
   }

   public Iterable<String> getSetBonusesWorn() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.sets;
      }
   }

   public int getTotalSetBonusesWorn() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.sets.size();
      }
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof SetBonusesWornStat) {
         SetBonusesWornStat var2 = (SetBonusesWornStat)var1;
         Iterator var3 = var2.sets.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            this.addSetBonusWorn(var4, true);
         }
      }

   }

   public void resetCombine() {
   }

   protected void updateSteam() {
      if (this.parent.controlAchievements) {
         ;
      }
   }

   public void loadSteam(SteamGameStats var1) {
   }

   public void addSaveData(SaveData var1) {
      if (!this.sets.isEmpty()) {
         var1.addStringHashSet("sets", this.sets);
      }
   }

   public void applyLoadData(LoadData var1) {
      Iterator var2 = var1.getStringHashSet("sets", new HashSet()).iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (!var3.isEmpty()) {
            this.addSetBonusWorn(var3, false);
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.sets.size());
      Iterator var2 = this.sets.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.putNextShortUnsigned(BuffRegistry.getBuffID(var3));
      }

   }

   public void applyContentPacket(PacketReader var1) {
      this.sets.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = BuffRegistry.getBuffStringID(var4);
         this.addSetBonusWorn(var5, true);
      }

   }

   public void setupDirtyPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.dirtySets.size());
      Iterator var2 = this.dirtySets.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.putNextShortUnsigned(BuffRegistry.getBuffID(var3));
      }

   }

   public void applyDirtyPacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = BuffRegistry.getBuffStringID(var4);
         this.addSetBonusWorn(var5, true);
      }

   }
}
