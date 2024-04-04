package necesse.inventory.container.settlement.actions.zones;

import java.awt.Rectangle;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ShrinkWorkZoneAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public ShrinkWorkZoneAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, Rectangle var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextShortUnsigned(var2.x);
      var4.putNextShortUnsigned(var2.y);
      var4.putNextShortUnsigned(var2.width);
      var4.putNextShortUnsigned(var2.height);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextShortUnsigned();
      int var4 = var1.getNextShortUnsigned();
      int var5 = var1.getNextShortUnsigned();
      int var6 = var1.getNextShortUnsigned();
      Rectangle var7 = new Rectangle(var3, var4, var5, var6);
      if (this.container.client.isServer()) {
         SettlementLevelData var8 = this.container.getLevelData();
         if (var8 != null) {
            ServerClient var9 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var9)) {
               (new SettlementBasicsEvent(var8)).applyAndSendToClient(var9);
               return;
            }

            var8.getWorkZones().shrinkZone(var2, var7, var9);
         }
      }

   }
}
