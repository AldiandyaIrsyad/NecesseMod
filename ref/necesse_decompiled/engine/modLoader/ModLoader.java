package necesse.engine.modLoader;

import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCCallback;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.WorldSave;
import necesse.engine.util.ComputedValue;
import necesse.engine.world.WorldEntity;
import net.bytebuddy.agent.ByteBuddyAgent;

public class ModLoader {
   public static String devMod = null;
   public static boolean disableMods;
   private static List<LoadedMod> enabledMods = new ArrayList();
   private static List<LoadedMod> notEnabledMods = new ArrayList();
   private static int modsHash;
   private static boolean hasLoadedMods;
   private static List<LoadedMod> allMods = new ArrayList();
   public static List<ModListData> currentList = new ArrayList();
   private static boolean loadedMods = false;
   public static final HashMap<Class<? extends Annotation>, HashSet<LoadedMod>> modErrors = new HashMap();

   public ModLoader() {
   }

   private static String modsPath() {
      return GlobalData.appDataPath() + "mods/";
   }

   private static String modsListFile() {
      return modsPath() + "modlist.data";
   }

   public static void loadMods(boolean var0) throws ModLoadException {
      if (loadedMods) {
         throw new IllegalStateException("Can only load mods once");
      } else {
         loadedMods = true;
         enabledMods = new ArrayList();
         notEnabledMods = new ArrayList();
         allMods = new ArrayList();
         File var1;
         File var2;
         if (devMod != null) {
            var1 = new File(devMod);
            System.out.println("Loading dev mod from " + var1.getAbsolutePath());
            var2 = LoadedMod.validateDevFolderAndReturnJar(var1);
            if (var2 != null) {
               loadMod(var2, ModLoadLocation.DEVELOPMENT_FOLDER, var1, (SteamPublishedFileID)null);
            } else {
               GameLog.warn.println(Localization.translate("ui", "moduploadfolderinvalid"));
            }
         }

         var1 = new File(modsPath());
         if (var1.exists()) {
            File[] var18 = var1.listFiles();
            if (var18 != null) {
               File[] var3 = var18;
               int var4 = var18.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  File var6 = var3[var5];
                  if (!var6.getPath().endsWith("modlist.data")) {
                     loadMod(var6, ModLoadLocation.MODS_FOLDER, (File)null, (SteamPublishedFileID)null);
                  }
               }
            }
         }

         int var7;
         if (!var0) {
            SteamUGC var19 = new SteamUGC(new SteamUGCCallback() {
            });
            int var20 = var19.getNumSubscribedItems();
            if (var20 > 0) {
               System.out.println("Found " + var20 + " subscribed items from Steam Workshop");
               SteamPublishedFileID[] var25 = new SteamPublishedFileID[var20];
               var19.getSubscribedItems(var25);
               SteamPublishedFileID[] var30 = var25;
               int var33 = var25.length;

               for(var7 = 0; var7 < var33; ++var7) {
                  SteamPublishedFileID var8 = var30[var7];
                  SteamUGC.ItemInstallInfo var9 = new SteamUGC.ItemInstallInfo();
                  if (var19.getItemInstallInfo(var8, var9)) {
                     File var10 = new File(var9.getFolder());
                     if (var10.isDirectory()) {
                        File[] var11 = var10.listFiles();
                        if (var11 == null) {
                           GameLog.warn.println("Could not list files in mod directory at " + var10.getAbsolutePath());
                        } else {
                           File var12 = null;
                           File[] var13 = var11;
                           int var14 = var11.length;

                           for(int var15 = 0; var15 < var14; ++var15) {
                              File var16 = var13[var15];
                              if (var16.getName().endsWith(".jar")) {
                                 if (var12 != null) {
                                    GameLog.warn.println("Already loaded one mod " + var12.getName() + " at " + var10.getAbsolutePath());
                                 } else {
                                    var12 = var16;
                                    loadMod(var16, ModLoadLocation.STEAM_WORKSHOP, (File)null, var8);
                                 }
                              }
                           }
                        }
                     }
                  } else {
                     System.out.println("Subscribed mod " + SteamNativeHandle.getNativeHandle(var8) + " is not installed yet");
                  }
               }
            }

            var19.dispose();
         }

         if (!allMods.isEmpty()) {
            GameLoadingScreen.addLog(Localization.translate("loading", "foundmods", "mods", (Object)allMods.size()));
            if (disableMods) {
               System.out.println("Disabling all mods because of launch option: -disablemods");

               LoadedMod var26;
               for(Iterator var21 = allMods.iterator(); var21.hasNext(); var26.listData.enabled = false) {
                  var26 = (LoadedMod)var21.next();
               }
            } else {
               var2 = new File(modsListFile());
               Iterator var29;
               LoadedMod var34;
               if (var2.exists()) {
                  ArrayList var23 = new ArrayList(allMods);
                  allMods = new ArrayList();

                  try {
                     LoadData var28 = new LoadData(var2);
                     currentList = ModListData.loadList(var28);
                     Iterator var32 = currentList.iterator();

                     label135:
                     while(true) {
                        while(true) {
                           if (!var32.hasNext()) {
                              break label135;
                           }

                           ModListData var35 = (ModListData)var32.next();

                           for(var7 = 0; var7 < var23.size(); ++var7) {
                              LoadedMod var36 = (LoadedMod)var23.get(var7);
                              if (var35.matchesMod(var36)) {
                                 allMods.add(var36);
                                 var36.listData.enabled = var35.enabled;
                                 var23.remove(var7);
                                 break;
                              }
                           }
                        }
                     }
                  } catch (Exception var17) {
                     System.err.println("Could not load mod list file");
                     var17.printStackTrace();
                  }

                  var29 = var23.iterator();

                  while(var29.hasNext()) {
                     var34 = (LoadedMod)var29.next();
                     addSortedMod(allMods, var34);
                  }
               }

               HashSet var24 = new HashSet();
               var29 = allMods.iterator();

               while(var29.hasNext()) {
                  var34 = (LoadedMod)var29.next();
                  if (var34.isEnabled()) {
                     if (var24.contains(var34.id)) {
                        var34.listData.enabled = false;
                        GameLog.warn.println("Disabled duplicate mod " + var34.id + " from " + var34.loadLocation.name() + " as one is already enabled");
                     } else {
                        var24.add(var34.id);
                        enabledMods.add(var34);
                     }
                  } else {
                     notEnabledMods.add(var34);
                  }
               }
            }

            saveModListSettings((List)allMods.stream().map((var0x) -> {
               return var0x.listData;
            }).collect(Collectors.toList()));
            GameLoadingScreen.addLog(Localization.translate("loading", "loadedmods", "mods", (Object)enabledMods.size()));
         }

         enabledMods = Collections.unmodifiableList(enabledMods);
         notEnabledMods = Collections.unmodifiableList(notEnabledMods);
         allMods = Collections.unmodifiableList(allMods);
         generateModsHash();
         if (!getEnabledMods().isEmpty()) {
            ComputedValue var22 = new ComputedValue(ByteBuddyAgent::install);
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "loadingmods"));
            Iterator var27 = getEnabledMods().iterator();

