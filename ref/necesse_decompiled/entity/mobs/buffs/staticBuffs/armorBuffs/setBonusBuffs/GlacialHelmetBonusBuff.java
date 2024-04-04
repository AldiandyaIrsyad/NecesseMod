package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.IcicleStaffProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.ThrowToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

public class GlacialHelmetBonusBuff extends SetBonusBuff {
   public IntUpgradeValue maxResilience = (new IntUpgradeValue()).setBaseValue(20).setUpgradedValue(1.0F, 30);
   public FloatUpgradeValue resilienceGain = (new FloatUpgradeValue()).setBaseValue(0.2F).setUpgradedValue(1.0F, 0.2F);
   public FloatUpgradeValue icicleDamage = (new FloatUpgradeValue(0.0F, 0.2F)).setBaseValue(25.0F).setUpgradedValue(1.0F, 30.0F);

   public GlacialHelmetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MAX_RESILIENCE_FLAT, this.maxResilience.getValue(this.getUpgradeTier(var1)));
      var1.setModifier(BuffModifiers.RESILIENCE_GAIN, this.resilienceGain.getValue(this.getUpgradeTier(var1)));
   }

   public void onItemAttacked(ActiveBuff var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8) {
      super.onItemAttacked(var1, var2, var3, var4, var5, var6, var7, var8);
      Level var9 = var1.owner.getLevel();
      if (var9.isServer() && var6.item instanceof ToolItem && !(var6.item instanceof ThrowToolItem)) {
         ToolItem var10 = (ToolItem)var6.item;
         if (var10.getDamageType(var6) == DamageTypeRegistry.MELEE) {
            String var11 = "icicleshottime";
            long var12 = var1.getGndData().getLong(var11);
            float var14 = DamageTypeRegistry.MELEE.calculateTotalAttackSpeedModifier(var4);
            int var15 = Math.round(750.0F * (1.0F / var14));
            if (var12 + (long)var15 < var9.getWorldEntity().getTime()) {
               var1.getGndData().setLong(var11, var9.getWorldEntity().getTime());
               GameRandom var16 = GameRandom.globalRandom;
               Point2D.Float var17 = GameMath.normalize(var4.x - (float)var2, var4.y - (float)var3);
               int var18 = var16.getIntBetween(30, 50);
               Point2D.Float var19 = new Point2D.Float(var17.x * (float)var18, var17.y * (float)var18);
               var19 = GameMath.getPerpendicularPoint(var19, (float)var16.getIntBetween(-50, 50), var17);
               float var20 = 125.0F * (Float)var4.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY);
               GameDamage var21 = new GameDamage(this.icicleDamage.getValue(this.getUpgradeTier(var1)));
               IcicleStaffProjectile var22 = new IcicleStaffProjectile(var9, var4, var4.x + var19.x, var4.y + var19.y, (float)var2, (float)var3, var20, 500, var21, 0);
               var9.entityManager.projectiles.add(var22);
            }
         }
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "glacialhelmetset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
