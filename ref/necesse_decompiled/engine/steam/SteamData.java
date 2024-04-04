package necesse.engine.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamAPICall;
import com.codedisaster.steamworks.SteamApps;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUser;
import com.codedisaster.steamworks.SteamUserCallback;
import com.codedisaster.steamworks.SteamUserStats;
import com.codedisaster.steamworks.SteamUserStatsCallback;
import com.codedisaster.steamworks.SteamUtils;
import com.codedisaster.steamworks.SteamUtilsCallback;
import com.codedisaster.steamworks.SteamFriends.FriendFlags;
import com.codedisaster.steamworks.SteamFriends.OverlayToWebPageMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.state.MainMenu;
import necesse.gfx.forms.presets.ConfirmationContinueForm;

public class SteamData {
   private static boolean initialized = false;
   private static boolean created = false;
   private static SteamApps apps;
   private static SteamUser user;
   private static SteamFriends friends;
   private static SteamUserStats stats;
   private static SteamMatchmaking matchmaking;
   private static SteamUtils utils;
   private static boolean statsLoaded = false;
   private static boolean overlayActive = false;
   private static SteamGameGlobalStats globalStats = new SteamGameGlobalStats((SteamUserStats)null);
   private static final long storeStatsTimeout = 5000L;
   private static final long storeStatsTimeLimit = 60000L;
   private static long nextStoreStatsTime = 0L;
   private static boolean shouldStoreStats = false;
   private static boolean waitForStoreStats;
   private static SteamID connectLobby = null;
   private static boolean joinedLobby;
   private static boolean inLobby;
   private static long connectLobbyTimeout;
   private static long lastCallbackError;

   public SteamData() {
   }

   public static void init() {
      if (!initialized) {
         if (SteamAPI.isSteamRunning()) {
            apps = new SteamApps();
            user = new SteamUser(new SteamUserCallback() {
            });
            friends = new SteamFriends(new SteamFriendsCallback() {
               public void onGameOverlayActivated(boolean var1, boolean var2, int var3) {
                  SteamData.overlayActive = var1;
               }

               public void onGameLobbyJoinRequested(SteamID var1, SteamID var2) {
                  GameLog.debug.println("Got join lobby request from " + SteamData.friends.getFriendPersonaName(var2) + ": " + var1);
                  SteamData.connectLobby = var1;
                  SteamData.joinedLobby = false;
                  SteamData.inLobby = false;
               }

               public void onGameRichPresenceJoinRequested(SteamID var1, String var2) {
                  GameLog.debug.println("Got join request from " + SteamData.friends.getFriendPersonaName(var1) + ": " + var2);
               }
            });
            stats = new SteamUserStats(new SteamUserStatsCallback() {
               public void onUserStatsReceived(long var1, SteamID var3, SteamResult var4) {
                  if (var1 == 1169040L) {
                     GameLog.debug.println("Loaded Steam user stats! " + SteamNativeHandle.getNativeHandle(var3) + ", " + var4);
                     SteamGameStats var5 = new SteamGameStats(SteamData.stats);
                     PlayerStats var6 = GlobalData.stats();
                     if (var6 != null) {
                        var6.loadSteam(var5);
                     }

                     SteamGameAchievements var7 = new SteamGameAchievements(SteamData.stats);
                     AchievementManager var8 = GlobalData.achievements();
                     if (var8 != null) {
                        var8.loadSteam(var7);
                     }

                     SteamData.updateGlobalStats();
                     SteamData.statsLoaded = true;
                  }

               }

               public void onUserStatsStored(long var1, SteamResult var3) {
                  if (var1 == 1169040L) {
                     GameLog.debug.println("Stored Steam user stats! " + var3);
                     SteamData.nextStoreStatsTime = System.currentTimeMillis() + 60000L;
                     SteamData.shouldStoreStats = false;
                     SteamData.waitForStoreStats = false;
                  }

               }

               public void onUserAchievementStored(long var1, boolean var3, String var4, int var5, int var6) {
                  if (var1 == 1169040L) {
                     GameLog.debug.println("Stored Steam user achievement " + var4 + (var3 ? "*" : "") + ": " + var5 + "/" + var6);
                     SteamData.nextStoreStatsTime = System.currentTimeMillis() + 60000L;
                     SteamData.shouldStoreStats = false;
                  }

               }

               public void onGlobalStatsReceived(long var1, SteamResult var3) {
                  if (var1 == 1169040L) {
                     SteamData.globalStats = new SteamGameGlobalStats(SteamData.stats);
                  }

               }
            });
            matchmaking = new SteamMatchmaking(new SteamMatchmakingCallback() {
               public void onLobbyInvite(SteamID var1, SteamID var2, long var3) {
                  if (var3 == 1169040L && GlobalData.getCurrentState() instanceof MainMenu) {
                     MainMenu var5 = (MainMenu)GlobalData.getCurrentState();
                     ConfirmationContinueForm var6 = new ConfirmationContinueForm("inviterecevied");
                     String var7 = (String)SteamData.tryToGetSteamName(var1).orElse(var1.toString());
                     var6.setupConfirmation((GameMessage)(new LocalMessage("ui", "gotinvite", "name", var7)), new LocalMessage("ui", "acceptbutton"), new LocalMessage("ui", "declinebutton"), () -> {
                        System.out.println("Accepted invite from " + var7);
                        SteamData.connectLobby(var2);
                     }, () -> {
                        System.out.println("Declined invite from " + var7);
                     });
                     var5.continueForm(var6);
                     Screen.requestWindowAttention();
                  }

               }

               public void onLobbyEnter(SteamID var1, int var2, boolean var3, SteamMatchmaking.ChatRoomEnterResponse var4) {
                  GameLog.debug.println("Entered lobby " + var1 + ", " + var2 + ", " + var3 + ", " + var4);
                  SteamData.joinedLobby = false;
                  if (var1.equals(SteamData.connectLobby)) {
                     SteamData.inLobby = true;
                  }

               }
            });
            utils = new SteamUtils(new SteamUtilsCallback() {
               public void onGamepadTextInputDismissed(boolean var1) {
                  if (var1) {
                     int var2 = SteamData.utils.getEnteredGamepadTextLength();
                     System.out.println("LENGTH: " + var2);
                     System.out.println("ENTERED: " + SteamData.utils.getEnteredGamepadTextInput(var2));
                  } else {
                     System.out.println("CANCELLED");
                  }

               }
            });
            created = true;
         }

         initialized = true;
      }
   }

