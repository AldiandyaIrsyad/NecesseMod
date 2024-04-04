package necesse.engine.save;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;

public class CharacterSave {
   public final int characterUniqueID;
   public ArrayList<ModSaveInfo> lastMods = null;
   public final PlayerMob player;
   public final boolean cheatsEnabled;
   public final PlayerStats characterStats;
   public GameMessage lastUsed;
   private long timeModified;
   private String dateModified;
   private static Pattern appendedFileNamePattern = Pattern.compile("(#\\d+)?(-backup(\\d+)?)?$");
   public static Pattern backupFileNamePattern = Pattern.compile("-backup(\\d+)?$");

   public CharacterSave(Client var1) {
      this.characterUniqueID = var1.getCharacterUniqueID();
      ClientClient var2 = var1.getClient();
      this.player = var2 == null ? null : var2.playerMob;
      this.cheatsEnabled = var1.worldSettings != null && var1.worldSettings.allowCheats;
      this.characterStats = var1.characterStats;
      if (var1.playingOnDisplayName != null) {
         this.lastUsed = new LocalMessage("ui", "characterlastworld", "world", var1.playingOnDisplayName);
      } else {
         this.lastUsed = null;
      }

   }

   public CharacterSave(ServerClient var1) {
      this.characterUniqueID = var1.getCharacterUniqueID();
      this.player = var1.playerMob;
      this.cheatsEnabled = var1.getServer().world.settings.allowCheats;
      this.characterStats = var1.characterStats();
   }

   private CharacterSave(int var1, PlayerMob var2) {
      this.characterUniqueID = var1;
      this.player = var2;
      this.cheatsEnabled = false;
      this.characterStats = null;
   }

   public static CharacterSave newCharacter(int var0, PlayerMob var1) {
      return new CharacterSave(var0, var1);
   }

   public static CharacterSave newCharacter(PlayerMob var0) {
      return newCharacter(getNewUniqueCharacterID((Predicate)null), var0);
   }

   public CharacterSave(LoadData var1, File var2, long var3) {
      this.timeModified = var3;
      if (var3 != 0L) {
         SimpleDateFormat var5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         this.dateModified = var5.format(var3);
      }

      this.characterUniqueID = var1.getInt("characterUniqueID", -1, false);
      if (this.characterUniqueID == -1) {
         throw new IllegalArgumentException("Could not load character uniqueID");
      } else {
         this.player = new PlayerMob((long)this.characterUniqueID, (NetworkClient)null);
         this.player.playerName = var1.getSafeString("name", "N/A");
         if (this.player.playerName.equals("N/A")) {
            throw new IllegalArgumentException("Could not load character \"" + this.characterUniqueID + "\" name");
         } else {
            if (var2 != null) {
               String var15 = GameUtils.removeFileExtension(var2.getName());
               var15 = appendedFileNamePattern.matcher(var15).replaceFirst("");
               String var6 = fromPlayerNameToFileName(this.player.playerName);
               if (!var6.equals(var15)) {
                  this.player.playerName = var15;
               }
            }

            LoadData var16 = var1.getFirstLoadDataByName("lastUsed");
            if (var16 != null) {
               try {
                  this.lastUsed = GameMessage.loadSave(var16);
               } catch (Exception var14) {
               }
            }

            LoadData var17 = var1.getFirstLoadDataByName("MODS");
            LoadData var8;
            if (var17 != null) {
               this.lastMods = new ArrayList();
               Iterator var7 = var17.getLoadData().iterator();

               while(var7.hasNext()) {
                  var8 = (LoadData)var7.next();

                  try {
                     this.lastMods.add(new ModSaveInfo(var8));
                  } catch (LoadDataException var13) {
                     GameLog.warn.print("Could not load mod info: " + var13.getMessage());
                  }
               }
            }

            LoadData var18 = var1.getFirstLoadDataByName("PLAYER");
            if (var18 != null) {
               try {
                  this.player.applyLoadedCharacterLoadData(var18);
               } catch (Exception var12) {
                  throw new IllegalArgumentException("Could not load character \"" + this.characterUniqueID + "\" player data", var12);
               }

               this.cheatsEnabled = var1.getBoolean("cheatsEnabled", false, false);
               var8 = var1.getFirstLoadDataByName("STATS");
               if (var8 != null) {
                  PlayerStats var9 = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);

                  try {
                     var9.applyLoadData(var8);
                  } catch (Exception var11) {
                     var9 = null;
                     System.err.println("Could not load character \"" + this.characterUniqueID + "\" stats");
                     var11.printStackTrace();
                  }

                  this.characterStats = var9;
               } else {
                  this.characterStats = null;
               }

            } else {
               throw new IllegalArgumentException("Could not find character \"" + this.characterUniqueID + "\" player data");
            }
         }
      }
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("characterUniqueID", this.characterUniqueID);
      var1.addSafeString("name", this.player.playerName);
      if (this.lastUsed != null) {
         var1.addSaveData(this.lastUsed.getSaveData("lastUsed"));
      }

      SaveData var2 = new SaveData("MODS");
      Iterator var3 = ModLoader.getEnabledMods().iterator();

      while(var3.hasNext()) {
         LoadedMod var4 = (LoadedMod)var3.next();
         SaveData var5 = (new ModSaveInfo(var4)).getSaveData();
         var2.addSaveData(var5);
      }

      var1.addSaveData(var2);
      SaveData var6 = new SaveData("PLAYER");
      this.player.addLoadedCharacterSaveData(var6);
      var1.addSaveData(var6);
      if (this.cheatsEnabled) {
         var1.addBoolean("cheatsEnabled", true);
      }

