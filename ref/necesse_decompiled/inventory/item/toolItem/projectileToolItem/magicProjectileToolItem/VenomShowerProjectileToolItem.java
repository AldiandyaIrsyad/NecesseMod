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
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.VenomShowerProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class VenomShowerProjectileToolItem extends MagicProjectileToolItem {
   public VenomShowerProjectileToolItem() {
      super(1200);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(200);
      this.attackDamage.setBaseValue(45.0F).setUpgradedValue(1.0F, 50.0F);
      this.velocity.setBaseValue(100);
      this.attackXOffset = 14;
      this.attackYOffset = 4;
      this.attackRange.setBaseValue(600);
      this.manaCost.setBaseValue(2.0F).setUpgradedValue(1.0F, 3.2F);
      this.resilienceGain.setBaseValue(0.5F);
      this.settlerProjectileCanHitWidth = 25.0F;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "venomshowertip1"));
      var4.add(Localization.translate("itemtooltip", "venomshowertip2"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4).forEachItemSprite((var0) -> {
         var0.itemRotateOffset(45.0F);
      });
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt2, SoundEffect.effect(var4).volume(0.4F).pitch(GameRandom.globalRandom.getFloatBetween(0.8F, 0.9F)));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      VenomShowerProjectile var11 = new VenomShowerProjectile(var1, var4.x, var4.y, (float)var2, (float)var3, this.getAttackRange(var6), this.getAttackDamage(var6), var4);
      var11.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      GameRandom var12 = new GameRandom((long)var9);
      var11.resetUniqueID(var12);
      var1.entityManager.projectiles.addHidden(var11);
      var11.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var11), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      int var7 = this.getAttackRange(var6);
      Point2D.Float var8 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7 * 1.1F, -30.0F);
      var2.attackItem((int)var8.x, (int)var8.y, var6);
      VenomShowerProjectile var9 = new VenomShowerProjectile(var1, var2.x, var2.y, var8.x, var8.y, var7, this.getAttackDamage(var6), var2);
      var9.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      GameRandom var10 = new GameRandom((long)var5);
      var9.resetUniqueID(var10);
      var1.entityManager.projectiles.addHidden(var9);
      var9.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var9), (Level)var1);
      }

      return var6;
   }
}
