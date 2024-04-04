package necesse.inventory.container.mob;

import java.util.Objects;
import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.EnchantableSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class MageContainer extends ShopContainer {
   public final EmptyCustomAction enchantButton;
   public final ContentCustomAction enchantButtonResponse;
   public final BooleanCustomAction setIsEnchanting;
   private boolean isEnchanting;
   public final int ENCHANT_SLOT;
   public MageHumanMob mageMob;
   public final long enchantCostSeed;
   public final PlayerTempInventory enchantInv;

   public MageContainer(final NetworkClient var1, int var2, MageHumanMob var3, PacketReader var4) {
      super(var1, var2, var3, var4.getNextContentPacket());
      this.enchantInv = var1.playerMob.getInv().applyTempInventoryPacket(var4.getNextContentPacket(), (var1x) -> {
         return this.isClosed();
      });
      this.ENCHANT_SLOT = this.addSlot(new EnchantableSlot(this.enchantInv, 0));
      this.addInventoryQuickTransfer((var1x) -> {
         return this.isEnchanting;
      }, this.ENCHANT_SLOT, this.ENCHANT_SLOT);
      this.mageMob = var3;
      this.isEnchanting = false;
      this.enchantCostSeed = this.priceSeed * (long)GameRandom.prime(28);
      this.enchantButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               if (MageContainer.this.canEnchant()) {
                  int var1x = MageContainer.this.getEnchantCost();
                  InventoryItem var2 = MageContainer.this.getSlot(MageContainer.this.ENCHANT_SLOT).getItem();
                  ((Enchantable)var2.item).setEnchantment(var2, MageContainer.this.getBiasedEnchantmentID(var2));
                  if (var1.getServerClient().achievementsLoaded()) {
                     var1.getServerClient().achievements().ENCHANT_ITEM.markCompleted(var1.getServerClient());
                  }

                  var1.getServerClient().newStats.money_spent.increment(var1x);
                  var1.playerMob.getInv().main.removeItems(var1.playerMob.getLevel(), var1.playerMob, ItemRegistry.getItem("coin"), var1x, "buy");
                  var1.getServerClient().newStats.items_enchanted.increment(1);
                  Packet var3 = InventoryItem.getContentPacket(var2);
                  MageContainer.this.enchantButtonResponse.runAndSend(var3);
               }

               MageContainer.this.getSlot(MageContainer.this.ENCHANT_SLOT).markDirty();
            }

         }
      });
      this.enchantButtonResponse = (ContentCustomAction)this.registerAction(new ContentCustomAction() {
         protected void run(Packet var1x) {
            if (var1.isClient()) {
               InventoryItem var2 = InventoryItem.fromContentPacket(var1x);
               var1.playerMob.getLevel().hudManager.addElement(new ItemPickupText(var1.playerMob, var2));
               Screen.playSound(GameResources.pop, SoundEffect.effect(var1.playerMob));
            }

         }
      });
      this.setIsEnchanting = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            MageContainer.this.isEnchanting = var1;
         }
      });
   }

   public int getEnchantCost() {
      if (this.getSlot(this.ENCHANT_SLOT).isClear()) {
         return 0;
      } else {
         InventoryItem var1 = this.getSlot(this.ENCHANT_SLOT).getItem();
         if (var1.item.isEnchantable(var1)) {
            Enchantable var2 = (Enchantable)var1.item;
            GameRandom var3 = new GameRandom(this.enchantCostSeed + (long)(var1.item.getID() * GameRandom.prime(54)) + (long)(var2.getEnchantmentID(var1) * GameRandom.prime(13)));
            return Math.abs(var2.getRandomEnchantCost(var1, var3, this.settlerHappiness));
         } else {
            return 0;
         }
      }
   }

   private int getBiasedEnchantmentID(InventoryItem var1) {
      if (var1.item.isEnchantable(var1)) {
         Enchantable var2 = (Enchantable)var1.item;
         ItemEnchantment[] var3 = (ItemEnchantment[])var2.getValidEnchantmentIDs(var1).stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter((var0) -> {
            return var0.getEnchantCostMod() >= 1.0F;
         }).toArray((var0) -> {
            return new ItemEnchantment[var0];
         });
         ItemEnchantment[] var4 = (ItemEnchantment[])var2.getValidEnchantmentIDs(var1).stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter((var0) -> {
            return var0.getEnchantCostMod() <= 1.0F;
         }).toArray((var0) -> {
            return new ItemEnchantment[var0];
         });
         float var5 = 0.05F;
         float var7 = 0.0F;
         int var8 = GameMath.limit(this.settlerHappiness, 0, 100);
         TicketSystemList var9 = new TicketSystemList();
         if (var8 > 50) {
            var7 = (float)(var8 - 50) * var5;
         } else if (var8 < 50) {
            var7 = (float)(-var8 + 50) * var5;
         }

         ItemEnchantment[] var10 = var3;
         int var11 = var3.length;

         float var6;
         int var12;
         ItemEnchantment var13;
         for(var12 = 0; var12 < var11; ++var12) {
            var13 = var10[var12];
            var6 = (var13.getEnchantCostMod() - 1.0F) * 100.0F;
            if (var8 > 50) {
               var9.addObject(100 + (int)(var7 * var6), var13);
            } else {
               var9.addObject(100 - (int)(var7 * var6), var13);
            }
         }

         var10 = var4;
         var11 = var4.length;

         for(var12 = 0; var12 < var11; ++var12) {
            var13 = var10[var12];
            var6 = (var13.getEnchantCostMod() - 1.0F) * 100.0F;
            if (var8 > 50) {
               var9.addObject(100 + (int)(var7 * var6), var13);
            } else {
               var9.addObject(100 - (int)(var7 * var6), var13);
            }
         }

         ItemEnchantment var14 = (ItemEnchantment)var9.getRandomObject(GameRandom.globalRandom);
         return EnchantmentRegistry.getEnchantmentID(var14.getStringID());
      } else {
         return 0;
      }
   }

   public boolean isItemEnchantable() {
      if (this.getSlot(this.ENCHANT_SLOT).isClear()) {
         return false;
      } else {
         InventoryItem var1 = this.getSlot(this.ENCHANT_SLOT).getItem();
         return var1.item.isEnchantable(var1);
      }
   }

   public boolean canEnchant() {
      if (!this.isItemEnchantable()) {
         return false;
      } else {
         boolean var1 = !this.getSlot(this.ENCHANT_SLOT).isClear();
         if (var1) {
            int var2 = this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("coin"), "buy");
            if (var2 < this.getEnchantCost()) {
               var1 = false;
            }
         }

         return var1;
      }
   }

   public static Packet getMageContainerContent(MageHumanMob var0, ServerClient var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextContentPacket(var0.getShopItemsContentPacket(var1));
      var3.putNextContentPacket(var1.playerMob.getInv().getTempInventoryPacket(1));
      return var2;
   }
}