      if (this.characterStats != null) {
         SaveData var7 = new SaveData("STATS");
         this.characterStats.addSaveData(var7);
         var1.addSaveData(var7);
      }

   }

   public CharacterSave(int var1, CharacterSaveNetworkData var2) {
      this.characterUniqueID = var1;
      this.player = new PlayerMob((long)var1, (NetworkClient)null);
      var2.applyToPlayer(this.player);
      if (var2.characterStatsData != null) {
         this.characterStats = new PlayerStats(false, PlayerStats.Mode.READ_ONLY);
         var2.applyToStats(this.characterStats);
      } else {
         this.characterStats = null;
      }

      this.cheatsEnabled = var2.cheatsEnabled;
   }

   public long getTimeModified() {
      return this.timeModified;
   }

   public String getModifiedDate() {
      return this.dateModified;
   }

   public static String fromPlayerNameToFileName(String var0) {
      String var1 = GameUtils.toValidFileName(var0);
      if (var1.length() > 200) {
         var1 = var1.substring(0, 200);
      } else if (var1.isEmpty()) {
         return "char";
      }

      return var1;
   }

   public static int getNewUniqueCharacterID(Predicate<Integer> var0) {
      for(int var1 = 0; var1 < 1000; ++var1) {
         int var2 = GameRandom.globalRandom.nextInt();
         if (var0 == null || var0.test(var2)) {
            return var2;
         }
      }

      GameLog.warn.println("Could not find a new valid character uniqueID. Using a random one");
      return GameRandom.globalRandom.nextInt();
   }

   public static File saveCharacter(CharacterSave var0, File var1, boolean var2) {
      SaveData var3 = new SaveData("CHARACTER");
      var0.addSaveData(var3);
      String var4;
      if (var1 == null) {
         var4 = fromPlayerNameToFileName(var0.player.playerName);
         int var5 = 0;

         while(true) {
            String var6 = var4 + (var5 != 0 ? "#" + var5 : "");
            File var7 = new File(getCharacterSavesPath() + var6 + ".dat");
            if (!var7.exists()) {
               var1 = var7;
               break;
            }

            try {
               CharacterSave var8 = new CharacterSave(new LoadData(var1), (File)null, 0L);
               if (var8.characterUniqueID == var0.characterUniqueID) {
                  var1 = var7;
                  break;
               }
            } catch (Exception var10) {
            }

            ++var5;
         }
      }

      var3.saveScript(var1);
      if (var2) {
         var4 = GameUtils.removeFileExtension(var1.getName());
         File var11 = GameUtils.resolveFile(var1.getParentFile(), var4 + "-backup" + ".dat");

         try {
            GameUtils.copyFileOrFolderReplaceExisting(var1, var11);
         } catch (IOException var9) {
            System.err.println("Could not create backup of character to " + var4);
            var9.printStackTrace();
         }
      }

      return var1;
   }

   public static File saveCharacter(Client var0, boolean var1) {
      ClientClient var2 = var0.getClient();
      if (var2 != null && var2.loadedPlayer && var2.playerMob != null && var0.worldSettings != null) {
         File var3 = saveCharacter(new CharacterSave(var0), var0.characterFilePath, var1);
         var0.characterFilePath = var3;
         return var3;
      } else {
         GameLog.warn.println("Could not save character because no character was loaded");
         return null;
      }
   }

   public static String getCharacterSavesPath() {
      return GlobalData.appDataPath() + "saves/characters/";
   }

   public static boolean deleteCharacter(File var0) {
      return GameUtils.deleteFileOrFolder(var0);
   }

   public static void loadCharacters(BiConsumer<File, CharacterSave> var0, Supplier<Boolean> var1, Consumer<Boolean> var2, int var3) {
      if (var1 != null && (Boolean)var1.get()) {
         if (var2 != null) {
            var2.accept(true);
         }

      } else {
         File[] var4 = (new File(getCharacterSavesPath())).listFiles();
         if (var4 == null) {
            var4 = new File[0];
         }

         ArrayList var5 = new ArrayList(var4.length);
         File[] var6 = var4;
         int var7 = var4.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            File var9 = var6[var8];
            if (var9.isFile() && !backupFileNamePattern.matcher(GameUtils.removeFileExtension(var9.getName())).find()) {
               var5.add(new ObjectValue(var9, new ComparableSequence(var9.lastModified())));
            }
         }

         Comparator var17 = Comparator.comparing((var0x) -> {
            return (ComparableSequence)var0x.value;
         });
         var5.sort(var17.reversed());
         var7 = 0;
         Iterator var18 = var5.iterator();

         while(var18.hasNext()) {
            ObjectValue var19 = (ObjectValue)var18.next();
            if (var1 != null && (Boolean)var1.get()) {
               if (var2 != null) {
                  var2.accept(true);
               }

               return;
            }

            if (var3 >= 0 && var7 >= var3) {
               break;
            }

            try {
               LoadData var10 = new LoadData(SaveComponent.loadScriptRaw((File)var19.object, false));
               long var11 = ((File)var19.object).lastModified();
               CharacterSave var13 = new CharacterSave(var10, (File)var19.object, var11);
               if (var1 != null && (Boolean)var1.get()) {
                  if (var2 != null) {
                     var2.accept(true);
                  }

                  return;
               }

               var0.accept((File)var19.object, var13);
               ++var7;
            } catch (IOException var14) {
               System.err.println("Error loading character file: " + ((File)var19.object).getName());
               var14.printStackTrace();
            } catch (IllegalArgumentException | SaveSyntaxException var15) {
               System.err.println("Error loading character script: " + ((File)var19.object).getName());
               var15.printStackTrace();
            } catch (Exception var16) {
               System.err.println("Unknown error loading character: " + ((File)var19.object).getName());
               var16.printStackTrace();
            }
         }

         if (var2 != null) {
            var2.accept(false);
         }

      }
   }
}
