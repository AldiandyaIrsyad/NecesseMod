package necesse.engine.modLoader;

import com.codedisaster.steamworks.SteamPublishedFileID;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import necesse.engine.GameLog;
import necesse.engine.modLoader.classes.ModClass;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTexture.GameTexture;

public class LoadedMod {
   private static final HashMap<String, LoadedClassEntry> loadedClasses = new HashMap();
   static LoadedMod runningMod = null;
   public final String id;
   public final String name;
   public final String version;
   public final String gameVersion;
   public final boolean clientside;
   public final String[] depends;
   public final String[] optionalDepends;
   public final String author;
   public final String description;
   public final Map<String, String> modInfo;
   private ModClasses classes;
   public final JarFile jarFile;
   public final ModLoadLocation loadLocation;
   public final File devModFolder;
   private boolean hasExampleModPackageClasses;
   public final SteamPublishedFileID workshopFileID;
   private boolean hasLoaded;
   private ModSettings settings;
   public GameTexture preview;
   public boolean initError;
   public boolean runError;
   private HashSet<ModdedSignature> editedSignatures = new HashSet();
   ModListData listData;

   public static LoadedMod getRunningMod() {
      return runningMod;
   }

   public static boolean isRunningModClientSide() {
      return runningMod != null && runningMod.clientside;
   }

   public LoadedMod(JarFile var1, ModInfoFile var2, ModLoadLocation var3, File var4, SteamPublishedFileID var5) {
      this.id = var2.id;
      this.name = var2.name;
      this.version = var2.version;
      this.gameVersion = var2.gameVersion;
      this.clientside = var2.clientside;
      this.author = var2.author;
      this.description = var2.description;
      this.depends = var2.depends;
      this.optionalDepends = var2.optionalDepends;
      this.modInfo = var2.extra;
      this.jarFile = var1;
      this.loadLocation = var3;
      this.workshopFileID = var5;
      this.devModFolder = var4;
      this.listData = new ModListData(this);
   }

   public boolean hasExampleModPackageClasses() {
      return this.hasExampleModPackageClasses;
   }

   public boolean validateDevFolder() {
      return validateDevFolderAndReturnJar(this.devModFolder) != null;
   }

