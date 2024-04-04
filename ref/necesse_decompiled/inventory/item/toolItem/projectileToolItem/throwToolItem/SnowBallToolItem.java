package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

public class SnowBallToolItem extends ThrowToolItem {
   public SnowBallToolItem() {
      this.attackAnimTime.setBaseValue(250);
      this.attackDamage.setBaseValue(0.0F);
      this.velocity.setBaseValue(100);
      this.rarity = Item.Rarity.COMMON;
      this.attackXOffset = -25;
      this.attackYOffset = 2;
      this.resilienceGain.setBaseValue(0.0F);
      this.incinerationTimeMillis = 3000;
   }

   public void addAttackSpeedTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4) {
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      return null;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      Projectile var11 = ProjectileRegistry.getProjectile("playersnowball", var1, var4.x, var4.y, (float)var2, (float)var3, (float)this.getThrowingVelocity(var6, var4), 800, this.getAttackDamage(var6), this.getKnockback(var6, var4), var4);
      var11.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var11.resetUniqueID(new GameRandom((long)var9));
      var1.entityManager.projectiles.addHidden(var11);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var11), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      var6.setAmount(var6.getAmount() - 1);
      return var6;
   }
}
