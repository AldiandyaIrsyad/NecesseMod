package necesse.entity.mobs;

import java.awt.Point;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;

public interface RaiderMob {
   void makeRaider(SettlementRaidLevelEvent var1, Point var2, Point var3, long var4, int var6, float var7);

   long getRaidingStartTicks();

   void updateRaidingStartTicks(long var1);

   void setRaidEvent(SettlementRaidLevelEvent var1);

   void raidOver();

   default boolean canRaiderSpawnAt(int var1, int var2) {
      return true;
   }
}
