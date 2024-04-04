package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.DuskMoonDiscFollowingMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.DuskVolleyProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.Level;

public class DuskSetBonusBuff extends SetBonusBuff {
   public FloatUpgradeValue moonDiscDamage = (new FloatUpgradeValue(0.0F, 0.2F)).setBaseValue(60.0F).setUpgradedValue(1.0F, 60.0F);
   public FloatUpgradeValue duskVolleyDamage = (new FloatUpgradeValue(0.0F, 0.2F)).setBaseValue(25.0F).setUpgradedValue(1.0F, 25.0F);

   public DuskSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MAX_MANA_FLAT, 400);
   }

   public void clientTick(ActiveBuff var1) {
      Mob var2 = var1.owner;
      Level var3 = var2.getLevel();
      if (var3 != null && var2.isVisible() && var3.getWorldEntity().isNight()) {
         var3.entityManager.addParticle(var2.x + (float)(GameRandom.globalRandom.nextGaussian() * 10.0), var2.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sizeFades(5, 10).lifeTime(1500).movesConstantAngle((float)GameRandom.globalRandom.getIntBetween(0, 360), 3.0F).givesLight().height(16.0F);
      }

   }

   public void onItemAttacked(ActiveBuff var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8) {
      super.onItemAttacked(var1, var2, var3, var4, var5, var6, var7, var8);
      Level var9 = var1.owner.getLevel();
      if (var9.getWorldEntity().isNight() && var9.isServer() && var6.item instanceof ToolItem) {
         ToolItem var10 = (ToolItem)var6.item;
         if (var10.getDamageType(var6) == DamageTypeRegistry.MAGIC) {
            this.fireDuskVolley(var1, var4, var9, var2, var3);
         } else if (var10.getDamageType(var6) == DamageTypeRegistry.SUMMON) {
            this.summonCrescentMoon(var1, var4, var9, var2, var3);
         }
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add((String)Localization.translate("itemtooltip", "duskset"), 400);
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }

   private void fireDuskVolley(ActiveBuff var1, PlayerMob var2, Level var3, int var4, int var5) {
      String var6 = "duskvolley";
      long var7 = var1.getGndData().getLong(var6);
      float var9 = (Float)var2.buffManager.getModifier(BuffModifiers.ATTACK_SPEED);
      var9 += (Float)var2.buffManager.getModifier(BuffModifiers.MAGIC_ATTACK_SPEED);
      var9 = (Float)BuffModifiers.ATTACK_SPEED.finalLimit(var9);
      int var10 = Math.round(500.0F * (1.0F / var9));
      if (var7 + (long)var10 < var3.getWorldEntity().getTime()) {
         var1.getGndData().setLong(var6, var3.getWorldEntity().getTime());
         GameRandom var11 = GameRandom.globalRandom;
         float var12 = 250.0F * (Float)var2.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY);
         GameDamage var13 = new GameDamage(this.duskVolleyDamage.getValue(this.getUpgradeTier(var1)) * GameDamage.getDamageModifier(var2, DamageTypeRegistry.MAGIC));

         for(int var14 = 0; var14 < 5; ++var14) {
            DuskVolleyProjectile var15 = new DuskVolleyProjectile(var3, var2, var2.x, var2.y, (float)var11.getIntBetween(0, 360), var12, 1500, var13, 0);
            var3.entityManager.projectiles.add(var15);
         }
      }

   }

   private void summonCrescentMoon(ActiveBuff var1, PlayerMob var2, Level var3, int var4, int var5) {
      String var6 = "summonmoon";
      long var7 = var1.getGndData().getLong(var6);
      float var9 = (Float)var2.buffManager.getModifier(BuffModifiers.ATTACK_SPEED);
      var9 += (Float)var2.buffManager.getModifier(BuffModifiers.SUMMON_ATTACK_SPEED);
      var9 = (Float)BuffModifiers.ATTACK_SPEED.finalLimit(var9);
      int var10 = Math.round(500.0F * (1.0F / var9));
      if (var7 + (long)var10 < var3.getWorldEntity().getTime()) {
         var1.getGndData().setLong(var6, var3.getWorldEntity().getTime());
         GameDamage var11 = new GameDamage(this.moonDiscDamage.getValue(this.getUpgradeTier(var1)) * GameDamage.getDamageModifier(var2, DamageTypeRegistry.SUMMON));
         DuskMoonDiscFollowingMob var12 = new DuskMoonDiscFollowingMob();
         var2.getServerClient().addFollower("duskmoondisc", var12, FollowPosition.FLYING_CIRCLE, "summonedmob", 1.0F, 10, (BiConsumer)null, false);
         Point2D.Float var13 = findSpawnLocation(var12, var2.getLevel(), var2.x, var2.y);
         var12.updateDamage(var11);
         var2.getLevel().entityManager.addMob(var12, var13.x, var13.y);
      }

   }

   public static Point2D.Float findSpawnLocation(Mob var0, Level var1, float var2, float var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            if (var5 != 0 || var6 != 0) {
               float var7 = var2 + (float)(var5 * 32);
               float var8 = var3 + (float)(var6 * 32);
               if (!var0.collidesWith(var1, (int)var7, (int)var8)) {
                  var4.add(new Point2D.Float(var7, var8));
               }
            }
         }
      }

      if (var4.size() > 0) {
         return (Point2D.Float)var4.get(GameRandom.globalRandom.nextInt(var4.size()));
      } else {
         return new Point2D.Float(var2, var3);
      }
   }
}
