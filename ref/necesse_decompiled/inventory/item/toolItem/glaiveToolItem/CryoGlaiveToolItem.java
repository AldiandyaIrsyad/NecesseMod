package necesse.inventory.item.toolItem.glaiveToolItem;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.entity.levelEvent.GlaiveShowAttackEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CryoGlaiveToolItem extends GlaiveToolItem {
   public CryoGlaiveToolItem() {
      super(1600);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(60.0F).setUpgradedValue(1.0F, 65.0F);
      this.attackRange.setBaseValue(160);
      this.knockback.setBaseValue(100);
      this.width = 20.0F;
      this.attackXOffset = 60;
      this.attackYOffset = 60;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      super.showAttack(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var1.isClient()) {
         var1.entityManager.addLevelEventHidden(new GlaiveShowAttackEvent(var4, var2, var3, var7, 10.0F) {
            public void tick(float var1) {
               Point2D.Float var2 = this.getAngleDir(var1);
               this.level.entityManager.addParticle(this.attackMob.x + var2.x * 75.0F + (float)this.attackMob.getCurrentAttackDrawXOffset(), this.attackMob.y + var2.y * 75.0F + (float)this.attackMob.getCurrentAttackDrawYOffset(), Particle.GType.COSMETIC).color(new Color(200, 172, 54)).minDrawLight(150).givesLight(45.0F, 1.0F).lifeTime(400);
               this.level.entityManager.addParticle(this.attackMob.x - var2.x * 75.0F + (float)this.attackMob.getCurrentAttackDrawXOffset(), this.attackMob.y - var2.y * 75.0F + (float)this.attackMob.getCurrentAttackDrawYOffset(), Particle.GType.COSMETIC).color(new Color(200, 172, 54)).minDrawLight(150).givesLight(45.0F, 1.0F).lifeTime(400);
            }
         });
      }

   }
}
