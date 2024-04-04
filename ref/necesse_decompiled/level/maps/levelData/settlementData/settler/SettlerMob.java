package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public interface SettlerMob {
   void setHome(Point var1);

   void assignBed(LevelSettler var1, SettlementBed var2, boolean var3);

   boolean isMovingIn();

   void moveOut();

   boolean isMovingOut();

   void tickSettler(SettlementLevelData var1, LevelSettler var2);

   void makeSettler(SettlementLevelData var1, LevelSettler var2);

   boolean isSettler();

   LevelIdentifier getSettlementLevelIdentifier();

   default boolean isSettlerOnCurrentLevel() {
      if (!this.isSettler()) {
         return false;
      } else {
         LevelIdentifier var1 = this.getSettlementLevelIdentifier();
         if (var1 == null) {
            return false;
         } else {
            Level var2 = this.getMob().getLevel();
            return var2 != null && var2.getIdentifier().equals(var1);
         }
      }
   }

   default Level getSettlementServerLevel() {
      if (!this.isSettler()) {
         return null;
      } else {
         Level var1 = this.getMob().getLevel();
         return var1 != null && var1.isServer() ? var1.getServer().world.getLevel(this.getSettlementLevelIdentifier()) : null;
      }
   }

   default SettlementLevelData getSettlementLevelData() {
      Level var1 = this.getSettlementServerLevel();
      return var1 == null ? null : SettlementLevelData.getSettlementData(var1);
   }

   void setSettlerSeed(int var1);

   int getSettlerSeed();

   void setSettlerName(String var1);

   String getSettlerName();

   String getSettlerStringID();

   default Settler getSettler() {
      String var1 = this.getSettlerStringID();
      return var1 == null ? null : SettlerRegistry.getSettler(var1);
   }

   boolean doesEatFood();

   int getSettlerHappiness();

   boolean hasCommandOrders();

   GameMessage getCurrentActivity();

   Mob getMob();

   default void runSettlerCheck() {
      if (this.isSettler() && !this.isMovingOut()) {
         SettlementLevelData var1 = this.getSettlementLevelData();
         if (var1 != null && !var1.isMobPartOf(this)) {
            this.moveOut();
         }

      }
   }
}
