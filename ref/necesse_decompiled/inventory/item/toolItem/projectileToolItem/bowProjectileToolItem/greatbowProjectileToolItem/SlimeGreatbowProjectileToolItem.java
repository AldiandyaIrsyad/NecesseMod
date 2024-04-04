package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SlimeGreatBowArrowProjectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SlimeGreatbowProjectileToolItem extends GreatbowProjectileToolItem {
   public int attackSpriteStretch = 8;
   public Color particleColor = new Color(255, 219, 36);
   public int projectileMaxHeight;

   public SlimeGreatbowProjectileToolItem() {
      super(1500);
      this.knockback.setBaseValue(10);
      this.projectileMaxHeight = 600;
      this.attackAnimTime.setBaseValue(500);
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(96.0F).setUpgradedValue(1.0F, 105.0F);
      this.velocity.setBaseValue(350);
      this.attackRange.setBaseValue(1600);
      this.attackXOffset = 12;
      this.attackYOffset = 38;
      this.resilienceGain.setBaseValue(1.0F);
   }

   public SlimeGreatBowArrowProjectile getSlimeGreatbowProjectile(Level var1, int var2, int var3, Mob var4, GameDamage var5, float var6, float var7, float var8, float var9) {
      Point2D.Float var10 = new Point2D.Float((float)var2, (float)var3);
      Point2D.Float var11 = GameMath.normalize(var10.x - var4.x, var10.y - var4.y);
      RayLinkedList var12 = GameUtils.castRay(var1, (double)var4.x, (double)var4.y, (double)var11.x, (double)var11.y, var10.distance((double)var4.x, (double)var4.y), 0, (new CollisionFilter()).projectileCollision().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      }));
      if (!var12.isEmpty()) {
         Ray var13 = (Ray)var12.getLast();
         var10.x = (float)var13.x2;
         var10.y = (float)var13.y2;
         Point2D.Double var14 = GameMath.normalize(var13.x1 - var13.x2, var13.y1 - var13.y2);
         var10.x = (float)((double)var10.x + var14.x * 2.0);
         var10.y = (float)((double)var10.y + var14.y * 2.0);
      }

      return new SlimeGreatBowArrowProjectile(var1, var4, var4.x, var4.y, var4.x, var4.y - 1.0F, var6, this.projectileMaxHeight, var5, var9, (int)var8, var10, false);
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.rotation(-85.0F);
   }

   protected void addExtraBowTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      super.addExtraBowTooltips(var1, var2, var3, var4);
      var1.add((String)Localization.translate("itemtooltip", "slimegreatbowtip"), 400);
   }

   public Projectile getProjectile(Level var1, int var2, int var3, Mob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, PacketReader var9) {
      float var10 = var5.getGndData().getFloat("chargePercent");
      var10 = GameMath.limit(var10, 0.0F, 1.0F);
      float var11;
      float var12;
      float var13;
      float var14;
      if (var10 >= 1.0F) {
         var11 = 1.0F;
         var12 = 1.0F;
         var13 = 1.0F;
         var14 = 1.0F;
      } else {
         var11 = GameMath.lerp(var10, 0.1F, 0.4F);
         var12 = GameMath.lerp(var10, 0.05F, 0.4F);
         var13 = GameMath.lerp(var10, 0.05F, 0.4F);
         var14 = GameMath.lerp(var10, 0.05F, 0.2F);
      }

      GameDamage var15 = var7.modDamage(this.getAttackDamage(var5)).modDamage(var13);
      float var16 = var7.modVelocity((float)this.getProjectileVelocity(var5, var4)) * var11;
      float var17 = (float)var7.modRange(this.getAttackRange(var5)) * var12;
      float var18 = (float)var7.modKnockback(this.getKnockback(var5, var4)) * var14;
      float var19 = this.getResilienceGain(var5);
      return this.getSlimeGreatbowProjectile(var1, var2, var3, var4, var15, var16, var17, var18, var19);
   }

   public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions var1, InventoryItem var2, PlayerMob var3, int var4, float var5, float var6, float var7, Color var8, GameLight var9) {
      float var10 = var2.getGndData().getFloat("chargePercent");
      if (var10 > 0.0F) {
         var10 = Math.min(var10, 1.0F);
         GameSprite var11 = this.getAttackSprite(var2, var3);
         int var12 = (int)(var10 * (float)this.attackSpriteStretch);
         var11 = new GameSprite(var11, var11.width + var12, var11.height);
         var1.armPosOffset(-var12 + this.attackSpriteStretch / 2, 0);
         ItemAttackDrawOptions.AttackItemSprite var13 = var1.itemSprite(var11);
         var13.itemRotatePoint(this.attackXOffset + var12 - this.attackSpriteStretch / 2, this.attackYOffset);
         if (var8 != null) {
            var13.itemColor(var8);
         }

         return var13.itemEnd();
      } else {
         return super.setupItemSpriteAttackDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      float var6 = 500.0F;
      return new Point((int)(var4.x + var2 * var6), (int)(var4.y + var3 * var6));
   }
}
