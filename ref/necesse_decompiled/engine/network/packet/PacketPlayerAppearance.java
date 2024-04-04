package necesse.engine.network.packet;

import java.util.Iterator;
import java.util.Map;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;

public class PacketPlayerAppearance extends Packet {
   public final int slot;
   public final int characterUniqueID;
   public final HumanLook look;
   public final String name;

   public PacketPlayerAppearance(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.characterUniqueID = var2.getNextInt();
      this.look = new HumanLook(var2);
      this.name = var2.getNextString();
   }

   public PacketPlayerAppearance(ServerClient var1) {
      this.slot = var1.slot;
      this.characterUniqueID = var1.getCharacterUniqueID();
      this.look = var1.playerMob.look;
      this.name = var1.getName();
      this.putData();
   }

   public PacketPlayerAppearance(int var1, int var2, PlayerMob var3) {
      this.slot = var1;
      this.characterUniqueID = var2;
      this.look = var3.look;
      this.name = var3.getDisplayName();
      this.putData();
   }

   private void putData() {
      PacketWriter var1 = new PacketWriter(this);
      var1.putNextByteUnsigned(this.slot);
      var1.putNextInt(this.characterUniqueID);
      this.look.setupContentPacket(var1, true);
      var1.putNextString(this.name);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.slot != var3.slot) {
         System.out.print("Player " + var3.authentication + " (\"" + var3.getName() + "\", slot " + var3.slot + ") tried to change wrong client slot appearance: " + this.slot);
      } else {
         if (var3.needAppearance()) {
            GameMessage var4 = GameUtils.isValidPlayerName(this.name);
            if (var4 != null) {
               var3.sendPacket(new PacketCharacterSelectError(this.look, var4));
            } else {
               Iterator var5 = var2.usedNames.entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry var6 = (Map.Entry)var5.next();
                  Long var7 = (Long)var6.getKey();
                  String var8 = (String)var6.getValue();
                  if (var7 != var3.authentication && var8.equalsIgnoreCase(this.name)) {
                     var3.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "characternameinuse", "name", this.name)));
                     return;
                  }
               }

               var3.applyAppearancePacket(this);
               if (var3.getName().equalsIgnoreCase(Settings.serverOwnerName)) {
                  var3.setPermissionLevel(PermissionLevel.OWNER, false);
               }

               var3.sendConnectingMessage();
            }
         } else if (var3.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            System.out.println(var3.getName() + " tried to change appearance, but isn't admin");
            var2.network.sendPacket(new PacketPlayerAppearance(var3), (ServerClient)var3);
         } else if (!var3.getName().equalsIgnoreCase(this.name)) {
            System.out.println(var3.getName() + " tried to change appearance with wrong name");
            var2.network.sendPacket(new PacketPlayerAppearance(var3), (ServerClient)var3);
         } else if (!var2.world.settings.allowCheats) {
            System.out.println(var3.getName() + " tried to change appearance, but cheats aren't allowed");
            var2.network.sendPacket(new PacketPlayerAppearance(var3), (ServerClient)var3);
         } else {
            var3.applyAppearancePacket(this);
         }

      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else {
         var3.applyAppearancePacket(this);
      }

      var2.loading.createCharPhase.submitPlayerAppearancePacket(this);
   }
}
