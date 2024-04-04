package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.NightPiercerArrowProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.Level;

public class NightPiercerGreatBowProjectileToolItem extends GreatbowProjectileToolItem {
   public NightPiercerGreatBowProjectileToolItem() {
      super(1600);
      this.attackAnimTime.setBaseValue(500);
      this.rarity = Item.Rarity.RARE;
      this.attackDamage.setBaseValue(124.0F).setUpgradedValue(1.0F, 135.0F);
      this.velocity.setBaseValue(500);
      this.attackRange.setBaseValue(1600);
      this.attackXOffset = 12;
      this.attackYOffset = 38;
      this.particleColor = new Color(108, 37, 92);
   }

   protected void addExtraBowTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      super.addExtraBowTooltips(var1, var2, var3, var4);
      var1.add(Localization.translate("itemtooltip", "nightpiercertip"));
   }

   public Projectile getProjectile(Level var1, int var2, int var3, Mob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, float var9, int var10, GameDamage var11, int var12, float var13, PacketReader var14) {
      return new NightPiercerArrowProjectile(var1, var4, var4.x, var4.y, (float)var2, (float)var3, var9, var10, var11, var12);
   }
}
