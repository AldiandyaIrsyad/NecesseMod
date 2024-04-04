package necesse.level.maps.layers;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import necesse.engine.registries.TileRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.Performance;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class TileLevelLayer extends ShortLevelLayer {
   public TileLevelLayer(Level var1) {
      super(var1);
   }

   public void init() {
   }

   public void onLoadingComplete() {
   }

   public void simulateWorld(long var1, boolean var3) {
      Performance.recordConstant(LevelSave.debugLoadingPerformance, "tiles", () -> {
         long var4 = var1 / 1000L;
         if (var4 > 0L) {
            SimulatePriorityList var6 = new SimulatePriorityList();

            for(int var7 = 0; var7 < this.level.height; ++var7) {
               for(int var8 = 0; var8 < this.level.width; ++var8) {
                  this.getTile(var8, var7).addSimulateLogic(this.level, var8, var7, var4, var6, var3);
               }
            }

            var6.run();
         }

      });
   }

   public void tickTile(int var1, int var2) {
      this.getTile(var1, var2).tick(this.level, var1, var2);
   }

   public void tickTileEffect(GameCamera var1, PlayerMob var2, int var3, int var4) {
      this.getTile(var3, var4).tickEffect(this.level, var3, var4);
   }

   public List<LevelJob> getLevelJobs(int var1, int var2) {
      return this.getTile(var1, var2).getLevelJobs(this.level, var1, var2);
   }

   public void setTile(int var1, int var2, int var3) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         GameTile var4 = this.getTile(var1, var2);
         this.set(var1, var2, (short)var3);
         GameTile var5 = this.getTile(var1, var2);
         this.level.liquidManager.tileUpdated(var1, var2, var4, var5);
         if (this.level.isLoadingComplete()) {
            if (var4.getLightLevel() != var5.getLightLevel()) {
               this.level.lightManager.updateStaticLight(var1, var2);
            }

            if (var4 != var5) {
               this.level.splattingManager.onSplattingChange(var1, var2);
               if (this.level.isServer()) {
                  SettlementLevelData var6 = SettlementLevelData.getSettlementData(this.level);
                  if (var6 != null) {
                     var6.rooms.recalculateStats(Collections.singletonList(new Point(var1, var2)));
                  }
               }
            }
         }

         if (this.level.isClient()) {
            this.level.getClient().levelManager.updateMap(var1, var2);
         }

      }
   }

   public int getTileID(int var1, int var2) {
      return this.get(var1, var2);
   }

   public GameTile getTile(int var1, int var2) {
      return TileRegistry.getTile(this.getTileID(var1, var2));
   }

   public void addSaveData(SaveData var1) {
      var1.addStringArray("tileData", TileRegistry.getTileStringIDs());
      super.addSaveData(var1);
   }

   public void loadSaveData(LoadData var1) {
      super.loadSaveData(var1);
      if (var1.hasLoadDataByName("tileData")) {
         String[] var2 = var1.getStringArray("tileData");
         String[] var3 = TileRegistry.getTileStringIDs();
         int[] var4 = new int[this.data.length];

         int var5;
         for(var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = this.data[var5];
         }

         if (!Arrays.equals(var2, var3)) {
            if (VersionMigration.convertArray(var4, var2, var3, 0, VersionMigration.oldTileStringIDs)) {
               for(var5 = 0; var5 < this.data.length; ++var5) {
                  this.data[var5] = (short)var4[var5];
               }

               System.out.println("Migrated level " + this.level.getIdentifier() + " tile data");
            } else {
               System.out.println("Failed to migrate level " + this.level.getIdentifier() + " tile data");
            }
         }
      }

      for(int var6 = 0; var6 < this.data.length; ++var6) {
         if (this.data[var6] == TileRegistry.emptyID) {
            this.data[var6] = (short)TileRegistry.waterID;
         }
      }

   }

   protected void handleSaveNotFound() {
      throw new RuntimeException("Could not find level tile data");
   }

   protected void handleLoadException(Exception var1) {
      throw new RuntimeException("Failed to load level tiles", var1);
   }

   protected boolean isValidRegionValue(int var1, int var2, short var3) {
      return var3 >= 0;
   }
}
