package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.engine.world.WorldSettings;

public class PacketSettings extends Packet {
   public PacketSettings(byte[] var1) {
      super(var1);
   }

   public PacketSettings(WorldSettings var1) {
      PacketWriter var2 = new PacketWriter(this);
      var1.setupContentPacket(var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         var2.world.settings.applyContentPacket(new PacketReader(this));
         var2.network.sendToAllClients(this);
         var2.world.settings.saveSettings();
      } else {
         System.out.println(var3.getName() + " tried to change world settings without permissions");
         var2.network.sendPacket(new PacketSettings(var2.world.settings), (ServerClient)var3);
         var2.network.sendPacket(new PacketPermissionUpdate(var3.getPermissionLevel()), (ServerClient)var3);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.worldSettings == null) {
         var2.worldSettings = new WorldSettings(var2, new PacketReader(this));
      } else {
         var2.worldSettings.applyContentPacket(new PacketReader(this));
      }

      if (GlobalData.getCurrentState() instanceof MainGame) {
         MainGame var3 = (MainGame)GlobalData.getCurrentState();
         if (!var2.worldSettings.achievementsEnabled()) {
            var3.formManager.pauseMenu.disableAchievements();
         }
      }

   }
}
