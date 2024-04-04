package necesse.entity.mobs.hostile.bosses;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.WormMoveLineSpawnData;

public class PestWardenMoveLine extends WormMoveLine {
   public boolean isHardened;

   public PestWardenMoveLine(Point2D var1, Point2D var2, boolean var3, float var4, boolean var5, boolean var6) {
      super(var1, var2, var3, var4, var5);
      this.isHardened = var6;
   }

   public PestWardenMoveLine(PacketReader var1, WormMoveLineSpawnData var2) {
      super(var1, var2);
      this.isHardened = var1.getNextBoolean();
   }

   public void writeSpawnPacket(PacketWriter var1, float var2, float var3, float var4) {
      super.writeSpawnPacket(var1, var2, var3, var4);
      var1.putNextBoolean(this.isHardened);
   }
}
