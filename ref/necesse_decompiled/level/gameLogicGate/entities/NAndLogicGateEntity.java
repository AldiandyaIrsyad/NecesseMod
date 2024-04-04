package necesse.level.gameLogicGate.entities;

import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.TilePosition;

public class NAndLogicGateEntity extends AndLogicGateEntity {
   public NAndLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      super(var1, var2);
   }

   public boolean condition() {
      return !super.condition();
   }
}
