package necesse.inventory.item.bulletItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class BulletItem extends Item {
   public int damage;
   public int armorPen;
   public float critChance;

   public BulletItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "ammo"});
      this.keyWords.add("bullet");
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "damagetip", "value", (Object)this.damage));
      return var4;
   }

   public float getAmmoConsumeChance() {
      return 1.0F;
   }

   public GameDamage modDamage(GameDamage var1) {
      return var1.add((float)this.damage, this.armorPen, this.critChance);
   }

   public float modVelocity(float var1) {
      return var1;
   }

   public int modRange(int var1) {
      return var1;
   }

   public int modKnockback(int var1) {
      return var1;
   }

   public Projectile getProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      return null;
   }

   public boolean overrideProjectile() {
      return false;
   }
}
