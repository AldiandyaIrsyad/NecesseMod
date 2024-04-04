package necesse.inventory.container.customAction;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;

public class CommandPartyGuardAction extends CommandPartyCustomAction {
   public CommandPartyGuardAction(Container var1) {
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

   public void executePacket(PacketReader var1, ServerClient var2, ArrayList<HumanMob> var3) {
      int var4;
      HumanMob var15;
      if (var1.getNextBoolean()) {
         var4 = var1.getNextShortUnsigned();
         if (var4 <= 0) {
            return;
         }

         ArrayList var5 = new ArrayList(var4);

         int var6;
         for(var6 = 0; var6 < var4; ++var6) {
            int var7 = var1.getNextInt();
            int var8 = var1.getNextInt();
            var5.add(new Point(var7, var8));
         }

         if (var4 < var3.size()) {
            double var14 = (double)var3.size() / (double)var4;
            double var17 = 0.0;

            for(Iterator var10 = var3.iterator(); var10.hasNext(); var17 += var14) {
               HumanMob var11 = (HumanMob)var10.next();
               Point var12 = (Point)var5.get(Math.min((int)var17, var5.size() - 1));
               var11.commandGuard(var2, var12.x, var12.y);
            }
         } else {
            for(var6 = 0; var6 < var3.size(); ++var6) {
               var15 = (HumanMob)var3.get(var6);
               Point var18 = (Point)var5.get(var6);
               var15.commandGuard(var2, var18.x, var18.y);
            }
         }
      } else {
         var4 = var1.getNextInt();
         int var13 = var1.getNextInt();
         Iterator var16 = var3.iterator();

         while(var16.hasNext()) {
            var15 = (HumanMob)var16.next();
            var15.commandGuard(var2, var4, var13);
         }
      }

   }
}
