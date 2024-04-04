package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.ThrowToolItem;
import necesse.level.maps.Level;

public class BoomerangToolItem extends ThrowToolItem {
   protected String projectileID;

   public BoomerangToolItem(int var1, String var2) {
      super(var1);
      this.projectileID = var2;
      this.setItemCategory(new String[]{"equipment", "weapons", "meleeweapons"});
      this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "meleeweapons"});
      this.keyWords.add("boomerang");
      this.damageType = DamageTypeRegistry.MELEE;
      this.knockback.setBaseValue(25);
      this.stackSize = 1;
      this.attackRange.setBaseValue(200);
      this.enchantCost.setUpgradedValue(1.0F, 1950);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      int var5 = this.getStackSize();
      if (var5 > 1) {
         var4.add(Localization.translate("itemtooltip", "boomerangstack", "value", (Object)var5));
      }

      return var4;
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return var4.getBoomerangsUsage() < Math.min(var5.getAmount(), var5.itemStackSize()) ? null : "";
   }

   protected boolean canSettlerBoomerangAttack(HumanMob var1, Mob var2, InventoryItem var3) {
      return var1.getBoomerangsUsage() < Math.min(var3.getAmount(), var3.itemStackSize());
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      Projectile var11 = ProjectileRegistry.getProjectile(this.projectileID, var1, var4.x, var4.y, (float)var2, (float)var3, (float)this.getThrowingVelocity(var6, var4), this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4), var4);
      var11.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var4.boomerangs.add(var11);
      var11.resetUniqueID(new GameRandom((long)var9));
      var1.entityManager.projectiles.addHidden(var11);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var11), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      if (this.canSettlerBoomerangAttack(var2, var3, var6)) {
         int var7 = this.getThrowingVelocity(var6, var2);
         Point2D.Float var8 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7, -10.0F);
         var2.attackItem((int)var8.x, (int)var8.y, var6);
         Projectile var9 = ProjectileRegistry.getProjectile(this.projectileID, var1, var2.x, var2.y, var8.x, var8.y, (float)var7, this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var2), var2);
         var9.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
         var2.boomerangs.add(var9);
         var9.resetUniqueID(new GameRandom((long)var5));
         var1.entityManager.projectiles.addHidden(var9);
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var9), (Level)var1);
         }
      }

      return var6;
   }

   public boolean isEnchantable(InventoryItem var1) {
      return var1.getAmount() >= this.getStackSize();
   }

   public String getIsEnchantableError(InventoryItem var1) {
      return var1.getAmount() < this.getStackSize() ? Localization.translate("itemtooltip", "enchantfullstack") : super.getIsEnchantableError(var1);
   }

   public String getCanBeUpgradedError(InventoryItem var1) {
      return var1.getAmount() < this.getStackSize() ? Localization.translate("ui", "upgradefullstack") : super.getCanBeUpgradedError(var1);
   }

   public Collection<InventoryItem> getSalvageRewards(InventoryItem var1) {
      int var2 = (int)((float)var1.getAmount() * this.getUpgradeTier(var1) * 10.0F / (float)this.getStackSize());
      return Collections.singleton(new InventoryItem("upgradeshard", var2));
   }
}
