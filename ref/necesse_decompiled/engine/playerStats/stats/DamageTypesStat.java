package necesse.engine.playerStats.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.BiConsumer;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStat;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamGameStats;
import necesse.entity.mobs.gameDamageType.DamageType;

public class DamageTypesStat extends PlayerStat {
   protected HashSet<DamageType> dirtyTypes = new HashSet();
   protected HashMap<DamageType, Integer> damage = new HashMap();

   public DamageTypesStat(PlayerStats var1, String var2) {
      super(var1, var2);
   }

   public void clean() {
      super.clean();
      this.dirtyTypes.clear();
   }

   protected void setDamage(DamageType var1, int var2, boolean var3) {
      int var4 = (Integer)this.damage.getOrDefault(var1, 0);
      if (var4 != var2) {
         this.damage.put(var1, var2);
         if (var3) {
            this.updateSteam(var1);
         }

         this.dirtyTypes.add(var1);
         this.markDirty();
      }
   }

   public void addDamage(DamageType var1, int var2) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else {
         this.setDamage(var1, (Integer)this.damage.getOrDefault(var1, 0) + var2, true);
      }
   }

   public int getDamage(DamageType var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return (Integer)this.damage.getOrDefault(var1, 0);
      }
   }

   public void forEach(BiConsumer<DamageType, Integer> var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         this.damage.forEach(var1);
      }
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof DamageTypesStat) {
         DamageTypesStat var2 = (DamageTypesStat)var1;
         var2.damage.forEach((var1x, var2x) -> {
            this.setDamage(var1x, (Integer)this.damage.getOrDefault(var1x, 0) + var2x, true);
         });
      }

   }

   public void resetCombine() {
      this.damage.clear();
   }

   protected void updateSteam(DamageType var1) {
      if (this.parent.controlAchievements) {
         String var2 = var1.getSteamStatKey();
         if (var2 != null) {
            SteamData.setStat(var2, (Integer)this.damage.get(var1), false);
         }

      }
   }

   public void loadSteam(SteamGameStats var1) {
      Iterator var2 = DamageTypeRegistry.getDamageTypes().iterator();

      while(var2.hasNext()) {
         DamageType var3 = (DamageType)var2.next();
         String var4 = var3.getSteamStatKey();
         if (var4 != null) {
            int var5 = Math.max(var1.getStatByName(var4, 0), (Integer)this.damage.getOrDefault(var3, 0));
            this.damage.put(var3, var5);
         }
      }

   }

   public void addSaveData(SaveData var1) {
      this.damage.forEach((var1x, var2) -> {
         var1.addInt(var1x.getStringID(), var2);
      });
   }

   public void applyLoadData(LoadData var1) {
      this.damage.clear();
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();
         if (var3.isData()) {
            try {
               int var4 = Integer.parseInt(var3.getData());
               DamageType var5 = DamageTypeRegistry.getDamageType(var3.getName());
               if (var5 == null) {
                  var5 = DamageTypeRegistry.getDamageType(var3.getName().toLowerCase());
               }

               if (var5 != null) {
                  this.setDamage(var5, var4, false);
               }
            } catch (NumberFormatException var6) {
               GameLog.warn.println("Could not load damage types stat number: " + var3.getData());
            }
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.damage.size());
      this.damage.forEach((var1x, var2) -> {
         var1.putNextShortUnsigned(var1x.getID());
         var1.putNextInt(var2);
      });
   }

   public void applyContentPacket(PacketReader var1) {
      this.damage.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         this.setDamage(DamageTypeRegistry.getDamageType(var4), var1.getNextInt(), true);
      }

   }

   public void setupDirtyPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.dirtyTypes.size());
      Iterator var2 = this.dirtyTypes.iterator();

      while(var2.hasNext()) {
         DamageType var3 = (DamageType)var2.next();
         var1.putNextShortUnsigned(var3.getID());
         var1.putNextInt(this.getDamage(var3));
      }

   }

   public void applyDirtyPacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         this.setDamage(DamageTypeRegistry.getDamageType(var4), var1.getNextInt(), true);
      }

   }
}
