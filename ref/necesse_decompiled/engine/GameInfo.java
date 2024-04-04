package necesse.engine;

import necesse.engine.steam.SteamData;

public class GameInfo {
   public static final String name = "Necesse";
   public static final int appID = 1169040;
   public static final String version = "0.24.2";
   public static final GameVersion gameVersion = new GameVersion("0.24.2");
   public static final int hotfix = 0;
   public static final String discord_invite_url = "https://discord.com/invite/FAFgrKD";
   public static final String steam_news_url = "https://store.steampowered.com/news/app/1169040";
   public static final String twitter_url = "https://twitter.com/NecesseGame";
   public static final String youtube_url = "https://www.youtube.com/channel/UCr4Jtyot0FRRWsXAWXOHW6g?sub_confirmation=1";
   public static final String website_url = "https://necessegame.com/";
   public static final String wiki_url = "https://necessewiki.com/";

   public GameInfo() {
   }

   public static String getFullVersionString() {
      String var0 = "0.24.2";
      return var0;
   }

   public static String getVersionStringAndBuild() {
      return "0.24.2 build " + SteamData.getApps().getAppBuildId();
   }

   public static String getFullVersionStringAndBuild() {
      return getFullVersionString() + " build " + SteamData.getApps().getAppBuildId();
   }
}