   private static boolean initAndIsCreated() {
      if (!initialized) {
         init();
      }

      return isCreated();
   }

   public static boolean isCreated() {
      return created;
   }

   public static SteamApps getApps() {
      return apps;
   }

   public static SteamUtils getUtils() {
      return utils;
   }

   public static SteamID getSteamID() {
      return !initAndIsCreated() ? null : user.getSteamID();
   }

   public static String getSteamName() {
      return !initAndIsCreated() ? null : friends.getPersonaName();
   }

   public static boolean isOverlayActive() {
      return overlayActive;
   }

   public static SteamFriend[] getFriends(SteamFriends.FriendFlags... var0) {
      if (!initAndIsCreated()) {
         return new SteamFriend[0];
      } else {
         List var1 = Arrays.asList(var0);
         int var2 = friends.getFriendCount(var1);
         SteamFriend[] var3 = new SteamFriend[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            SteamID var5 = friends.getFriendByIndex(var4, var1);
            var3[var4] = new SteamFriend(var5) {
               public String getName() {
                  return SteamData.friends.getFriendPersonaName(this.steamID);
               }

               public SteamFriends.PersonaState getState() {
                  return SteamData.friends.getFriendPersonaState(this.steamID);
               }

               public SteamFriends.FriendRelationship getRelationship() {
                  return SteamData.friends.getFriendRelationship(this.steamID);
               }
            };
         }

         return var3;
      }
   }

   public static Optional<String> tryToGetSteamName(SteamID var0) {
      SteamFriend[] var1 = getFriends(FriendFlags.values());
      SteamFriend[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SteamFriend var5 = var2[var4];
         if (var5.steamID.equals(var0)) {
            return Optional.of(var5.getName());
         }
      }

      return Optional.empty();
   }

