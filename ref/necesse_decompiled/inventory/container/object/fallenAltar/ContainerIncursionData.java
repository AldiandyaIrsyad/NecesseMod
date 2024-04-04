package necesse.inventory.container.object.fallenAltar;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.level.maps.incursion.IncursionData;

public class ContainerIncursionData {
   public final IncursionData data;
   public final GameMessage unavailableError;

   public ContainerIncursionData(IncursionData var1, GameMessage var2) {
      this.data = var1;
      this.unavailableError = var2;
   }

   public ContainerIncursionData(PacketReader var1) {
      this.data = IncursionData.fromPacket(var1);
      if (var1.getNextBoolean()) {
         this.unavailableError = GameMessage.fromPacket(var1);
      } else {
         this.unavailableError = null;
      }

   }

   public void writePacket(PacketWriter var1) {
      IncursionData.writePacket(this.data, var1);
      if (this.unavailableError != null) {
         var1.putNextBoolean(true);
         this.unavailableError.writePacket(var1);
      } else {
         var1.putNextBoolean(false);
      }

   }
}
