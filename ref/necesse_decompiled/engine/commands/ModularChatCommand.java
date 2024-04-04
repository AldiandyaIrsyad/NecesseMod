package necesse.engine.commands;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public abstract class ModularChatCommand extends ChatCommand {
   public final boolean isCheat;
   public final String action;
   private CmdParameter[] parameters;

   public ModularChatCommand(String var1, String var2, PermissionLevel var3, boolean var4, CmdParameter... var5) {
      super(var1, var3);
      this.action = var2;
      this.isCheat = var4;
      this.parameters = var5;
   }

   public final boolean run(Client var1, Server var2, ServerClient var3, ArrayList<String> var4, CommandLog var5) {
      while(!var4.isEmpty() && ((String)var4.get(var4.size() - 1)).length() == 0) {
         var4.remove(0);
      }

      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();
      int var8 = 0;
      CmdParameter[] var9 = this.parameters;
      int var10 = var9.length;

      int var11;
      for(var11 = 0; var11 < var10; ++var11) {
         CmdParameter var12 = var9[var11];
         var8 += var12.countParameters();
      }

      CmdParameter.ArgCounter var14 = new CmdParameter.ArgCounter(var8, var4.size());
      CmdParameter[] var15 = this.parameters;
      var11 = var15.length;

      for(int var16 = 0; var16 < var11; ++var16) {
         CmdParameter var13 = var15[var16];
         if (!var13.parse(var1, var2, var3, var4, var6, var7, var14, var5)) {
            return false;
         }
      }

      this.runModular(var1, var2, var3, var6.toArray(), (String[])var7.toArray(new String[0]), var5);
      return true;
   }

   public abstract void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6);

   public List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, String[] var4) {
      return CmdParameter.autoComplete(var1, var2, var3, this.parameters, var4);
   }

   public String getCurrentUsage(Client var1, Server var2, ServerClient var3, String[] var4) {
      return CmdParameter.getCurrentUsage(this, var1, var2, var3, this.parameters, var4);
   }

   public String getUsage() {
      return CmdParameter.getUsage(this.parameters);
   }

   public String getAction() {
      return this.action;
   }

   public boolean isCheat() {
      return this.isCheat;
   }
}
