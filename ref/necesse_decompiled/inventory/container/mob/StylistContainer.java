package necesse.inventory.container.mob;

import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.StylistHumanMob;
import necesse.gfx.GameResources;
import necesse.gfx.HumanLook;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.item.armorItem.ShirtArmorItem;
import necesse.inventory.item.armorItem.ShoesArmorItem;
import necesse.inventory.item.armorItem.WigArmorItem;

public class StylistContainer extends ShopContainer {
   private static final int HAIR_COST = 200;
   private static final int HAIR_COLOR_COST = 100;
   private static final int SHIRT_COLOR_COST = 200;
   private static final int SHOES_COLOR_COST = 100;
   public final ContentCustomAction styleButton;
   public final EmptyCustomAction styleButtonResponse;
   public StylistHumanMob stylistMob;
   public final long styleCostSeed;
   public PlayerMob newPlayer;

   public StylistContainer(final NetworkClient var1, int var2, StylistHumanMob var3, Packet var4) {
      super(var1, var2, var3, var4);
      this.stylistMob = var3;
      this.newPlayer = new PlayerMob(0L, (NetworkClient)null);
      this.newPlayer.playerName = var1.playerMob.getDisplayName();
      this.newPlayer.look = new HumanLook(var1.playerMob.look);
      this.newPlayer.getInv().giveLookArmor();
      this.styleCostSeed = this.priceSeed * (long)GameRandom.prime(42);
      this.styleButton = (ContentCustomAction)this.registerAction(new ContentCustomAction() {
         protected void run(Packet var1x) {
            if (var1.isServer()) {
               StylistContainer.this.newPlayer.look.applyContentPacket(new PacketReader(var1x));
               if (StylistContainer.this.canStyle()) {
                  int var2 = StylistContainer.this.getStyleCost();
                  var1.playerMob.getInv().main.removeItems(var1.playerMob.getLevel(), var1.playerMob, ItemRegistry.getItem("coin"), var2, "buy");
                  var1.getServerClient().newStats.money_spent.increment(var2);
                  HumanLook var3 = var1.playerMob.look;
                  var1.playerMob.look = new HumanLook(StylistContainer.this.newPlayer.look);
                  StylistContainer.this.updatePlayerInventory(var1.playerMob.getInv().main, var3);
                  StylistContainer.this.updatePlayerInventory(var1.playerMob.getInv().armor, var3);
                  StylistContainer.this.updatePlayerInventory(var1.playerMob.getInv().cosmetic, var3);
                  StylistContainer.this.updatePlayerInventory(var1.playerMob.getInv().trinkets, var3);
                  StylistContainer.this.updatePlayerInventory(var1.playerMob.getInv().drag, var3);
                  var1.getServerClient().getServer().network.sendToAllClients(new PacketPlayerAppearance(var1.getServerClient()));
                  StylistContainer.this.styleButtonResponse.runAndSend();
               }
            }

         }
      });
      this.styleButtonResponse = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isClient()) {
               Screen.playSound(GameResources.coins, SoundEffect.effect(var1.playerMob));
            }

         }
      });
   }

   private void updatePlayerInventory(PlayerInventory var1, HumanLook var2) {
      WigArmorItem var3 = (WigArmorItem)ItemRegistry.getItem("wig");
      ShirtArmorItem var4 = (ShirtArmorItem)ItemRegistry.getItem("shirt");
      ShoesArmorItem var5 = (ShoesArmorItem)ItemRegistry.getItem("shoes");

      for(int var6 = 0; var6 < var1.getSize(); ++var6) {
         if (!var1.isSlotClear(var6)) {
            InventoryItem var7 = var1.getItem(var6);
            if (var7.item == var3) {
               if (WigArmorItem.getHair(var7.getGndData()) == var2.getHair() && WigArmorItem.getHairCol(var7.getGndData()) == var2.getHairColor()) {
                  WigArmorItem.addWigData(var7, var1.player.look);
                  var1.markDirty(var6);
               }
            } else if (var7.item == var4) {
               if (ShirtArmorItem.getColor(var7.getGndData()).equals(var2.getShirtColor())) {
                  ShirtArmorItem.addColorData(var7, var1.player.look.getShirtColor());
                  var1.markDirty(var6);
               }
            } else if (var7.item == var5 && ShoesArmorItem.getColor(var7.getGndData()).equals(var2.getShoesColor())) {
               ShoesArmorItem.addColorData(var7, var1.player.look.getShoesColor());
               var1.markDirty(var6);
            }
         }
      }

   }

   public int getStyleCost() {
      int var1 = 0;
      if (this.client.playerMob.look.getHair() != this.newPlayer.look.getHair()) {
         var1 += this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(24) + (long)(this.newPlayer.look.getHair() * GameRandom.prime(82)), 200);
      }

      if (this.client.playerMob.look.getHairColor() != this.newPlayer.look.getHairColor()) {
         var1 += this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(67) + (long)(this.newPlayer.look.getHairColor() * GameRandom.prime(817)), 100);
      }

      if (!this.client.playerMob.look.getShirtColor().equals(this.newPlayer.look.getShirtColor())) {
         var1 += this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(466), 200);
      }

      if (!this.client.playerMob.look.getShoesColor().equals(this.newPlayer.look.getShoesColor())) {
         var1 += this.getRandomPrice(this.styleCostSeed * (long)GameRandom.prime(576), 100);
      }

      return var1;
   }

   private int getRandomPrice(long var1, int var3) {
      return HumanShop.getRandomHappinessMiddlePrice(new GameRandom(var1), this.settlerHappiness, var3, 5, 4);
   }

   public boolean canStyle() {
      int var1 = this.getStyleCost();
      if (var1 <= 0) {
         return false;
      } else {
         int var2 = this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("coin"), "buy");
         return var2 >= var1;
      }
   }
}
