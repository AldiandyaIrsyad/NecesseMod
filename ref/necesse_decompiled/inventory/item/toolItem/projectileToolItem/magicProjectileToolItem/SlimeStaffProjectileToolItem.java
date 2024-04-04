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
import necesse.entity.projectile.SlimeBoltProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class SlimeStaffProjectileToolItem extends MagicProjectileToolItem {
   public SlimeStaffProjectileToolItem() {
      super(1800);
      this.rarity = Item.Rarity.EPIC;
      this.attackAnimTime.setBaseValue(300);
      this.attackDamage.setBaseValue(32.0F).setUpgradedValue(1.0F, 35.0F);
      this.velocity.setBaseValue(100);
      this.attackXOffset = 20;
      this.attackYOffset = 30;
      this.attackRange.setBaseValue(1250);
      this.knockback.setBaseValue(15);
      this.manaCost.setBaseValue(3.5F).setUpgradedValue(1.0F, 3.2F);
      this.resilienceGain.setBaseValue(0.5F);
      this.settlerProjectileCanHitWidth = 5.0F;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "slimestafftip"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(var4).volume(0.3F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.6F)));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      GameRandom var11 = new GameRandom((long)var9);

      for(int var12 = -1; var12 <= 1; ++var12) {
         SlimeBoltProjectile var13 = new SlimeBoltProjectile(var1, var4.x, var4.y, (float)var2, (float)var3, (float)this.getProjectileVelocity(var6, var4), this.getAttackRange(var6), this.getAttackDamage(var6), var4);
         var13.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
         var13.resetUniqueID(var11);
         var1.entityManager.projectiles.addHidden(var13);
         var13.moveDist(20.0);
         var13.setAngle(var13.getAngle() + (float)(10 * var12));
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var13), (ServerClient)var4.getServerClient(), var4.getServerClient());
         }
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      int var7 = this.getProjectileVelocity(var6, var2);
      Point2D.Float var8 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7, -30.0F);
      var2.attackItem((int)var8.x, (int)var8.y, var6);
      GameRandom var9 = new GameRandom((long)var5);

      for(int var10 = -1; var10 <= 1; ++var10) {
         SlimeBoltProjectile var11 = new SlimeBoltProjectile(var1, var2.x, var2.y, var8.x, var8.y, (float)this.getProjectileVelocity(var6, var2), this.getAttackRange(var6), this.getAttackDamage(var6), var2);
         var11.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
         var11.resetUniqueID(var9);
         var1.entityManager.projectiles.addHidden(var11);
         var11.moveDist(20.0);
         var11.setAngle(var11.getAngle() + (float)(10 * var10));
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var11), (Level)var1);
         }
      }

      return var6;
   }
}
