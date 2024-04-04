package necesse.level.maps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.World;

public class DiscoveredMapManager {
   private HashMap<LevelIdentifier, DiscoveredMap> discoveries = new HashMap();

   public DiscoveredMapManager() {
   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.discoveries.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         LevelIdentifier var4 = (LevelIdentifier)var3.getKey();
         DiscoveredMap var5 = (DiscoveredMap)var3.getValue();
         SaveData var6 = new SaveData(var4.stringID);
         var5.addSaveData(var6);
         var1.addSaveData(var6);
      }

   }

   public void applySaveData(LoadData var1) {
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();
         LevelIdentifier var4 = new LevelIdentifier(var3.getName());
         DiscoveredMap var5 = new DiscoveredMap(var3);
         this.discoveries.put(var4, var5);
      }

   }

   public int migrateSaveData(LoadData var1) {
      int var2 = 0;
      Iterator var3 = var1.getLoadData().iterator();

      while(var3.hasNext()) {
         LoadData var4 = (LoadData)var3.next();
         LevelIdentifier var5 = null;

         try {
            var5 = new LevelIdentifier(var4.getName());
            if (!this.discoveries.containsKey(var5)) {
               DiscoveredMap var6 = DiscoveredMap.migrateOldSaveData(var4);
               if (var6 != null) {
                  this.discoveries.put(var5, var6);
                  ++var2;
               }
            }
         } catch (Exception var7) {
            System.err.println("Error migrating old save data from " + var5);
         }
      }

      return var2;
   }

   public DiscoveredMap getDiscovery(LevelIdentifier var1) {
      return (DiscoveredMap)this.discoveries.get(var1);
   }

   public void tickDiscovery(ServerClient var1) {
      DiscoveredMap var2 = (DiscoveredMap)this.discoveries.compute(var1.getLevelIdentifier(), (var1x, var2x) -> {
         return var2x == null ? new DiscoveredMap(var1.getLevel()) : var2x;
      });
      var2.tickDiscovery(var1.playerMob, var1.lastDiscoverPoint, (BiConsumer)null);
   }

   public boolean ensureMapSize(Level var1) {
      DiscoveredMap var2 = (DiscoveredMap)this.discoveries.get(var1.getIdentifier());
      return var2 != null ? var2.ensureMapSize(var1) : false;
   }

   public void clearRemovedLevelIdentifiers(World var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = this.discoveries.keySet().iterator();

      LevelIdentifier var4;
      while(var3.hasNext()) {
         var4 = (LevelIdentifier)var3.next();
         if (!var1.levelExists(var4)) {
            var2.add(var4);
         }
      }

      var3 = var2.iterator();

      while(var3.hasNext()) {
         var4 = (LevelIdentifier)var3.next();
         this.discoveries.remove(var4);
      }

   }

   public boolean combine(DiscoveredMapManager var1) {
      return this.addDiscoveries(var1.discoveries);
   }

   private boolean addDiscoveries(HashMap<LevelIdentifier, DiscoveredMap> var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         boolean var2 = false;
         Iterator var3 = var1.entrySet().iterator();

         while(true) {
            while(var3.hasNext()) {
               Map.Entry var4 = (Map.Entry)var3.next();
               LevelIdentifier var5 = (LevelIdentifier)var4.getKey();
               DiscoveredMap var6 = (DiscoveredMap)var4.getValue();
               DiscoveredMap var7 = (DiscoveredMap)this.discoveries.get(var5);
               if (var7 != null) {
                  var2 = var7.combine(var6) || var2;
               } else {
                  this.discoveries.put(var5, new DiscoveredMap(var6));
                  var2 = true;
               }
            }

            return var2;
         }
      }
   }
}
