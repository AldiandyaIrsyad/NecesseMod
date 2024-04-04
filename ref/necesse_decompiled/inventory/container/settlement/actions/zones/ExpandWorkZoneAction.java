package necesse.inventory.container.settlement.actions.zones;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkZoneConfigEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class ExpandWorkZoneAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public ExpandWorkZoneAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, Rectangle var2, Point var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextInt(var1);
      var5.putNextShortUnsigned(var2.x);
      var5.putNextShortUnsigned(var2.y);
      var5.putNextShortUnsigned(var2.width);
      var5.putNextShortUnsigned(var2.height);
      var5.putNextBoolean(var3 != null);
      if (var3 != null) {
         var5.putNextShortUnsigned(var3.x);
         var5.putNextShortUnsigned(var3.y);
      }

      this.runAndSendAction(var4);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextShortUnsigned();
      int var4 = var1.getNextShortUnsigned();
      int var5 = var1.getNextShortUnsigned();
      int var6 = var1.getNextShortUnsigned();
      Rectangle var7 = new Rectangle(var3, var4, var5, var6);
      Point var8 = null;
      if (var1.getNextBoolean()) {
         int var9 = var1.getNextShortUnsigned();
         int var10 = var1.getNextShortUnsigned();
         var8 = new Point(var9, var10);
      }

      if (this.container.client.isServer()) {
         SettlementLevelData var13 = this.container.getLevelData();
         if (var13 != null) {
            ServerClient var12 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var12)) {
               (new SettlementBasicsEvent(var13)).applyAndSendToClient(var12);
               return;
            }

            SettlementWorkZone var11 = var13.getWorkZones().expandZone(var2, var7, var8, var12);
            if (var11 != null) {
               (new SettlementOpenWorkZoneConfigEvent(var11)).applyAndSendToClient(var12.getServerClient());
            }
         }
      }

   }
}