            LoadedMod var31;
            while(var27.hasNext()) {
               var31 = (LoadedMod)var27.next();
               var31.loadClasses(var22);
            }

            var27 = getEnabledMods().iterator();

            while(var27.hasNext()) {
               var31 = (LoadedMod)var27.next();
               var31.initSettings();
            }

            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "patchmods"));
            var27 = getEnabledMods().iterator();

            while(var27.hasNext()) {
               var31 = (LoadedMod)var27.next();
               var31.applyPatches(var22);
            }
         }

         hasLoadedMods = true;
      }
   }

   private static void loadMod(File var0, ModLoadLocation var1, File var2, SteamPublishedFileID var3) {
      try {
         if (!var0.exists()) {
            GameLog.warn.println("Could not find mod jar located at " + var0.getAbsolutePath());
            return;
         }

         if (!var0.getName().endsWith(".jar")) {
            GameLog.warn.println("Invalid mod jar located at " + var0.getAbsolutePath());
            return;
         }

         JarFile var4 = new JarFile(var0);

         try {
            ModInfoFile var5 = new ModInfoFile(var4);
            loadMod(new LoadedMod(var4, var5, var1, var2, var3));
         } catch (ModInfoNotFoundException var6) {
            logLoadError(var0.getName() + " did not contain a " + "mod.info" + " file.");
         }
      } catch (MalformedURLException var7) {
         logLoadError("Could not load mod " + var0.getName() + " url");
         System.err.println(var7.getMessage());
      } catch (IOException var8) {
         logLoadError("Could not load mod " + var0.getName() + " file");
         System.err.println(var8.getMessage());
      } catch (LinkageError var9) {
         logLoadError("Linkage error for " + var0.getName() + " mod");
         var9.printStackTrace();
      } catch (Exception var10) {
         logLoadError("Unknown error loading " + var0.getName() + " mod");
         var10.printStackTrace();
      }

   }

   protected static void logLoadError(String var0) {
      GameLoadingScreen.addLog(var0);
      System.err.println(var0);
   }

   private static void loadMod(LoadedMod var0) {
      if (allMods.stream().anyMatch((var1) -> {
         return var1.id.equals(var0.id);
      })) {
         GameLog.warn.println("Could not add mod " + var0.id + " from " + var0.loadLocation + " because another one with the same ID already exists");
      } else {
         System.out.println("Found mod: " + var0.name + " (" + var0.id + ", " + var0.version + ") from " + var0.loadLocation.name());
         addSortedMod(allMods, var0);
      }
   }

   public static void addSortedMod(List<LoadedMod> var0, LoadedMod var1) {
      int var2 = var0.size();

      for(int var3 = var0.size() - 1; var3 >= 0; --var3) {
         if (((LoadedMod)var0.get(var3)).dependsOn(var1) || ((LoadedMod)var0.get(var3)).optionalDependsOn(var1)) {
            var2 = var3;
         }
      }

      var0.add(var2, var1);
   }

   public static void addSortedNextMod(List<ModNextListData> var0, LoadedMod var1) {
      int var2 = var0.size();

      for(int var3 = var0.size() - 1; var3 >= 0; --var3) {
         if (((ModNextListData)var0.get(var3)).mod.dependsOn(var1) || ((ModNextListData)var0.get(var3)).mod.optionalDependsOn(var1)) {
            var2 = var3;
         }
      }

      var0.add(var2, new ModNextListData(var1, true));
   }

   public static List<LoadedMod> getEnabledMods() {
      return enabledMods;
   }

   public static List<LoadedMod> getNotEnabledMods() {
      return notEnabledMods;
   }

   public static List<LoadedMod> getAllMods() {
      return allMods;
   }

   public static List<ModNextListData> getAllModsSortedByCurrentList() {
      ArrayList var0 = new ArrayList(allMods.size());
      ArrayList var1 = new ArrayList(allMods);
      Iterator var2 = currentList.iterator();

      while(true) {
         while(var2.hasNext()) {
            ModListData var3 = (ModListData)var2.next();

            for(int var4 = 0; var4 < var1.size(); ++var4) {
               LoadedMod var5 = (LoadedMod)var1.get(var4);
               if (var3.matchesMod(var5)) {
                  var0.add(new ModNextListData(var5, var3.enabled));
                  var1.remove(var4);
                  break;
               }
            }
         }

         var2 = var1.iterator();

         while(var2.hasNext()) {
            LoadedMod var6 = (LoadedMod)var2.next();
            addSortedNextMod(var0, var6);
         }

         return var0;
      }
   }

   public static ArrayList<LoadedMod> getResponsibleMods(Iterable<? extends Throwable> var0, boolean var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = getEnabledMods().iterator();

      while(var3.hasNext()) {
         LoadedMod var4 = (LoadedMod)var3.next();
         Iterator var5 = var0.iterator();

         while(var5.hasNext()) {
            Throwable var6 = (Throwable)var5.next();
            if (var4.isResponsibleForError(var6)) {
               if (var1) {
                  var4.runError = true;
               }

               var2.add(var4);
            }
         }
      }

      return var2;
   }

   private static void generateModsHash() {
      modsHash = 0;
      Iterator var0 = getEnabledMods().iterator();

      while(var0.hasNext()) {
         LoadedMod var1 = (LoadedMod)var0.next();
         if (!var1.clientside) {
            modsHash = modsHash * 37 + var1.id.hashCode();
            modsHash = modsHash * 13 + var1.version.hashCode();
         }
      }

   }

   public static int getModsHash() {
      return modsHash;
   }

   public static boolean hasLoadedMods() {
      return hasLoadedMods;
   }

   public static String getModName(String var0) {
      LoadedMod var1 = (LoadedMod)getAllMods().stream().filter((var1x) -> {
         return var1x.id.equals(var0);
      }).findAny().orElse((Object)null);
      return var1 == null ? var0 : var1.name;
   }

   public static void saveModListSettings(List<ModListData> var0) {
      File var1 = new File(modsListFile());
      SaveData var2 = ModListData.getSaveList(var0);
      var2.saveScript(var1);
      currentList = var0;
   }

   public static boolean matchesCurrentMods(WorldSave var0) {
      WorldEntity var1 = var0.getWorldEntity();
      if (var1 == null) {
         return true;
      } else {
         return var1.lastMods == null ? true : matchesCurrentMods((List)var1.lastMods);
      }
   }

   public static boolean matchesCurrentMods(List<ModSaveInfo> var0) {
      List var1 = getEnabledMods();
      if (var0.size() != var1.size()) {
         return false;
      } else {
         for(int var2 = 0; var2 < var0.size(); ++var2) {
            ModSaveInfo var3 = (ModSaveInfo)var0.get(var2);
            LoadedMod var4 = (LoadedMod)var1.get(var2);
            if (!var3.id.equals(var4.id)) {
               return false;
            }

            if (!var3.version.equals(var4.version)) {
               return false;
            }
         }

         return true;
      }
   }
}
