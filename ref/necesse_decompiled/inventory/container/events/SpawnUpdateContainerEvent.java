package necesse.inventory.container.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.LevelObject;

public class SpawnUpdateContainerEvent extends ContainerEvent {
   public final int tileX;
   public final int tileY;
   public final boolean isCurrentSpawn;

   public SpawnUpdateContainerEvent(int var1, int var2, boolean var3) {
      this.tileX = var1;
      this.tileY = var2;
      this.isCurrentSpawn = var3;
   }

   public SpawnUpdateContainerEvent(ServerClient var1, LevelObject var2) {
      this.tileX = var2.tileX;
      this.tileY = var2.tileY;
      if (var2.level.isSamePlace(var1.getLevel())) {
         if (var2.object instanceof RespawnObject) {
            this.isCurrentSpawn = ((RespawnObject)var2.object).isCurrentSpawn(var2.level, this.tileX, this.tileY, var1);
         } else {
            this.isCurrentSpawn = false;
         }
      } else {
         this.isCurrentSpawn = false;
      }

   }

   public SpawnUpdateContainerEvent(PacketReader var1) {
      super(var1);
      this.tileX = var1.getNextInt();
      this.tileY = var1.getNextInt();
      this.isCurrentSpawn = var1.getNextBoolean();
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.tileX);
      var1.putNextInt(this.tileY);
      var1.putNextBoolean(this.isCurrentSpawn);
   }
}
