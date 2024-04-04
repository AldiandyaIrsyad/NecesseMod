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
import necesse.entity.projectile.IcicleStaffProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class IcicleStaffProjectileToolItem extends MagicProjectileToolItem {
   public IcicleStaffProjectileToolItem() {
      super(1200);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(50.0F).setUpgradedValue(1.0F, 80.0F);
      this.velocity.setBaseValue(150);
      this.attackXOffset = 14;
      this.attackYOffset = 4;
      this.attackRange.setBaseValue(900);
      this.knockback.setBaseValue(50);
      this.manaCost.setBaseValue(1.75F).setUpgradedValue(1.0F, 2.5F);
      this.settlerProjectileCanHitWidth = 5.0F;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4).forEachItemSprite((var0) -> {
         var0.itemRotateOffset(45.0F);
      });
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "iciclestafftip"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.jingle, SoundEffect.effect(var4).volume(0.6F).pitch(GameRandom.globalRandom.getFloatBetween(0.95F, 1.0F)));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      GameRandom var11 = new GameRandom((long)var9);
      Point2D.Float var12 = GameMath.normalize(var4.x - (float)var2, var4.y - (float)var3);
      int var13 = var11.getIntBetween(30, 50);
      Point2D.Float var14 = new Point2D.Float(var12.x * (float)var13, var12.y * (float)var13);
      var14 = GameMath.getPerpendicularPoint(var14, (float)var11.getIntBetween(-50, 50), var12);
      IcicleStaffProjectile var15 = new IcicleStaffProjectile(var1, var4, var4.x + var14.x, var4.y + var14.y, (float)var2, (float)var3, (float)this.getProjectileVelocity(var6, var4), this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4));
      var15.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var15.resetUniqueID(var11);
      var1.entityManager.projectiles.addHidden(var15);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var15), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      int var7 = this.getProjectileVelocity(var6, var2);
      Point2D.Float var8 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7, -10.0F);
      var2.attackItem((int)var8.x, (int)var8.y, var6);
      GameRandom var9 = new GameRandom((long)var5);
      IcicleStaffProjectile var10 = new IcicleStaffProjectile(var1, var2, var2.x, var2.y, var8.x, var8.y, (float)var7, this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var2));
      var10.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var10.resetUniqueID(var9);
      var1.entityManager.projectiles.addHidden(var10);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var10), (Level)var1);
      }

      return var6;
   }
}
