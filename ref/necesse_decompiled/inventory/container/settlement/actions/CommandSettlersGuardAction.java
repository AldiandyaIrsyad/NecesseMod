package necesse.inventory.container.settlement.actions;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public class CommandSettlersGuardAction extends CommandSettlersCustomAction {
   public CommandSettlersGuardAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(Collection<Integer> var1, int var2, int var3) {
      PacketWriter var4 = this.setupPacket(var1);
      var4.putNextBoolean(false);
      var4.putNextInt(var2);
      var4.putNextInt(var3);
      this.runAndSendAction(var4.getPacket());
   }

   public void runAndSend(Collection<Integer> var1, ArrayList<Point> var2) {
      PacketWriter var3 = this.setupPacket(var1);
      var3.putNextBoolean(true);
      var3.putNextShortUnsigned(var2.size());
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Point var5 = (Point)var4.next();
         var3.putNextInt(var5.x);
         var3.putNextInt(var5.y);
      }

      this.runAndSendAction(var3.getPacket());
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3, ArrayList<CommandMob> var4) {
      int var5;
      CommandMob var16;
      if (var1.getNextBoolean()) {
         var5 = var1.getNextShortUnsigned();
         if (var5 <= 0) {
            return;
         }

         ArrayList var6 = new ArrayList(var5);

         int var7;
         for(var7 = 0; var7 < var5; ++var7) {
            int var8 = var1.getNextInt();
            int var9 = var1.getNextInt();
            var6.add(new Point(var8, var9));
         }

         if (var5 < var4.size()) {
            double var15 = (double)var4.size() / (double)var5;
            double var18 = 0.0;

            for(Iterator var11 = var4.iterator(); var11.hasNext(); var18 += var15) {
               CommandMob var12 = (CommandMob)var11.next();
               Point var13 = (Point)var6.get(Math.min((int)var18, var6.size() - 1));
               var12.commandGuard(var3, var13.x, var13.y);
            }
         } else {
            for(var7 = 0; var7 < var4.size(); ++var7) {
               var16 = (CommandMob)var4.get(var7);
               Point var19 = (Point)var6.get(var7);
               var16.commandGuard(var3, var19.x, var19.y);
            }
         }
      } else {
         var5 = var1.getNextInt();
         int var14 = var1.getNextInt();
         Iterator var17 = var4.iterator();

         while(var17.hasNext()) {
            var16 = (CommandMob)var17.next();
            var16.commandGuard(var3, var5, var14);
         }
      }

   }
}
