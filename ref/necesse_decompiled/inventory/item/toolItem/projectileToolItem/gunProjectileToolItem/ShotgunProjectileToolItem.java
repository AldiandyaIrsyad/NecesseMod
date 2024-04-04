package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.level.maps.Level;

public class ShotgunProjectileToolItem extends GunProjectileToolItem {
   public ShotgunProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 700);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(750);
      this.attackDamage.setBaseValue(17.0F).setUpgradedValue(1.0F, 45.0F);
      this.attackXOffset = 12;
      this.attackYOffset = 10;
      this.attackRange.setBaseValue(800);
      this.velocity.setBaseValue(350);
      this.moveDist = 20;
      this.resilienceGain.setBaseValue(0.3F);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   protected void fireProjectiles(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, int var6, BulletItem var7, boolean var8, PacketReader var9) {
      GameRandom var10 = new GameRandom((long)var6);
      GameRandom var11 = new GameRandom((long)(var6 + 10));
      int var12;
      if (this.controlledRange) {
         Point var13 = this.controlledRangePosition(var11, var4, var2, var3, var5, this.controlledMinRange, this.controlledInaccuracy);
         var2 = var13.x;
         var3 = var13.y;
         var12 = (int)var4.getDistance((float)var2, (float)var3);
      } else {
         var12 = this.getAttackRange(var5);
      }

      for(int var15 = 0; var15 <= 3; ++var15) {
         Projectile var14 = this.getProjectile(var5, var7, var4.x, var4.y, (float)var2, (float)var3, var12, var4);
         var14.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var5)));
         var14.dropItem = var8;
         var14.getUniqueID(var10);
         var1.entityManager.projectiles.addHidden(var14);
         if (this.moveDist != 0) {
            var14.moveDist((double)this.moveDist);
         }

         var14.setAngle(var14.getAngle() + (var11.nextFloat() - 0.5F) * 20.0F);
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var14), (ServerClient)var4.getServerClient(), var4.getServerClient());
         }
      }

   }

   protected void fireSettlerProjectiles(Level var1, HumanMob var2, Mob var3, InventoryItem var4, int var5, BulletItem var6, boolean var7) {
      GameRandom var8 = new GameRandom((long)var5);
      GameRandom var9 = new GameRandom((long)(var5 + 10));
      int var11 = this.getProjectileVelocity(var4, var2);
      Point2D.Float var12 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var11, -10.0F);
      int var13 = (int)var12.x;
      int var14 = (int)var12.y;
      int var10;
      if (this.controlledRange) {
         Point var15 = this.controlledRangePosition(var9, var2, var13, var14, var4, this.controlledMinRange, this.controlledInaccuracy);
         var13 = var15.x;
         var14 = var15.y;
         var10 = (int)var2.getDistance((float)var13, (float)var14);
      } else {
         var10 = this.getAttackRange(var4);
      }

      for(int var17 = 0; var17 <= 3; ++var17) {
         Projectile var16 = this.getProjectile(var4, var6, var2.x, var2.y, (float)var13, (float)var14, var10, var2);
         var16.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var4)));
         var16.dropItem = var7;
         var16.getUniqueID(var8);
         var1.entityManager.projectiles.addHidden(var16);
         if (this.moveDist != 0) {
            var16.moveDist((double)this.moveDist);
         }

         var16.setAngle(var16.getAngle() + (var9.nextFloat() - 0.5F) * 20.0F);
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var16), (Level)var1);
         }
      }

   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.shotgun, SoundEffect.effect(var1));
   }
}
