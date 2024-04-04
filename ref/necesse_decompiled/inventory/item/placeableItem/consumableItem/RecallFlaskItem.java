package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Point;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.TeleportResult;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;

public class RecallFlaskItem extends ConsumableItem {
   public RecallFlaskItem() {
      super(1, false);
      this.attackAnimTime.setBaseValue(500);
      this.rarity = Item.Rarity.RARE;
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

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4);
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
      var4.add(Localization.translate("itemtooltip", "recalltip"));
      if (!this.singleUse) {
         var4.add(Localization.translate("itemtooltip", "infiniteuse"));
      }

      return var4;
   }
}
