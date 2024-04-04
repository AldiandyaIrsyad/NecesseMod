package necesse.engine.commands.parameterHandlers;

import java.util.List;
import java.util.function.Function;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class BoolParameterHandler extends ParameterHandler<Boolean> {
   public static String[] validTrue = new String[]{"1", "true"};
   public static String[] validFalse = new String[]{"0", "false"};
   private Boolean defaultValue;

   public BoolParameterHandler() {
      this.defaultValue = false;
   }

   public BoolParameterHandler(Boolean var1) {
      this.defaultValue = var1;
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return autocompleteFromArray((String[])GameUtils.concat(validTrue, validFalse), (Function)null, (Function)null, var4);
   }

   public Boolean parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      String[] var6 = validTrue;
      int var7 = var6.length;

      int var8;
      String var9;
      for(var8 = 0; var8 < var7; ++var8) {
         var9 = var6[var8];
         if (var4.equalsIgnoreCase(var9)) {
            return true;
         }
      }

      var6 = validFalse;
      var7 = var6.length;

      for(var8 = 0; var8 < var7; ++var8) {
         var9 = var6[var8];
         if (var4.equalsIgnoreCase(var9)) {
            return false;
         }
      }

      throw new IllegalArgumentException((var4.isEmpty() ? "Argument" : var4) + " for <" + var5.name + "> must be either " + GameUtils.join(GameUtils.concat(validTrue, validFalse), ", ", " or "));
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public Boolean getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.defaultValue;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
      return this.getDefault(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      return this.parse(var1, var2, var3, var4, var5);
   }
}
