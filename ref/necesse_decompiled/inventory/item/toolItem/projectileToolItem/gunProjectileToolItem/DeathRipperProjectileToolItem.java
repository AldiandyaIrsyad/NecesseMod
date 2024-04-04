package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.DeathRipperAttackHandler;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.level.maps.Level;

public class DeathRipperProjectileToolItem extends GunProjectileToolItem {
   public DeathRipperProjectileToolItem() {
      super((String[])NORMAL_AMMO_TYPES, 1600);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(2000);
      this.attackDamage.setBaseValue(19.0F).setUpgradedValue(1.0F, 35.0F);
      this.attackXOffset = 12;
      this.attackYOffset = 16;
      this.moveDist = 30;
      this.attackRange.setBaseValue(1000);
      this.velocity.setBaseValue(500);
      this.knockback.setBaseValue(25);
      this.ammoConsumeChance = 0.25F;
      this.resilienceGain.setBaseValue(0.5F);
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return 1.0F;
   }

   public boolean animDrawBehindHand() {
      return false;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4, 90.0F);
   }

   public Projectile getProjectile(InventoryItem var1, BulletItem var2, float var3, float var4, float var5, float var6, int var7, Mob var8) {
      Projectile var9 = super.getProjectile(var1, var2, var3, var4, var5, var6, var7, var8);
      var9.height = 6.0F;
      return var9;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      var4.startAttackHandler(new DeathRipperAttackHandler(var4, var7, var6, this, var9, var2, var3));
      return var6;
   }

   public InventoryItem superOnAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      return super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   protected void addExtraGunTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      super.addExtraGunTooltips(var1, var2, var3, var4);
      var1.add(Localization.translate("itemtooltip", "deathrippertip2"));
   }

   protected void addAmmoTooltips(ListGameTooltips var1, InventoryItem var2) {
      super.addAmmoTooltips(var1, var2);
      var1.add(Localization.translate("itemtooltip", "deathrippertip1"));
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

      Projectile var14 = this.getProjectile(var5, var7, var4.x, var4.y, (float)var2, (float)var3, var12, var4);
      var14.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var5)));
      var14.dropItem = var8;
      var14.getUniqueID(var10);
      var1.entityManager.projectiles.addHidden(var14);
      if (this.moveDist != 0) {
         var14.moveDist((double)this.moveDist);
      }

      var14.setAngle(var14.getAngle() + var11.getFloatOffset(0.0F, 6.0F));
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var14), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

   }

   public void playFireSound(AttackAnimMob var1) {
   }
}
