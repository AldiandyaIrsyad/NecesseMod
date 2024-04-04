package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseProjectileAttackHandler;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class MouseTestProjectileToolItem extends MagicProjectileToolItem {
   public MouseTestProjectileToolItem() {
      super(600);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(13.0F);
      this.velocity.setBaseValue(120);
      this.attackXOffset = 6;
      this.attackYOffset = 8;
      this.attackCooldownTime.setBaseValue(500);
      this.attackRange.setBaseValue(1000);
   }

   public GameMessage getNewLocalization() {
      return new StaticMessage("Mouse Projectile Test");
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      Projectile var11 = ProjectileRegistry.getProjectile("mousetest", var1, var4.x, var4.y, (float)var2, (float)var3, (float)this.getProjectileVelocity(var6, var4), 100, this.getAttackDamage(var6), this.getKnockback(var6, var4), var4);
      var11.resetUniqueID(new GameRandom((long)var9));
      var1.entityManager.projectiles.addHidden(var11);
      var11.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var11), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      if (var4.isAttackHandlerFrom(var6, var7)) {
         ((MouseProjectileAttackHandler)var4.getAttackHandler()).addProjectiles((FollowingProjectile)var11);
      } else {
         var4.startAttackHandler(new MouseProjectileAttackHandler(var4, var7, this.getAttackRange(var6), new FollowingProjectile[]{(FollowingProjectile)var11}));
      }

      return var6;
   }
}
