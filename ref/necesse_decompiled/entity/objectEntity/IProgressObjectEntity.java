package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public interface IProgressObjectEntity {
   void setupProgressPacket(PacketWriter var1);

   void applyProgressPacket(PacketReader var1);
}
