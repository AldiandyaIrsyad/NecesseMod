package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.EnchantableSpecificSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.miscItem.EnchantingScrollItem;

public class EnchantingScrollContainer extends Container {
   public final EmptyCustomAction enchantButton;
   public final int ENCHANT_SLOT;
   public final PlayerTempInventory ingredientInv;
   public final ItemEnchantment enchantment;
   public final EnchantingScrollItem.EnchantScrollType scrollType;
   private final ContainerSlot scrollSlot;

   public EnchantingScrollContainer(final NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      PacketReader var4 = new PacketReader(var3);
      int var5 = var4.getNextInt();
      Packet var6 = var4.getNextContentPacket();
      this.ingredientInv = var1.playerMob.getInv().applyTempInventoryPacket(var6, (var1x) -> {
         return this.isClosed();
      });
      ContainerSlot var7 = this.getSlot(var5);
      if (var7 != null && !var7.isClear() && var7.getItem().item instanceof EnchantingScrollItem) {
         InventoryItem var8 = var7.getItem();
         this.lockSlot(var5);
         this.scrollSlot = var7;
         EnchantingScrollItem var9 = (EnchantingScrollItem)var8.item;
         this.enchantment = var9.getEnchantment(var8);
         this.scrollType = var9.getType(this.enchantment);
      } else {
         this.scrollSlot = null;
         this.enchantment = null;
         this.scrollType = null;
      }

      this.ENCHANT_SLOT = this.addSlot(new EnchantableSpecificSlot(this.ingredientInv, 0, this.enchantment));
      this.addInventoryQuickTransfer(this.ENCHANT_SLOT, this.ENCHANT_SLOT);
      this.enchantButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               ServerClient var1x = var1.getServerClient();
               if (EnchantingScrollContainer.this.canEnchant() && EnchantingScrollContainer.this.isValid(var1x) && EnchantingScrollContainer.this.scrollSlot != null) {
                  InventoryItem var2 = EnchantingScrollContainer.this.getSlot(EnchantingScrollContainer.this.ENCHANT_SLOT).getItem();
                  InventoryItem var3 = EnchantingScrollContainer.this.scrollSlot.getItem();
                  ItemEnchantment var4 = ((EnchantingScrollItem)var3.item).getEnchantment(var3);
                  ((Enchantable)var2.item).setEnchantment(var2, var4.getID());
                  if (var1x.achievementsLoaded()) {
                     var1x.achievements().ENCHANT_ITEM.markCompleted(var1x);
                  }

                  var1x.newStats.items_enchanted.increment(1);
                  InventoryItem var5 = EnchantingScrollContainer.this.getSlot(EnchantingScrollContainer.this.CLIENT_DRAGGING_SLOT).getItem();
                  if (var5 != null) {
                     var1x.playerMob.getInv().addItemsDropRemaining(var5, "addback", var1.playerMob, false, true);
                  }

                  EnchantingScrollContainer.this.getSlot(EnchantingScrollContainer.this.ENCHANT_SLOT).setItem((InventoryItem)null);
                  EnchantingScrollContainer.this.getSlot(EnchantingScrollContainer.this.CLIENT_DRAGGING_SLOT).setItem(var2);
                  EnchantingScrollContainer.this.scrollSlot.setAmount(EnchantingScrollContainer.this.scrollSlot.getItemAmount() - 1);
                  EnchantingScrollContainer.this.close();
               } else {
                  EnchantingScrollContainer.this.close();
               }
            }

         }
      });
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else if (this.scrollSlot == null) {
         return false;
      } else if (this.scrollSlot.isClear()) {
         return false;
      } else {
         InventoryItem var2 = this.scrollSlot.getItem();
         return var2.item instanceof EnchantingScrollItem && ((EnchantingScrollItem)var2.item).getEnchantment(var2) == this.enchantment;
      }
   }

   public boolean canEnchant() {
      if (this.getSlot(this.ENCHANT_SLOT).isClear()) {
         return false;
      } else {
         InventoryItem var1 = this.getSlot(this.ENCHANT_SLOT).getItem();
         return var1.item.isEnchantable(var1);
      }
   }

   public static Packet getContainerContent(ServerClient var0, int var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1);
      var3.putNextContentPacket(var0.playerMob.getInv().getTempInventoryPacket(1));
      return var2;
   }
}
