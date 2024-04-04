package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

public class IvyCircletSetBonusBuff extends SetBonusBuff {
   public float secondsPerSlime = 1.5F;
   public FloatUpgradeValue slimeDamage = (new FloatUpgradeValue(0.0F, 0.2F)).setBaseValue(20.0F).setUpgradedValue(1.0F, 45.0F);
   public IntUpgradeValue maxSummons = (new IntUpgradeValue()).setBaseValue(1).setUpgradedValue(1.0F, 2);

   public IvyCircletSetBonusBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MAX_SUMMONS, this.maxSummons.getValue(this.getUpgradeTier(var1)));
   }

   public void serverTick(ActiveBuff var1) {
      super.serverTick(var1);
      if (var1.owner.isPlayer && ((PlayerMob)var1.owner).isServerClient() && var1.owner.isInCombat()) {
         float var2 = 1.0F / this.secondsPerSlime / 20.0F;
         if (GameRandom.globalRandom.getChance(var2)) {
            ServerClient var3 = ((PlayerMob)var1.owner).getServerClient();
            Level var4 = var1.owner.getLevel();
            AttackingFollowingMob var5 = (AttackingFollowingMob)MobRegistry.getMob("playerpoisonslime", var4);
            var3.addFollower("playerivypoisonslime", var5, FollowPosition.WALK_CLOSE, "summonedmob", 1.0F, (var0) -> {
               return 100;
            }, (BiConsumer)null, false);
            Point2D.Float var6 = SummonToolItem.findSpawnLocation(var5, var4, var3.playerMob.x, var3.playerMob.y);
            var5.updateDamage(new GameDamage(DamageTypeRegistry.SUMMON, this.slimeDamage.getValue(this.getUpgradeTier(var1))));
            var5.getLevel().entityManager.addMob(var5, var6.x, var6.y);
         }
      }

   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("itemtooltip", "ivycircletset"));
      return var3;
   }

   public void addStatTooltips(LinkedList<ItemStatTip> var1, ActiveBuff var2, ActiveBuff var3) {
      super.addStatTooltips(var1, var2, var3);
      var2.getModifierTooltipsBuilder(true, true).addLastValues(var3).buildToStatList(var1);
   }
}
