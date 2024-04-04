package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.DredgingStaffEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class DredgingStaffProjectileToolItem extends MagicProjectileToolItem {
   public DredgingStaffProjectileToolItem() {
      super(750);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(30.0F).setUpgradedValue(1.0F, 85.0F);
      this.velocity.setBaseValue(150);
      this.attackXOffset = 14;
      this.attackYOffset = 4;
      this.attackRange.setBaseValue(200);
      this.knockback.setBaseValue(50);
      this.manaCost.setBaseValue(1.25F).setUpgradedValue(1.0F, 4.5F);
      this.resilienceGain.setBaseValue(1.0F);
      this.settlerProjectileCanHitWidth = 10.0F;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.pointRotation(var3, var4).forEachItemSprite((var0) -> {
         var0.itemRotateOffset(45.0F);
      });
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "dredgingstafftip"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt3, SoundEffect.effect(var4).pitch(0.8F));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = this.getAttackRange(var6);
      DredgingStaffEvent var12 = new DredgingStaffEvent(var4, var4.getX(), var4.getY(), new GameRandom((long)var9), var2, var3, this.getAttackDamage(var6), this.getResilienceGain(var6), (float)this.getProjectileVelocity(var6, var4), var11, (int)((float)var11 * 0.7F));
      var1.entityManager.addLevelEventHidden(var12);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(var12), var12, var4.getServerClient());
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      int var7 = this.getProjectileVelocity(var6, var2);
      Point2D.Float var8 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7, -10.0F);
      var2.attackItem((int)var8.x, (int)var8.y, var6);
      int var9 = this.getAttackRange(var6);
      DredgingStaffEvent var10 = new DredgingStaffEvent(var2, var2.getX(), var2.getY(), new GameRandom((long)var5), (int)var8.x, (int)var8.y, this.getAttackDamage(var6), this.getResilienceGain(var6), (float)var7, var9, (int)((float)var9 * 0.7F));
      var1.entityManager.addLevelEventHidden(var10);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsWithEntity(new PacketLevelEvent(var10), var10);
      }

      return var6;
   }
}
