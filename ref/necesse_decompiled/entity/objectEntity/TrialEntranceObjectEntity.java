package necesse.entity.objectEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.desert.DesertBiome;
import necesse.level.maps.biomes.desert.DesertDeepCaveLevel;
import necesse.level.maps.biomes.forest.ForestDeepCaveLevel;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.biomes.snow.SnowDeepCaveLevel;
import necesse.level.maps.biomes.swamp.SwampBiome;
import necesse.level.maps.biomes.swamp.SwampDeepCaveLevel;
import necesse.level.maps.biomes.trial.TrialRoomLevel;
import necesse.level.maps.presets.set.TrialRoomSet;
import necesse.level.maps.presets.trialRoomPresets.GenericTrialRoom;
import necesse.level.maps.presets.trialRoomPresets.PressurePlateMazePreset;
import necesse.level.maps.presets.trialRoomPresets.TrialRoomPreset;

public class TrialEntranceObjectEntity extends PortalObjectEntity {
   public ArrayList<List<InventoryItem>> lootList = new ArrayList();

   public TrialEntranceObjectEntity(Level var1, int var2, int var3) {
      super(var1, "trialentrance", var2, var3, var1.getIdentifier(), var2, var3);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.lootList.clear();
      LoadData var2 = var1.getFirstLoadDataByName("LOOT");
      if (var2 != null) {
         List var3 = var2.getLoadDataByName("INVENTORY");
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            LoadData var5 = (LoadData)var4.next();
            ArrayList var6 = new ArrayList();
            List var7 = var5.getLoadDataByName("ITEM");
            Iterator var8 = var7.iterator();

            while(var8.hasNext()) {
               LoadData var9 = (LoadData)var8.next();
               InventoryItem var10 = InventoryItem.fromLoadData(var9);
               if (var10 != null) {
                  var6.add(var10);
               }
            }

            if (!var6.isEmpty()) {
               this.lootList.add(var6);
            }
         }
      }

   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (!this.lootList.isEmpty()) {
         SaveData var2 = new SaveData("LOOT");
         Iterator var3 = this.lootList.iterator();

         while(true) {
            List var4;
            do {
               if (!var3.hasNext()) {
                  if (!var2.isEmpty()) {
                     var1.addSaveData(var2);
                  }

                  return;
               }

               var4 = (List)var3.next();
            } while(var4.isEmpty());

            SaveData var5 = new SaveData("INVENTORY");
            Iterator var6 = var4.iterator();

            while(var6.hasNext()) {
               InventoryItem var7 = (InventoryItem)var6.next();
               SaveData var8 = new SaveData("ITEM");
               var7.addSaveData(var8);
               var5.addSaveData(var8);
            }

            var2.addSaveData(var5);
         }
      }
   }

   public void addLootList(List<InventoryItem> var1) {
      this.lootList.add(var1);
   }

   public List<InventoryItem> getNextLootList() {
      return this.lootList.isEmpty() ? null : (List)this.lootList.remove(0);
   }

   public void use(Server var1, ServerClient var2) {
      this.destinationIdentifier = new LevelIdentifier(this.getLevel().getIdentifier().stringID + "trial" + this.getTileX() + "x" + this.getTileY());
      this.teleportClientToAroundDestination(var2, (var2x) -> {
         if (var2x.equals(this.destinationIdentifier)) {
            this.getLevel().childLevels.add(this.destinationIdentifier);
            return this.generateTrialLevel(this.getLevel(), this.destinationIdentifier, var1);
         } else {
            return null;
         }
      }, (var1x) -> {
         GameObject var2 = ObjectRegistry.getObject(ObjectRegistry.getObjectID("trialexit"));
         if (var2 != null) {
            var2.placeObject(var1x, this.destinationTileX, this.destinationTileY, 0);
            PortalObjectEntity var3 = (PortalObjectEntity)var1x.entityManager.getObjectEntity(this.destinationTileX, this.destinationTileY);
            if (var3 != null) {
               var3.destinationTileX = this.getX();
               var3.destinationTileY = this.getY();
               var3.destinationIdentifier = this.getLevel().getIdentifier();
            }
         }

         return true;
      }, true);
      this.runClearMobs(var2);
   }

   public Level generateTrialLevel(Level var1, LevelIdentifier var2, Server var3) {
      Biome var5 = var1.biome;
      TrialRoomSet var4;
      if (var5 instanceof DesertBiome) {
         if (var1 instanceof DesertDeepCaveLevel) {
            var4 = TrialRoomSet.deepSandstone;
         } else {
            var4 = TrialRoomSet.sandStone;
         }
      } else if (var5 instanceof SnowBiome) {
         if (var1 instanceof SnowDeepCaveLevel) {
            var4 = TrialRoomSet.deepSnowStone;
         } else {
            var4 = TrialRoomSet.snowStone;
         }
      } else if (var5 instanceof SwampBiome) {
         if (var1 instanceof SwampDeepCaveLevel) {
            var4 = TrialRoomSet.deepSwampStone;
         } else {
            var4 = TrialRoomSet.swampStone;
         }
      } else if (var1 instanceof ForestDeepCaveLevel) {
         var4 = TrialRoomSet.deepStone;
      } else {
         var4 = TrialRoomSet.stone;
      }

      TrialRoomLevel var6 = new TrialRoomLevel(var2, var3.world.worldEntity);
      GameRandom var7 = new GameRandom((long)var2.stringID.hashCode());
      TrialRoomPreset[] var8 = new TrialRoomPreset[]{new PressurePlateMazePreset(var7, var4, this::getNextLootList), new GenericTrialRoom(var7, var4, this::getNextLootList)};
      TrialRoomPreset var9 = var8[var7.getIntBetween(0, var8.length - 1)];
      var9.applyToLevel(var6, 0, 0);
      this.destinationTileX = var9.exitTileX;
      this.destinationTileY = var9.exitTileY;
      return var6;
   }
}
