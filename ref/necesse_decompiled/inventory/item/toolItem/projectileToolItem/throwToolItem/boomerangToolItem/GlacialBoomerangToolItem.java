package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseProjectileAttackHandler;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.projectile.followingProjectile.GlacialBoomerangProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class GlacialBoomerangToolItem extends BoomerangToolItem {
   public GlacialBoomerangToolItem() {
      super(1200, (String)null);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(300);
      this.attackCooldownTime.setBaseValue(400);
      this.attackDamage.setBaseValue(38.0F).setUpgradedValue(1.0F, 45.0F);
      this.attackRange.setBaseValue(2000);
      this.velocity.setBaseValue(180);
      this.stackSize = 6;
      this.knockback.setBaseValue(100);
      this.resilienceGain.setBaseValue(0.3F);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "glacialboomerangtip"));
      var4.add(Localization.translate("itemtooltip", "glacialboomerangtip2"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   public boolean getConstantUse(InventoryItem var1) {
      return true;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = GameMath.limit(Math.min(var6.getAmount(), var6.itemStackSize() - var4.getBoomerangsUsage()), 1, 2);
      GameRandom var12 = new GameRandom((long)var9);
      FollowingProjectile[] var13 = new FollowingProjectile[var11];
      float var14 = GameMath.getAngle(new Point2D.Float((float)var2 - var4.x, (float)var3 - var4.y)) + 90.0F;
      float var15 = 60.0F;
      float var16 = (float)(-var11) * var15 / 2.0F + var15 / 2.0F;

      for(int var17 = 0; var17 < var13.length; ++var17) {
         GlacialBoomerangProjectile var18 = new GlacialBoomerangProjectile(var1, var4, var4.x, var4.y, (float)var2, (float)var3, (float)this.getThrowingVelocity(var6, var4), this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4));
         var18.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
         var18.setAngle(var14 + var16 + (float)var17 * var15);
         var4.boomerangs.add(var18);
         var18.resetUniqueID(var12);
         var1.entityManager.projectiles.addHidden(var18);
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var18), (ServerClient)var4.getServerClient(), var4.getServerClient());
         }

         var13[var17] = var18;
      }

      if (var4.isAttackHandlerFrom(var6, var7)) {
         ((MouseProjectileAttackHandler)var4.getAttackHandler()).addProjectiles(var13);
      } else {
         var4.startAttackHandler(new MouseProjectileAttackHandler(var4, var7, this.getAttackRange(var6), 100, var13));
      }

      return var6;
   }
}
