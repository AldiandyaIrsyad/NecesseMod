package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Shape;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.CausticExecutionerProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class CausticExecutionerToolItem extends SwordToolItem {
   public CausticExecutionerToolItem() {
      super(2000);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(50.0F).setUpgradedValue(1.0F, 55.0F);
      this.attackRange.setBaseValue(70);
      this.knockback.setBaseValue(75);
      this.resilienceGain.setBaseValue(1.5F);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "causticexecutionertip"));
      return var4;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      var6 = super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      float var11 = 2.0F;
      GameDamage var12 = this.getAttackDamage(var6).modDamage(0.6F);
      CausticExecutionerProjectile var13 = new CausticExecutionerProjectile(var1, var4.x, var4.y, (float)var2, (float)var3, (int)((float)this.getAttackRange(var6) * var11), var12, var4);
      GameRandom var14 = new GameRandom((long)var9);
      var13.resetUniqueID(var14);
      var1.entityManager.projectiles.addHidden(var13);
      var13.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToAllClientsExcept(new PacketSpawnProjectile(var13), var4.getServerClient());
      }

      return var6;
   }

   public int getSettlerAttackRange(HumanMob var1, InventoryItem var2) {
      return this.getAttackRange(var2) * 5;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      if (var3.getDistance(var2) <= (float)this.getAttackRange(var6)) {
         var6 = super.onSettlerAttack(var1, var2, var3, var4, var5, var6);
      } else {
         var2.attackItem(var3.getX(), var3.getY(), var6);
      }

      float var7 = 2.0F;
      GameDamage var8 = this.getAttackDamage(var6).modDamage(0.6F);
      CausticExecutionerProjectile var9 = new CausticExecutionerProjectile(var1, var2.x, var2.y, var3.x, var3.y, (int)((float)this.getAttackRange(var6) * var7), var8, var2);
      GameRandom var10 = new GameRandom((long)var5);
      var9.resetUniqueID(var10);
      var1.entityManager.projectiles.addHidden(var9);
      var9.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var9), (Level)var1);
      }

      return var6;
   }

   public boolean canSettlerHitTarget(HumanMob var1, float var2, float var3, Mob var4, InventoryItem var5) {
      float var6 = var1.getDistance(var4);
      int var7 = this.getAttackRange(var5);
      if (var6 < (float)var7) {
         return super.canSettlerHitTarget(var1, var2, var3, var4, var5);
      } else if (var6 < (float)var7 * 1.5F) {
         return !var1.getLevel().collides((Shape)(new LineHitbox(var2, var3, var4.x, var4.y, 45.0F)), (CollisionFilter)(new CollisionFilter()).projectileCollision());
      } else {
         return false;
      }
   }
}
