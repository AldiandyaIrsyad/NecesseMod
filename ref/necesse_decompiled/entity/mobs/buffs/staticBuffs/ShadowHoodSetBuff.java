package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class ShadowHoodSetBuff extends Buff {
   public ShadowHoodSetBuff() {
      this.shouldSave = false;
      this.canCancel = false;
      this.isPassive = true;
   }

   public boolean shouldDrawDuration(ActiveBuff var1) {
      return false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.ALL_DAMAGE, 0.1F);
   }

   public void serverTick(ActiveBuff var1) {
      super.serverTick(var1);
      if (!var1.owner.buffManager.hasBuff(BuffRegistry.SetBonuses.SHADOWHOOD.getID())) {
         var1.remove();
      }

   }

   public void clientTick(ActiveBuff var1) {
      super.clientTick(var1);
      if (!var1.owner.buffManager.hasBuff(BuffRegistry.SetBonuses.SHADOWHOOD.getID())) {
         var1.remove();
      }

   }

   public void updateLocalDisplayName() {
      this.displayName = new StaticMessage(this.getStringID());
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      return new ListGameTooltips(BuffModifiers.ALL_DAMAGE.getTooltip(0.1F, 0.0F).toTooltip(false));
   }
}