   public static void loadUserStats() {
      stats.requestCurrentStats();
   }

   public static boolean isStatsLoaded() {
      return statsLoaded;
   }

   public static SteamAPICall updateGlobalStats() {
      return stats.requestGlobalStats(Integer.MAX_VALUE);
   }

   public static SteamGameGlobalStats globalStats() {
      return globalStats;
   }

   public static void setStat(String var0, int var1) {
      setStat(var0, var1, true);
   }

   public static void setStat(String var0, int var1, boolean var2) {
      if (!stats.setStatI(var0, var1) && var2) {
         GameLog.warn.println("Could not set Steam stat " + var0 + " to " + var1);
      }

   }

   public static void setStat(String var0, float var1) {
      if (!stats.setStatF(var0, var1)) {
         GameLog.warn.println("Could not set Steam stat " + var0 + " to " + var1);
      }

   }

   public static void setAchievement(String var0) {
      if (!stats.setAchievement(var0)) {
         GameLog.warn.println("Could not set Steam achievement " + var0);
      }

   }

   public static void resetStatsAndAchievements(boolean var0) {
      if (!stats.resetAllStats(var0)) {
         GameLog.warn.println("Could not reset Steam stats" + (var0 ? " and achievements" : ""));
      }

   }

   public static void forceStoreStatsAndAchievements() {
      if (!stats.storeStats()) {
         GameLog.warn.println("Could not store Steam stats!");
      }

   }

   public static void storeStatsAndAchievements() {
      shouldStoreStats = true;
   }

   public static void tickStatsStore() {
      if (isStatsLoaded()) {
         if (shouldStoreStats && nextStoreStatsTime < System.currentTimeMillis()) {
            if (!stats.storeStats()) {
               GameLog.warn.println("Could not store Steam stats!");
            }

            nextStoreStatsTime = System.currentTimeMillis() + 5000L;
         }

      }
   }

   public static void connectLobby(long var0) {
      connectLobby(SteamID.createFromNativeHandle(var0));
   }

   public static void connectLobby(SteamID var0) {
      if (var0.isValid()) {
         connectLobby = var0;
         joinedLobby = false;
         inLobby = false;
      } else {
         GameLog.warn.println(var0 + " was not a valid Steam lobby id");
      }

   }

   public static boolean isOnCallbackErrorCooldown() {
      long var0 = System.currentTimeMillis() - lastCallbackError;
      return lastCallbackError != 0L && var0 < 60000L;
   }

   public static void resetCallbackErrorCooldown() {
      lastCallbackError = System.currentTimeMillis();
   }

   public static boolean setRichPresence(String var0, String var1) {
      return friends.setRichPresence(var0, var1);
   }

   public static void clearRichPresence() {
      friends.clearRichPresence();
   }

   public static GameMessage getFriendStatusMessage(SteamFriends.PersonaState var0) {
      switch (var0) {
         case Online:
            return new LocalMessage("ui", "statusonline");
         case Offline:
            return new LocalMessage("ui", "statusoffline");
         case Away:
            return new LocalMessage("ui", "statusaway");
         case Busy:
            return new LocalMessage("ui", "statusbusy");
         case Snooze:
            return new LocalMessage("ui", "statusSnooze");
         case LookingToPlay:
            return new LocalMessage("ui", "statuslookingtoplay");
         case LookingToTrade:
            return new LocalMessage("ui", "statuslookingtotrade");
         default:
            return new StaticMessage(var0.toString());
      }
   }

