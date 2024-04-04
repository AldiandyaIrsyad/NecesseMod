package necesse.engine.commands;

public abstract class ManualChatCommand extends ChatCommand {
   public final String usage;
   public final String action;

   public ManualChatCommand(String var1, String var2, String var3, PermissionLevel var4) {
      super(var1, var4);
      this.usage = var2;
      this.action = var3;
   }

   public String getUsage() {
      return this.usage;
   }

   public String getAction() {
      return this.action;
   }
}
