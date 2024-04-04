package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class DragonsReboundToolItem extends BoomerangToolItem {
   public DragonsReboundToolItem() {
      super(1400, (String)null);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(65.0F).setUpgradedValue(1.0F, 67.0F);
      this.attackRange.setBaseValue(650);
      this.velocity.setBaseValue(210);
      this.resilienceGain.setBaseValue(0.5F);
      this.settlerProjectileCanHitWidth = 14.0F;
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return null;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      GNDItemMap var11 = var6.getGndData();
      boolean var12 = var11.getBoolean("sage");
      Projectile var13 = ProjectileRegistry.getProjectile(var12 ? "sageboomerang" : "gritboomerang", var1, var4.x, var4.y, (float)var2, (float)var3, (float)this.getThrowingVelocity(var6, var4), this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4), var4);
      var13.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var4.boomerangs.add(var13);
      var13.resetUniqueID(new GameRandom((long)var9));
      var1.entityManager.projectiles.addHidden(var13);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var13), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      var11.setBoolean("sage", !var12);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      if (this.canSettlerBoomerangAttack(var2, var3, var6)) {
         GNDItemMap var7 = var6.getGndData();
         boolean var8 = var7.getBoolean("sage");
         int var9 = this.getThrowingVelocity(var6, var2);
         Point2D.Float var10 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var9, -10.0F);
         var2.attackItem((int)var10.x, (int)var10.y, var6);
         Projectile var11 = ProjectileRegistry.getProjectile(var8 ? "sageboomerang" : "gritboomerang", var1, var2.x, var2.y, var10.x, var10.y, (float)var9, this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var2), var2);
         var11.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
         var2.boomerangs.add(var11);
         var11.resetUniqueID(new GameRandom((long)var5));
         var1.entityManager.projectiles.addHidden(var11);
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var11), (Level)var1);
         }

         var7.setBoolean("sage", !var8);
      }

      return var6;
   }
}
