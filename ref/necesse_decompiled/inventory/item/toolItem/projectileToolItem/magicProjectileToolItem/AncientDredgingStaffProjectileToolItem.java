package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.AncientDredgingStaffEvent;
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

public class AncientDredgingStaffProjectileToolItem extends MagicProjectileToolItem {
   public AncientDredgingStaffProjectileToolItem() {
      super(1300);
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(600);
      this.attackDamage.setBaseValue(55.0F).setUpgradedValue(1.0F, 75.0F);
      this.velocity.setBaseValue(150);
      this.attackXOffset = 20;
      this.attackYOffset = 20;
      this.attackRange.setBaseValue(300);
      this.knockback.setBaseValue(50);
      this.manaCost.setBaseValue(5.0F).setUpgradedValue(1.0F, 5.5F);
      this.resilienceGain.setBaseValue(1.0F);
      this.settlerProjectileCanHitWidth = 10.0F;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      super.setDrawAttackRotation(var1, var2, var3, var4, var5);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "ancientdredgingstafftip"));
      return var4;
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt3, SoundEffect.effect(var4).pitch(1.2F));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = this.getAttackRange(var6);
      Point2D.Float var12 = new Point2D.Float((float)(var2 - var4.getX()), (float)(var3 - var4.getY()));
      AncientDredgingStaffEvent var13 = new AncientDredgingStaffEvent(var4, var4.getX(), var4.getY(), new GameRandom((long)var9), GameMath.getAngle(var12), this.getAttackDamage(var6), this.getResilienceGain(var6), (float)this.getProjectileVelocity(var6, var4), (float)this.getKnockback(var6, var4), (float)var11);
      var1.entityManager.addLevelEventHidden(var13);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(var13), var13, var4.getServerClient());
      }

      this.consumeMana(var4, var6);
      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      int var7 = this.getProjectileVelocity(var6, var2);
      Point2D.Float var8 = Projectile.getPredictedTargetPos(var3, var2.x, var2.y, (float)var7, -10.0F);
      var2.attackItem((int)var8.x, (int)var8.y, var6);
      int var9 = this.getAttackRange(var6);
      Point2D.Float var10 = new Point2D.Float(var8.x - (float)var2.getX(), var8.y - (float)var2.getY());
      AncientDredgingStaffEvent var11 = new AncientDredgingStaffEvent(var2, var2.getX(), var2.getY(), new GameRandom((long)var5), GameMath.getAngle(var10), this.getAttackDamage(var6), this.getResilienceGain(var6), (float)var7, (float)this.getKnockback(var6, var2), (float)var9);
      var1.entityManager.addLevelEventHidden(var11);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsWithEntity(new PacketLevelEvent(var11), var11);
      }

      return var6;
   }
}
