package necesse.level.gameLogicGate.entities;

import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.TilePosition;

public class OrLogicGateEntity extends SimpleLogicGateEntity {
   public OrLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      super(var1, var2);
   }

   public boolean condition() {
      for(int var1 = 0; var1 < 4; ++var1) {
         if (this.wireInputs[var1] && this.isWireActive(var1)) {
            return true;
         }
      }

      return false;
   }
}
