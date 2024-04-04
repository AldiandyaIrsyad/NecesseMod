package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerBasicData extends SettlementSettlerData {
   public final boolean canMoveOut;
   public final boolean canBanish;

   public SettlementSettlerBasicData(LevelSettler var1) {
      super(var1);
      this.canMoveOut = var1.canMoveOut();
      this.canBanish = var1.canBanish();
   }

   public SettlementSettlerBasicData(PacketReader var1) {
      super(var1);
      this.canMoveOut = var1.getNextBoolean();
      this.canBanish = var1.getNextBoolean();
   }

   public void writeContentPacket(PacketWriter var1) {
      super.writeContentPacket(var1);
      var1.putNextBoolean(this.canMoveOut);
      var1.putNextBoolean(this.canBanish);
   }
}
