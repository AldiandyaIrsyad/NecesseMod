package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketTroughFeed;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.FeedingTroughMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.food.GrainItem;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SemiRegion;

public class FeedingTroughObjectEntity extends ObjectEntity implements OEInventory {
   public static int feedTileRange = 12;
   public static final int INV_SIZE = 10;
   public final Inventory inventory = new Inventory(10) {
      public void updateSlot(int var1) {
         super.updateSlot(var1);
         FeedingTroughObjectEntity.this.updateFeed = true;
      }
   };
   private boolean updateFeed;
   private boolean hasFeed;
   private int feedTimer;

   public FeedingTroughObjectEntity(Level var1, int var2, int var3) {
      super(var1, "feedingtrough", var2, var3);
      this.inventory.filter = (var1x, var2x) -> {
         return var2x == null || this.isValidFeed(var2x.item);
      };
      this.updateFeed = true;
      this.feedTimer = 200;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSaveData(InventorySave.getSave(this.inventory));
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.inventory.override(InventorySave.loadSave(var1.getFirstLoadDataByName("INVENTORY")));
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.inventory.writeContent(var1);
      var1.putNextBoolean(this.hasFeed);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.inventory.override(Inventory.getInventory(var1));
      this.hasFeed = var1.getNextBoolean();
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
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.inventory.tickItems(this);
      }));
      this.serverTickInventorySync(this.getLevel().getServer(), this);
      if (this.updateFeed) {
         this.updateFeed();
      }

      if (this.hasFeed()) {
         --this.feedTimer;
         if (this.feedTimer <= 0) {
            Set var1 = (Set)this.getLevel().regionManager.getSemiRegion(this.getTileX(), this.getTileY()).getAllConnected((var0) -> {
               return var0.getType() == SemiRegion.RegionType.OPEN || var0.getType() == SemiRegion.RegionType.SOLID;
            }).stream().map(SemiRegion::getRegionID).collect(Collectors.toSet());
            Point var2 = this.getMidPos();
            List var3 = (List)this.getLevel().entityManager.mobs.getInRegionByTileRange(this.getTileX(), this.getTileY(), feedTileRange).stream().filter((var2x) -> {
               return var1.contains(this.getLevel().getRegionID(var2x.getTileX(), var2x.getTileY()));
            }).filter((var0) -> {
               return var0 instanceof FeedingTroughMob;
            }).map((var0) -> {
               return (FeedingTroughMob)var0;
            }).filter((var1x) -> {
               return !var1x.isOnFeedCooldown() && GameMath.diagonalMoveDistance(var2, ((Mob)var1x).getPositionPoint()) <= (double)(feedTileRange * 32);
            }).collect(Collectors.toList());

            for(int var4 = 0; var4 < this.inventory.getSize(); ++var4) {
               if (!this.inventory.isSlotClear(var4)) {
                  InventoryItem var5 = this.inventory.getItem(var4);
                  Iterator var6 = var3.iterator();

                  while(var6.hasNext()) {
                     FeedingTroughMob var7 = (FeedingTroughMob)var6.next();
                     if (var7.canFeed(var5)) {
                        this.getLevel().getServer().network.sendToClientsAt(new PacketTroughFeed((HusbandryMob)var7, var5.copy()), (Level)this.getLevel());
                        var7.onFed(var5);
                        if (var5.getAmount() <= 0) {
                           this.inventory.clearSlot(var4);
                           break;
                        }

                        this.inventory.markDirty(var4);
                     }
                  }
               }
            }

            this.feedTimer = 200;
         }
      }

   }

   private Point getMidPos() {
      byte var1 = this.getLevel().getObjectRotation(this.getX(), this.getY());
      if (var1 == 0) {
         return new Point(this.getX() * 32 + 16, this.getY() * 32);
      } else if (var1 == 1) {
         return new Point(this.getX() * 32 + 32, this.getY() * 32 + 16);
      } else {
         return var1 == 2 ? new Point(this.getX() * 32 + 16, this.getY() * 32 + 32) : new Point(this.getX() * 32, this.getY() * 32 + 16);
      }
   }

   private void updateFeed() {
      this.updateFeed = false;
      boolean var1 = this.hasFeed;
      this.hasFeed = false;

      for(int var2 = 0; var2 < this.inventory.getSize(); ++var2) {
         if (!this.inventory.isSlotClear(var2) && this.isValidFeed(this.inventory.getItemSlot(var2))) {
            this.hasFeed = true;
            break;
         }
      }

      if (var1 != this.hasFeed) {
         this.markDirty();
      }

   }

   private boolean isValidFeed(Item var1) {
      return var1 == null ? false : var1 instanceof GrainItem;
   }

   public boolean hasFeed() {
      return this.hasFeed;
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public GameMessage getInventoryName() {
      return this.getObject().getLocalization();
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

   public boolean isSettlementStorageItemDisabled(Item var1) {
      return !this.isValidFeed(var1);
   }
}
