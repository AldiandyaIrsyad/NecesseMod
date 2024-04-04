package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.TileBombProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class TileBombToolItem extends ThrowToolItem {
   public TileBombToolItem() {
      this.stackSize = 100;
      this.attackAnimTime.setBaseValue(500);
      this.attackRange.setBaseValue(300);
      this.attackDamage.setBaseValue(60.0F);
      this.velocity.setBaseValue(100);
      this.rarity = Item.Rarity.COMMON;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "destructivetiletip"));
      return var4;
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      int var6 = this.getAttackRange(var5);
      return new Point((int)(var4.x + var2 * (float)var6), (int)(var4.y + var3 * (float)var6));
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      GameRandom var11 = new GameRandom((long)var9);
      Point var12 = this.controlledRangePosition(var11, var4, var2, var3, var6, 0, 40);
      int var13 = (int)var4.getDistance((float)var12.x, (float)var12.y);
      TileBombProjectile var14 = new TileBombProjectile(var4.x, var4.y, (float)var12.x, (float)var12.y, this.getThrowingVelocity(var6, var4), var13, this.getAttackDamage(var6), var4);
      var14.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var14.setLevel(var1);
      var14.resetUniqueID(var11);
      var1.entityManager.projectiles.addHidden(var14);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var14), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      var6.setAmount(var6.getAmount() - 1);
      return var6;
   }
}
