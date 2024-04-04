package necesse.entity.mobs.buffs;

import necesse.engine.network.Packet;
import necesse.entity.mobs.PlayerMob;

public class ActiveBuffAbilityContainer {
   public final int uniqueID;
   public final boolean isRunningClient;
   public final ActiveBuffAbility buffAbility;
   public final ActiveBuff activeBuff;

   public ActiveBuffAbilityContainer(int var1, boolean var2, ActiveBuffAbility var3, ActiveBuff var4) {
      this.uniqueID = var1;
      this.isRunningClient = var2;
      this.buffAbility = var3;
      this.activeBuff = var4;
   }

   public boolean tick(PlayerMob var1) {
      return this.activeBuff.isRemoved() ? false : this.buffAbility.tickActiveAbility(var1, this.activeBuff, this.isRunningClient);
   }

   public void update(PlayerMob var1, Packet var2) {
      this.buffAbility.onActiveAbilityUpdate(var1, this.activeBuff, var2);
   }

   public void onStopped(PlayerMob var1) {
      this.buffAbility.onActiveAbilityStopped(var1, this.activeBuff);
   }
}
