package necesse.engine.world.worldData.incursions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;

public class IncursionBiomeStats {
   private int total = 0;
   private HashMap<Integer, IncursionDataStats> biomeStats = new HashMap();

   public IncursionBiomeStats() {
   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.biomeStats.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if (((IncursionDataStats)var3.getValue()).getTotal() > 0) {
            SaveData var4 = new SaveData("BIOME");
            String var5 = IncursionBiomeRegistry.getBiomeStringID((Integer)var3.getKey());
            var4.addUnsafeString("biomeStringID", var5);
            SaveData var6 = new SaveData("DATA");
            ((IncursionDataStats)var3.getValue()).addSaveData(var6);
            var4.addSaveData(var6);
            var1.addSaveData(var4);
         }
      }

   }

   public void applyLoadData(LoadData var1) {
      this.total = 0;
      this.biomeStats.clear();
      Iterator var2 = var1.getLoadDataByName("BIOME").iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();

         try {
            String var4 = var3.getUnsafeString("biomeStringID", (String)null, false);
            if (var4 != null) {
               int var5 = IncursionBiomeRegistry.getBiomeID(var4);
               if (var5 != -1) {
                  LoadData var6 = var3.getFirstLoadDataByName("DATA");
                  if (var6 != null) {
                     IncursionDataStats var7 = new IncursionDataStats();
                     var7.applyLoadData(var6);
                     if (var7.getTotal() > 0) {
                        this.biomeStats.put(var5, var7);
                        this.total += var7.getTotal();
                     }
                  }
               } else {
                  System.err.println("Error loading incursion biome stats: Could not find biome with stringID " + var4);
               }
            }
         } catch (Exception var8) {
            System.err.println("Unknown error loading incursion biome stats");
            var8.printStackTrace();
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextInt(this.total);
      var1.putNextShortUnsigned(this.biomeStats.size());
      Iterator var2 = this.biomeStats.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         var1.putNextShortUnsigned((Integer)var3.getKey());
         ((IncursionDataStats)var3.getValue()).setupContentPacket(var1);
      }

   }

   public void applyContentPacket(PacketReader var1) {
      this.total = var1.getNextInt();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         this.getData(var4).applyContentPacket(var1);
      }

   }

   public void combine(IncursionBiomeStats var1) {
      this.total += var1.total;
      var1.biomeStats.forEach((var1x, var2) -> {
         this.getData(var1x).combine(var2);
      });
   }

   public void loadSteamTotal(int var1) {
      this.total = Math.max(var1, this.total);
   }

   public void reset() {
      this.total = 0;
      this.biomeStats.clear();
   }

   public int getTotal() {
      return this.total;
   }

   public int getTotal(IncursionBiome var1) {
      return this.getTotal(var1.getID());
   }

   public int getTotal(String var1) {
      return this.getTotal(IncursionBiomeRegistry.getBiomeID(var1));
   }

   public int getTotal(int var1) {
      IncursionDataStats var2 = (IncursionDataStats)this.biomeStats.get(var1);
      return var2 == null ? 0 : var2.getTotal();
   }

   public IncursionDataStats getData(IncursionBiome var1) {
      return this.getData(var1.getID());
   }

   public IncursionDataStats getData(String var1) {
      return this.getData(IncursionBiomeRegistry.getBiomeID(var1));
   }

   public IncursionDataStats getData(int var1) {
      return (IncursionDataStats)this.biomeStats.compute(var1, (var0, var1x) -> {
         return var1x == null ? new IncursionDataStats() : var1x;
      });
   }

   public void add(BiomeMissionIncursionData var1) {
      int var2 = var1.biome.getID();
      this.getData(var2).add(var1);
      ++this.total;
   }
}
