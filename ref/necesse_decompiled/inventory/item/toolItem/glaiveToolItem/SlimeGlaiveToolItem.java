package necesse.inventory.item.toolItem.glaiveToolItem;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.GlaiveShowAttackEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class SlimeGlaiveToolItem extends GlaiveToolItem {
   public SlimeGlaiveToolItem() {
      super(1800);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(60.0F).setUpgradedValue(1.0F, 65.0F);
      this.attackRange.setBaseValue(200);
      this.knockback.setBaseValue(150);
      this.width = 20.0F;
      this.attackXOffset = 74;
      this.attackYOffset = 74;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      super.showAttack(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var1.isClient()) {
         var1.entityManager.addLevelEventHidden(new GlaiveShowAttackEvent(var4, var2, var3, var7, 10.0F) {
            public void tick(float var1) {
               GameRandom var2 = new GameRandom();
               float var3 = var2.getFloatBetween(0.0F, 1.0F);
               Color var4 = SlimeGlaiveToolItem.this.getParticleColor(var3);
               Point2D.Float var5 = this.getAngleDir(var1);
               this.level.entityManager.addParticle(this.attackMob.x + var5.x * 85.0F + (float)this.attackMob.getCurrentAttackDrawXOffset(), this.attackMob.y + var5.y * 85.0F + (float)this.attackMob.getCurrentAttackDrawYOffset(), Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(var4).movesConstant(var5.x * 40.0F, var5.y * 40.0F).lifeTime(400);
               this.level.entityManager.addParticle(this.attackMob.x - var5.x * 85.0F + (float)this.attackMob.getCurrentAttackDrawXOffset(), this.attackMob.y - var5.y * 85.0F + (float)this.attackMob.getCurrentAttackDrawYOffset(), Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(var4).movesConstant(var5.x * -40.0F, var5.y * -40.0F).lifeTime(400);
            }
         });
      }

   }

   private Color getParticleColor(float var1) {
      return new Color((int)(70.0F * (1.0F + 1.8F * var1)), (int)(178.0F * (1.0F + 0.3F * var1)), (int)(170.0F * (1.0F + 0.2F * var1)));
   }
}
