package necesse.level.gameLogicGate.entities;

import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.TilePosition;

public class XOrLogicGateEntity extends SimpleLogicGateEntity {
   public XOrLogicGateEntity(GameLogicGate var1, TilePosition var2) {
      super(var1, var2);
   }

   public boolean condition() {
      int var1 = 0;

      for(int var2 = 0; var2 < 4; ++var2) {
         if (this.wireInputs[var2] && this.isWireActive(var2)) {
            ++var1;
         }
      }

      return var1 == 1;
   }
}
