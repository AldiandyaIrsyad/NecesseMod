package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.LevelEventRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.Level;

public class PacketLevelEvent extends Packet {
   public final int levelIdentifierHashCode;
   public final int eventID;
   public final Packet spawnContent;

   public PacketLevelEvent(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.eventID = var2.getNextShortUnsigned();
      this.spawnContent = var2.getNextContentPacket();
   }

   public PacketLevelEvent(LevelEvent var1) {
      if (var1.getID() == -1) {
         throw new IllegalArgumentException("Specific level event cannot be sent over network");
      } else {
         this.levelIdentifierHashCode = var1.level == null ? 0 : var1.level.getIdentifierHashCode();
         this.eventID = var1.getID();
         this.spawnContent = new Packet();
         var1.setupSpawnPacket(new PacketWriter(this.spawnContent));
         PacketWriter var2 = new PacketWriter(this);
         var2.putNextInt(this.levelIdentifierHashCode);
         var2.putNextShortUnsigned(this.eventID);
         var2.putNextContentPacket(this.spawnContent);
      }
   }

   public LevelEvent getNewEvent(Level var1) {
      LevelEvent var2 = LevelEventRegistry.getEvent(this.eventID);
      if (var2 != null) {
         var2.level = var1;
         var2.applySpawnPacket(new PacketReader(this.spawnContent));
      }

      return var2;
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Level var4 = var2.world.getLevel(var3);
            if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
               var4.entityManager.addLevelEventHidden(this.getNewEvent(var4));
               var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
            } else {
               System.out.println(var3.getName() + " tried to spawn a level event on wrong level");
            }
         } else {
            System.out.println(var3.getName() + " tried to spawn a level event, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to spawn a level event, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null && (this.levelIdentifierHashCode == 0 || var2.getLevel().getIdentifierHashCode() == this.levelIdentifierHashCode)) {
         var2.getLevel().entityManager.addLevelEventHidden(this.getNewEvent(var2.getLevel()));
      }
   }
}
