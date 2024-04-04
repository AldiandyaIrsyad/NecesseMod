package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Color;
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
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.SwordCleanSliceAttackEvent;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.BloodClawProjectile;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class BloodClawToolItem extends SwordToolItem implements ItemInteractAction {
   public BloodClawToolItem() {
      super(1600);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(180);
      this.attackDamage.setBaseValue(45.0F).setUpgradedValue(1.0F, 50.0F);
      this.attackRange.setBaseValue(100);
      this.knockback.setBaseValue(40);
      this.attackXOffset = 4;
      this.attackYOffset = 4;
      this.resilienceGain.setBaseValue(1.0F);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add((String)Localization.translate("itemtooltip", "bloodclawtip"), 400);
      var4.add((String)Localization.translate("itemtooltip", "bloodclawsecondarytip"), 400);
      return var4;
   }

   public void showBloodClawAttack(Level var1, AttackAnimMob var2, int var3, final InventoryItem var4) {
      GameRandom var5 = new GameRandom((long)var3);
      final int var6 = var5.getIntBetween(30, 45);
      final int var7 = var5.getIntBetween(30, 45);
      boolean var8 = var5.getChance(2);
      boolean var9 = var5.getChance(2);
      if (var8) {
         var6 = -var6;
      }

      if (var9) {
         var7 = -var7;
      }

      final Point2D.Float var12 = GameMath.normalize((float)(-var6), (float)(-var7));
      final double var13 = Math.sqrt((double)(var6 * var6 + var7 * var7)) * 2.0;
      var1.entityManager.addLevelEventHidden(new SwordCleanSliceAttackEvent(var2, var3, 12, this) {
         Trail trail = null;
         final float maxSliceThickness = 20.0F;
         float thickness;
         final float mobStartX;
         final float mobStartY;

         {
            this.mobStartX = this.attackMob.x;
            this.mobStartY = this.attackMob.y;
         }

         public void tick(float var1, float var2) {
            if (var2 < 0.33F) {
               this.thickness = 60.0F * var2;
            } else if (var2 > 0.66F) {
               this.thickness = 60.0F * (1.0F - var2);
            } else {
               this.thickness = 20.0F;
            }

            int var3 = BloodClawToolItem.this.getAttackRange(var4);
            Point2D.Float var4x = GameMath.getAngleDir(var1);
            float var5 = this.mobStartX + var4x.x * (float)var3 * 0.7F + (float)this.attackMob.getCurrentAttackDrawXOffset();
            var5 = (float)((double)var5 + (double)var6 + (double)(var12.x * var2) * var13);
            float var6x = this.mobStartY + var4x.y * (float)var3 * 0.7F + (float)this.attackMob.getCurrentAttackDrawYOffset();
            var6x = (float)((double)var6x + (double)var7 + (double)(var12.y * var2) * var13);
            if (this.trail == null) {
               this.trail = new Trail(new TrailVector(var5, var6x, var12.x, var12.y, this.thickness, 0.0F), this.level, new Color(164, 0, 0), 500);
               this.trail.removeOnFadeOut = false;
               this.trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
               this.level.entityManager.addTrail(this.trail);
            } else {
               this.trail.addPoint(new TrailVector(var5, var6x, var12.x, var12.y, this.thickness, 0.0F));
            }

         }

         public void onDispose() {
            super.onDispose();
            if (this.trail != null) {
               this.trail.removeOnFadeOut = true;
            }

         }
      });
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      super.showAttack(var1, var2, var3, var4, var5, var6, var7, var8);
      if (var1.isClient()) {
         this.showBloodClawAttack(var1, var4, var7, var6);
      }

   }

   public void hitMob(InventoryItem var1, ToolItemEvent var2, Level var3, Mob var4, Mob var5) {
      super.hitMob(var1, var2, var3, var4, var5);
      if (var5.isServer()) {
         ActiveBuff var6 = new ActiveBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF, var5, 10.0F, var5);
         var5.addBuff(var6, true);
      }
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      if (var3.getDistance(var2) <= (float)this.getAttackRange(var6)) {
         var6 = super.onSettlerAttack(var1, var2, var3, var4, var5, var6);
      } else {
         var2.attackItem(var3.getX(), var3.getY(), var6);
      }

      this.showBloodClawAttack(var1, var2, var5, var6);
      return var6;
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

   public boolean canLevelInteract(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return var4.buffManager.hasBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF);
   }

   public InventoryItem onLevelInteract(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, PacketReader var9) {
      if (var4.buffManager.hasBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF)) {
         int var10 = var4.buffManager.getStacks(BuffRegistry.BLOOD_CLAW_STACKS_BUFF);
         var4.useLife(var10, var4.isServerClient() ? var4.getServerClient() : null, this.getLocalization(var6));

         for(int var11 = 0; var11 < var10; ++var11) {
            float var12 = 2.5F;
            float var13 = 300.0F;
            float var14 = (float)Math.round((Float)this.getEnchantment(var6).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * var13 * (Float)var4.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
            GameRandom var15 = new GameRandom((long)var8);
            GameDamage var16 = this.getAttackDamage(var6).modFinalMultiplier(0.75F);
            BloodClawProjectile var17 = new BloodClawProjectile(var1, var4.x, var4.y, (float)var2, (float)var3, var14, (int)((float)this.getAttackRange(var6) * var12), var16, var4);
            var17.setAngle(var17.getAngle() + (float)var15.getIntBetween(-30, 30));
            var17.getUniqueID(var15);
            var1.entityManager.projectiles.addHidden(var17);
            var17.moveDist(20.0);
            if (var1.isServer()) {
               var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var17), (ServerClient)var4.getServerClient(), var4.getServerClient());
            }
         }

         var4.buffManager.removeBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF, true);
      }

      return var6;
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
}
