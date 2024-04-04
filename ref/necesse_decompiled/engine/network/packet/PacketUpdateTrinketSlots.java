package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;

public class PacketUpdateTrinketSlots extends Packet {
   public final int slot;
   public final int trinketSlots;

   public PacketUpdateTrinketSlots(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.trinketSlots = var2.getNextByteUnsigned();
   }

   public PacketUpdateTrinketSlots(ServerClient var1) {
      this.slot = var1.slot;
      this.trinketSlots = var1.playerMob.getInv().trinkets.getSize();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(this.slot);
      var2.putNextByteUnsigned(this.trinketSlots);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         PlayerMob var3 = var2.getPlayer(this.slot);
         if (var3 != null && this.trinketSlots != var3.getInv().trinkets.getSize()) {
            var3.getInv().trinkets.changeSize(this.trinketSlots);
            var3.equipmentBuffManager.updateTrinketBuffs();
         }

         if (this.slot == var2.getSlot()) {
            var2.closeContainer(false);
            var2.initInventoryContainer();
            if (GlobalData.getCurrentState() instanceof MainGame) {
               ((MainGame)GlobalData.getCurrentState()).formManager.updateInventoryForm();
            }
         }

      }
   }
}
