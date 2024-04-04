package necesse.inventory.container.travel;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.inventory.container.events.ContainerEvent;

public class IslandsResponseEvent extends ContainerEvent {
   public final int startX;
   public final int startY;
   public final int width;
   public final int height;
   public final IslandData[][] islands;

   public IslandsResponseEvent(Server var1, ServerClient var2, TravelContainer var3, int var4, int var5, int var6, int var7) {
      var6 = Math.max(1, var6);
      var7 = Math.max(1, var7);
      if (var6 * var7 > 361) {
         var6 = Math.min(13, var6);
         var7 = Math.min(13, var7);
      }

      this.startX = var4;
      this.startY = var5;
      this.width = var6;
      this.height = var7;
      this.islands = new IslandData[var6][var7];

      for(int var8 = 0; var8 < var7; ++var8) {
         int var9 = var8 + var5;

         for(int var10 = 0; var10 < var6; ++var10) {
            int var11 = var10 + var4;
            this.islands[var10][var8] = IslandData.generateIslandData(var1, var2, var3, var11, var9);
         }
      }

   }

   public IslandsResponseEvent(PacketReader var1) {
      super(var1);
      this.startX = var1.getNextInt();
      this.startY = var1.getNextInt();
      this.width = var1.getNextInt();
      this.height = var1.getNextInt();
      this.islands = new IslandData[this.width][this.height];

      for(int var2 = 0; var2 < this.height; ++var2) {
         int var3 = var2 + this.startY;

         for(int var4 = 0; var4 < this.width; ++var4) {
            int var5 = var4 + this.startX;
            boolean var6 = var1.getNextBoolean();
            boolean var7 = var1.getNextBoolean();
            boolean var8 = var1.getNextBoolean();
            boolean var9 = var1.getNextBoolean();
            boolean var10 = var1.getNextBoolean();
            boolean var11 = var1.getNextBoolean();
            int var12 = BiomeRegistry.UNKNOWN.getID();
            GameMessage var13 = null;
            if (var11) {
               var12 = var1.getNextShortUnsigned();
               if (var1.getNextBoolean()) {
                  var13 = GameMessage.fromContentPacket(var1.getNextContentPacket());
               }
            }

            this.islands[var4][var2] = new IslandData(var5, var3, var12, var6, var7, var8, var10, var9, var13);
         }
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.startX);
      var1.putNextInt(this.startY);
      var1.putNextInt(this.width);
      var1.putNextInt(this.height);

      for(int var2 = 0; var2 < this.height; ++var2) {
         for(int var3 = 0; var3 < this.width; ++var3) {
            IslandData var4 = this.islands[var3][var2];
            var1.putNextBoolean(var4.isOutsideWorldBorder);
            var1.putNextBoolean(var4.discovered);
            var1.putNextBoolean(var4.visited);
            var1.putNextBoolean(var4.canTravel);
            var1.putNextBoolean(var4.hasDeath);
            if (var4.biome != BiomeRegistry.UNKNOWN.getID()) {
               var1.putNextBoolean(true);
               var1.putNextShortUnsigned(var4.biome);
               var1.putNextBoolean(var4.settlementName != null);
               if (var4.settlementName != null) {
                  var1.putNextContentPacket(var4.settlementName.getContentPacket());
               }
            } else {
               var1.putNextBoolean(false);
            }
         }
      }

   }
}
