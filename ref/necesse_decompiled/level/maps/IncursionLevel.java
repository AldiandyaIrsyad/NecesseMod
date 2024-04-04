package necesse.level.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.engine.world.WorldEntity;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.FallenAltarObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.incursion.IncursionData;

public class IncursionLevel extends Level {
   public static LootTable randomTabletLootTable = new LootTable(new LootItemInterface[]{new LootItemInterface() {
      public void addPossibleLoot(LootList var1, Object... var2) {
         var1.add("gatewaytablet");
      }

      public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
         Mob var5 = (Mob)LootTable.expectExtra(Mob.class, var4, 0);
         if (var5 != null) {
            Level var6 = var5.getLevel();
            if (var6 instanceof IncursionLevel) {
               IncursionLevel var7 = (IncursionLevel)var6;
               if (var7.incursionData != null) {
                  IncursionLevelEvent var8 = var7.getIncursionLevelEvent();
                  if (var8 != null) {
                     float var9 = var8.getRandomTabletDropChance(var5);
                     if (!(var9 <= 0.0F)) {
                        LootTable.runChance(var2, var9, var3, (var3x) -> {
                           InventoryItem var4 = new InventoryItem("gatewaytablet");

                           int var5;
                           for(var5 = var7.incursionData.getTabletTier(); var2.getChance(0.5F) && var5 > 1; --var5) {
                           }

                           GatewayTabletItem.initializeGatewayTablet(var4, var2, var5);
                           var1.add(var4);
                        });
                     }
                  }
               }
            }
         }
      }
   }});
   public IncursionData incursionData;
   public int incursionEventUniqueID;
   public LevelIdentifier altarLevelIdentifier;
   public int altarTileX = -1;
   public int altarTileY = -1;

   public IncursionLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public IncursionLevel(LevelIdentifier var1, int var2, int var3, IncursionData var4, WorldEntity var5) {
      super(var1, var2, var3, var5);
      this.incursionData = var4;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("incursionEventUniqueID", this.incursionEventUniqueID);
      if (this.incursionData != null) {
         SaveData var2 = new SaveData("incursionData");
         this.incursionData.addSaveData(var2);
         var1.addSaveData(var2);
      }

      if (this.altarLevelIdentifier != null) {
         var1.addUnsafeString("altarLevel", this.altarLevelIdentifier.stringID);
      }

      if (this.altarTileX != -1) {
         var1.addInt("altarTileX", this.altarTileX);
      }

      if (this.altarTileY != -1) {
         var1.addInt("altarTileY", this.altarTileY);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.incursionEventUniqueID = var1.getInt("incursionEventUniqueID", this.incursionEventUniqueID, false);
      LoadData var2 = var1.getFirstLoadDataByName("incursionData");
      if (var2 != null) {
         try {
            this.incursionData = IncursionData.fromLoadData(var2);
         } catch (Exception var4) {
            System.err.println("Could not load incursion data from level " + this.getIdentifier());
            var4.printStackTrace();
         }
      } else {
         this.incursionData = null;
      }

      String var3 = var1.getUnsafeString("altarLevel", (String)null, false);
      if (var3 != null) {
         this.altarLevelIdentifier = new LevelIdentifier(var3);
      }

      if (var1.getInt("altarTileX", this.altarTileX) != -1) {
         this.altarTileX = var1.getInt("altarTileX", this.altarTileX);
      }

      if (var1.getInt("altarTileY", this.altarTileY) != -1) {
         this.altarTileY = var1.getInt("altarTileY", this.altarTileY);
      }

   }

   public void writeLevelDataPacket(PacketWriter var1) {
      super.writeLevelDataPacket(var1);
      var1.putNextInt(this.incursionEventUniqueID);
      if (this.incursionData != null) {
         var1.putNextBoolean(true);
         IncursionData.writePacket(this.incursionData, var1);
      } else {
         var1.putNextBoolean(false);
      }

   }

   public void readLevelDataPacket(PacketReader var1) {
      super.readLevelDataPacket(var1);
      this.incursionEventUniqueID = var1.getNextInt();
      if (var1.getNextBoolean()) {
         this.incursionData = IncursionData.fromPacket(var1);
      } else {
         this.incursionData = null;
      }

   }

   public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
      return this.incursionData == null ? super.getDefaultLevelModifiers() : Stream.concat(super.getDefaultLevelModifiers(), this.incursionData.getDefaultLevelModifiers());
   }

   public Stream<ModifierValue<?>> getMobModifiers(Mob var1) {
      Stream var2 = super.getMobModifiers(var1);
      if (this.incursionData != null) {
         var2 = Stream.concat(var2, this.incursionData.getMobModifiers(var1));
      }

      return var2;
   }

   public LootTable getExtraMobDrops(Mob var1) {
      if (this.incursionData == null) {
         return super.getExtraMobDrops(var1);
      } else {
         LootTable var2 = new LootTable(new LootItemInterface[]{super.getExtraMobDrops(var1), this.incursionData.getExtraMobDrops(var1)});
         if (var1.isHostile && !var1.isSummoned) {
            var2 = new LootTable(new LootItemInterface[]{var2, randomTabletLootTable});
         }

         return var2;
      }
   }

   public LootTable getExtraPrivateMobDrops(Mob var1, ServerClient var2) {
      return this.incursionData == null ? super.getExtraPrivateMobDrops(var1, var2) : new LootTable(new LootItemInterface[]{super.getExtraPrivateMobDrops(var1, var2), this.incursionData.getExtraPrivateMobDrops(var1, var2)});
   }

   public GameMessage getSetSpawnError(int var1, int var2, ServerClient var3) {
      return new LocalMessage("misc", "spawnincursion");
   }

   public LootTable getCrateLootTable() {
      return LootTablePresets.incursionCrate;
   }

   public IncursionLevelEvent getIncursionLevelEvent() {
      LevelEvent var1 = this.entityManager.getLevelEvent(this.incursionEventUniqueID, false);
      return var1 instanceof IncursionLevelEvent ? (IncursionLevelEvent)var1 : null;
   }

   public GameMessage canSummonBoss(String var1) {
      IncursionLevelEvent var2 = this.getIncursionLevelEvent();
      return (GameMessage)(var2 != null ? var2.canSpawnBoss(var1) : new LocalMessage("misc", "cannotsummonhere"));
   }

   public void onBossSummoned(Mob var1) {
      IncursionLevelEvent var2 = this.getIncursionLevelEvent();
      if (var2 != null) {
         var2.onBossSummoned(var1);
      }

   }

   public GameMessage getLocationMessage() {
      return this.incursionData != null ? this.incursionData.getDisplayName() : super.getLocationMessage();
   }

   public void returnToAltar(ServerClient var1) {
      TeleportEvent var2;
      if (this.altarLevelIdentifier != null && this.altarTileX != -1 && this.altarTileY != -1) {
         var2 = new TeleportEvent(var1, 0, this.altarLevelIdentifier, 3.0F, (Function)null, (var2x) -> {
            GameObject var3 = var2x.getObject(this.altarTileX, this.altarTileY);
            if (var3 instanceof FallenAltarObject) {
               return new TeleportResult(true, var1.getPlayerPosFromTile(var2x, this.altarTileX, this.altarTileY));
            } else {
               this.altarLevelIdentifier = null;
               var1.validateSpawnPoint(false);
               Point var4;
               if (!var1.isDefaultSpawnPoint()) {
                  Point var5 = RespawnObject.calculateSpawnOffset(var2x, var1.spawnTile.x, var1.spawnTile.y, var1);
                  var4 = new Point(var1.spawnTile.x * 32 + var5.x, var1.spawnTile.y * 32 + var5.y);
               } else {
                  var4 = var1.getPlayerPosFromTile(var2x, var1.spawnTile.x, var1.spawnTile.y);
               }

               return new TeleportResult(true, var4);
            }
         });
         var1.getLevel().entityManager.addLevelEventHidden(var2);
      } else {
         this.altarLevelIdentifier = null;
         var1.validateSpawnPoint(false);
         var2 = new TeleportEvent(var1, 0, var1.spawnLevelIdentifier, 3.0F, (Function)null, (var1x) -> {
            Point var2;
            if (!var1.isDefaultSpawnPoint()) {
               Point var3 = RespawnObject.calculateSpawnOffset(var1x, var1.spawnTile.x, var1.spawnTile.y, var1);
               var2 = new Point(var1.spawnTile.x * 32 + var3.x, var1.spawnTile.y * 32 + var3.y);
            } else {
               var2 = var1.getPlayerPosFromTile(var1x, var1.spawnTile.x, var1.spawnTile.y);
            }

            return new TeleportResult(true, var2);
         });
         var1.getLevel().entityManager.addLevelEventHidden(var2);
      }
   }

   public FallenAltarObjectEntity getAltarObjectEntity() {
      if (!this.isServer()) {
         return null;
      } else {
         if (this.altarLevelIdentifier != null) {
            Level var1 = this.getServer().world.getLevel(this.altarLevelIdentifier);
            ObjectEntity var2 = var1.entityManager.getObjectEntity(this.altarTileX, this.altarTileY);
            if (var2 instanceof FallenAltarObjectEntity) {
               return (FallenAltarObjectEntity)var2;
            }
         }

         return null;
      }
   }

   public void markCanComplete(Supplier<Point> var1, Function<ServerClient, Point> var2) {
      FallenAltarObjectEntity var3 = this.getAltarObjectEntity();
      if (var3 != null) {
         var3.markCanComplete(this);
      }

      Iterator var4 = this.incursionData.playerSharedIncursionCompleteRewards.iterator();

      while(var4.hasNext()) {
         ArrayList var5 = (ArrayList)var4.next();
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Supplier var7 = (Supplier)var6.next();
            Point var8 = (Point)var1.get();
            if (var8 == null) {
               var8 = new Point(this.width * 32 / 2 + 16, this.height * 32 / 2 + 16);
            }

            ItemPickupEntity var9 = ((InventoryItem)var7.get()).getPickupEntity(this, (float)var8.x, (float)var8.y);
            var9.showsLightBeam = true;
            this.entityManager.pickups.add(var9);
         }
      }

      this.getServer().streamClients().filter((var1x) -> {
         return var1x.isSamePlace(this);
      }).forEach((var2x) -> {
         Iterator var3 = this.incursionData.playerPersonalIncursionCompleteRewards.iterator();

         while(var3.hasNext()) {
            ArrayList var4 = (ArrayList)var3.next();
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               Supplier var6 = (Supplier)var5.next();
               Point var7 = (Point)var2.apply(var2x);
               if (var7 == null) {
                  var7 = var2x.playerMob.getPositionPoint();
               }

               ItemPickupEntity var8 = ((InventoryItem)var6.get()).getPickupEntity(this, (float)var7.x, (float)var7.y);
               var8.setReservedAuth(var2x.authentication);
               var8.showsLightBeam = true;
               this.entityManager.pickups.add(var8);
            }
         }

      });
   }
}
