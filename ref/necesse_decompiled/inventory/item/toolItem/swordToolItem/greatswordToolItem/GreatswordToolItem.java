package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.level.maps.Level;

public class GreatswordToolItem extends SwordToolItem {
   public GreatswordChargeLevel[] chargeLevels;

   public GreatswordToolItem(int var1, GreatswordChargeLevel... var2) {
      super(var1);
      this.chargeLevels = var2;
      if (var2.length == 0) {
         throw new IllegalArgumentException("Must have at least one charge level for greatswords");
      } else {
         this.attackAnimTime.setBaseValue(200);
         this.attackXOffset = 16;
         this.attackYOffset = 16;
         this.resilienceGain.setBaseValue(4.0F);
         this.enchantCost.setUpgradedValue(1.0F, 2200);
      }
   }

   public static GreatswordChargeLevel[] getThreeChargeLevels(int var0, int var1, int var2) {
      return new GreatswordChargeLevel[]{new GreatswordChargeLevel(var0, 1.0F, new Color(200, 200, 200)), new GreatswordChargeLevel(var1, 1.5F, new Color(200, 200, 100)), new GreatswordChargeLevel(var2, 2.0F, new Color(200, 100, 100))};
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   public boolean animDrawBehindHand() {
      return true;
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "greatswordtip"));
      return var4;
   }

   public float getSwingRotation(InventoryItem var1, int var2, float var3) {
      if (!var1.getGndData().getBoolean("shouldFire")) {
         float var4 = var1.getGndData().getFloat("chargePercent");
         var4 = GameMath.limit(var4, 0.0F, 1.0F);
         var3 = 0.2F - var4 * 0.2F;
      }

      return super.getSwingRotation(var1, var2, var3);
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var6.getGndData().getBoolean("shouldFire")) {
         super.showAttack(var1, var2, var3, var4, var5, var6, var7, var8);
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      var4.startAttackHandler(new GreatswordAttackHandler(var4, var7, var6, this, var9, var2, var3, this.chargeLevels));
      return var6;
   }

   public InventoryItem superOnAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      return super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean shouldRunOnAttackedBuffEvent(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PlayerInventorySlot var6, int var7, int var8, PacketReader var9) {
      return false;
   }

   public GameDamage getAttackDamage(InventoryItem var1) {
      float var2 = var1.getGndData().getFloat("chargeDamageMultiplier", 1.0F);
      return super.getAttackDamage(var1).modDamage(var2);
   }
}
