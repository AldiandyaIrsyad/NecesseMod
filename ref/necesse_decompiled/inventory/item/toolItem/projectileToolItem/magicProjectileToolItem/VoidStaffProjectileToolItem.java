package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class VoidStaffProjectileToolItem extends MagicProjectileToolItem {
   public VoidStaffProjectileToolItem() {
      super(800);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(200);
      this.attackDamage.setBaseValue(32.0F).setUpgradedValue(1.0F, 80.0F);
      this.knockback.setBaseValue(0);
      this.attackXOffset = 20;
      this.attackYOffset = 20;
      this.attackCooldownTime.setBaseValue(400);
      this.attackRange.setBaseValue(400);
      this.manaCost.setBaseValue(2.0F).setUpgradedValue(1.0F, 3.5F);
      this.settlerProjectileCanHitWidth = 5.0F;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "voidstafftip"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt3, SoundEffect.effect(var4).volume(0.4F).pitch(GameRandom.globalRandom.getFloatBetween(1.3F, 1.4F)));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      Projectile var11 = ProjectileRegistry.getProjectile("voidlaser", var1, var4.x, var4.y, (float)var2, (float)var3, (float)this.getProjectileVelocity(var6, var4), this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4), var4);
      var11.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var11.resetUniqueID(new GameRandom((long)var9));
      var1.entityManager.projectiles.addHidden(var11);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var11), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      var2.attackItem(var3.getX(), var3.getY(), var6);
      Projectile var7 = ProjectileRegistry.getProjectile("voidlaser", var1, var2.x, var2.y, var3.x, var3.y, (float)this.getProjectileVelocity(var6, var2), this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var2), var2);
      var7.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var7.resetUniqueID(new GameRandom((long)var5));
      var1.entityManager.projectiles.addHidden(var7);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var7), (Level)var1);
      }

      return var6;
   }
}
