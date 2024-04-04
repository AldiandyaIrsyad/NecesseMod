package necesse.inventory.item.toolItem.projectileToolItem;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Set;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.SettlerWeaponItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class ProjectileToolItem extends ToolItem implements SettlerWeaponItem {
   protected float settlerProjectileCanHitWidth = 8.0F;
   protected IntUpgradeValue velocity = new IntUpgradeValue(50, 0.0F);

   public ProjectileToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "weapons"});
      this.knockback.setBaseValue(25);
   }

   public int getFlatVelocity(InventoryItem var1) {
      GNDItemMap var2 = var1.getGndData();
      return var2.hasKey("velocity") ? var2.getInt("velocity") : this.velocity.getValue(this.getUpgradeTier(var1));
   }

   /** @deprecated */
   @Deprecated
   public int getVelocity(InventoryItem var1, Mob var2) {
      return this.getProjectileVelocity(var1, var2);
   }

   public int getProjectileVelocity(InventoryItem var1, Mob var2) {
      int var3 = this.getFlatVelocity(var1);
      return Math.round((Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * (float)var3 * (Float)var2.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
   }

   public int getThrowingVelocity(InventoryItem var1, Mob var2) {
      int var3 = this.getFlatVelocity(var1);
      return Math.round((Float)this.getEnchantment(var1).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * (float)var3 * (Float)var2.buffManager.getModifier(BuffModifiers.THROWING_VELOCITY));
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4);
   }

   public boolean animDrawBehindHand() {
      return true;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      return var6;
   }

   public boolean canSettlerHitTarget(HumanMob var1, float var2, float var3, Mob var4, InventoryItem var5) {
      float var6 = this.getSettlerProjectileCanHitWidth(var1, var4, var5);
      if (var6 > 0.0F) {
         return !var1.getLevel().collides((Shape)(new LineHitbox(var2, var3, var4.x, var4.y, var6)), (CollisionFilter)(new CollisionFilter()).projectileCollision());
      } else {
         return SettlerWeaponItem.super.canSettlerHitTarget(var1, var2, var3, var4, var5);
      }
   }

   protected float getSettlerProjectileCanHitWidth(HumanMob var1, Mob var2, InventoryItem var3) {
      return this.settlerProjectileCanHitWidth;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   public int getSettlerAttackRange(HumanMob var1, InventoryItem var2) {
      return this.getAttackRange(var2) / 2;
   }

   public ToolItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return this.getDamageType(var2) == DamageTypeRegistry.MAGIC ? (ToolItemEnchantment)Enchantable.getRandomEnchantment(var1, EnchantmentRegistry.magicItemEnchantments, this.getEnchantmentID(var2), ToolItemEnchantment.class) : (ToolItemEnchantment)Enchantable.getRandomEnchantment(var1, EnchantmentRegistry.rangedItemEnchantments, this.getEnchantmentID(var2), ToolItemEnchantment.class);
   }

   public boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2) {
      if (this.getDamageType(var1) == DamageTypeRegistry.MAGIC) {
         return EnchantmentRegistry.magicItemEnchantments.contains(var2.getID());
      } else {
         return this.getDamageType(var1) == DamageTypeRegistry.MELEE ? EnchantmentRegistry.meleeItemEnchantments.contains(var2.getID()) : EnchantmentRegistry.rangedItemEnchantments.contains(var2.getID());
      }
   }

   public Set<Integer> getValidEnchantmentIDs(InventoryItem var1) {
      if (this.getDamageType(var1) == DamageTypeRegistry.MAGIC) {
         return EnchantmentRegistry.magicItemEnchantments;
      } else {
         return this.getDamageType(var1) == DamageTypeRegistry.MELEE ? EnchantmentRegistry.meleeItemEnchantments : EnchantmentRegistry.rangedItemEnchantments;
      }
   }

   protected Point controlledRangePosition(GameRandom var1, Mob var2, int var3, int var4, InventoryItem var5, int var6, int var7) {
      return controlledRangePosition(var1, var2.getX(), var2.getY(), var3, var4, this.getAttackRange(var5), var6, var7);
   }

   public static Point controlledRangePosition(GameRandom var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      float var8 = (float)var3;
      float var9 = (float)var4;
      float var10 = (float)(new Point(var1, var2)).distance((double)var8, (double)var9);
      Point2D.Float var11 = GameMath.normalize(var8 - (float)var1, var9 - (float)var2);
      if (var10 > (float)var5) {
         var8 = (float)((int)((float)var1 + var11.x * (float)var5));
         var9 = (float)((int)((float)var2 + var11.y * (float)var5));
      } else if (var10 < (float)var6) {
         var8 = (float)((int)((float)var1 + var11.x * (float)var6));
         var9 = (float)((int)((float)var2 + var11.y * (float)var6));
      }

      float var12 = (float)(new Point(var1, var2)).distance((double)var8, (double)var9) / (float)var5;
      if (var7 > 0) {
         var8 += (var0.nextFloat() * 2.0F - 1.0F) * (float)var7 * var12;
         var9 += (var0.nextFloat() * 2.0F - 1.0F) * (float)var7 * var12;
      }

      return new Point((int)var8, (int)var9);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return this.getRandomEnchantment(var1, var2);
   }
}
