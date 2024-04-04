package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatbowAttackHandler;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GreatbowProjectileToolItem extends BowProjectileToolItem {
   public int attackSpriteStretch = 8;
   public Color particleColor = new Color(255, 219, 36);

   public GreatbowProjectileToolItem(int var1) {
      super(var1);
      this.knockback.setBaseValue(100);
      this.enchantCost.setUpgradedValue(1.0F, 2000);
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   protected void addExtraBowTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      super.addExtraBowTooltips(var1, var2, var3, var4);
      var1.add(Localization.translate("itemtooltip", "greatbowtip"));
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient() && var6.getGndData().getBoolean("shouldFire")) {
         Screen.playSound(GameResources.bow, SoundEffect.effect(var4));
      }

   }

   public Projectile getProjectile(Level var1, int var2, int var3, Mob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, PacketReader var9) {
      float var10 = var5.getGndData().getFloat("chargePercent");
      var10 = GameMath.limit(var10, 0.0F, 1.0F);
      float var11;
      float var12;
      float var13;
      float var14;
      float var15;
      if (var10 >= 1.0F) {
         var11 = 1.0F;
         var12 = 1.0F;
         var13 = 1.0F;
         var14 = 1.0F;
         var15 = 1.0F;
      } else {
         var11 = GameMath.lerp(var10, 0.1F, 0.4F);
         var12 = GameMath.lerp(var10, 0.05F, 0.4F);
         var13 = GameMath.lerp(var10, 0.05F, 0.4F);
         var14 = GameMath.lerp(var10, 0.05F, 0.2F);
         var15 = GameMath.lerp(var10, 0.0F, 0.5F);
      }

      GameDamage var16 = var7.modDamage(this.getAttackDamage(var5)).modDamage(var13);
      float var17 = var7.modVelocity((float)this.getProjectileVelocity(var5, var4)) * var11;
      float var18 = (float)var7.modRange(this.getAttackRange(var5)) * var12;
      float var19 = (float)var7.modKnockback(this.getKnockback(var5, var4)) * var14;
      float var20 = this.getResilienceGain(var5) * var15;
      return this.getProjectile(var1, var2, var3, var4, var5, var6, var7, var8, var17, (int)var18, var16, (int)var19, var20, var9);
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      int var11 = this.getAttackAnimTime(var6, var4);
      var4.startAttackHandler(new GreatbowAttackHandler(var4, var7, var6, this, var11, this.particleColor, var9));
      return var6;
   }

   public InventoryItem superOnAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      return super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean shouldRunOnAttackedBuffEvent(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PlayerInventorySlot var6, int var7, int var8, PacketReader var9) {
      return false;
   }

   public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions var1, InventoryItem var2, PlayerMob var3, int var4, float var5, float var6, float var7, Color var8, GameLight var9) {
      float var10 = var2.getGndData().getFloat("chargePercent");
      if (var10 > 0.0F) {
         var10 = Math.min(var10, 1.0F);
         GameSprite var11 = this.getAttackSprite(var2, var3);
         int var12 = (int)(var10 * (float)this.attackSpriteStretch);
         var11 = new GameSprite(var11, var11.width + var12, var11.height);
         var1.armPosOffset(-var12 + this.attackSpriteStretch / 2, 0);
         ItemAttackDrawOptions.AttackItemSprite var13 = var1.itemSprite(var11);
         var13.itemRotatePoint(this.attackXOffset + var12 - this.attackSpriteStretch / 2, this.attackYOffset);
         if (var8 != null) {
            var13.itemColor(var8);
         }

         return var13.itemEnd();
      } else {
         return super.setupItemSpriteAttackDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }
}
