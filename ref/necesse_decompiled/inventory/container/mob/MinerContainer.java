package necesse.inventory.container.mob;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.friendly.human.humanShop.MinerHumanMob;

public class MinerContainer extends ShopContainer {
   public MinerContainer(NetworkClient var1, int var2, MinerHumanMob var3, PacketReader var4) {
      super(var1, var2, var3, var4.getNextContentPacket());
   }

   public static Packet getMinerContainerContent(Packet var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      var2.putNextContentPacket(var0);
      return var1;
   }
}
