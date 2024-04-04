package necesse.entity.objectEntity;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.incursions.OpenIncursion;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ReturnPortalMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class FallenAltarObjectEntity extends ObjectEntity implements OEInventory {
   public Inventory inventory = new Inventory(1) {
      public void updateSlot(int var1) {
         super.updateSlot(var1);
         InventoryItem var2 = this.getItem(var1);
         if (var2 != null && var2.item instanceof GatewayTabletItem) {
            IncursionData var3 = GatewayTabletItem.getIncursionData(var2);
            if (var3 == null) {
               GatewayTabletItem.initializeGatewayTablet(var2, new GameRandom(), 1);
               this.markDirty(var1);
            }
         }

      }
   };
   public int returnPortalUniqueID;
   public OpenIncursion currentOpenIncursion;

   public FallenAltarObjectEntity(Level var1, int var2, int var3) {
      super(var1, "fallenaltar", var2, var3);
      this.inventory.filter = (var0, var1x) -> {
         return var1x == null || var1x.item.getStringID().equals("gatewaytablet");
      };
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSaveData(InventorySave.getSave(this.inventory, "INVENTORY"));
      if (this.currentOpenIncursion != null) {
         SaveData var2 = new SaveData("openIncursion");
         this.currentOpenIncursion.addSaveData(var2);
         var1.addSaveData(var2);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.inventory.override(InventorySave.loadSave(var1.getFirstLoadDataByName("INVENTORY")));
      LoadData var2 = var1.getFirstLoadDataByName("openIncursion");
      if (var2 != null) {
         try {
            this.currentOpenIncursion = new OpenIncursion(var2);
         } catch (Exception var4) {
            System.err.println("Could not load open incursion from altar at: " + this.getTileX() + "," + this.getTileY());
            var4.printStackTrace();
         }
      } else {
         this.currentOpenIncursion = null;
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.inventory.writeContent(var1);
      var1.putNextBoolean(this.currentOpenIncursion != null);
      if (this.currentOpenIncursion != null) {
         this.currentOpenIncursion.writePacket(var1);
      }

   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.inventory.override(Inventory.getInventory(var1));
      if (var1.getNextBoolean()) {
         this.currentOpenIncursion = new OpenIncursion(var1);
      } else {
         this.currentOpenIncursion = null;
      }

   }

   public InventoryItem getGatewayTablet() {
      InventoryItem var1 = this.inventory.getItem(0);
      return var1 != null && var1.item.getStringID().equals("gatewaytablet") ? var1 : null;
   }

   public boolean hasOpenIncursion() {
      return this.currentOpenIncursion != null;
   }

   public OpenIncursion getOpenIncursion() {
      return this.currentOpenIncursion;
   }

   public void openIncursion(FallenAltarContainer var1, IncursionData var2, ServerClient var3) {
      if (!this.isServer()) {
         throw new IllegalStateException("Only the server can open incursions");
      } else {
         InventoryItem var4 = this.getGatewayTablet();
         if (var4 != null) {
            Server var5 = this.getLevel().getServer();
            LevelIdentifier var6 = new LevelIdentifier("incursion" + var2.getUniqueID());
            this.currentOpenIncursion = new OpenIncursion(var6, var2);
            this.currentOpenIncursion.incursionData.onOpened(var1, var3);
            this.inventory.setItem(0, (InventoryItem)null);
            this.markDirty();
         }

      }
   }

   public void closeOpenIncursion(FallenAltarContainer var1, ServerClient var2, ArrayList<InventoryItem> var3) {
      if (!this.isServer()) {
         throw new IllegalStateException("Only the server can open incursions");
      } else {
         if (this.currentOpenIncursion != null) {
            this.currentOpenIncursion.incursionData.onClosed(this, var2);
            this.getServer().world.levelManager.deleteLevel(this.currentOpenIncursion.incursionLevelIdentifier, var3);
            this.currentOpenIncursion = null;
            this.markDirty();
         }

      }
   }

   public boolean markCanComplete(IncursionLevel var1) {
      if (!this.isServer()) {
         throw new IllegalStateException("Only the server can mark incursions as complete");
      } else if (this.currentOpenIncursion == null) {
         return false;
      } else {
         this.currentOpenIncursion.canComplete = true;
         ArrayList var2 = this.currentOpenIncursion.incursionData.uniqueIncursionModifiers;

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            ((UniqueIncursionModifier)var2.get(var3)).onIncursionLevelCompleted(var1, var3);
         }

         this.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("ui", "incursionnowcomplete")), (LevelIdentifier)this.currentOpenIncursion.incursionLevelIdentifier);
         this.markDirty();
         return true;
      }
   }

   public void completeOpenIncursion(FallenAltarContainer var1, ServerClient var2, ArrayList<InventoryItem> var3) {
      if (!this.isServer()) {
         throw new IllegalStateException("Only the server can open incursions");
      } else {
         if (this.currentOpenIncursion != null) {
            this.currentOpenIncursion.incursionData.onCompleted(this, var2);
            this.getServer().world.levelManager.deleteLevel(this.currentOpenIncursion.incursionLevelIdentifier, var3);
            this.currentOpenIncursion = null;
            this.markDirty();
         }

      }
   }

   public void enterIncursion(ServerClient var1) {
      if (!this.isServer()) {
         throw new IllegalStateException("Only the server can enter incursions");
      } else {
         if (this.currentOpenIncursion != null) {
            OpenIncursion var2 = this.getOpenIncursion();
            if (var2 != null) {
               var1.setFallbackLevel(this.getLevel(), this.getTileX(), this.getTileY());
               var1.changeLevel(this.currentOpenIncursion.incursionLevelIdentifier, (var3) -> {
                  IncursionLevel var4 = var2.incursionData.getNewIncursionLevel(var3, var1.getServer(), var1.getServer().world.worldEntity);
                  var4.altarLevelIdentifier = this.getLevel().getIdentifier();
                  var4.altarTileX = this.getTileX();
                  var4.altarTileY = this.getTileY();
                  ArrayList var5 = var2.incursionData.getUniqueIncursionModifiers();

                  for(int var6 = 0; var6 < var5.size(); ++var6) {
                     ((UniqueIncursionModifier)var5.get(var6)).onIncursionLevelGenerated(var4, var6);
                  }

                  return var4;
               }, (var2x) -> {
                  Mob var3 = this.returnPortalUniqueID == 0 ? null : (Mob)var2x.entityManager.mobs.get(this.returnPortalUniqueID, false);
                  if (var3 == null) {
                     var3 = (Mob)var2x.entityManager.mobs.stream().filter((var0) -> {
                        return var0 instanceof ReturnPortalMob;
                     }).findFirst().orElse((Object)null);
                  }

                  if (var3 != null) {
                     this.returnPortalUniqueID = var3.getUniqueID();
                     return var1.getPlayerPosFromTile(var2x, var3.getTileX(), var3.getTileY());
                  } else {
                     this.returnPortalUniqueID = 0;
                     return var1.getPlayerPosFromTile(var2x, var2x.width / 2, var2x.height / 2);
                  }
               }, true);
            }
         }

      }
   }

   public ArrayList<InventoryItem> getDroppedItems() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.inventory.getSize(); ++var2) {
         if (!this.inventory.isSlotClear(var2)) {
            var1.add(this.inventory.getItem(var2));
         }
      }

      return var1;
   }

   public void clientTick() {
      super.clientTick();
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.inventory.tickItems(this);
      }));
   }

   public void serverTick() {
      super.serverTick();
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.inventory.tickItems(this);
      }));
      this.serverTickInventorySync(this.getLevel().getServer(), this);
   }

   public void markClean() {
      super.markClean();
      this.inventory.clean();
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public GameMessage getInventoryName() {
      return this.getObject().getLocalization();
   }

   public boolean canSetInventoryName() {
      return false;
   }

   public boolean canQuickStackInventory() {
      return false;
   }

   public boolean canRestockInventory() {
      return false;
   }

   public boolean canSortInventory() {
      return false;
   }

   public boolean canUseForNearbyCrafting() {
      return false;
   }

   public InventoryRange getSettlementStorage() {
      return null;
   }
}
