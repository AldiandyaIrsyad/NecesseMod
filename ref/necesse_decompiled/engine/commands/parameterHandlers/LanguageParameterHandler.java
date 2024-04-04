package necesse.engine.commands.parameterHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class LanguageParameterHandler extends ParameterHandler<Language> {
   private Language defaultValue;

   public LanguageParameterHandler() {
      this.defaultValue = null;
   }

   public LanguageParameterHandler(Language var1) {
      this.defaultValue = var1;
   }

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, CmdArgument var4) {
      ArrayList var5 = new ArrayList();
      var5.addAll(autocompleteFromArray(Localization.getLanguages(), (Function)null, (var0) -> {
         return var0.localDisplayName;
      }, var4));
      var5.addAll(autocompleteFromArray(Localization.getLanguages(), (Function)null, (var0) -> {
         return var0.stringID;
      }, var4));
      return var5;
   }

   public Language parse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) throws IllegalArgumentException {
      Language[] var6 = Localization.getLanguages();
      Language[] var7 = var6;
      int var8 = var6.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         Language var10 = var7[var9];
         if (var4.equalsIgnoreCase(var10.localDisplayName)) {
            return var10;
         }

         if (var4.equalsIgnoreCase(var10.englishDisplayName)) {
            return var10;
         }

         if (var4.equalsIgnoreCase(var10.stringID)) {
            return var10;
         }
      }

      throw new IllegalArgumentException("Could not find language \"" + var4 + "\" for <" + var5.name + ">");
   }

   public boolean tryParse(Client var1, Server var2, ServerClient var3, String var4, CmdParameter var5) {
      return !this.autocomplete(var1, var2, var3, new CmdArgument(var5, var4, 1)).isEmpty();
   }

   public Language getDefault(Client var1, Server var2, ServerClient var3, CmdParameter var4) {
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
