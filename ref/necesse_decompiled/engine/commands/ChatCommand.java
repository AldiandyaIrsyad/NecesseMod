package necesse.engine.commands;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public abstract class ChatCommand implements Comparable<ChatCommand> {
   public final String name;
   public final PermissionLevel permissionLevel;

   public ChatCommand(String var1, PermissionLevel var2) {
      this.name = var1;
      this.permissionLevel = var2;
   }

   public String getName() {
      return this.name;
   }

   public String getFullUsage(boolean var1) {
      return "Usage: " + (var1 ? "/" : "") + this.name + " " + this.getUsage();
   }

   public String getFullAction() {
      return "Action: " + this.getAction();
   }

   public abstract String getUsage();

   public abstract String getAction();

   public boolean isCheat() {
      return true;
   }

   public boolean shouldBeListed() {
      return true;
   }

   public boolean onlyForHelp() {
      return false;
   }

   public abstract boolean run(Client var1, Server var2, ServerClient var3, ArrayList<String> var4, CommandLog var5);

   public abstract List<AutoComplete> autocomplete(Client var1, Server var2, ServerClient var3, String[] var4);

   public boolean autocompleteOnServer() {
      return false;
   }

   public abstract String getCurrentUsage(Client var1, Server var2, ServerClient var3, String[] var4);

   public boolean havePermissions(Client var1, Server var2, ServerClient var3) {
      return this.permissionLevel.getLevel() <= CommandsManager.getPermissionLevel(var1, var3).getLevel();
   }

   public String getFullHelp(boolean var1) {
      return (var1 ? "/" : "") + this.name + " " + this.getUsage();
   }

   public int compareTo(ChatCommand var1) {
      return this.name.compareTo(var1.name);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((ChatCommand)var1);
   }
}
