package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.level.maps.Level;

public class CoinPileObjectEntity extends ObjectEntity {
   public int coinAmount;

   public CoinPileObjectEntity(Level var1, int var2, int var3) {
      super(var1, "coinpile", var2, var3);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("coinAmount", this.coinAmount);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.coinAmount = var1.getInt("coinAmount", 1);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      var1.putNextInt(this.coinAmount);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.coinAmount = var1.getNextInt();
   }
}
