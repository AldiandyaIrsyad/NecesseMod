package necesse.level.maps.layers;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.Performance;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ObjectLevelLayer extends ShortLevelLayer {
   public ObjectLevelLayer(Level var1) {
      super(var1);
   }

   public void init() {
   }

   public void onLoadingComplete() {
      for(int var1 = 0; var1 < this.level.width; ++var1) {
         for(int var2 = 0; var2 < this.level.height; ++var2) {
            GameObject var3 = this.getObject(var1, var2);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.level.wireManager.updateWire(var1, var2, var4, var3.isWireActive(this.level, var1, var2, var4));
            }
         }
      }

   }

   public void simulateWorld(long var1, boolean var3) {
      Performance.recordConstant(LevelSave.debugLoadingPerformance, "objects", () -> {
         long var4 = var1 / 1000L;
         if (var4 > 0L) {
            SimulatePriorityList var6 = new SimulatePriorityList();

            for(int var7 = 0; var7 < this.level.height; ++var7) {
               for(int var8 = 0; var8 < this.level.width; ++var8) {
                  this.getObject(var8, var7).addSimulateLogic(this.level, var8, var7, var4, var6, var3);
               }
            }

            var6.run();
         }

      });
   }

   public void tickTile(int var1, int var2) {
      this.getObject(var1, var2).tick(this.level, var1, var2);
   }

   public void tickTileEffect(GameCamera var1, PlayerMob var2, int var3, int var4) {
      this.getObject(var3, var4).tickEffect(this.level, var3, var4);
   }

   public void onWireUpdate(int var1, int var2, int var3, boolean var4) {
      this.getObject(var1, var2).onWireUpdate(this.level, var1, var2, var3, var4);
   }

   public List<LevelJob> getLevelJobs(int var1, int var2) {
      return this.getObject(var1, var2).getLevelJobs(this.level, var1, var2);
   }

   public void setObject(int var1, int var2, int var3) {
      if (var1 >= 0 && var1 < this.level.width && var2 >= 0 && var2 < this.level.height) {
         if (!this.level.isLoadingComplete()) {
            if (this.get(var1, var2) != var3) {
               this.set(var1, var2, (short)var3);
               this.level.replaceObjectEntity(var1, var2);
            }
         } else if (this.get(var1, var2) != var3) {
            short var4 = this.get(var1, var2);
            GameObject var5 = ObjectRegistry.getObject(var4);
            boolean[] var6 = new boolean[4];

            for(int var7 = 0; var7 < 4; ++var7) {
               var6[var7] = var5.isWireActive(this.level, var1, var2, var7);
            }

            this.set(var1, var2, (short)var3);
            this.level.replaceObjectEntity(var1, var2);
            GameObject var10 = ObjectRegistry.getObject(var3);

            for(int var8 = 0; var8 < 4; ++var8) {
               if (var10.isWireActive(this.level, var1, var2, var8) != var6[var8]) {
                  this.level.wireManager.updateWire(var1, var2, var8, !var6[var8]);
               }
            }

            this.level.regionManager.objectUpdated(var1, var2, var5, var10);
            this.level.lightManager.updateStaticLight(var1, var2);
            if (var5.stopsTerrainSplatting() != var10.stopsTerrainSplatting()) {
               this.level.splattingManager.onSplattingChange(var1, var2);
            }

            if (this.level.isServer()) {
               SettlementLevelData var11 = SettlementLevelData.getSettlementData(this.level);
               if (var11 != null) {
                  HashSet var9 = new HashSet();
                  var9.add(new Point(var1, var2));
                  if (var5.getRegionType().roomInt != var10.getRegionType().roomInt) {
                     var9.add(new Point(var1 - 1, var2));
                     var9.add(new Point(var1 + 1, var2));
                     var9.add(new Point(var1, var2 - 1));
                     var9.add(new Point(var1, var2 + 1));
                  }

                  var11.rooms.refreshRooms(var9);
               }
            }
         }

         if (this.level.isClient()) {
            this.level.getClient().levelManager.updateMap(var1, var2);
         }

      }
   }

   public int getObjectID(int var1, int var2) {
      return this.get(var1, var2);
   }

   public GameObject getObject(int var1, int var2) {
      return ObjectRegistry.getObject(this.getObjectID(var1, var2));
   }

   public void addSaveData(SaveData var1) {
      var1.addStringArray("objectData", ObjectRegistry.getObjectStringIDs());
      super.addSaveData(var1);
   }

   public void loadSaveData(LoadData var1) {
      super.loadSaveData(var1);
      if (var1.hasLoadDataByName("objectData")) {
         String[] var2 = var1.getStringArray("objectData");
         String[] var3 = ObjectRegistry.getObjectStringIDs();
         int[] var4 = new int[this.data.length];

         int var5;
         for(var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = this.data[var5];
         }

         if (!Arrays.equals(var2, var3)) {
            if (VersionMigration.convertArray(var4, var2, var3, 0, VersionMigration.oldObjectStringIDs)) {
               for(var5 = 0; var5 < this.data.length; ++var5) {
                  this.data[var5] = (short)var4[var5];
               }

               System.out.println("Migrated level " + this.level.getIdentifier() + " object data");
            } else {
               System.out.println("Failed to migrate level " + this.level.getIdentifier() + " object data");
            }
         }
      }

      this.level.entityManager.objectEntities.forEach(Entity::remove);

      for(int var6 = 0; var6 < this.level.width; ++var6) {
         for(int var7 = 0; var7 < this.level.height; ++var7) {
            ObjectEntity var8 = this.getObject(var6, var7).getNewObjectEntity(this.level, var6, var7);
            if (var8 != null) {
               this.level.entityManager.objectEntities.addHidden(var8);
            }
         }
      }

   }

   protected void handleSaveNotFound() {
      throw new RuntimeException("Could not find level object data");
   }

   protected void handleLoadException(Exception var1) {
      throw new RuntimeException("Failed to load level objects", var1);
   }

   protected boolean isValidRegionValue(int var1, int var2, short var3) {
      return var3 >= 0;
   }
}
