package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.ShopContainer;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;

public abstract class HumanShop extends HumanMob {
   public HumanShop(int var1, int var2, String var3) {
      super(var1, var2, var3);
   }

   public GameMessage getWorkInvMessage() {
      return new LocalMessage("ui", "settlercarryitems");
   }

   public List<InventoryItem> getFreeItems() {
      return this.workInventory;
   }

   public ArrayList<ShopItem> getShopItems(VillageShopsData var1, ServerClient var2) {
      return null;
   }

   public int getRandomHappinessPrice(GameRandom var1, int var2, int var3, int var4) {
      return getRandomHappinessPrice(var1, this.isSettler() ? this.settlerHappiness : 50, var2, var3, var4);
   }

   public static int getRandomHappinessPrice(GameRandom var0, int var1, int var2, int var3, int var4) {
      float var5 = (float)var1 / 100.0F;
      int var6 = var3 - var2;
      var4 = GameMath.limit(var4, 1, Math.abs(var6));
      if (var6 < 0) {
         var4 = -var4;
      }

      float var7 = Math.abs(GameMath.limit(var5, 0.0F, 1.0F) - 1.0F);
      int var8 = (int)(var7 * (float)(var6 - var4));
      int var9 = var4 < 0 ? -var0.nextInt(-var4 + 1) : var0.nextInt(var4 + 1);
      return var2 + var8 + var9;
   }

   public static int getRandomHappinessMiddlePrice(GameRandom var0, int var1, int var2, int var3, int var4) {
      int var5 = var2 / var3;
      int var6 = var5 / var4;
      return getRandomHappinessPrice(var0, var1, var2 - var5 / 2, var2 + var5 / 2, var6);
   }

   public static void conditionSection(GameRandom var0, boolean var1, Consumer<GameRandom> var2, Consumer<GameRandom> var3) {
      GameRandom var4 = var0.nextSeeded();
      if (var1) {
         if (var2 != null) {
            var2.accept(var4);
         }
      } else if (var3 != null) {
         var3.accept(var4);
      }

   }

   public static void conditionSection(GameRandom var0, boolean var1, Consumer<GameRandom> var2) {
      conditionSection(var0, var1, var2, (Consumer)null);
   }

   public void interact(PlayerMob var1) {
      super.interact(var1);
      if (this.isServer() && var1.isServerClient()) {
         ServerClient var2 = var1.getServerClient();
         GameMessage var3 = this.getInteractError(var1);
         if (var3 == null) {
            PacketOpenContainer var4 = this.getOpenShopPacket(var2.getServer(), var2);
            if (var4 != null) {
               ContainerRegistry.openAndSendContainer(var2, var4);
            }
         } else {
            var2.sendChatMessage(var3);
         }
      }

   }

   public PacketOpenContainer getOpenShopPacket(Server var1, ServerClient var2) {
      return PacketOpenContainer.Mob(ContainerRegistry.SHOP_CONTAINER, this, this.getShopItemsContentPacket(var2));
   }

   public GameMessage getDialogueIntroMessage(ServerClient var1) {
      return this.getRandomMessage(GameRandom.globalRandom, var1);
   }

   public GameMessage getRecruitError(ServerClient var1) {
      if (this.isSettler()) {
         return null;
      } else {
         LevelIdentifier var2 = this.getRecruitedToLevel(var1);
         if (var2 != null) {
            if (!var2.isIslandPosition()) {
               return new LocalMessage("ui", "settlementnotfound");
            }

            SettlementCache var3 = var1.getServer().levelCache.getSettlement(var2.getIslandX(), var2.getIslandY());
            if (var3 == null) {
               return new LocalMessage("ui", "settlementnotfound");
            }

            if (!var3.hasAccess(var1)) {
               return new LocalMessage("ui", "settlerrecruitnoperm");
            }
         }

         return null;
      }
   }

   public List<InventoryItem> getRecruitItems(ServerClient var1) {
      return null;
   }

   public boolean startInRecruitForm(ServerClient var1) {
      return false;
   }

   public Packet getShopItemsContentPacket(ServerClient var1) {
      VillageShopsData var2 = VillageShopsData.getShopData(this.getLevel());
      return ShopContainer.getContainerContent(this, var1, this.getWorkInvMessage(), this.getMissionFailedMessage(), this.getDialogueIntroMessage(var1), this.getShopItems(var2, var1), this.getRecruitError(var1), this.getRecruitItems(var1), this.startInRecruitForm(var1), this.getPossibleExpeditions(), this.workSettings);
   }

   public GameMessage getMissionFailedMessage() {
      return this.missionFailedMessage;
   }

   public long getShopSeed() {
      return (long)(this.getWorldEntity().getDay() * 191 * this.settlerSeed);
   }

   public GameMessage getInteractError(Mob var1) {
      if (this.buffManager.hasBuff(BuffRegistry.HUMAN_ANGRY)) {
         return new LocalMessage("mobmsg", "angryshop");
      } else {
         return this.isBusy() ? new LocalMessage("mobmsg", "busyhuman", "name", this.getSettlerName()) : null;
      }
   }
}
