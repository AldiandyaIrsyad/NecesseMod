package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.FrostStaffProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class FrostStaffProjectileToolItem extends MagicProjectileToolItem {
   public FrostStaffProjectileToolItem() {
      super(700);
      this.rarity = Item.Rarity.COMMON;
      this.attackAnimTime.setBaseValue(400);
      this.attackCooldownTime.setBaseValue(500);
      this.attackDamage.setBaseValue(16.0F).setUpgradedValue(1.0F, 60.0F);
      this.velocity.setBaseValue(400);
      this.attackXOffset = 26;
      this.attackYOffset = 26;
      this.attackRange.setBaseValue(800);
      this.knockback.setBaseValue(50);
      this.manaCost.setBaseValue(1.0F).setUpgradedValue(1.0F, 4.0F);
      this.resilienceGain.setBaseValue(0.75F);
      this.settlerProjectileCanHitWidth = 32.0F;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      super.setDrawAttackRotation(var1, var2, var3, var4, var5);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "froststafftip"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.jingle, SoundEffect.effect(var4).volume(0.9F).pitch(1.2F));
         Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(var4).volume(0.4F).pitch(1.0F));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = this.getProjectileVelocity(var6, var4);
      int var12 = this.getAttackRange(var6);
      int var13 = this.getKnockback(var6, var4);
      Point2D.Float var14 = GameMath.normalize((float)var2 - var4.x, (float)var3 - var4.y);
      Point2D.Float var15 = GameMath.getPerpendicularPoint(var4.x, var4.y, 10.0F, var14);
      int var16 = this.getAttackRange(var6);
      FrostStaffProjectile var17 = new FrostStaffProjectile(var1, var4, var15.x, var15.y, var14.x, var14.y, (float)var11 / 10.0F, (float)var11, (float)var16, var12, this.getAttackDamage(var6), var13);
      var17.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var17.resetUniqueID(new GameRandom((long)var9));
      var1.entityManager.projectiles.addHidden(var17);
      var17.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var17), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      Point2D.Float var18 = GameMath.getPerpendicularPoint(var4.x, var4.y, -10.0F, var14);
      FrostStaffProjectile var19 = new FrostStaffProjectile(var1, var4, var18.x, var18.y, var14.x, var14.y, (float)var11 / 10.0F, (float)var11, (float)var16, var12, this.getAttackDamage(var6), var13);
      var19.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var19.resetUniqueID(new GameRandom((long)var9));
      var1.entityManager.projectiles.addHidden(var19);
      var19.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var19), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      int var7 = this.getProjectileVelocity(var6, var2);
      int var8 = this.getAttackRange(var6);
      int var9 = this.getKnockback(var6, var2);
      Point2D.Float var10 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7, -30.0F);
      var2.attackItem((int)var10.x, (int)var10.y, var6);
      Point2D.Float var11 = GameMath.normalize(var10.x - var2.x, var10.y - var2.y);
      Point2D.Float var12 = GameMath.getPerpendicularPoint(var2.x, var2.y, 10.0F, var11);
      int var13 = this.getAttackRange(var6);
      FrostStaffProjectile var14 = new FrostStaffProjectile(var1, var2, var12.x, var12.y, var11.x, var11.y, (float)var7 / 10.0F, (float)var7, (float)var13, var8, this.getAttackDamage(var6), var9);
      var14.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var14.resetUniqueID(new GameRandom((long)var5));
      var1.entityManager.projectiles.addHidden(var14);
      var14.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var14), (Level)var1);
      }

      Point2D.Float var15 = GameMath.getPerpendicularPoint(var2.x, var2.y, -10.0F, var11);
      FrostStaffProjectile var16 = new FrostStaffProjectile(var1, var2, var15.x, var15.y, var11.x, var11.y, (float)var7 / 10.0F, (float)var7, (float)var13, var8, this.getAttackDamage(var6), var9);
      var16.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var16.resetUniqueID(new GameRandom((long)var5));
      var1.entityManager.projectiles.addHidden(var16);
      var16.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var16), (Level)var1);
      }

      return var6;
   }
}
