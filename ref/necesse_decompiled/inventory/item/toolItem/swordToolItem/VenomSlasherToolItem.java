package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Shape;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.VenomSlasherWaveProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class VenomSlasherToolItem extends SwordToolItem {
   public VenomSlasherToolItem() {
      super(1400);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(50.0F).setUpgradedValue(1.0F, 60.0F);
      this.attackRange.setBaseValue(70);
      this.knockback.setBaseValue(75);
      this.resilienceGain.setBaseValue(1.5F);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "venomslashertip"));
      return var4;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      var6 = super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      float var11 = 7.0F;
      float var12 = 140.0F;
      float var13 = (float)Math.round((Float)this.getEnchantment(var6).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * var12 * (Float)var4.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
      VenomSlasherWaveProjectile var14 = new VenomSlasherWaveProjectile(var1, var4.x, var4.y, (float)var2, (float)var3, var13, (int)((float)this.getAttackRange(var6) * var11), this.getAttackDamage(var6), var4);
      GameRandom var15 = new GameRandom((long)var9);
      var14.resetUniqueID(var15);
      var1.entityManager.projectiles.addHidden(var14);
      var14.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var14), (ServerClient)var4.getServerClient(), var4.getServerClient());
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

      float var7 = 7.0F;
      float var8 = 140.0F;
      float var9 = (float)Math.round((Float)this.getEnchantment(var6).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * var8 * (Float)var2.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
      VenomSlasherWaveProjectile var10 = new VenomSlasherWaveProjectile(var1, var2.x, var2.y, var3.x, var3.y, var9, (int)((float)this.getAttackRange(var6) * var7), this.getAttackDamage(var6), var2);
      GameRandom var11 = new GameRandom((long)var5);
      var10.resetUniqueID(var11);
      var1.entityManager.projectiles.addHidden(var10);
      var10.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var10), (Level)var1);
      }

      return var6;
   }

   public boolean canSettlerHitTarget(HumanMob var1, float var2, float var3, Mob var4, InventoryItem var5) {
      float var6 = var1.getDistance(var4);
      int var7 = this.getAttackRange(var5);
      if (var6 < (float)var7) {
         return super.canSettlerHitTarget(var1, var2, var3, var4, var5);
      } else if (var6 < (float)(var7 * 5)) {
         return !var1.getLevel().collides((Shape)(new LineHitbox(var2, var3, var4.x, var4.y, 45.0F)), (CollisionFilter)(new CollisionFilter()).projectileCollision());
      } else {
         return false;
      }
   }
}
