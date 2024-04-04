package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.ContainerSlot;

public class RecipeBookContainer extends Container {
   public final int INGREDIENT_SLOT;
   public final PlayerTempInventory ingredientInv;
   public final PlayerInventorySlot guideSlot;
   public final EmptyCustomAction clearingIngredientSlot;

   public RecipeBookContainer(NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      PacketReader var4 = new PacketReader(var3);
      int var5 = var4.getNextInt();
      int var6 = var4.getNextInt();
      this.guideSlot = new PlayerInventorySlot(var5, var6);
      Packet var7 = var4.getNextContentPacket();
      this.ingredientInv = var1.playerMob.getInv().applyTempInventoryPacket(var7, (var1x) -> {
         return this.isClosed();
      });
      this.INGREDIENT_SLOT = this.addSlot(new ContainerSlot(this.ingredientInv, 0));
      this.addInventoryQuickTransfer(this.INGREDIENT_SLOT, this.INGREDIENT_SLOT);
      InventoryItem var8 = this.guideSlot.getItem(var1.playerMob.getInv());
      if (var8 != null && var8.item.getStringID().equals("recipebook")) {
         this.lockSlot(this.guideSlot);
      }

      this.clearingIngredientSlot = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ContainerSlot var1 = RecipeBookContainer.this.getSlot(RecipeBookContainer.this.INGREDIENT_SLOT);
            InventoryItem var2 = var1.getItem();
            if (var2 != null) {
               PlayerMob var3 = this.getContainer().client.playerMob;
               var3.getInv().addItemsDropRemaining(var2, "addback", var3, false, true);
               var1.setItem((InventoryItem)null);
            }

         }
      });
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         InventoryItem var2 = this.guideSlot.getItem(var1.playerMob.getInv());
         return var2 != null && var2.item.getStringID().equals("recipebook");
      }
   }

   public static Packet getContainerContent(ServerClient var0, PlayerInventorySlot var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1.inventoryID);
      var3.putNextInt(var1.slot);
      var3.putNextContentPacket(var0.playerMob.getInv().getTempInventoryPacket(1));
      return var2;
   }
}
