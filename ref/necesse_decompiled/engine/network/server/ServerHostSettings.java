package necesse.engine.network.server;

public class ServerHostSettings {
   public String password = null;
   public boolean allowOutsideCharacters;
   public boolean forcedPvP;

   public ServerHostSettings() {
   }

   public void apply(Server var1, boolean var2) {
      if (this.password != null) {
         var1.settings.password = this.password;
      }

      var1.world.settings.allowOutsideCharacters = this.allowOutsideCharacters;
      var1.world.settings.forcedPvP = this.forcedPvP;
      if (var2) {
         var1.world.settings.sendSettingsPacket();
      }

   }
}
