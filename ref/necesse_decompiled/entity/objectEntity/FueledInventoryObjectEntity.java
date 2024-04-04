package necesse.entity.objectEntity;

import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEProgressUpdate;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameMath;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;

public abstract class FueledInventoryObjectEntity extends ObjectEntity implements OEInventory, IProgressObjectEntity {
   public final Inventory inventory;
   public final boolean alwaysOn;
   protected boolean updateFueled;
   public int fuelBurnTime;
   public long fuelStartTime;
   public boolean keepRunning;
   public boolean useWorldTime = true;
   private boolean fueledDirty = false;

   public FueledInventoryObjectEntity(Level var1, String var2, int var3, int var4, int var5, boolean var6) {
      super(var1, var2, var3, var4);
      this.alwaysOn = var6;
      this.inventory = new Inventory(var5) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            FueledInventoryObjectEntity.this.updateFueled = true;
         }
      };
      this.inventory.filter = (var1x, var2x) -> {
         return var2x == null ? true : this.isValidFuelItem(var1x, var2x);
      };
      this.updateFueled = true;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSaveData(InventorySave.getSave(this.inventory));
      var1.addInt("fuelBurnTime", this.fuelBurnTime);
      var1.addLong("fuelStartTime", this.fuelStartTime);
      if (!this.alwaysOn) {
         var1.addBoolean("keepRunning", this.keepRunning);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.inventory.override(InventorySave.loadSave(var1.getFirstLoadDataByName("INVENTORY")));
      this.fuelBurnTime = var1.getInt("fuelBurnTime", 0);
      this.fuelStartTime = var1.getLong("fuelStartTime", 0L);
      if (!this.alwaysOn) {
         this.keepRunning = var1.getBoolean("keepRunning", this.keepRunning);
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.inventory.writeContent(var1);
      var1.putNextInt(this.fuelBurnTime);
      var1.putNextLong(this.fuelStartTime);
      if (!this.alwaysOn) {
         var1.putNextBoolean(this.keepRunning);
      }

   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.inventory.override(Inventory.getInventory(var1));
      boolean var2 = this.isFueled();
      this.fuelBurnTime = var1.getNextInt();
      this.fuelStartTime = var1.getNextLong();
      if (!this.alwaysOn) {
         this.keepRunning = var1.getNextBoolean();
      }

      if (var2 != this.isFueled()) {
         this.getLevel().lightManager.updateStaticLight(this.getX(), this.getY());
      }

   }

   public void setupProgressPacket(PacketWriter var1) {
      var1.putNextInt(this.fuelBurnTime);
      var1.putNextLong(this.fuelStartTime);
      if (!this.alwaysOn) {
         var1.putNextBoolean(this.keepRunning);
      }

   }

   public void applyProgressPacket(PacketReader var1) {
      boolean var2 = this.isFueled();
      this.fuelBurnTime = var1.getNextInt();
      this.fuelStartTime = var1.getNextLong();
      if (!this.alwaysOn) {
         this.keepRunning = var1.getNextBoolean();
      }

      if (var2 != this.isFueled()) {
         this.getLevel().lightManager.updateStaticLight(this.getX(), this.getY());
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
      boolean var1 = this.isFueled();
      long var2 = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
      if (this.updateFueled || this.fuelBurnTime > 0) {
         if (this.fuelStartTime <= 0L) {
            this.fuelStartTime = var2;
         }

         if ((this.alwaysOn || this.keepRunning) && this.fuelBurnTime == 0) {
            this.useFuel();
         }

         for(; this.fuelBurnTime > 0 && this.fuelStartTime + (long)this.fuelBurnTime <= var2; this.markFuelDirty()) {
            this.fuelStartTime += (long)this.fuelBurnTime;
            if (!this.alwaysOn && !this.keepRunning) {
               this.fuelBurnTime = 0;
            } else {
               this.useFuel();
            }
         }

         this.updateFueled = false;
      }

      if (!this.isFueled()) {
         this.fuelStartTime = var2;
      }

      if (var1 != this.isFueled()) {
         this.markFuelDirty();
      }

      if (this.fueledDirty) {
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsAt(new PacketOEProgressUpdate(this), (Level)this.getLevel());
         }

         this.fueledDirty = false;
         this.getLevel().lightManager.updateStaticLight(this.getX(), this.getY());
      }

      this.serverTickInventorySync(this.getLevel().getServer(), this);
   }

   public void useFuel() {
      boolean var1 = this.isFueled();
      this.fuelBurnTime = this.getNextFuelBurnTime(true);
      if (this.fuelBurnTime > 0) {
         this.markFuelDirty();
      }

      if (var1 != this.isFueled()) {
         this.getLevel().lightManager.updateStaticLight(this.getX(), this.getY());
      }

   }

   public boolean canFuel() {
      return this.getNextFuelBurnTime(false) > 0;
   }

   public void markClean() {
      super.markClean();
      this.inventory.clean();
      this.fueledDirty = false;
   }

   public abstract boolean isValidFuelItem(int var1, InventoryItem var2);

   public abstract int getNextFuelBurnTime(boolean var1);

   protected int itemToBurnTime(boolean var1, Function<InventoryItem, Integer> var2) {
      for(int var3 = this.inventory.getSize() - 1; var3 >= 0; --var3) {
         InventoryItem var4 = this.inventory.getItem(var3);
         if (var4 != null) {
            int var5 = (Integer)var2.apply(var4);
            if (var5 > 0) {
               if (var1) {
                  this.inventory.addAmount(var3, -1);
                  if (this.inventory.getAmount(var3) <= 0) {
                     this.inventory.setItem(var3, (InventoryItem)null);
                  }

                  this.inventory.markDirty(var3);
               }

               return var5;
            }
         }
      }

      return 0;
   }

   public void markFuelDirty() {
      this.fueledDirty = true;
      this.updateFueled = true;
   }

   public boolean isFueled() {
      return this.fuelBurnTime > 0;
   }

   public float getFuelProgressLeft() {
      if (this.isFueled()) {
         long var1 = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
         long var3 = var1 - this.fuelStartTime;
         float var5 = GameMath.limit((float)var3 / (float)this.fuelBurnTime, 0.0F, 1.0F);
         return Math.abs(var5 - 1.0F);
      } else {
         return 0.0F;
      }
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

   public InventoryRange getSettlementStorage() {
      return null;
   }
}
