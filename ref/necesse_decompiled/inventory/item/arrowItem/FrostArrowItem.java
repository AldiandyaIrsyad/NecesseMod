package necesse.inventory.item.arrowItem;

import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.item.Item;

public class FrostArrowItem extends ArrowItem {
   public FrostArrowItem() {
      this.damage = 7;
      this.rarity = Item.Rarity.COMMON;
   }

   public Projectile getProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      return ProjectileRegistry.getProjectile("frostarrow", var9.getLevel(), var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }
}
