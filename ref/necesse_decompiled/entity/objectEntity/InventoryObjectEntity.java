package necesse.entity.objectEntity;

import java.util.ArrayList;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.tickManager.Performance;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryFilter;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.interfaces.OpenSound;
import necesse.level.maps.Level;

public class InventoryObjectEntity extends ObjectEntity implements OEInventory, OEUsers {
   private String name;
   private FairTypeDrawOptions textDrawOptions;
   private int textDrawFontSize;
   public final Inventory inventory;
   public final int slots;
   private boolean interactedWith;
   public final OEUsers.Users users = this.constructUsersObject(2000L);

   public InventoryObjectEntity(final Level var1, int var2, int var3, int var4) {
      super(var1, "inventory", var2, var3);
      this.slots = var4;
      this.name = "";
      this.setInventoryName("");
      this.inventory = new Inventory(var4) {
         public void updateSlot(int var1x) {
            super.updateSlot(var1x);
            InventoryObjectEntity.this.onInventorySlotUpdated(var1x);
            if (var1.isLoadingComplete()) {
               InventoryObjectEntity.this.triggerInteracted();
            }

         }
      };
      this.inventory.filter = new InventoryFilter() {
         public boolean isItemValid(int var1, InventoryItem var2) {
            return InventoryObjectEntity.this.isItemValid(var1, var2);
         }

         public int getItemStackLimit(int var1, InventoryItem var2) {
            return InventoryObjectEntity.this.getItemStackLimit(var1, var2);
         }
      };
      if (var1 != null && !var1.isLoadingComplete()) {
         this.interactedWith = false;
         this.inventory.spoilRateModifier = -1415.0F;
      } else {
         this.interactedWith = true;
      }

   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSafeString("name", this.name);
      var1.addSaveData(InventorySave.getSave(this.inventory));
      if (this.interactedWith) {
         var1.addBoolean("interactedWith", this.interactedWith);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.setInventoryName(var1.getSafeString("name", this.name));
      this.inventory.override(InventorySave.loadSave(var1.getFirstLoadDataByName("INVENTORY")));
      boolean var2 = var1.getBoolean("interactedWith", this.interactedWith, false);
      if (var2 != this.interactedWith) {
         this.interactedWith = var2;
         if (this.interactedWith && this.inventory.spoilRateModifier == -1415.0F) {
            this.inventory.spoilRateModifier = 1.0F;
         } else if (!this.interactedWith) {
            this.inventory.spoilRateModifier = 0.0F;
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.users.writeUsersSpawnPacket(var1);
      this.inventory.writeContent(var1);
      var1.putNextString(this.name);
      var1.putNextBoolean(this.interactedWith);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.users.readUsersSpawnPacket(var1, this);
      this.inventory.override(Inventory.getInventory(var1));
      this.setInventoryName(var1.getNextString());
      boolean var2 = var1.getNextBoolean();
      if (var2 != this.interactedWith) {
         this.interactedWith = var2;
         if (this.interactedWith && this.inventory.spoilRateModifier == -1415.0F) {
            this.inventory.spoilRateModifier = 1.0F;
         } else if (!this.interactedWith) {
            this.inventory.spoilRateModifier = 0.0F;
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

   public void serverTick() {
      super.serverTick();
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.inventory.tickItems(this);
      }));
      this.serverTickInventorySync(this.getLevel().getServer(), this);
      this.users.serverTick(this);
   }

   public void clientTick() {
      super.clientTick();
      Performance.record(this.getLevel().tickManager(), "tickItems", (Runnable)(() -> {
         this.inventory.tickItems(this);
      }));
      this.users.clientTick(this);
   }

   protected void onInventorySlotUpdated(int var1) {
   }

   public void onMouseHover(PlayerMob var1, boolean var2) {
      super.onMouseHover(var1, var2);
      if (!this.name.isEmpty()) {
         Screen.addTooltip(new FairTypeTooltip(this.getTextDrawOptions()), TooltipLocation.INTERACT_FOCUS);
      }

   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public boolean isItemValid(int var1, InventoryItem var2) {
      return true;
   }

   public int getItemStackLimit(int var1, InventoryItem var2) {
      return var2 == null ? Integer.MAX_VALUE : var2.itemStackSize();
   }

   public void triggerInteracted() {
      if (!this.interactedWith) {
         if (this.inventory.spoilRateModifier == -1415.0F) {
            this.inventory.spoilRateModifier = 1.0F;
         }

         this.interactedWith = true;
         this.markDirty();
      }

   }

   public GameMessage getInventoryName() {
      return (GameMessage)(this.name.isEmpty() ? this.getLevel().getObjectName(this.getTileX(), this.getTileY()) : new StaticMessage(this.name));
   }

   public void setInventoryName(String var1) {
      String var2 = this.name;
      if (this.getLevel().getObjectName(this.getX(), this.getY()).translate().equals(var1)) {
         this.name = "";
      } else {
         this.name = var1;
      }

      if (!this.name.equals(var2)) {
         this.textDrawOptions = null;
      }

   }

   private FairTypeDrawOptions getTextDrawOptions() {
      if (this.textDrawOptions == null || this.textDrawFontSize != Settings.tooltipTextSize) {
         FairType var1 = new FairType();
         FontOptions var2 = (new FontOptions(Settings.tooltipTextSize)).outline();
         var1.append(var2, this.getInventoryName().translate());
         var1.applyParsers(OEInventoryContainerForm.getParsers(var2));
         this.textDrawOptions = var1.getDrawOptions(FairType.TextAlign.LEFT);
         this.textDrawFontSize = var2.getSize();
      }

      return this.textDrawOptions;
   }

   public boolean canSetInventoryName() {
      return true;
   }

   public OEUsers.Users getUsersObject() {
      return this.users;
   }

   public boolean canUse(Mob var1) {
      return true;
   }

   public void onUsageChanged(Mob var1, boolean var2) {
   }

   public void remove() {
      super.remove();
      this.users.onRemoved(this);
   }

   public void onIsInUseChanged(boolean var1) {
      if (this.isClient()) {
         GameObject var2 = this.getObject();
         if (var2 instanceof OpenSound) {
            if (var1) {
               ((OpenSound)var2).playOpenSound(this.getLevel(), this.getX(), this.getY());
            } else {
               ((OpenSound)var2).playCloseSound(this.getLevel(), this.getX(), this.getY());
            }
         }
      }

   }

   public GameTooltips getMapTooltips() {
      ListGameTooltips var1 = new ListGameTooltips();
      var1.add((Object)(new FairTypeTooltip(this.getTextDrawOptions())));
      return var1;
   }
}
