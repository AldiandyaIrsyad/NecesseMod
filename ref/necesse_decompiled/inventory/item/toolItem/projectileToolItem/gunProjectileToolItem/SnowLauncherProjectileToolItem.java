package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.level.maps.Level;

public class SnowLauncherProjectileToolItem extends GunProjectileToolItem {
   public SnowLauncherProjectileToolItem() {
      super((String)"snowball", 700);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(100);
      this.attackXOffset = 23;
      this.attackYOffset = 13;
      this.attackRange.setBaseValue(1000);
      this.velocity.setBaseValue(400);
      this.ammoConsumeChance = 0.5F;
      this.addGlobalIngredient(new String[]{"bulletuser"});
   }

   protected void addAmmoTooltips(ListGameTooltips var1, InventoryItem var2) {
      var1.add(Localization.translate("itemtooltip", "snowlaunchertip1"));
      var1.add(Localization.translate("itemtooltip", "snowlaunchertip2"));
   }

   public void addAttackDamageTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Attacker var4, boolean var5) {
   }

   public void addCritChanceTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Attacker var4, boolean var5) {
   }

   public void addKnockbackTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Attacker var4) {
   }

   public void addResilienceGainTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
   }

   public void addAttackRangeTip(ItemStatTipList var1, InventoryItem var2, InventoryItem var3) {
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = var10.getNextShortUnsigned();
      if (var11 != 65535) {
         Item var12 = ItemRegistry.getItem(var11);
         if (var12 != null) {
            GameRandom var13 = new GameRandom((long)(var9 + 5));
            float var14 = this.getAmmoConsumeChance(var4, var6);
            boolean var15 = var14 >= 1.0F || var14 > 0.0F && var13.getChance(var14);
            if (!var15 || var4.getInv().main.removeItems(var1, var4, var12, 1, "bulletammo") >= 1) {
               Projectile var16 = ProjectileRegistry.getProjectile("playersnowball", var1, var4.x, var4.y, (float)var2, (float)var3, (float)this.getProjectileVelocity(var6, var4), 800, this.getAttackDamage(var6), this.getKnockback(var6, var4), var4);
               var16.setAngle(var16.getAngle() + (float)var13.getIntBetween(-5, 5));
               var16.resetUniqueID(new GameRandom((long)var9));
               var16.moveDist(55.0);
               var1.entityManager.projectiles.addHidden(var16);
               if (var1.isServer()) {
                  var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var16), (ServerClient)var4.getServerClient(), var4.getServerClient());
               }
            }
         } else {
            GameLog.warn.println(var4.getDisplayName() + " tried to use item " + (var12 == null ? var11 : var12.getStringID()) + " as bullet.");
         }
      }

      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      var2.attackItem((int)var3.x, (int)var3.y, var6);
      Projectile var7 = ProjectileRegistry.getProjectile("playersnowball", var1, var2.x, var2.y, var3.x, var3.y, (float)this.getProjectileVelocity(var6, var2), 800, this.getAttackDamage(var6), this.getKnockback(var6, var2), var2);
      GameRandom var8 = new GameRandom((long)(var5 + 5));
      var7.setAngle(var7.getAngle() + (float)var8.getIntBetween(-5, 5));
      var7.resetUniqueID(new GameRandom((long)var5));
      var7.moveDist(55.0);
      var1.entityManager.projectiles.addHidden(var7);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var7), (Level)var1);
      }

      return var6;
   }

   public float getSettlerWeaponValue(HumanMob var1, InventoryItem var2) {
      return 0.0F;
   }

   public void playFireSound(AttackAnimMob var1) {
      Screen.playSound(GameResources.flick, SoundEffect.effect(var1));
   }
}
