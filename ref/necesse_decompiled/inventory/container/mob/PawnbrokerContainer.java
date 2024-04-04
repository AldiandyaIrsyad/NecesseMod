package necesse.inventory.container.mob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.ContainerSlot;

public class PawnbrokerContainer extends ShopContainer {
   public HumanMob pawnbrokerMob;
   public boolean isPawning;
   public PlayerTempInventory inventory;
   public int INVENTORY_START = -1;
   public int INVENTORY_END = -1;
   private float profit;
   public final EmptyCustomAction quickStackButton;
   public final EmptyCustomAction transferAll;
   public final EmptyCustomAction restockButton;
   public final EmptyCustomAction lootButton;
   public final EmptyCustomAction sellButton;
   public final BooleanCustomAction setIsPawning;

   public PawnbrokerContainer(final NetworkClient var1, int var2, HumanShop var3, PacketReader var4) {
      super(var1, var2, var3, var4.getNextContentPacket());
      this.pawnbrokerMob = var3;
      this.inventory = var1.playerMob.getInv().applyTempInventoryPacket(var4.getNextContentPacket(), (var1x, var2x, var3x) -> {
         return new PlayerTempInventory(var1x, var2x, var3x) {
            public boolean shouldDispose() {
               return PawnbrokerContainer.this.isClosed();
            }

            public void updateSlot(int var1) {
               super.updateSlot(var1);
               PawnbrokerContainer.this.updateProfit();
            }
         };
      });

      for(int var5 = 0; var5 < this.inventory.getSize(); ++var5) {
         int var6 = this.addSlot(new ContainerSlot(this.inventory, var5) {
            public boolean canLockItem() {
               return false;
            }

            public String getItemInvalidError(InventoryItem var1) {
               return var1 != null && var1.item.getStringID().equals("coin") ? "" : null;
            }
         });
         if (this.INVENTORY_START == -1) {
            this.INVENTORY_START = var6;
         }

         if (this.INVENTORY_END == -1) {
            this.INVENTORY_END = var6;
         }

         this.INVENTORY_START = Math.min(this.INVENTORY_START, var6);
         this.INVENTORY_END = Math.max(this.INVENTORY_END, var6);
      }

      this.addInventoryQuickTransfer((var1x) -> {
         return this.isPawning;
      }, this.INVENTORY_START, this.INVENTORY_END);
      this.quickStackButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ArrayList var1x = new ArrayList(Collections.singleton(new InventoryRange(PawnbrokerContainer.this.inventory)));
            PawnbrokerContainer.this.quickStackToInventories(var1x, var1.playerMob.getInv().main);
         }
      });
      this.transferAll = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            for(int var1 = PawnbrokerContainer.this.CLIENT_INVENTORY_START; var1 <= PawnbrokerContainer.this.CLIENT_INVENTORY_END; ++var1) {
               if (!PawnbrokerContainer.this.getSlot(var1).isItemLocked()) {
                  PawnbrokerContainer.this.transferToSlots(PawnbrokerContainer.this.getSlot(var1), PawnbrokerContainer.this.INVENTORY_START, PawnbrokerContainer.this.INVENTORY_END, "transferall");
               }
            }

         }
      });
      this.restockButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            ArrayList var1x = new ArrayList(Collections.singleton(new InventoryRange(PawnbrokerContainer.this.inventory)));
            PawnbrokerContainer.this.restockFromInventories(var1x, var1.playerMob.getInv().main);
         }
      });
      this.lootButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            for(int var1 = PawnbrokerContainer.this.INVENTORY_START; var1 <= PawnbrokerContainer.this.INVENTORY_END; ++var1) {
               if (!PawnbrokerContainer.this.getSlot(var1).isItemLocked()) {
                  PawnbrokerContainer.this.transferToSlots(PawnbrokerContainer.this.getSlot(var1), Arrays.asList(new SlotIndexRange(PawnbrokerContainer.this.CLIENT_HOTBAR_START, PawnbrokerContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(PawnbrokerContainer.this.CLIENT_INVENTORY_START, PawnbrokerContainer.this.CLIENT_INVENTORY_END)), "lootall");
               }
            }

         }
      });
      this.sellButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            PawnbrokerContainer.this.sellItems();
         }
      });
      this.setIsPawning = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            PawnbrokerContainer.this.isPawning = var1;
         }
      });
   }

   public void lootAllControlPressed() {
      if (this.isPawning) {
         this.lootButton.runAndSend();
      }

   }

   public void quickStackControlPressed() {
      if (this.isPawning) {
         this.quickStackButton.runAndSend();
      }

   }

   public void updateProfit() {
      this.profit = 0.0F;

      for(int var1 = 0; var1 < this.inventory.getSize(); ++var1) {
         InventoryItem var2 = this.inventory.getItem(var1);
         if (var2 != null) {
            this.profit += var2.getBrokerValue();
         }
      }

   }

   public int getProfit() {
      return (int)this.profit;
   }

   public void sellItems() {
      this.updateProfit();
      if (this.client.isServer()) {
         int var1 = (int)this.profit;
         if (var1 > 0) {
            int var2 = 0;

            for(int var3 = 0; var3 < this.inventory.getSize(); ++var3) {
               InventoryItem var4 = this.inventory.getItem(var3);
               if (var4 != null) {
                  var2 += var4.getAmount();
               }
            }

            InventoryItem var6 = new InventoryItem("coin", var1);
            this.client.playerMob.getInv().addItemsDropRemaining(var6, "sell", this.client.playerMob, !this.client.isServer(), false, true);
            this.client.getServerClient().newStats.items_sold.increment(var2);
            this.client.getServerClient().newStats.money_earned.increment(var1);
         }
      } else if (this.client.isClient() && (int)this.profit > 0) {
         InventoryItem var5 = new InventoryItem("coin", (int)this.profit);
         this.client.playerMob.getInv().addItemsDropRemaining(var5, "sell", this.client.playerMob, !this.client.isServer(), false, true);
         Screen.playSound(GameResources.coins, SoundEffect.effect(this.client.playerMob));
      }

      this.inventory.clearInventory();
      this.updateProfit();
   }

   public void init() {
      super.init();
      if (this.client.isServer()) {
         this.pawnbrokerMob.addInteractClient(this.client.getServerClient());
      }

   }

   public void onClose() {
      for(int var1 = 0; var1 < this.inventory.getSize(); ++var1) {
         InventoryItem var2 = this.inventory.getItem(var1);
         if (var2 != null) {
            this.client.playerMob.getInv().addItemsDropRemaining(var2, "itempickup", this.client.playerMob, !this.client.isServer(), false);
         }
      }

      this.inventory.clearInventory();
      super.onClose();
      if (this.client.isServer()) {
         this.pawnbrokerMob.removeInteractClient(this.client.getServerClient());
      }

   }

   public static Packet getBrokerContainerContent(ServerClient var0, Packet var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextContentPacket(var1);
      var3.putNextContentPacket(var0.playerMob.getInv().getTempInventoryPacket(20));
      return var2;
   }
}
