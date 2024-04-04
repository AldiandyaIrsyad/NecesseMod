package necesse.entity.objectEntity;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEProgressUpdate;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;

public abstract class ProcessingInventoryObjectEntity extends ObjectEntity implements OEInventory, IProgressObjectEntity {
   public final Inventory inventory;
   public final int inputSlots;
   protected boolean updateProcessing;
   protected boolean isProcessing;
   protected int currentProcessTime;
   public long lastProcessTime;
   protected boolean useWorldTime = true;
   private boolean processingDirty = false;

   public ProcessingInventoryObjectEntity(Level var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4);
      this.inputSlots = var5;
      this.inventory = new Inventory(var5 + var6) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            ProcessingInventoryObjectEntity.this.onSlotUpdate(var1);
         }
      };
      this.inventory.filter = (var2x, var3x) -> {
         if (var3x == null) {
            return true;
         } else {
            return var2x < var5 ? this.isValidInputItem(var3x) : false;
         }
      };
      this.updateProcessing = true;
   }

   protected void onSlotUpdate(int var1) {
      if (var1 < this.inputSlots) {
         this.updateProcessing = true;
      }

   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSaveData(InventorySave.getSave(this.inventory));
      var1.addLong("lastProcessTime", this.lastProcessTime);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.inventory.override(InventorySave.loadSave(var1.getFirstLoadDataByName("INVENTORY")));
      this.lastProcessTime = var1.getLong("lastProcessTime", 0L);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.inventory.writeContent(var1);
      var1.putNextBoolean(this.isProcessing);
      var1.putNextLong(this.lastProcessTime);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.inventory.override(Inventory.getInventory(var1));
      this.isProcessing = var1.getNextBoolean();
      this.lastProcessTime = var1.getNextLong();
   }

   public void setupProgressPacket(PacketWriter var1) {
      var1.putNextBoolean(this.isProcessing);
      var1.putNextLong(this.lastProcessTime);
   }

   public void applyProgressPacket(PacketReader var1) {
      this.isProcessing = var1.getNextBoolean();
      this.lastProcessTime = var1.getNextLong();
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
      long var1 = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
      if (!this.isProcessing) {
         this.lastProcessTime = var1;
      }

   }

   public void serverTick() {
      super.serverTick();
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.inventory.tickItems(this);
      }));
      boolean var1 = this.isProcessing;
      long var2 = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
      if (this.lastProcessTime <= 0L) {
         this.lastProcessTime = var2;
      }

      if (this.updateProcessing || this.isProcessing) {
         while(true) {
            if (this.updateProcessing) {
               this.isProcessing = this.canProcessInput();
               this.currentProcessTime = this.getProcessTime();
            }

            if (!this.isProcessing || this.lastProcessTime + (long)this.currentProcessTime > var2) {
               break;
            }

            if (!this.processInput()) {
               this.lastProcessTime = var2 - (long)this.currentProcessTime + (long)Math.min(this.currentProcessTime / 10, 1000);
               break;
            }

            this.lastProcessTime += (long)this.currentProcessTime;
            this.markProcessingDirty();
         }

         this.updateProcessing = false;
      }

      if (!this.isProcessing) {
         this.lastProcessTime = var2;
      }

      if (var1 != this.isProcessing) {
         this.markProcessingDirty();
      }

      if (this.processingDirty) {
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsWithTile(new PacketOEProgressUpdate(this), this.getLevel(), this.getTileX(), this.getTileY());
         }

         this.processingDirty = false;
      }

      this.serverTickInventorySync(this.getLevel().getServer(), this);
   }

   public void markClean() {
      super.markClean();
      this.inventory.clean();
      this.processingDirty = false;
   }

   public abstract boolean isValidInputItem(InventoryItem var1);

   public abstract boolean canProcessInput();

   public abstract int getProcessTime();

   public abstract boolean processInput();

   public abstract ProcessingHelp getProcessingHelp();

   public boolean canAddOutput(InventoryItem... var1) {
      Inventory var2 = this.inventory.copy();
      InventoryItem[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         InventoryItem var6 = var3[var5];
         InventoryItem var7 = var6.copy();
         if (!var2.addItem(this.getLevel(), (PlayerMob)null, var7, this.inputSlots, var2.getSize() - 1, false, "add", true, false, (InventoryAddConsumer)null)) {
            return false;
         }

         if (var7.getAmount() > 0) {
            return false;
         }
      }

      return true;
   }

   public void addOutput(InventoryItem... var1) {
      InventoryItem[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         InventoryItem var5 = var2[var4];
         this.inventory.addItem(this.getLevel(), (PlayerMob)null, var5, this.inputSlots, this.inventory.getSize() - 1, false, "add", true, false, (InventoryAddConsumer)null);
      }

   }

   public void markProcessingDirty() {
      this.processingDirty = true;
   }

   public boolean isProcessing() {
      return this.isProcessing;
   }

   public float getProcessingProgress() {
      if (this.isProcessing) {
         long var1 = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
         int var3 = this.getProcessTime();
         return var3 <= 0 ? 1.0F : GameMath.limit((float)(var1 - this.lastProcessTime) / (float)var3, 0.0F, 1.0F);
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

   public InventoryRange getInputInventoryRange() {
      return new InventoryRange(this.inventory, 0, this.inputSlots - 1);
   }

   public InventoryRange getOutputInventoryRange() {
      return new InventoryRange(this.inventory, this.inputSlots, this.inventory.getSize() - 1);
   }
}
