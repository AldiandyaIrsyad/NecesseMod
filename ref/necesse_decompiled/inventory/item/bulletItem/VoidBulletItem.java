package necesse.inventory.item.bulletItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.VoidBulletProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class VoidBulletItem extends BulletItem {
   public VoidBulletItem() {
      super(1000);
      this.damage = 7;
      this.rarity = Item.Rarity.UNCOMMON;
   }

   public boolean overrideProjectile() {
      return true;
   }

   public Projectile getProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      return new VoidBulletProjectile(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "voidammotip"));
      return var4;
   }
}
