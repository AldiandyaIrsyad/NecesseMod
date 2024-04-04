package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;

public abstract class VicinityBuff extends Buff {
   public VicinityBuff() {
   }

   public boolean canCancel(ActiveBuff var1) {
      return false;
   }

   public boolean shouldDrawDuration(ActiveBuff var1) {
      return false;
   }

   public boolean shouldNetworkSync() {
      return false;
   }

   public boolean shouldSave() {
      return false;
   }
}
