package necesse.engine.world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class LevelManager {
   private World world;
   private HashMap<LevelIdentifier, Level> loadedLevels;

   public LevelManager(World var1) {
      this.world = var1;
      this.loadedLevels = new HashMap();
   }

   public void serverTick() {
      Level[] var1 = (Level[])this.loadedLevels.values().toArray(new Level[0]);
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Level var4 = var1[var3];
         if (this.loadedLevels.containsKey(var4.getIdentifier())) {
            var4.serverTick();
         }
      }

   }

   public void frameTick(TickManager var1) {
      Level[] var2 = (Level[])this.loadedLevels.values().toArray(new Level[0]);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Level var5 = var2[var4];
         if (this.loadedLevels.containsKey(var5.getIdentifier())) {
            var5.frameTick(var1);
         }
      }

   }

   public Level getLevel(LevelIdentifier var1) {
      return (Level)this.loadedLevels.get(var1);
   }

   public void overwriteLevel(Level var1) {
      LevelIdentifier var2 = var1.getIdentifier();
      Level var3 = this.getLevel(var2);
      if (var3 != var1) {
         this.unloadLevel(var1);
         if (!var1.isLoadingComplete()) {
            var1.onLoadingComplete();
         }

         this.loadedLevels.put(var2, var1);
         if (this.world.server != null) {
            this.world.server.levelCache.updateLevel(var1);
         }

         this.world.saveLevel(var1);
      }
   }

   public void deleteLevel(LevelIdentifier var1, ArrayList<InventoryItem> var2) {
      this.deleteLevel(var1, var2, new HashSet());
   }

   private void deleteLevel(LevelIdentifier var1, ArrayList<InventoryItem> var2, HashSet<LevelIdentifier> var3) {
      if (!var3.contains(var1)) {
         var3.add(var1);
         if (this.world.server != null) {
            this.world.server.streamClients().filter((var1x) -> {
               return var1x.getLevelIdentifier().equals(var1);
            }).forEach((var1x) -> {
               var1x.changeToFallbackLevel(var1);
            });
         }

         Level var4 = this.getLevel(var1);
         if (var4 == null && var2 != null) {
            var4 = this.world.loadLevel(var1);
         }

         if (var4 != null) {
            var4.addReturnedItems(var2);
            Iterator var5 = var4.childLevels.iterator();

            while(var5.hasNext()) {
               LevelIdentifier var6 = (LevelIdentifier)var5.next();
               this.deleteLevel(var6, var2, var3);
            }
         } else {
            LoadData var10 = this.world.loadLevelScript(var1);
            if (var10 != null) {
               HashSet var12 = LevelSave.getChildLevels(var10);
               Iterator var7 = var12.iterator();

               while(var7.hasNext()) {
                  LevelIdentifier var8 = (LevelIdentifier)var7.next();
                  this.deleteLevel(var8, var2, var3);
               }
            }
         }

         this.unloadLevel(var1);
         WorldFile var11 = this.world.fileSystem.getLevelFile(var1);

         try {
            var11.delete();
         } catch (IOException var9) {
            var9.printStackTrace();
         }

      }
   }

   private boolean loadLevel(WorldFile var1, LevelIdentifier var2) {
      Level var3 = this.world.loadLevel(var1);
      if (var3 == null) {
         return false;
      } else {
         if (!var3.getIdentifier().equals(var2)) {
            LevelIdentifier var4 = var3.getIdentifier();
            var3.overwriteIdentifier(var2);
            System.out.println("Fixed level identifier from " + var4 + " to " + var2);
         }

         this.loadedLevels.put(var2, var3);
         if (this.world.server != null) {
            this.world.server.levelCache.updateLevel(var3);
         }

         return true;
      }
   }

   public boolean loadLevel(LevelIdentifier var1) {
      return this.isLoaded(var1) || this.loadLevel(this.world.fileSystem.getLevelFile(var1), var1);
   }

   public void unloadLevel(LevelIdentifier var1) {
      Level var2 = (Level)this.loadedLevels.get(var1);
      if (var2 != null) {
         var2.onUnloading();
         var2.dispose();
         this.loadedLevels.remove(var1);
      }

   }

   public void unloadLevel(Level var1) {
      this.unloadLevel(var1.getIdentifier());
   }

   public int getLoadedLevelsNum() {
      return this.loadedLevels.size();
   }

   public Collection<Level> getLoadedLevels() {
      return this.loadedLevels.values();
   }

   public boolean isLoaded(LevelIdentifier var1) {
      return this.loadedLevels.containsKey(var1);
   }

   public void dispose() {
      this.loadedLevels.values().forEach(Level::dispose);
   }
}
