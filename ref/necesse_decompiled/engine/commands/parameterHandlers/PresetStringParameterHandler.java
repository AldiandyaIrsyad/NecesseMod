package necesse.engine.commands.parameterHandlers;

import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class PresetStringParameterHandler extends ParameterHandler<String> {
   private String[] presets;
   private String defaultValue;

   public PresetStringParameterHandler(boolean var1, String... var2) {
      this.defaultValue = null;
      if (var1) {
         this.presets = new String[var2.length - 1];
         System.arraycopy(var2, 0, this.presets, 0, this.presets.length);
         this.defaultValue = var2[var2.length - 1];
      } else {
         this.presets = var2;
      }

   }

   public PresetStringParameterHandler(String... var1) {
      this(false, var1);
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return autocompleteFromArray(this.presets, (var0) -> {
         return true;
      }, (var0) -> {
         return var0;
      }, var4);
   }

   public String parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      String[] var6 = this.presets;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String var9 = var6[var8];
         if (var9.equalsIgnoreCase(var4)) {
            return var9;
         }
      }

      throw new IllegalArgumentException("Missing either: " + GameUtils.join(this.presets, "/") + " for <" + var5.name + ">");
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public String getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
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
