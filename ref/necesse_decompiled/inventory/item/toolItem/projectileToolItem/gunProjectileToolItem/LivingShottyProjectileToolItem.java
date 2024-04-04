package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.LivingShottyLeafProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.level.maps.Level;

public class LivingShottyProjectileToolItem extends GunProjectileToolItem {
   public LivingShottyProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 1200);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(40.0F).setUpgradedValue(1.0F, 55.0F);
      this.attackXOffset = 12;
      this.attackYOffset = 10;
      this.attackRange.setBaseValue(1000);
      this.velocity.setBaseValue(350);
      this.moveDist = 20;
      this.resilienceGain.setBaseValue(0.2F);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   protected void fireProjectiles(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, int var6, BulletItem var7, boolean var8, PacketReader var9) {
      GameRandom var10 = new GameRandom((long)var6);
      GameRandom var11 = var10.nextSeeded(27);
      int var12;
      if (this.controlledRange) {
         Point var13 = this.controlledRangePosition(var11, var4, var2, var3, var5, this.controlledMinRange, this.controlledInaccuracy);
         var2 = var13.x;
         var3 = var13.y;
         var12 = (int)var4.getDistance((float)var2, (float)var3);
      } else {
         var12 = this.getAttackRange(var5);
      }

      for(int var15 = 0; var15 <= 5; ++var15) {
         Object var14;
         if (var15 == 2) {
            var14 = new LivingShottyLeafProjectile(var1, var4, var4.x, var4.y, (float)var2, (float)var3, (float)this.getProjectileVelocity(var5, var4), var12, this.getAttackDamage(var5), this.getKnockback(var5, var4));
         } else {
            var14 = this.getProjectile(var5, var7, var4.x, var4.y, (float)var2, (float)var3, var12, var4);
            ((Projectile)var14).setDamage(this.getAttackDamage(var5).modFinalMultiplier(0.4F));
         }

         ((Projectile)var14).setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var5)));
         ((Projectile)var14).dropItem = var8;
         ((Projectile)var14).getUniqueID(var10);
         var1.entityManager.projectiles.addHidden((Entity)var14);
         if (this.moveDist != 0) {
            ((Projectile)var14).moveDist((double)this.moveDist);
         }

         if (var15 != 2) {
            ((Projectile)var14).setAngle(((Projectile)var14).getAngle() + (var11.nextFloat() - 0.5F) * 20.0F);
         }

         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile((Projectile)var14), (ServerClient)var4.getServerClient(), var4.getServerClient());
         }
      }

   }

   protected void fireSettlerProjectiles(Level var1, HumanMob var2, Mob var3, InventoryItem var4, int var5, BulletItem var6, boolean var7) {
      GameRandom var8 = new GameRandom((long)var5);
      GameRandom var9 = var8.nextSeeded(27);
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

      for(int var17 = 0; var17 <= 5; ++var17) {
         Object var16;
         if (var17 == 2) {
            var16 = new LivingShottyLeafProjectile(var1, var2, var2.x, var2.y, (float)var13, (float)var14, (float)this.getProjectileVelocity(var4, var2), var10, this.getAttackDamage(var4), this.getKnockback(var4, var2));
         } else {
            var16 = this.getProjectile(var4, var6, var2.x, var2.y, (float)var13, (float)var14, var10, var2);
            ((Projectile)var16).setDamage(this.getAttackDamage(var4).modFinalMultiplier(0.4F));
         }

         ((Projectile)var16).setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var4)));
         ((Projectile)var16).dropItem = var7;
         ((Projectile)var16).getUniqueID(var8);
         var1.entityManager.projectiles.addHidden((Entity)var16);
         if (this.moveDist != 0) {
            ((Projectile)var16).moveDist((double)this.moveDist);
         }

         if (var17 != 2) {
            ((Projectile)var16).setAngle(((Projectile)var16).getAngle() + (var9.nextFloat() - 0.5F) * 20.0F);
         }

         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile((Projectile)var16), (Level)var1);
         }
      }

   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.shotgun, SoundEffect.effect(var1));
   }
}
