package necesse.inventory.container.customAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketAdventurePartyUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;
import necesse.inventory.container.events.AdventurePartyChangedEvent;

public abstract class CommandPartyCustomAction extends ContainerCustomAction {
   public final Container container;

   public CommandPartyCustomAction(Container var1) {
      this.container = var1;
   }

   protected PacketWriter setupPacket(Collection<Integer> var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextShortUnsigned(var1.size());
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         int var5 = (Integer)var4.next();
         var3.putNextInt(var5);
      }

      return var3;
   }

   protected boolean preCheckIfCanExecute(ServerClient var1) {
      return true;
   }

   protected void sendSyncUpdate(ServerClient var1) {
      var1.sendPacket(new PacketAdventurePartyUpdate(var1));
      (new AdventurePartyChangedEvent()).applyAndSendToClient(var1);
   }

   public void executePacket(PacketReader var1) {
      if (this.container.client.isServer()) {
         ServerClient var2 = this.container.client.getServerClient();
         if (this.preCheckIfCanExecute(var2)) {
            int var3 = var1.getNextShortUnsigned();
            ArrayList var4 = new ArrayList(var3);
            boolean var5 = false;

            for(int var6 = 0; var6 < var3; ++var6) {
               int var7 = var1.getNextInt();
               Object var8 = (Mob)var2.getLevel().entityManager.mobs.get(var7, false);
               if (var8 == null) {
                  var8 = var2.adventureParty.getMemberMob(var7);
               }

               if (var8 instanceof HumanMob && ((HumanMob)var8).canBeCommanded(var2)) {
                  var4.add((HumanMob)var8);
               } else {
                  var5 = true;
               }
            }

            this.executePacket(var1, var2, var4);
            if (var5) {
               this.sendSyncUpdate(var2);
            }

         }
      }
   }

   public abstract void executePacket(PacketReader var1, ServerClient var2, ArrayList<HumanMob> var3);
}
