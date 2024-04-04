package necesse.entity.mobs.friendly.human;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class HappinessModifier {
   public static HappinessModifier noBedModifier = new HappinessModifier(-40, new LocalMessage("settlement", "nobed"));
   public static HappinessModifier bedOutsideModifier = new HappinessModifier(-30, new LocalMessage("settlement", "bedoutside"));
   public final int happiness;
   public final GameMessage description;

   public HappinessModifier(int var1, GameMessage var2) {
      this.happiness = var1;
      this.description = var2;
   }

   public HappinessModifier(PacketReader var1) {
      this.happiness = var1.getNextInt();
      this.description = GameMessage.fromPacket(var1);
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextInt(this.happiness);
      this.description.writePacket(var1);
   }
}
