package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.CrimsonSkyArrowProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class TheCrimsonSkyProjectileToolItem extends BowProjectileToolItem implements ItemInteractAction {
   public int projectileMaxHeight;
   public int specialAttackProjectileCount;

   public TheCrimsonSkyProjectileToolItem() {
      super(1800);
      this.attackAnimTime.setBaseValue(500);
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(90.0F).setUpgradedValue(1.0F, 95.0F);
      this.velocity.setBaseValue(350);
      this.attackRange.setBaseValue(1600);
      this.attackXOffset = 12;
      this.attackYOffset = 38;
      this.projectileMaxHeight = 600;
      this.specialAttackProjectileCount = 5;
      this.resilienceGain.setBaseValue(1.0F);
   }

   public CrimsonSkyArrowProjectile getTheCrimsonSkyProjectile(Level var1, int var2, int var3, Mob var4, GameDamage var5, float var6, int var7, float var8) {
      Point2D.Float var9 = new Point2D.Float((float)var2, (float)var3);
      Point2D.Float var10 = GameMath.normalize(var9.x - var4.x, var9.y - var4.y);
      RayLinkedList var11 = GameUtils.castRay(var1, (double)var4.x, (double)var4.y, (double)var10.x, (double)var10.y, var9.distance((double)var4.x, (double)var4.y), 0, (new CollisionFilter()).projectileCollision().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      }));
      if (!var11.isEmpty()) {
         Ray var12 = (Ray)var11.getLast();
         var9.x = (float)var12.x2;
         var9.y = (float)var12.y2;
      }

      return new CrimsonSkyArrowProjectile(var1, var4, var4.x, var4.y, var4.x, var4.y - 1.0F, var6, this.projectileMaxHeight, var5, var8, var7, var9, false);
   }

   protected void addExtraBowTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      super.addExtraBowTooltips(var1, var2, var3, var4);
   }

   protected void addAmmoTooltips(ListGameTooltips var1, InventoryItem var2) {
      super.addAmmoTooltips(var1, var2);
      var1.add((String)Localization.translate("itemtooltip", "thecrimsonskytip"), 400);
      var1.add((String)Localization.translate("itemtooltip", "thecrimsonskysecondarytip"), 400);
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.rotation(-85.0F);
   }

   public Projectile getProjectile(Level var1, int var2, int var3, Mob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, float var9, int var10, GameDamage var11, int var12, float var13, PacketReader var14) {
      return this.getTheCrimsonSkyProjectile(var1, var2, var3, var4, var11, var9, var12, var13);
   }

   public boolean canLevelInteract(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return !var4.buffManager.hasBuff(BuffRegistry.Debuffs.THE_CRIMSON_SKY_COOLDOWN);
   }

   public float getItemCooldownPercent(InventoryItem var1, PlayerMob var2) {
      return var2.buffManager.getBuffDurationLeftSeconds(BuffRegistry.Debuffs.THE_CRIMSON_SKY_COOLDOWN) / 8.0F;
   }

   public InventoryItem onLevelInteract(Level var1, final int var2, final int var3, final PlayerMob var4, int var5, final InventoryItem var6, PlayerInventorySlot var7, int var8, PacketReader var9) {
      final GameRandom var10 = new GameRandom((long)var8);
      var4.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.THE_CRIMSON_SKY_COOLDOWN, var4, 8.0F, (Attacker)null), false);
      var4.useLife(10, var4.isServerClient() ? var4.getServerClient() : null, this.getLocalization(var6));

      for(int var11 = 0; var11 < this.specialAttackProjectileCount; ++var11) {
         var1.entityManager.addLevelEventHidden(new WaitForSecondsEvent((float)var11 / 10.0F) {
            public void onWaitOver() {
               Point2D.Float var1 = new Point2D.Float((float)var2, (float)var3);
               int var2x = var10.getIntBetween(-75, 75);
               int var3x = var10.getIntBetween(-75, 75);
               var1.x += (float)var2x;
               var1.y += (float)var3x;
               RayLinkedList var4x = GameUtils.castRay(this.level, (double)var4.x, (double)var4.y, (double)(var1.x - var4.x), (double)(var1.y - var4.y), var1.distance((double)var4.x, (double)var4.y), 0, (new CollisionFilter()).projectileCollision().addFilter((var0) -> {
                  return var0.object().object.isWall || var0.object().object.isRock;
               }));
               if (!var4x.isEmpty()) {
                  Ray var5 = (Ray)var4x.getLast();
                  var1.x = (float)var5.x2;
                  var1.y = (float)var5.y2;
               }

               GameDamage var7 = TheCrimsonSkyProjectileToolItem.this.getAttackDamage(var6).modFinalMultiplier(1.25F);
               CrimsonSkyArrowProjectile var6x = new CrimsonSkyArrowProjectile(this.level, var4, var4.x, var4.y, var4.x, var4.y - 1.0F, (float)TheCrimsonSkyProjectileToolItem.this.getProjectileVelocity(var6, var4), TheCrimsonSkyProjectileToolItem.this.projectileMaxHeight, var7, TheCrimsonSkyProjectileToolItem.this.getResilienceGain(var6), TheCrimsonSkyProjectileToolItem.this.getKnockback(var6, var4), var1, false);
               var6x.getUniqueID(var10);
               this.level.entityManager.projectiles.addHidden(var6x);
               if (this.level.isServer()) {
                  this.level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var6x), (ServerClient)var4.getServerClient(), var4.getServerClient());
               }

            }
         });
      }

      return var6;
   }

   protected void fireSettlerProjectiles(Level var1, HumanMob var2, Mob var3, InventoryItem var4, int var5, ArrowItem var6, boolean var7) {
      float var8 = (float)this.getProjectileVelocity(var4, var2) * var6.speedMod;
      float var9 = Projectile.getTravelTimeMillis(var8, (float)(this.projectileMaxHeight * 2));
      float var10 = Projectile.getPositionAfterMillis(var3.dx, var9);
      float var11 = Projectile.getPositionAfterMillis(var3.dy, var9);
      int var12 = (int)(var3.x + var10);
      int var13 = (int)(var3.y + var11);
      CrimsonSkyArrowProjectile var14 = this.getTheCrimsonSkyProjectile(var1, var12, var13, var2, this.getAttackDamage(var4), var8, this.getKnockback(var4, var2), this.getResilienceGain(var4));
      var14.dropItem = var7;
      var14.getUniqueID(new GameRandom((long)var5));
      var1.entityManager.projectiles.addHidden(var14);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var14), (Level)var1);
      }

   }

   public boolean canSettlerHitTarget(HumanMob var1, float var2, float var3, Mob var4, InventoryItem var5) {
      float var6 = var1.getDistance(var4);
      int var7 = this.getAttackRange(var5);
      if (var6 < (float)var7) {
         return super.canSettlerHitTarget(var1, var2, var3, var4, var5);
      } else if (var6 < (float)(var7 * 5)) {
         return !var1.getLevel().collides((Shape)(new LineHitbox(var2, var3, var4.x, var4.y, 45.0F)), (CollisionFilter)(new CollisionFilter()).projectileCollision());
      } else {
         return false;
      }
   }

   public ItemControllerInteract getControllerInteract(Level var1, PlayerMob var2, InventoryItem var3, boolean var4, int var5, LinkedList<Rectangle> var6, LinkedList<Rectangle> var7) {
      Point2D.Float var8 = var2.getControllerAimDir();
      Point var9 = this.getControllerAttackLevelPos(var1, var8.x, var8.y, var2, var3);
      return new ItemControllerInteract(var9.x, var9.y) {
         public DrawOptions getDrawOptions(GameCamera var1) {
            return null;
         }

         public void onCurrentlyFocused(GameCamera var1) {
         }
      };
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      float var6 = 500.0F;
      return new Point((int)(var4.x + var2 * var6), (int)(var4.y + var3 * var6));
   }
}
