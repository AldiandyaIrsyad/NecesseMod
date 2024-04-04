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
import necesse.entity.projectile.VenomStaffProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class VenomStaffProjectileToolItem extends MagicProjectileToolItem {
   public VenomStaffProjectileToolItem() {
      super(400);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(500);
      this.itemCooldownTime.setBaseValue(2000);
      this.attackDamage.setBaseValue(15.0F).setUpgradedValue(1.0F, 45.0F);
      this.velocity.setBaseValue(150);
      this.attackXOffset = 20;
      this.attackYOffset = 20;
      this.attackRange.setBaseValue(300);
      this.manaCost.setBaseValue(3.0F).setUpgradedValue(1.0F, 12.0F);
      this.resilienceGain.setBaseValue(2.0F);
      this.settlerProjectileCanHitWidth = 5.0F;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "venomstafftip"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt2, SoundEffect.effect(var4).volume(0.4F).pitch(GameRandom.globalRandom.getFloatBetween(0.8F, 0.9F)));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = (int)Math.min((float)this.getAttackRange(var6), var4.getDistance((float)var2, (float)var3));
      VenomStaffProjectile var12 = new VenomStaffProjectile(var1, var4.x, var4.y, (float)var2, (float)var3, this.getProjectileVelocity(var6, var4), var11, this.getAttackDamage(var6), this.getResilienceGain(var6), var4);
      GameRandom var13 = new GameRandom((long)var9);
      var12.resetUniqueID(var13);
      var1.entityManager.projectiles.addHidden(var12);
      var12.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var12), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      int var7 = this.getProjectileVelocity(var6, var2);
      Point2D.Float var8 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7, -30.0F);
      var2.attackItem((int)var8.x, (int)var8.y, var6);
      int var9 = (int)Math.min((float)this.getAttackRange(var6), var2.getDistance(var8.x, var8.y));
      VenomStaffProjectile var10 = new VenomStaffProjectile(var1, var2.x, var2.y, var8.x, var8.y, var7, var9, this.getAttackDamage(var6), this.getResilienceGain(var6), var2);
      GameRandom var11 = new GameRandom((long)var5);
      var10.resetUniqueID(var11);
      var1.entityManager.projectiles.addHidden(var10);
      var10.moveDist(20.0);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var10), (Level)var1);
      }

      return var6;
   }
}
