package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.SlimeGreatswordFollowingMob;
import necesse.entity.projectile.SlimeGreatswordProjectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.SlimeGreatswordToolItem;
import necesse.level.maps.Level;

public class SlimeGreatswordAttackHandler extends GreatswordAttackHandler {
   public SlimeGreatswordAttackHandler(SlimeGreatswordToolItem var1, PlayerMob var2, PlayerInventorySlot var3, InventoryItem var4, GreatswordToolItem var5, int var6, int var7, int var8, GreatswordChargeLevel... var9) {
      super(var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void onEndAttack(boolean var1) {
      super.onEndAttack(var1);
      Point2D.Float var2 = GameMath.getAngleDir(this.currentAngle);
      switch (this.currentChargeLevel) {
         case 2:
            this.summonSlimeMob(var2);
         case 1:
            this.launchSlimeProjectile(var2);
         default:
      }
   }

   private void summonSlimeMob(Point2D.Float var1) {
      if (this.player.isServerClient()) {
         ServerClient var2 = this.player.getServerClient();
         SlimeGreatswordFollowingMob var3 = new SlimeGreatswordFollowingMob();
         var2.addFollower("slimegreatswordslime", var3, FollowPosition.WALK_CLOSE, "summonedmob", 1.0F, 10, (BiConsumer)null, false);
         Point2D.Float var4 = this.findSpawnLocation(var3, this.player.getLevel(), var2.playerMob);
         var3.updateDamage(this.toolItem.getAttackDamage(this.item));
         var3.setEnchantment(this.toolItem.getEnchantment(this.item));
         var3.dx = var1.x * 300.0F;
         var3.dy = var1.y * 300.0F;
         this.player.getLevel().entityManager.addMob(var3, var4.x, var4.y);
      }

   }

   private void launchSlimeProjectile(Point2D.Float var1) {
      float var2 = 7.0F;
      float var3 = 140.0F;
      float var4 = (float)Math.round((Float)this.toolItem.getEnchantment(this.item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue) * var3 * (Float)this.player.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY));
      SlimeGreatswordProjectile var5 = new SlimeGreatswordProjectile(this.player.getLevel(), this.player.x, this.player.y, this.player.x + var1.x * 100.0F, this.player.y + var1.y * 100.0F, var4, (int)((float)this.toolItem.getAttackRange(this.item) * var2), this.toolItem.getAttackDamage(this.item), this.player);
      GameRandom var6 = new GameRandom((long)this.seed);
      var5.resetUniqueID(var6);
      this.player.getLevel().entityManager.projectiles.addHidden(var5);
      var5.moveDist(20.0);
      if (this.player.isServer()) {
         this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var5), (ServerClient)this.player.getServerClient(), this.player.getServerClient());
      }

   }

   public Point2D.Float findSpawnLocation(AttackingFollowingMob var1, Level var2, PlayerMob var3) {
      return findSpawnLocation(var1, var2, var3.x, var3.y);
   }

   public static Point2D.Float findSpawnLocation(Mob var0, Level var1, float var2, float var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            if (var5 != 0 || var6 != 0) {
               float var7 = var2 + (float)(var5 * 32);
               float var8 = var3 + (float)(var6 * 32);
               if (!var0.collidesWith(var1, (int)var7, (int)var8)) {
                  var4.add(new Point2D.Float(var7, var8));
               }
            }
         }
      }

      if (var4.size() > 0) {
         return (Point2D.Float)var4.get(GameRandom.globalRandom.nextInt(var4.size()));
      } else {
         return new Point2D.Float(var2, var3);
      }
   }
}
