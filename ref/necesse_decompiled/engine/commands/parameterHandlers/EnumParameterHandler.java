package necesse.engine.commands.parameterHandlers;

import java.util.List;
import java.util.Locale;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class EnumParameterHandler<T extends Enum<T>> extends ParameterHandler<T> {
   private T defaultValue;
   private T[] values;

   public EnumParameterHandler(T var1, T... var2) {
      this.defaultValue = var1;
      this.values = var2;
   }

   public EnumParameterHandler(T... var1) {
      this((Enum)null, var1);
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      return autocompleteFromArray(this.values, (var0) -> {
         return true;
      }, (var0) -> {
         return var0.name().toLowerCase(Locale.ENGLISH);
      }, var4);
   }

   public T parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      Enum[] var6 = this.values;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Enum var9 = var6[var8];
         if (var9.name().equalsIgnoreCase(var4)) {
            return var9;
         }
      }

      throw new IllegalArgumentException("Missing value for <" + var5.name + ">");
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public T getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
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
