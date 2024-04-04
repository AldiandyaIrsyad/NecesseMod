package necesse.inventory.container.mob;

import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.AlchemistHumanMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;

public class AlchemistContainer extends ShopContainer {
   private static final float BASE_COST = 10.0F;
   private static final float SHAPE_COST = 5.0F;
   private static final float COLOR_COST = 5.0F;
   private static final float CRACKLE_COST = 5.0F;
   public final ContentCustomAction buyFireworkButton;
   public AlchemistHumanMob alchemistMob;
   public long costSeed;

   public AlchemistContainer(final NetworkClient var1, int var2, AlchemistHumanMob var3, Packet var4) {
      super(var1, var2, var3, var4);
      this.alchemistMob = var3;
      this.costSeed = var3.getShopSeed();
      this.buyFireworkButton = (ContentCustomAction)this.registerAction(new ContentCustomAction() {
         protected void run(Packet var1x) {
            PacketReader var2 = new PacketReader(var1x);
            GNDItemMap var3 = new GNDItemMap(var2);
            int var4 = var2.getNextShortUnsigned();
            if (AlchemistContainer.this.buyFirework(var3, var4) > 0 && var1.isClient()) {
               Screen.playSound(GameResources.coins, SoundEffect.effect(var1.playerMob));
            }

         }
      });
   }

   public int buyFirework(GNDItemMap var1, int var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < var2 && this.canBuyFirework(var1); ++var4) {
         InventoryItem var5 = new InventoryItem("fireworkrocket");
         var5.setGndData(var1);
         ContainerSlot var6 = this.getClientDraggingSlot();
         if (!var6.isClear() && (!var6.getItem().canCombine(this.client.playerMob.getLevel(), this.client.playerMob, var5, "buy") || var6.getItemAmount() + var5.getAmount() > var6.getItemStackLimit(var6.getItem()))) {
            break;
         }

         var3 += var5.getAmount();
         int var7 = this.getFireworksCost(var1);
         this.client.playerMob.getInv().main.removeItems(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("coin"), var7, "buy");
         if (this.client.isServer()) {
            this.client.getServerClient().newStats.money_spent.increment(var7);
         }

         if (var6.isClear()) {
            var6.setItem(var5.copy());
         } else {
            var6.getItem().combine(this.client.playerMob.getLevel(), this.client.playerMob, var6.getInventory(), var6.getInventorySlot(), var5.copy(), "buy", (InventoryAddConsumer)null);
         }
      }

      return var3;
   }

   private float getRandomPrice(long var1, float var3) {
      return this.getRandomPrice(var1, var3, var3 / 5.0F);
   }

   private float getRandomPrice(long var1, float var3, float var4) {
      return (new GameRandom(var1)).getFloatOffset(var3, var4);
   }

   public int getFireworksCost(GNDItemMap var1) {
      float var2 = this.getRandomPrice(this.costSeed, 10.0F);
      FireworkPlaceableItem.FireworksShape var3 = FireworkPlaceableItem.getShape(var1);
      if (var3 != null) {
         var2 += this.getRandomPrice(this.costSeed * (long)GameRandom.prime(4) * (long)(var3.ordinal() + 1), 5.0F);
      }

      FireworkPlaceableItem.FireworkColor var4 = FireworkPlaceableItem.getColor(var1);
      if (var4 != null) {
         var2 += this.getRandomPrice(this.costSeed * (long)GameRandom.prime(8) * (long)(var4.ordinal() + 1), 5.0F);
      }

      FireworkPlaceableItem.FireworkCrackle var5 = FireworkPlaceableItem.getCrackle(var1);
      if (var5 != null) {
         var2 += this.getRandomPrice(this.costSeed * (long)GameRandom.prime(12) * (long)(var5.ordinal() + 1), 5.0F);
      }

      return (int)var2;
   }

   public boolean canBuyFirework(GNDItemMap var1) {
      int var2 = this.getFireworksCost(var1);
      if (var2 <= 0) {
         return true;
      } else {
         int var3 = this.client.playerMob.getInv().main.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, ItemRegistry.getItem("coin"), "buy");
         return var3 >= var2;
      }
   }
}
