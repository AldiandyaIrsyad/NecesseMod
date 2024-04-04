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

public class CreateNewWorkZoneAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public CreateNewWorkZoneAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, int var2, Rectangle var3, Point var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextInt(var1);
      var6.putNextInt(var2);
      var6.putNextShortUnsigned(var3.x);
      var6.putNextShortUnsigned(var3.y);
      var6.putNextShortUnsigned(var3.width);
      var6.putNextShortUnsigned(var3.height);
      var6.putNextBoolean(var4 != null);
      if (var4 != null) {
         var6.putNextShortUnsigned(var4.x);
         var6.putNextShortUnsigned(var4.y);
      }

      this.runAndSendAction(var5);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextInt();
      int var4 = var1.getNextShortUnsigned();
      int var5 = var1.getNextShortUnsigned();
      int var6 = var1.getNextShortUnsigned();
      int var7 = var1.getNextShortUnsigned();
      Rectangle var8 = new Rectangle(var4, var5, var6, var7);
      Point var9 = null;
      if (var1.getNextBoolean()) {
         int var10 = var1.getNextShortUnsigned();
         int var11 = var1.getNextShortUnsigned();
         var9 = new Point(var10, var11);
      }

      if (this.container.client.isServer()) {
         SettlementLevelData var13 = this.container.getLevelData();
         if (var13 != null) {
            ServerClient var14 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var14)) {
               (new SettlementBasicsEvent(var13)).applyAndSendToClient(var14);
               return;
            }

            SettlementWorkZone var12 = var13.getWorkZones().createZone(var2, var3, var8, var9, var14);
            if (var12 != null) {
               (new SettlementOpenWorkZoneConfigEvent(var12)).applyAndSendToClient(var14.getServerClient());
            }
         }
      }

   }
}
