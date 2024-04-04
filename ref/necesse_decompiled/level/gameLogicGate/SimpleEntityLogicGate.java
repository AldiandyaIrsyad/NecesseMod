package necesse.level.gameLogicGate;

import java.util.function.BiFunction;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class SimpleEntityLogicGate extends GameLogicGate {
   public BiFunction<GameLogicGate, TilePosition, LogicGateEntity> provider;

   public SimpleEntityLogicGate(BiFunction<GameLogicGate, TilePosition, LogicGateEntity> var1) {
      this.provider = var1;
   }

   public LogicGateEntity getNewEntity(Level var1, int var2, int var3) {
      return (LogicGateEntity)this.provider.apply(this, new TilePosition(var1, var2, var3));
   }
}
