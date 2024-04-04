package necesse.engine.network.packet;

import java.util.function.Function;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainMenu;
import necesse.engine.util.LevelIdentifier;

public class PacketPlayerLevelChange extends Packet {
   public final int slot;
   public final LevelIdentifier identifier;
   public final boolean mountFollow;

   public PacketPlayerLevelChange(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.identifier = new LevelIdentifier(var2);
      this.mountFollow = var2.getNextBoolean();
   }

   public PacketPlayerLevelChange(int var1, LevelIdentifier var2, boolean var3) {
      this.slot = var1;
      this.identifier = var2;
      this.mountFollow = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextByteUnsigned(var1);
      var2.writePacket(var4);
      var4.putNextBoolean(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            if (var3.slot == this.slot) {
               var3.changeLevel(this.identifier, (Function)null, this.mountFollow);
            }

         }
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (this.slot == var2.getSlot()) {
         if (var3 != null) {
            var3.applyLevelChangePacket(this);
         }

         if (Settings.instantLevelChange) {
            var2.reset();
         } else if (GlobalData.getCurrentState() instanceof MainMenu) {
            ((MainMenu)GlobalData.getCurrentState()).changeLevel(var2);
         } else {
            GlobalData.setCurrentState(new MainMenu(var2));
         }
      } else if (var3 == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else {
         var3.applyLevelChangePacket(this);
      }

   }
}
