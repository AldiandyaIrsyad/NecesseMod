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
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;

public abstract class FueledProcessingInventoryObjectEntity extends ObjectEntity implements OEInventory, IProgressObjectEntity {
   public final Inventory inventory;
   public final int fuelSlots;
   public final int inputSlots;
   public final int outputSlots;
   public final boolean fuelAlwaysOn;
   public final boolean fuelRunsOutWhenNotProcessing;
   public final boolean runningOutOfFuelResetsProcessingTime;
   public boolean useWorldTime = true;
   protected boolean forceUpdate = true;
   protected boolean keepFuelRunning;
   protected long lastTickedTime;
   protected int remainingFuelTime;
   protected int remainingProcessingTime;
   protected int usedFuelTime;
   protected int usedProcessingTime;
   protected boolean processingPaused;
   protected boolean fuelPaused;
   protected int lastProcessingHash;
   protected boolean progressDirty = false;

   public FueledProcessingInventoryObjectEntity(Level var1, String var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, boolean var10) {
      super(var1, var2, var3, var4);
      this.fuelSlots = var5;
      this.inputSlots = var6;
      this.outputSlots = var7;
      this.fuelAlwaysOn = var8;
      this.fuelRunsOutWhenNotProcessing = var9;
      this.runningOutOfFuelResetsProcessingTime = var10;
      this.inventory = new Inventory(var5 + var6 + var7) {
         public void updateSlot(int var1) {
            super.updateSlot(var1);
            FueledProcessingInventoryObjectEntity.this.onSlotUpdate(var1);
         }
      };
      this.inventory.filter = (var3x, var4x) -> {
         if (var4x == null) {
            return true;
         } else if (var3x < var5) {
            return this.isValidFuelItem(var4x);
         } else {
            return var3x < var5 + var6 ? this.isValidInputItem(var4x) : false;
         }
      };
   }

   protected void onSlotUpdate(int var1) {
      this.forceNextUpdate();
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSaveData(InventorySave.getSave(this.inventory));
      var1.addLong("lastTickedTime", this.lastTickedTime);
      var1.addInt("remainingFuelTime", this.remainingFuelTime);
      var1.addInt("remainingProcessingTime", this.remainingProcessingTime);
      var1.addInt("usedFuelTime", this.usedFuelTime);
      var1.addInt("usedProcessingTime", this.usedProcessingTime);
      var1.addInt("lastProcessingHash", this.lastProcessingHash);
      if (!this.fuelAlwaysOn) {
         var1.addBoolean("keepFuelRunning", this.keepFuelRunning);
      }

      var1.addBoolean("fuelPaused", this.fuelPaused);
      var1.addBoolean("processingPaused", this.processingPaused);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.inventory.override(InventorySave.loadSave(var1.getFirstLoadDataByName("INVENTORY")));
      this.lastTickedTime = var1.getLong("lastTickedTime", 0L);
      this.remainingFuelTime = var1.getInt("remainingFuelTime", 0);
      this.remainingProcessingTime = var1.getInt("remainingProcessingTime", 0);
      this.usedFuelTime = var1.getInt("usedFuelTime", 0);
      this.usedProcessingTime = var1.getInt("usedProcessingTime", 0);
      this.lastProcessingHash = var1.getInt("lastProcessingHash", 0);
      if (!this.fuelAlwaysOn) {
         this.keepFuelRunning = var1.getBoolean("keepFuelRunning", this.keepFuelRunning);
      }

      this.fuelPaused = var1.getBoolean("fuelPaused", false);
      this.processingPaused = var1.getBoolean("processingPaused", false);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.inventory.writeContent(var1);
      var1.putNextInt(this.remainingFuelTime);
      var1.putNextInt(this.remainingProcessingTime);
      var1.putNextInt(this.usedFuelTime);
      var1.putNextInt(this.usedProcessingTime);
      if (!this.fuelAlwaysOn) {
         var1.putNextBoolean(this.keepFuelRunning);
      }

      var1.putNextBoolean(this.processingPaused);
      var1.putNextBoolean(this.fuelPaused);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.inventory.override(Inventory.getInventory(var1));
      boolean var2 = this.isFuelRunning();
      this.remainingFuelTime = var1.getNextInt();
      this.remainingProcessingTime = var1.getNextInt();
      this.usedFuelTime = var1.getNextInt();
      this.usedProcessingTime = var1.getNextInt();
      if (!this.fuelAlwaysOn) {
         this.keepFuelRunning = var1.getNextBoolean();
      }

      this.processingPaused = var1.getNextBoolean();
      this.fuelPaused = var1.getNextBoolean();
      if (var2 != this.isFuelRunning()) {
         this.getLevel().lightManager.updateStaticLight(this.getX(), this.getY());
      }

   }

