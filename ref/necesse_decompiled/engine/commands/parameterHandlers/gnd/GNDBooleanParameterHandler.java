package necesse.engine.commands.parameterHandlers.gnd;

import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemBoolean;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class GNDBooleanParameterHandler extends GNDItemParameterHandler<GNDItemBoolean> {
   public GNDBooleanParameterHandler() {
      super("bool");
   }

   protected List<AutoComplete> autocompleteSecondArg(CmdArgument var1) {
      return Collections.emptyList();
   }

   protected GNDItemBoolean parseSecondArg(String var1, CmdParameter var2) {
      String[] var3 = BoolParameterHandler.validTrue;
      int var4 = var3.length;

      int var5;
      String var6;
      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         if (var1.equalsIgnoreCase(var6)) {
            return new GNDItemBoolean(true);
         }
      }

      var3 = BoolParameterHandler.validFalse;
      var4 = var3.length;

      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var3[var5];
         if (var1.equalsIgnoreCase(var6)) {
            return new GNDItemBoolean(false);
         }
      }

      throw new IllegalArgumentException(var1 + " for <" + var2.name + "> must be either " + GameUtils.join(GameUtils.concat(BoolParameterHandler.validTrue, BoolParameterHandler.validFalse), ", ", " or "));
   }

   public GNDItemBoolean getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
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