   public static ConnectInfo tickLobbyConnectRequested() {
      if (connectLobby != null) {
         if (!joinedLobby && !inLobby) {
            matchmaking.joinLobby(connectLobby);
            connectLobbyTimeout = System.currentTimeMillis() + 5000L;
            joinedLobby = true;
            return null;
         }

         if (inLobby) {
            String var0 = matchmaking.getLobbyData(connectLobby, "serverHostSteamID");
            if (var0 != null && !var0.isEmpty()) {
               try {
                  long var6 = Long.parseLong(var0);
                  if (var6 == 0L) {
                     throw new NumberFormatException();
                  }

                  matchmaking.leaveLobby(connectLobby);
                  connectLobby = null;
                  joinedLobby = false;
                  inLobby = false;
                  return new SteamConnectInfo(SteamID.createFromNativeHandle(var6));
               } catch (NumberFormatException var5) {
                  GameLog.warn.println("Lobby remote SteamID invalid: " + var0);
               }
            } else {
               String var1 = matchmaking.getLobbyData(connectLobby, "serverPort");
               if (var1 != null && !var1.isEmpty()) {
                  String var2 = matchmaking.getLobbyData(connectLobby, "serverAddress");
                  if (var2 != null && !var2.isEmpty()) {
                     try {
                        int var3 = Integer.parseInt(var1);
                        if (var3 < 0) {
                           throw new NumberFormatException();
                        }

                        matchmaking.leaveLobby(connectLobby);
                        connectLobby = null;
                        joinedLobby = false;
                        inLobby = false;
                        return new AddressConnectInfo(var2, var3);
                     } catch (NumberFormatException var4) {
                        GameLog.warn.println("Lobby port invalid: " + var1);
                     }
                  }
               }
            }
         }

         if ((joinedLobby || inLobby) && connectLobbyTimeout < System.currentTimeMillis()) {
            GameLog.warn.println("Timed out getting connect info from lobby " + connectLobby);
            matchmaking.leaveLobby(connectLobby);
            connectLobby = null;
            joinedLobby = false;
            inLobby = false;
            return null;
         }
      }

      return null;
   }

   public static void activateGameOverlayToWebPage(String var0) {
      friends.activateGameOverlayToWebPage(var0, OverlayToWebPageMode.Default);
   }

   public static boolean showGamepadTextInput(SteamUtils.GamepadTextInputMode var0, SteamUtils.GamepadTextLineMode var1, String var2, int var3, String var4) {
      return utils.showGamepadTextInput(var0, var1, var2, var3, var4);
   }

   public static boolean showFloatingGamepadTextInput(SteamUtils.FloatingGamepadTextInputMode var0, int var1, int var2, int var3, int var4) {
      return utils.showFloatingGamepadTextInput(var0, var1, var2, var3, var4);
   }

   public static void dispose() {
      if (isCreated()) {
         apps.dispose();
         user.dispose();
         friends.dispose();
         if (isStatsLoaded()) {
            if (stats.storeStats()) {
               waitForStoreStats = true;
               long var0 = System.currentTimeMillis() + 5000L;

               while(waitForStoreStats) {
                  if (var0 < System.currentTimeMillis()) {
                     GameLog.warn.println("Timed out storing Steam stats before dispose");
                     break;
                  }

                  SteamAPI.runCallbacks();

                  try {
                     Thread.sleep(500L);
                  } catch (InterruptedException var3) {
                     var3.printStackTrace();
                  }
               }
            } else {
               GameLog.warn.println("Could not store Steam stats before dispose");
            }
         }

         stats.dispose();
         matchmaking.dispose();
         utils.dispose();
      }

   }

   public static class SteamConnectInfo extends ConnectInfo {
      public final SteamID remoteID;

      public SteamConnectInfo(SteamID var1) {
         this.remoteID = var1;
      }

      public void startConnectionClient(MainMenu var1) {
         var1.connect(this.remoteID, (MainMenu.ConnectFrom)null);
      }

      public String toString() {
         return "STEAM:" + SteamID.getNativeHandle(this.remoteID);
      }
   }

   public static class AddressConnectInfo extends ConnectInfo {
      public final String address;
      public final int port;

      protected AddressConnectInfo(String var1, int var2) {
         this.address = var1;
         this.port = var2;
      }

      public void startConnectionClient(MainMenu var1) {
         var1.connect((String)null, this.address, this.port, (MainMenu.ConnectFrom)null);
      }

      public String toString() {
         return this.address + ":" + this.port;
      }
   }

   public abstract static class ConnectInfo {
      public ConnectInfo() {
      }

      public abstract void startConnectionClient(MainMenu var1);
   }
}
