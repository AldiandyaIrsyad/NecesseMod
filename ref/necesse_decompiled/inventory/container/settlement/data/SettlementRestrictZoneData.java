package necesse.inventory.container.settlement.data;

import java.awt.Rectangle;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.Zoning;
import necesse.engine.util.ZoningChange;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.RestrictZone;

public class SettlementRestrictZoneData {
   public final int uniqueID;
   public int index;
   public int colorHue;
   public GameMessage name;
   private ZoningChange fullZonePacket;
   private Zoning zoning;

   public SettlementRestrictZoneData(RestrictZone var1) {
      this.uniqueID = var1.uniqueID;
      this.index = var1.index;
      this.colorHue = var1.colorHue;
      this.name = var1.name;
      this.fullZonePacket = var1.getFullChange();
   }

   public SettlementRestrictZoneData(PacketReader var1) {
      this.uniqueID = var1.getNextInt();
      this.index = var1.getNextShortUnsigned();
      this.colorHue = var1.getNextMaxValue(360);
      this.name = GameMessage.fromPacket(var1);
      this.fullZonePacket = ZoningChange.fromPacket(var1);
   }

   public void writeContentPacket(PacketWriter var1) {
      var1.putNextInt(this.uniqueID);
      var1.putNextShortUnsigned(this.index);
      var1.putNextMaxValue(this.colorHue, 360);
      this.name.writePacket(var1);
      this.fullZonePacket.write(var1);
   }

   public Zoning getZoning(Level var1) {
      if (this.zoning == null) {
         this.zoning = new Zoning(new Rectangle(var1.width, var1.height));
         this.fullZonePacket.applyTo(this.zoning);
      }

      return this.zoning;
   }
}