   public static File validateDevFolderAndReturnJar(File var0) {
      if (var0.isDirectory()) {
         File[] var1 = var0.listFiles();
         if (var1 == null) {
            return null;
         } else {
            File var2 = null;
            File[] var3 = var1;
            int var4 = var1.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               File var6 = var3[var5];
               if (!var6.getName().equals("preview.png")) {
                  if (var6.isDirectory()) {
                     return null;
                  }

                  if (var6.isFile()) {
                     if (var2 != null) {
                        return null;
                     }

                     if (var6.getName().endsWith(".jar")) {
                        var2 = var6;
                     }
                  }
               }
            }

            return var2;
         }
      } else {
         return null;
      }
   }

   public void loadClasses(ComputedValue<Instrumentation> var1) throws ModLoadException {
      try {
         boolean var2 = false;
         boolean var3 = false;
         boolean var4 = false;
         ((Instrumentation)var1.get()).appendToSystemClassLoaderSearch(this.jarFile);
         ClassLoader var5 = ClassLoader.getSystemClassLoader();
         Enumeration var6 = this.jarFile.entries();
         this.classes = new ModClasses();

         while(true) {
            JarEntry var7;
            String var8;
            do {
               do {
                  if (!var6.hasMoreElements()) {
                     Iterator var18 = this.classes.getAllClasses().iterator();

                     while(var18.hasNext()) {
                        ModClass var19 = (ModClass)var18.next();
                        var19.finalizeLoading(this);
                     }

                     if (var2) {
                        if (this.loadLocation == ModLoadLocation.DEVELOPMENT_FOLDER) {
                           GameLog.warn.println("Your mod \"" + this.name + "\" contains classes inside examplemod package. To upload your mod, you have to move them to a different and unique package.");
                        } else {
                           GameLog.warn.println(this.name + " contains classes inside examplemod package. This will likely not be allowed in future versions.");
                        }
                     }

                     if (var3) {
                        GameLog.warn.println(this.name + " contains classes inside necesse package. This will likely not be allowed in future versions.");
                     }

                     if (var4) {
                        GameLog.warn.println(this.name + " contains classes not inside any package. This will likely not be allowed in future versions.");
                     }

                     this.hasLoaded = true;
                     return;
                  }

                  var7 = (JarEntry)var6.nextElement();
                  var8 = var7.getName();
               } while(var7.isDirectory());
            } while(!var8.endsWith(".class"));

            String var9 = var8.substring(0, var8.length() - 6);
            var9 = var9.replace("/", ".");
            if (var9.startsWith("examplemod")) {
               var2 = true;
               if (this.loadLocation == ModLoadLocation.DEVELOPMENT_FOLDER) {
                  this.hasExampleModPackageClasses = true;
               }
            }

            if (var9.startsWith("necesse")) {
               var3 = true;
            }

            if (!var9.contains(".")) {
               var4 = true;
            }

            LoadedClassEntry var10 = (LoadedClassEntry)loadedClasses.get(var9);
            if (var10 != null && !var10.isSame(var7)) {
               throw new ModLoadException(this, "Another mod (" + var10.mod.id + ") has already loaded a class from " + var9 + ". Contact the mod author to get this fixed.");
            }

            Class var11 = var5.loadClass(var9);
            loadedClasses.put(var9, new LoadedClassEntry(this, var7));
            this.editedSignatures.add(new ModdedSignature(var9, (String)null));
            Iterator var12 = this.classes.getAllClasses().iterator();

            while(var12.hasNext()) {
               ModClass var13 = (ModClass)var12.next();
               if (var13.shouldRegisterModClass(var11)) {
                  var13.registerModClass(this, var11);
               }
            }
         }
      } catch (ModLoadException var14) {
         throw var14;
      } catch (ClassNotFoundException var15) {
         throw new ModLoadException(this, "Could not load mod " + this.id + " class", var15);
      } catch (LinkageError var16) {
         throw new ModLoadException(this, "Linkage error for " + this.id + " mod", var16);
      } catch (Exception var17) {
         throw new ModLoadException(this, "Unknown error loading " + this.id + " mod", var17);
      }
   }

   public void applyPatches(ComputedValue<Instrumentation> var1) {
      this.classes.patchClasses.applyPatches(var1, this.editedSignatures);
   }

   public void loadPreviewImage() {
      ZipEntry var1 = this.jarFile.getEntry("resources/preview.png");
      if (var1 != null) {
         try {
            byte[] var2 = GameUtils.loadInputStream(this.jarFile.getInputStream(var1));
            this.preview = new GameTexture(this.id + " preview", var2);
            this.preview.makeFinal();
         } catch (IllegalArgumentException var3) {
            System.err.println("Failed loading of mod " + this.id + " preview image: " + var3.getMessage());
         } catch (IOException var4) {
            System.err.println("IOException: Could not load mod " + this.id + " preview image");
            var4.printStackTrace();
         }
      }

   }

   public void preInit() {
      this.classes.entry.preInit();
   }

   public void init() {
      this.classes.entry.init();
   }

   public void initResources() {
      this.classes.entry.initResources();
   }

   public void postInit() {
      this.classes.entry.postInit();
   }

   public void initSettings() {
      this.settings = this.classes.entry.initSettings();
   }

   public ModSettings getSettings() {
      return this.settings;
   }

   public void dispose() {
      if (this.preview != null) {
         this.preview.delete();
      }

      if (this.classes != null) {
         this.classes.entry.dispose();
      }

   }

   public boolean dependsOn(LoadedMod var1) {
      return this.arrayContains(this.depends, var1.id);
   }

   public boolean optionalDependsOn(LoadedMod var1) {
      return this.arrayContains(this.optionalDepends, var1.id);
   }

   private boolean arrayContains(String[] var1, String var2) {
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var6.equals(var2)) {
            return true;
         }
      }

      return false;
   }

   public boolean isEnabled() {
      return this.listData.enabled;
   }

   public boolean hasLoaded() {
      return this.hasLoaded;
   }

   public String getModDebugString() {
      return this.id + " (v. " + this.version + ")";
   }

   public String getModNameString() {
      return this.name + " (v. " + this.version + ")";
   }

   public boolean isResponsibleForError(Throwable var1) {
      if (var1 == null) {
         return false;
      } else {
         StackTraceElement[] var2 = var1.getStackTrace();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement var5 = var2[var4];
            if (this.hasEdited(var5)) {
               return true;
            }
         }

         Throwable[] var6 = var1.getSuppressed();
         var3 = var6.length;
         byte var7 = 0;
         if (var7 < var3) {
            Throwable var8 = var6[var7];
            return this.isResponsibleForError(var8);
         } else {
            return this.isResponsibleForError(var1.getCause());
         }
      }
   }

   public boolean hasEdited(StackTraceElement var1) {
      Iterator var2 = this.editedSignatures.iterator();

      ModdedSignature var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (ModdedSignature)var2.next();
      } while(!var3.matches(var1));

      return true;
   }

   private static class LoadedClassEntry {
      public final LoadedMod mod;
      public long size;

      public LoadedClassEntry(LoadedMod var1, JarEntry var2) {
         this.mod = var1;
         this.size = var2.getSize();
      }

      public boolean isSame(JarEntry var1) {
         return this.size == var1.getSize();
      }
   }
}