   public void setupProgressPacket(PacketWriter var1) {
      var1.putNextInt(this.remainingFuelTime);
      var1.putNextInt(this.remainingProcessingTime);
      var1.putNextInt(this.usedFuelTime);
      var1.putNextInt(this.usedProcessingTime);
      if (!this.fuelAlwaysOn) {
         var1.putNextBoolean(this.keepFuelRunning);
      }

      var1.putNextBoolean(this.processingPaused);
      var1.putNextBoolean(this.fuelPaused);
   }

   public void applyProgressPacket(PacketReader var1) {
      boolean var2 = this.isFuelRunning();
      this.remainingFuelTime = var1.getNextInt();
      this.remainingProcessingTime = var1.getNextInt();
      this.usedFuelTime = var1.getNextInt();
      this.usedProcessingTime = var1.getNextInt();
      if (!this.fuelAlwaysOn) {
         this.keepFuelRunning = var1.getNextBoolean();
      }

      this.processingPaused = var1.getNextBoolean();
      this.fuelPaused = var1.getNextBoolean();
      if (var2 != this.isFuelRunning()) {
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
      long var1 = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
      long var3 = this.lastTickedTime == 0L ? var1 : this.lastTickedTime;
      long var5 = Math.max(0L, var1 - var3);
      if (!this.fuelPaused) {
         this.remainingFuelTime = (int)Math.max((long)this.remainingFuelTime - var5, 0L);
         if (this.remainingFuelTime > 0) {
            this.usedFuelTime = (int)((long)this.usedFuelTime + var5);
         }
      }

      if (!this.processingPaused) {
         this.remainingProcessingTime = (int)Math.max((long)this.remainingProcessingTime - var5, 0L);
         if (this.remainingProcessingTime > 0) {
            this.usedProcessingTime = (int)((long)this.usedProcessingTime + var5);
         }
      }

      this.lastTickedTime = var1;
   }

   public void serverTick() {
      super.serverTick();
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.inventory.tickItems(this);
      }));
      long var1 = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
      if (this.forceUpdate || this.remainingFuelTime > 0 || this.remainingProcessingTime > 0) {
         long var3 = this.lastTickedTime == 0L ? var1 : this.lastTickedTime;
         long var5 = var1 - var3;

         while(var5 > 0L || this.forceUpdate) {
            if (this.forceUpdate) {
               this.forceUpdate = false;
               if (this.keepFuelRunning || this.fuelAlwaysOn) {
                  if (this.remainingFuelTime <= 0) {
                     this.useFuel(true);
                  }

                  if (this.fuelPaused) {
                     this.fuelPaused = false;
                     this.markProgressDirty();
                  }
               }

               NextProcessTask var7 = this.getNextProcessTask();
               if (var7 != null) {
                  if (this.lastProcessingHash != var7.recipeHash) {
                     this.markProgressDirty();
                     this.lastProcessingHash = var7.recipeHash;
                     this.remainingProcessingTime = var7.processTime;
                     this.usedProcessingTime = 0;
                  }
               } else {
                  if (this.lastProcessingHash != 0 || this.remainingProcessingTime != 0 || this.usedProcessingTime != 0) {
                     this.markProgressDirty();
                  }

                  this.lastProcessingHash = 0;
                  this.remainingProcessingTime = 0;
                  this.usedProcessingTime = 0;
               }
            }

            boolean var11 = true;
            boolean var12;
            if (this.lastProcessingHash != 0) {
               var11 = false;
               var12 = false;
               if (this.remainingFuelTime <= 0) {
                  if (!this.useFuel(true)) {
                     var12 = true;
                     if (this.runningOutOfFuelResetsProcessingTime) {
                        if (this.usedProcessingTime > 0 || !this.processingPaused) {
                           this.markProgressDirty();
                        }

                        this.remainingProcessingTime += this.usedProcessingTime;
                        this.usedProcessingTime = 0;
                        this.processingPaused = true;
                     } else {
                        if (!this.processingPaused) {
                           this.markProgressDirty();
                        }

                        this.processingPaused = true;
                     }
                  } else {
                     if (this.processingPaused) {
                        this.markProgressDirty();
                     }

                     this.processingPaused = false;
                  }
               } else {
                  if (this.processingPaused) {
                     this.markProgressDirty();
                  }

                  this.processingPaused = false;
               }

               long var9 = Math.max(0L, GameMath.min((long)this.remainingFuelTime, (long)this.remainingProcessingTime, var5));
               this.remainingFuelTime = (int)((long)this.remainingFuelTime - var9);
               this.usedFuelTime = (int)((long)this.usedFuelTime + var9);
               this.remainingProcessingTime = (int)((long)this.remainingProcessingTime - var9);
               this.usedProcessingTime = (int)((long)this.usedProcessingTime + var9);
               var5 -= var9;
               if (this.remainingProcessingTime <= 0) {
                  if (this.processInput()) {
                     this.markProgressDirty();
                     this.lastProcessingHash = 0;
                     this.remainingProcessingTime = 0;
                     this.usedProcessingTime = 0;
                     this.forceNextUpdate();
                  } else {
                     if (!this.fuelPaused) {
                        this.markProgressDirty();
                     }

                     this.fuelPaused = true;
                     var11 = true;
                  }
               } else if (this.fuelPaused) {
                  this.fuelPaused = false;
                  this.markProgressDirty();
               }

               if (this.remainingFuelTime <= 0) {
                  if (!var12) {
                     this.forceNextUpdate();
                  } else {
                     var11 = true;
                  }

                  if (this.usedFuelTime != 0 || !this.fuelPaused) {
                     this.markProgressDirty();
                  }

                  this.usedFuelTime = 0;
                  this.fuelPaused = false;
               }
            } else {
               if (this.remainingFuelTime > 0) {
                  if (!this.fuelRunsOutWhenNotProcessing && !this.fuelAlwaysOn && !this.keepFuelRunning) {
                     if (!this.fuelPaused) {
                        this.markProgressDirty();
                     }

                     this.fuelPaused = true;
                  } else {
                     var11 = false;
                     long var8 = Math.max(0L, GameMath.min((long)this.remainingFuelTime, var5));
                     this.remainingFuelTime = (int)((long)this.remainingFuelTime - var8);
                     this.usedFuelTime = (int)((long)this.usedFuelTime + var8);
                     var5 -= var8;
                  }
               } else {
                  if (this.usedFuelTime != 0 || !this.fuelPaused) {
                     this.markProgressDirty();
                  }

                  this.usedFuelTime = 0;
                  if (!this.fuelAlwaysOn && !this.keepFuelRunning) {
                     this.fuelPaused = true;
                  } else {
                     var11 = false;
                     var12 = !this.useFuel(true);
                     if (this.remainingFuelTime <= 0) {
                        if (!var12) {
                           this.forceNextUpdate();
                        } else {
                           var11 = true;
                        }
                     }

                     this.fuelPaused = false;
                  }
               }

               if (!this.processingPaused) {
                  this.markProgressDirty();
               }

               this.processingPaused = true;
            }

            if (var11) {
               break;
            }
         }
      }

      this.lastTickedTime = var1;
      if (this.progressDirty) {
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsAt(new PacketOEProgressUpdate(this), (Level)this.getLevel());
         }

         this.progressDirty = false;
         this.getLevel().lightManager.updateStaticLight(this.getX(), this.getY());
      }

      this.serverTickInventorySync(this.getLevel().getServer(), this);
   }

   public boolean useFuel(boolean var1) {
      boolean var2 = this.isFuelRunning();
      int var3 = Math.max(0, this.getNextFuelBurnTime(true));
      this.remainingFuelTime += var3;
      if (this.remainingFuelTime > 0 && this.fuelPaused && var1) {
         this.fuelPaused = false;
         this.markProgressDirty();
      }

      if (var3 != 0) {
         this.markProgressDirty();
      }

      if (var2 != this.isFuelRunning()) {
         this.getLevel().lightManager.updateStaticLight(this.getX(), this.getY());
      }

      return var3 > 0;
   }

   public boolean canUseFuel() {
      return this.getNextFuelBurnTime(false) > 0;
   }

   public void markClean() {
      super.markClean();
      this.inventory.clean();
      this.progressDirty = false;
   }

   public abstract boolean isValidFuelItem(InventoryItem var1);

   public abstract int getNextFuelBurnTime(boolean var1);

   protected int itemToBurnTime(boolean var1, Function<InventoryItem, Integer> var2) {
      for(int var3 = this.fuelSlots - 1; var3 >= 0; --var3) {
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

   public abstract boolean isValidInputItem(InventoryItem var1);

   public abstract NextProcessTask getNextProcessTask();

   public abstract boolean processInput();

   public abstract ProcessingHelp getProcessingHelp();

   public boolean canAddOutput(InventoryItem... var1) {
      Inventory var2 = this.inventory.copy();
      InventoryItem[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         InventoryItem var6 = var3[var5];
         InventoryItem var7 = var6.copy();
         if (!var2.addItem(this.getLevel(), (PlayerMob)null, var7, this.fuelSlots + this.inputSlots, var2.getSize() - 1, false, "add", true, false, (InventoryAddConsumer)null)) {
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
         this.inventory.addItem(this.getLevel(), (PlayerMob)null, var5, this.fuelSlots + this.inputSlots, this.inventory.getSize() - 1, false, "add", true, false, (InventoryAddConsumer)null);
      }

   }

   public void forceNextUpdate() {
      this.forceUpdate = true;
   }

   public void markProgressDirty() {
      this.progressDirty = true;
   }

   public boolean isFuelRunning() {
      return !this.fuelPaused && (this.remainingFuelTime > 0 || this.usedFuelTime > 0);
   }

   public boolean hasFuel() {
      return this.remainingFuelTime > 0 || this.getNextFuelBurnTime(false) > 0;
   }

   public boolean isProcessingRunning() {
      return !this.processingPaused;
   }

   public boolean shouldKeepFuelRunning() {
      return this.keepFuelRunning;
   }

   public void setKeepFuelRunning(boolean var1) {
      if (this.keepFuelRunning != var1) {
         this.keepFuelRunning = var1;
         this.forceNextUpdate();
         this.markProgressDirty();
      }

   }

   public boolean shouldBeAbleToChangeKeepFuelRunning() {
      return true;
   }

   public float getFuelProgress() {
      if (this.remainingFuelTime > 0) {
         int var1 = this.usedFuelTime + this.remainingFuelTime;
         return Math.abs(GameMath.limit((float)this.usedFuelTime / (float)var1, 0.0F, 1.0F) - 1.0F);
      } else {
         return 0.0F;
      }
   }

   public float getProcessingProgress() {
      if (this.remainingProcessingTime > 0) {
         int var1 = this.usedProcessingTime + this.remainingProcessingTime;
         return GameMath.limit((float)this.usedProcessingTime / (float)var1, 0.0F, 1.0F);
      } else {
         return this.processingPaused ? 0.0F : 1.0F;
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

   public InventoryRange getFuelInventoryRange() {
      return new InventoryRange(this.inventory, 0, this.fuelSlots - 1);
   }

   public InventoryRange getInputInventoryRange() {
      return new InventoryRange(this.inventory, this.fuelSlots, this.fuelSlots + this.inputSlots - 1);
   }

   public InventoryRange getOutputInventoryRange() {
      return new InventoryRange(this.inventory, this.fuelSlots + this.inputSlots, this.inventory.getSize() - 1);
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
   }

   public static class NextProcessTask {
      public final int recipeHash;
      public final int processTime;

      public NextProcessTask(int var1, int var2) {
         this.recipeHash = var1 == 0 ? 1415926 : var1;
         this.processTime = var2;
      }
   }
}
