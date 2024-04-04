package necesse.engine.commands.parameterHandlers.gnd;

import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemByte;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class GNDByteParameterHandler extends GNDItemParameterHandler<GNDItemByte> {
   public GNDByteParameterHandler() {
      super("byte");
   }

   protected List<AutoComplete> autocompleteSecondArg(CmdArgument var1) {
      return Collections.emptyList();
   }

   protected GNDItemByte parseSecondArg(String var1, CmdParameter var2) {
      try {
         return new GNDItemByte(Byte.parseByte(var1));
      } catch (NumberFormatException var4) {
         throw new IllegalArgumentException(var1 + " for <" + var2.name + "> is not a byte");
      }
   }

   public GNDItemByte getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return null;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GNDItem getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.getDefault(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected GNDItem parseSecondArg(String var1, CmdParameter var2) {
      return this.parseSecondArg(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.getDefault(var1, var2, var3, var4);
   }
}
