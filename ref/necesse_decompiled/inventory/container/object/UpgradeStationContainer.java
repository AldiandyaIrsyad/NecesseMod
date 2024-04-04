package necesse.inventory.container.object;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import necesse.engine.GameTileRange;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.objectEntity.UpgradeStationObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.UpgradableItemContainerSlot;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;

public class UpgradeStationContainer extends Container {
   public final int UPGRADE_SLOT;
   public final UpgradeStationObjectEntity upgradeEntity;
   public final EmptyCustomAction upgradeButton;
   public final GameTileRange ingredientRange;
   private LinkedHashSet<Inventory> nearbyInventories = new LinkedHashSet();

   public UpgradeStationContainer(final NetworkClient var1, int var2, UpgradeStationObjectEntity var3, PacketReader var4) {
      super(var1, var2);
      this.upgradeEntity = var3;
      this.UPGRADE_SLOT = this.addSlot(new UpgradableItemContainerSlot(var3.inventory, 0));
      this.addInventoryQuickTransfer(this.UPGRADE_SLOT, this.UPGRADE_SLOT);
      MultiTile var5 = var3.getLevelObject().getMultiTile();
      Rectangle var6 = var5.getTileRectangle(0, 0);
      this.ingredientRange = new GameTileRange(CraftingStationContainer.nearbyCraftTileRange, var6);
      this.nearbyInventories.addAll(this.craftInventories);
      Iterator var7 = this.getNearbyInventories(var3.getLevel(), var3.getTileX(), var3.getTileY(), this.ingredientRange, OEInventory::canUseForNearbyCrafting).iterator();

      while(var7.hasNext()) {
         InventoryRange var8 = (InventoryRange)var7.next();
         this.nearbyInventories.add(var8.inventory);
      }

      this.upgradeButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            UpgradedItem var1x = UpgradeStationContainer.this.getUpgradedItem();
            if (var1x != null && UpgradeStationContainer.this.canUpgrade(var1x, false).canCraft()) {
               Recipe var2 = new Recipe("air", RecipeTechRegistry.NONE, var1x.cost);
               var2.craft(var1.playerMob.getLevel(), var1.playerMob, (Iterable)UpgradeStationContainer.this.getCraftInventories());
               if (UpgradeStationContainer.this.getSlot(UpgradeStationContainer.this.CLIENT_DRAGGING_SLOT).isClear()) {
                  UpgradeStationContainer.this.getSlot(UpgradeStationContainer.this.CLIENT_DRAGGING_SLOT).setItem(var1x.upgradedItem);
                  UpgradeStationContainer.this.getSlot(UpgradeStationContainer.this.UPGRADE_SLOT).setItem((InventoryItem)null);
               } else {
                  UpgradeStationContainer.this.getSlot(UpgradeStationContainer.this.UPGRADE_SLOT).setItem(var1x.upgradedItem);
               }

               if (var1.isServer()) {
                  var1.getServerClient().newStats.items_upgraded.increment(1);
               }
            }

         }
      });
   }

   public CanCraft canUpgrade(UpgradedItem var1, boolean var2) {
      if (var1 != null) {
         Recipe var3 = new Recipe("air", RecipeTechRegistry.NONE, var1.cost);
         return this.canCraftRecipe(var3, this.getCraftInventories(), var2);
      } else {
         return null;
      }
   }

   private boolean useNearbyInventories() {
      return this.client.isServer() ? this.client.craftingUsesNearbyInventories : (Boolean)Settings.craftingUseNearby.get();
   }

   public Collection<Inventory> getCraftInventories() {
      return (Collection)(this.useNearbyInventories() ? this.nearbyInventories : super.getCraftInventories());
   }

   public UpgradedItem getUpgradedItem() {
      ContainerSlot var1 = this.getSlot(this.UPGRADE_SLOT);
      if (!var1.isClear()) {
         InventoryItem var2 = var1.getItem();
         if (var2.item instanceof UpgradableItem && ((UpgradableItem)var2.item).getCanBeUpgradedError(var2) == null) {
            return ((UpgradableItem)var2.item).getUpgradedItem(var2);
         }
      }

      return null;
   }

   public void tick() {
      super.tick();
      if (this.client.isClient() && (Boolean)Settings.craftingUseNearby.get()) {
         boolean var1 = false;

         Inventory var3;
         for(Iterator var2 = this.nearbyInventories.iterator(); var2.hasNext(); var3.clean()) {
            var3 = (Inventory)var2.next();
            if (var3.isDirty()) {
               var1 = true;
            }
         }

         if (var1) {
            GlobalData.updateCraftable();
         }
      }

   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         return !this.upgradeEntity.removed() && this.upgradeEntity.getLevelObject().inInteractRange(var1.playerMob);
      }
   }

   public static void openAndSendContainer(int var0, ServerClient var1, Level var2, int var3, int var4, Packet var5) {
      if (!var2.isServer()) {
         throw new IllegalStateException("Level must be a server level");
      } else {
         Packet var6 = new Packet();
         PacketWriter var7 = new PacketWriter(var6);
         if (var5 != null) {
            var7.putNextContentPacket(var5);
         }

         PacketOpenContainer var8 = PacketOpenContainer.LevelObject(var0, var3, var4, var6);
         ContainerRegistry.openAndSendContainer(var1, var8);
      }
   }

   public static void openAndSendContainer(int var0, ServerClient var1, Level var2, int var3, int var4) {
      openAndSendContainer(var0, var1, var2, var3, var4, (Packet)null);
   }
}
