package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementOpenSettlementListEvent extends ContainerEvent {
   public final int mobUniqueID;
   public final ArrayList<SettlementOption> options = new ArrayList();

   public SettlementOpenSettlementListEvent(SettlementLevelData var1, ServerClient var2, int var3) {
      this.mobUniqueID = var3;
      Iterator var4 = var2.getServer().levelCache.getSettlements().iterator();

      while(var4.hasNext()) {
         SettlementCache var5 = (SettlementCache)var4.next();
         if (SettlementContainer.hasAccess(var1.getLevel().settlementLayer, var5, var2) && var5.name != null) {
            this.options.add(new SettlementOption(var5.islandX, var5.islandY, var5.name));
         }
      }

   }

   public SettlementOpenSettlementListEvent(PacketReader var1) {
      super(var1);
      this.mobUniqueID = var1.getNextInt();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextInt();
         int var5 = var1.getNextInt();
         GameMessage var6 = GameMessage.fromContentPacket(var1.getNextContentPacket());
         this.options.add(new SettlementOption(var4, var5, var6));
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.mobUniqueID);
      var1.putNextShortUnsigned(this.options.size());
      Iterator var2 = this.options.iterator();

      while(var2.hasNext()) {
         SettlementOption var3 = (SettlementOption)var2.next();
         var1.putNextInt(var3.islandX);
         var1.putNextInt(var3.islandY);
         var1.putNextContentPacket(var3.name.getContentPacket());
      }

   }

   public static class SettlementOption {
      public final int islandX;
      public final int islandY;
      public final GameMessage name;

      public SettlementOption(int var1, int var2, GameMessage var3) {
         this.islandX = var1;
         this.islandY = var2;
         this.name = var3;
      }
   }
}
