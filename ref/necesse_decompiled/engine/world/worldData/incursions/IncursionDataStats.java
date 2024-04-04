package necesse.engine.world.worldData.incursions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IncursionDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionData;

public class IncursionDataStats {
   private int total = 0;
   private HashMap<Integer, Integer> dataStats = new HashMap();

   public IncursionDataStats() {
   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.dataStats.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if ((Integer)var3.getValue() > 0) {
            String var4 = IncursionDataRegistry.getIncursionDataStringID((Integer)var3.getKey());
            var1.addInt(var4, (Integer)var3.getValue());
         }
      }

   }

   public void applyLoadData(LoadData var1) {
      this.total = 0;
      this.dataStats.clear();
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();

         try {
            String var4 = var3.getName().trim();
            int var5 = IncursionDataRegistry.getIncursionDataID(var4);
            if (var5 != -1) {
               int var6;
               try {
                  var6 = Integer.parseInt(var3.getData().trim());
               } catch (NumberFormatException var8) {
                  var6 = 0;
               }

               this.dataStats.put(var5, var6);
               this.total += var6;
            } else {
               System.err.println("Error loading incursion data stats: Could not find data with stringID " + var4);
            }
         } catch (Exception var9) {
            System.err.println("Unknown error loading incursion data stats");
            var9.printStackTrace();
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextInt(this.total);
      var1.putNextShortUnsigned(this.dataStats.size());
      Iterator var2 = this.dataStats.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         var1.putNextShortUnsigned((Integer)var3.getKey());
         var1.putNextInt((Integer)var3.getValue());
      }

   }

   public void applyContentPacket(PacketReader var1) {
      this.total = var1.getNextInt();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         this.dataStats.put(var4, var1.getNextInt());
      }

   }

   public void combine(IncursionDataStats var1) {
      this.total += var1.total;
      var1.dataStats.forEach((var1x, var2) -> {
         this.dataStats.compute(var1x, (var1, var2x) -> {
            return var2x == null ? var2 : var2x + var2;
         });
      });
   }

   public int getTotal() {
      return this.total;
   }

   public int getCount(IncursionData var1) {
      return this.getCount(var1.getID());
   }

   public int getCount(String var1) {
      return this.getCount(IncursionDataRegistry.getIncursionDataID(var1));
   }

   public int getCount(Class<? extends IncursionData> var1) {
      return this.getCount(IncursionDataRegistry.getIncursionDataID(var1));
   }

   public int getCount(int var1) {
      return (Integer)this.dataStats.getOrDefault(var1, 0);
   }

   public void add(BiomeMissionIncursionData var1) {
      int var2 = var1.getID();
      this.dataStats.compute(var2, (var0, var1x) -> {
         return var1x == null ? 1 : var1x + 1;
      });
      ++this.total;
   }
}
