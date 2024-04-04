package necesse.reports;

public class FeedbackData extends BasicsData {
   public FeedbackData(String var1) {
      super(var1);
   }

   public String generateFullFeedback(String var1) {
      String var2 = "";
      var2 = var2 + "Game version: " + (String)this.data.get("game_version") + "\n";
      var2 = var2 + "Steam build: " + (String)this.data.get("steam_build") + "\n";
      var2 = var2 + "Steam name: " + (String)this.data.get("steam_name") + "\n";
      var2 = var2 + "Authentication: " + (String)this.data.get("authentication") + "\n";
      var2 = var2 + "Launch parameters: " + (String)this.data.get("launch_parameters") + "\n";
      var2 = var2 + "Current state: " + (String)this.data.get("game_state") + "\n";
      var2 = var2 + "Current language: " + (String)this.data.get("game_language") + "\n";
      var2 = var2 + "\n";
      var2 = var2 + "Message:\n";
      var2 = var2 + var1;
      return var2;
   }
}
