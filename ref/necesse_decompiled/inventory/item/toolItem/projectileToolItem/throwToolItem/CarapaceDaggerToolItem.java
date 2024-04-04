package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.CarapaceDaggerProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.level.maps.Level;

public class CarapaceDaggerToolItem extends ThrowToolItem {
   public CarapaceDaggerToolItem() {
      super(1200);
      this.attackAnimTime.setBaseValue(175);
      this.damageType = DamageTypeRegistry.MELEE;
      this.attackDamage.setBaseValue(40.0F).setUpgradedValue(1.0F, 50.0F);
      this.velocity.setBaseValue(200);
      this.rarity = Item.Rarity.UNCOMMON;
      this.stackSize = 1;
      this.attackRange.setBaseValue(800);
      this.resilienceGain.setBaseValue(0.5F);
      this.settlerProjectileCanHitWidth = 8.0F;
      this.setItemCategory(new String[]{"equipment", "weapons", "meleeweapons"});
      this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "meleeweapons"});
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "carapacedaggertip1"));
      var4.add(Localization.translate("itemtooltip", "carapacedaggertip2"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      return null;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = this.getThrowingVelocity(var6, var4);
      CarapaceDaggerProjectile var12 = new CarapaceDaggerProjectile(var1, var4, var4.x, var4.y, (float)var2, (float)var3, (float)var11, this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var4));
      var12.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      GameRandom var13 = new GameRandom((long)var9);
      var12.resetUniqueID(var13);
      var12.moveDist(30.0);
      var12.setAngle(var12.getAngle() + (float)var13.getIntBetween(-15, 15));
      var1.entityManager.projectiles.addHidden(var12);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var12), (ServerClient)var4.getServerClient(), var4.getServerClient());
      }

      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      int var7 = this.getProjectileVelocity(var6, var2);
      Point2D.Float var8 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7, -30.0F);
      var2.attackItem((int)var8.x, (int)var8.y, var6);
      GameRandom var9 = new GameRandom((long)var5);
      CarapaceDaggerProjectile var10 = new CarapaceDaggerProjectile(var1, var2, var2.x, var2.y, var8.x, var8.y, (float)var7, this.getAttackRange(var6), this.getAttackDamage(var6), this.getKnockback(var6, var2));
      var10.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(var6)));
      var10.resetUniqueID(var9);
      var10.moveDist(30.0);
      var10.setAngle(var10.getAngle() + (float)var9.getIntBetween(-15, 15));
      var1.entityManager.projectiles.addHidden(var10);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsAt(new PacketSpawnProjectile(var10), (Level)var1);
      }

      return var6;
   }

   public boolean isEnchantable(InventoryItem var1) {
      return var1.getAmount() >= this.getStackSize();
   }

   public String getIsEnchantableError(InventoryItem var1) {
      return var1.getAmount() < this.getStackSize() ? Localization.translate("itemtooltip", "enchantfullstack") : super.getIsEnchantableError(var1);
   }
}
