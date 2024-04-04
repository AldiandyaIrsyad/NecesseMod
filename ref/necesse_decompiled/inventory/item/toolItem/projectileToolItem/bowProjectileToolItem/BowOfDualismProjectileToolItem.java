package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.GritArrowProjectile;
import necesse.entity.projectile.SageArrowProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.Level;

public class BowOfDualismProjectileToolItem extends BowProjectileToolItem {
   public BowOfDualismProjectileToolItem() {
      super(1500);
      this.attackAnimTime.setBaseValue(450);
      this.rarity = Item.Rarity.RARE;
      this.attackDamage.setBaseValue(42.0F).setUpgradedValue(1.0F, 80.0F);
      this.velocity.setBaseValue(220);
      this.attackRange.setBaseValue(800);
      this.attackXOffset = 12;
      this.attackYOffset = 28;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(var4).pitch(1.1F));
      }

   }

   protected void addExtraBowTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      var1.add(Localization.translate("itemtooltip", "bowofdualismtip"));
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   protected void fireProjectiles(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, PacketReader var9) {
      GameRandom var10 = new GameRandom((long)var6);
      boolean var11 = (float)var2 < var4.x;
      GritArrowProjectile var12 = new GritArrowProjectile(var4, var4.x, var4.y, (float)var2, (float)var3, (float)this.getProjectileVelocity(var5, var4) * var7.speedMod, this.getAttackRange(var5), this.getAttackDamage(var5).add((float)var7.damage, var7.armorPen, var7.critChance), this.getKnockback(var5, var4));
      var12.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var5)));
      var12.setAngle(var12.getAngle() + (float)(var11 ? 5 : -5));
      var12.dropItem = false;
      var12.getUniqueID(var10);
      SageArrowProjectile var13 = new SageArrowProjectile(var4, var4.x, var4.y, (float)var2, (float)var3, (float)this.getProjectileVelocity(var5, var4) * var7.speedMod, this.getAttackRange(var5), this.getAttackDamage(var5).add((float)var7.damage, var7.armorPen, var7.critChance), this.getKnockback(var5, var4));
      var13.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var5)));
      var13.setAngle(var13.getAngle() - (float)(var11 ? 5 : -5));
      var13.dropItem = false;
      var13.getUniqueID(var10);
      var1.entityManager.projectiles.addHidden(var12);
      var1.entityManager.projectiles.addHidden(var13);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var12), (ServerClient)var4.getServerClient(), var4.getServerClient());
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var13), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

   }
}
