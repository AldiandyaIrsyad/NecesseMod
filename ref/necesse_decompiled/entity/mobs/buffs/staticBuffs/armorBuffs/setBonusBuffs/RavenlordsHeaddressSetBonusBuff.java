package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RavenLordFeatherFollowingMob;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;

public class RavenlordsHeaddressSetBonusBuff extends RavenlordsSetBonusBuff {
   public FloatUpgradeValue featherDamage = (new FloatUpgradeValue(0.0F, 0.2F)).setBaseValue(50.0F).setUpgradedValue(1.0F, 50.0F);
   public static int FEATHER_SPAWN_RUN_DISTANCE = 480;

   public RavenlordsHeaddressSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      super.init(var1, var2);
      var1.getGndData().setDouble("distanceRan", var1.owner.getDistanceRan());
   }

   public void serverTick(ActiveBuff var1) {
      super.serverTick(var1);
      Mob var2 = var1.owner;
      if (var2.isPlayer) {
         double var3 = var2.getDistanceRan();
         double var5 = var1.getGndData().getDouble("distanceRan");
         if (var3 - var5 > (double)FEATHER_SPAWN_RUN_DISTANCE) {
            this.summonFeather(var1, (PlayerMob)var2);
            var1.getGndData().setDouble("distanceRan", var3);
         }
      }

   }

   private void summonFeather(ActiveBuff var1, PlayerMob var2) {
      if (var2.isServerClient()) {
         ServerClient var3 = var2.getServerClient();
         if (var3.getFollowerCount("ravenlordfeather") < 6.0F) {
            RavenLordFeatherFollowingMob var4 = new RavenLordFeatherFollowingMob();
            var3.addFollower("ravenlordfeather", var4, FollowPosition.FLYING_CIRCLE, "summonedmob", 1.0F, 6, (BiConsumer)null, false);
            var4.baseDamage = this.featherDamage.getValue(this.getUpgradeTier(var1));
            var2.getLevel().entityManager.addMob(var4, var2.x, var2.y);
         }
      }

   }

   public static float getFinalDamage(Mob var0, float var1) {
      return ((Float)var0.buffManager.getModifier(BuffModifiers.SPEED) - 1.0F) * var1;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
      DoubleItemStatTip var4 = new DoubleItemStatTip((double)this.featherDamage.getValue(this.getUpgradeTier(var2)), 0) {
         public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
            return (new GameMessageBuilder()).append((GameMessage)(new LocalMessage("itemtooltip", "ravenlordset"))).append("\n").append((GameMessage)(new LocalMessage("itemtooltip", "ravenlordsetdamage", "damage", this.getReplaceValue(var1, var2, var4))));
         }
      };
      if (var3 != null) {
         var4.setCompareValue((double)this.featherDamage.getValue(this.getUpgradeTier(var3)));
      }

      var1.add(var4);
   }
}
