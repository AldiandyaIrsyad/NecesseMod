package necesse.engine.network.packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;

public class PacketCmdAutocomplete extends Packet {
   public PacketCmdAutocomplete(byte[] var1) {
      super(var1);
   }

   public PacketCmdAutocomplete(String var1) {
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextString(var1);
   }

   public PacketCmdAutocomplete(List<AutoComplete> var1) {
      PacketWriter var2 = new PacketWriter(this);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         AutoComplete var4 = (AutoComplete)var3.next();
         var2.putNextContentPacket(var4.getContentPacket());
      }

   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      PacketReader var4 = new PacketReader(this);
      String var5 = var4.getNextString();
      List var6 = var2.commandsManager.autocomplete(new ParsedCommand(var5), var3);
      if (var6.size() > 10) {
         var6 = var6.subList(0, 10);
      }

      if (!var6.isEmpty()) {
         var3.sendPacket(new PacketCmdAutocomplete(var6));
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      ArrayList var3 = new ArrayList();
      PacketReader var4 = new PacketReader(this);

      while(var4.hasNext()) {
         var3.add(new AutoComplete(new PacketReader(var4.getNextContentPacket())));
      }

      if (GlobalData.getCurrentState() instanceof MainGame) {
         ((MainGame)GlobalData.getCurrentState()).formManager.chat.onAutocompletePacket(var3);
      }

   }
}
