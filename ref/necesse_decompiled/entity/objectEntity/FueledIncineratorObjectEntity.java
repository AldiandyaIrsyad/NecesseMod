package necesse.entity.objectEntity;

import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.GameResources;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class FueledIncineratorObjectEntity extends FueledProcessingInventoryObjectEntity implements OEUsers {
   public static int FUEL_TIME_ADDED = 240000;
   private SoundPlayer sound;
   public final OEUsers.Users users = this.constructUsersObject(2000L);

   public FueledIncineratorObjectEntity(Level var1, int var2, int var3, int var4, int var5) {
      super(var1, "incinerator", var2, var3, var4, var5, 0, false, false, true);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.users.writeUsersSpawnPacket(var1);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.users.readUsersSpawnPacket(var1, this);
   }

   public void clientTick() {
      super.clientTick();
      this.users.clientTick(this);
      if (this.isFuelRunning()) {
         if (this.sound == null || this.sound.isDone()) {
            this.sound = Screen.playSound(GameResources.campfireAmbient, SoundEffect.effect(this).falloffDistance(400).volume(0.25F));
         }

         if (this.sound != null) {
            this.sound.refreshLooping(1.0F);
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      this.users.serverTick(this);
   }

   public boolean isValidFuelItem(InventoryItem var1) {
      return var1.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"));
   }

   public int getNextFuelBurnTime(boolean var1) {
      return this.itemToBurnTime(var1, (var0) -> {
         return var0.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog")) ? FUEL_TIME_ADDED : 0;
      });
   }

   public boolean isValidInputItem(InventoryItem var1) {
      return true;
   }

   public FueledProcessingInventoryObjectEntity.NextProcessTask getNextProcessTask() {
      InventoryRange var1 = this.getInputInventoryRange();

      for(int var2 = var1.startSlot; var2 <= var1.endSlot; ++var2) {
         InventoryItem var3 = var1.inventory.getItem(var2);
         if (var3 != null) {
            int var4 = var3.item.getID() * GameRandom.prime(341);
            return new FueledProcessingInventoryObjectEntity.NextProcessTask(var4 == 0 ? 2260395 : var4, var3.item.getIncinerationRate());
         }
      }

      return null;
   }

   public boolean processInput() {
      InventoryRange var1 = this.getInputInventoryRange();

      for(int var2 = var1.startSlot; var2 <= var1.endSlot; ++var2) {
         InventoryItem var3 = var1.inventory.getItem(var2);
         if (var3 != null) {
            var3.setAmount(var3.getAmount() - 1);
            if (var3.getAmount() <= 0) {
               var1.inventory.setItem(var2, (InventoryItem)null);
            }

            this.compressInventory();
            var1.inventory.markDirty(var2);
            return true;
         }
      }

      return false;
   }

   protected void compressInventory() {
      InventoryRange var1 = this.getInputInventoryRange();
      int var2 = -1;

      for(int var3 = var1.startSlot; var3 <= var1.endSlot; ++var3) {
         InventoryItem var4 = var1.inventory.getItem(var3);
         if (var4 == null) {
            if (var2 == -1) {
               var2 = var3;
            }
         } else if (var2 != -1) {
            var1.inventory.markDirty(var2);
            var1.inventory.setItem(var2, var4);
            var1.inventory.setItem(var3, (InventoryItem)null);
            var1.inventory.markDirty(var3);
            ++var2;
         }
      }

   }

   public ProcessingHelp getProcessingHelp() {
      return null;
   }

   public InventoryRange getSettlementStorage() {
      return this.getInputInventoryRange();
   }

   public InventoryRange getSettlementFuelInventoryRange() {
      return this.getFuelInventoryRange();
   }

   public SettlementRequestOptions getSettlementFuelRequestOptions() {
      return new SettlementRequestOptions(5, 10) {
         public SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords var1) {
            return ((SettlementStorageGlobalIngredientIDIndex)var1.getIndex(SettlementStorageGlobalIngredientIDIndex.class)).getGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID("anylog"));
         }
      };
   }

   public void setupDefaultSettlementStorage(SettlementInventory var1) {
      super.setupDefaultSettlementStorage(var1);
      var1.filter.master.setAllowed(false);
      var1.priority = SettlementInventory.Priority.LAST.priorityValue;
   }

   public OEUsers.Users getUsersObject() {
      return this.users;
   }

   public boolean canUse(Mob var1) {
      return true;
   }

   public void onUsageChanged(Mob var1, boolean var2) {
   }

   public void onIsInUseChanged(boolean var1) {
   }

   public void remove() {
      super.remove();
      this.users.onRemoved(this);
   }
}
