package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Point;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.TeleportResult;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class RecallScrollItem extends ConsumableItem implements AdventurePartyConsumableItem {
   public RecallScrollItem() {
      super(50, true);
      this.rarity = Item.Rarity.COMMON;
      this.itemCooldownTime.setBaseValue(2000);
      this.worldDrawSize = 32;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      if (var1.isServer()) {
         ServerClient var7 = var4.getServerClient();
         int var8 = this.getFlatAttackAnimTime(var5);
         TeleportEvent var9 = new TeleportEvent(var7, var8, var7.spawnLevelIdentifier, 5.0F, (Function)null, (var1x) -> {
            var7.validateSpawnPoint(true);
            Point var2 = new Point(16, 16);
            if (!var7.isDefaultSpawnPoint()) {
               var2 = RespawnObject.calculateSpawnOffset(var1x, var7.spawnTile.x, var7.spawnTile.y, var7);
            }

            return new TeleportResult(true, var7.spawnLevelIdentifier, var7.spawnTile.x * 32 + var2.x, var7.spawnTile.y * 32 + var2.y);
         });
         var1.entityManager.addLevelEventHidden(var9);
      }

      return var5;
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      String var6 = super.canAttack(var1, var2, var3, var4, var5);
      if (var6 != null) {
         return var6;
      } else {
         return !var4.buffManager.hasBuff("teleportsickness") ? null : "";
      }
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return var4.buffManager.hasBuff("teleportsickness") ? "teleportsickness" : null;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "consumetip"));
      var4.add(Localization.translate("itemtooltip", "recalltip"));
      return var4;
   }

   public ComparableSequence<Integer> getPartyPriority(Level var1, HumanMob var2, ServerClient var3, Inventory var4, int var5, InventoryItem var6, String var7) {
      return (new ComparableSequence(-1000)).thenBy((Comparable)var5);
   }

   public boolean canAndShouldPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      return var2.getHealthPercent() <= 0.25F || var5.equals("secondwind");
   }

   public InventoryItem onPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      var2.adventureParty.clear(true);
      var2.clearCommandsOrders((ServerClient)null);
      if (var2.isSettlerOnCurrentLevel()) {
         SettlementLevelData var6 = var2.getSettlementLevelData();
         Point var7 = SettlersWorldData.getReturnPos(var2, var6);
         TeleportEvent var8 = new TeleportEvent(var2, 0, var6.getLevel().getIdentifier(), 0.0F, (Function)null, (var2x) -> {
            return new TeleportResult(true, var6.getLevel().getIdentifier(), var7.x, var7.y);
         });
         var2.getLevel().entityManager.addLevelEvent(var8);
      } else {
         SettlersWorldData var9 = SettlersWorldData.getSettlersData(var2.getLevel().getServer());
         var9.returnToSettlement(var2, true);
      }

      LocalMessage var10 = new LocalMessage("ui", "adventurepartyleftrecall", "name", var2.getLocalization());
      var3.sendChatMessage((GameMessage)var10);
      InventoryItem var11 = var4.copy();
      if (this.singleUse) {
         var4.setAmount(var4.getAmount() - 1);
      }

      return var11;
   }

   public boolean shouldPreventHit(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4) {
      return true;
   }
}
