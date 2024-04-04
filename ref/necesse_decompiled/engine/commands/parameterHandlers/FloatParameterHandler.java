package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class FloatParameterHandler extends ParameterHandler<Float> {
   private Float defaultValue;

   public FloatParameterHandler() {
      this.defaultValue = 0.0F;
   }

   public FloatParameterHandler(Float var1) {
      this.defaultValue = var1;
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return Collections.emptyList();
   }

   public Float parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      try {
         return Float.parseFloat(var4);
      } catch (NumberFormatException var7) {
         throw new IllegalArgumentException((var4.isEmpty() ? "Argument" : var4) + " for <" + var5.name + "> is not a number");
      }
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      if (var4.isEmpty()) {
         return true;
      } else {
         try {
            Float.parseFloat(var4);
            return true;
         } catch (NumberFormatException var7) {
            return false;
         }
      }
   }

   public Float getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
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
