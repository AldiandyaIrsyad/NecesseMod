package necesse.engine.modLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;

public class ModInfoFile {
   public static final String modInfoPath = "mod.info";
   public final String id;
   public final String name;
   public final String version;
   public final String gameVersion;
   public final String author;
   public final String description;
   public final boolean clientside;
   public final String[] depends;
   public final String[] optionalDepends;
   public final Map<String, String> extra;

   public ModInfoFile(JarFile var1) throws IOException, ModInfoNotFoundException {
      this(var1.getInputStream((ZipEntry)var1.stream().filter((var0) -> {
         return var0.getName().equals("mod.info");
      }).findFirst().orElseThrow(ModInfoNotFoundException::new)));
   }

   public ModInfoFile(List<GameFileEntry> var1) throws IOException, ModInfoNotFoundException {
      this(((GameFileEntry)var1.stream().filter((var0) -> {
         return var0.getPath().equals("mod.info");
      }).findFirst().orElseThrow(ModInfoNotFoundException::new)).getFileInputStream());
   }

   public ModInfoFile(InputStream var1) throws IOException {
      this(new String(GameUtils.loadInputStream(var1)));
   }

   public ModInfoFile(String var1) {
      this(new LoadData(var1));
   }

   public ModInfoFile(LoadData var1) {
      this.id = this.loadUnsafeString(var1, "id");
      if (this.id.length() > 40) {
         throw new IllegalArgumentException("Mod id cannot be longer than 40 characters");
      } else if (!this.id.matches("[a-z0-9.-]+")) {
         throw new IllegalArgumentException("Mod id includes illegal characters");
      } else {
         this.name = this.loadSafeString(var1, "name");
         if (this.name.length() > 80) {
            throw new IllegalArgumentException("Mod name cannot be longer than 80 characters");
         } else if (!this.name.matches("[ a-zA-Z0-9.!:-]+")) {
            throw new IllegalArgumentException("Mod name includes illegal characters");
         } else {
            this.version = this.loadUnsafeString(var1, "version");
            if (this.version.length() > 20) {
               throw new IllegalArgumentException("Mod version cannot be longer than 20 characters");
            } else if (!this.version.matches("\\d+((\\.[\\d]+)+)?")) {
               throw new IllegalArgumentException("Mod version uses illegal format");
            } else {
               this.gameVersion = this.loadUnsafeString(var1, "gameVersion");
               this.author = this.loadSafeString(var1, "author");
               this.description = this.loadSafeString(var1, "description");
               this.clientside = var1.getBoolean("clientside", false, false);
               this.depends = this.loadOptionalArray(var1, "dependencies");
               this.optionalDepends = this.loadOptionalArray(var1, "optionalDependencies");
               HashMap var2 = new HashMap();
               Iterator var3 = var1.getLoadData().iterator();

               while(var3.hasNext()) {
                  LoadData var4 = (LoadData)var3.next();
                  if (var4.isData()) {
                     String var5 = var4.getData();
                     if (var5.startsWith("\"") && var5.endsWith("\"")) {
                        var5 = var5.substring(1, var5.length() - 1);
                     }

                     var5 = SaveComponent.fromSafeData(var5);
                     var2.put(var4.getName(), var5);
                  }
               }

               var2.remove("id");
               var2.remove("name");
               var2.remove("version");
               var2.remove("gameVersion");
               var2.remove("author");
               var2.remove("description");
               var2.remove("dependencies");
               var2.remove("optionalDependencies");
               var2.remove("clientside");
               this.extra = Collections.unmodifiableMap(var2);
            }
         }
      }
   }

   public static void saveModInfoFile(File var0, HashMap<String, String> var1) {
      SaveData var2 = new SaveData("");
      String var3 = (String)var1.get("id");
      if (var3 != null && !var3.isEmpty()) {
         var2.addUnsafeString("id", var3);
      }

      var1.remove("id");
      String var4 = (String)var1.get("name");
      if (var4 != null && !var4.isEmpty()) {
         var2.addSafeString("name", var4);
      }

      var1.remove("name");
      String var5 = (String)var1.get("version");
      if (var5 != null && !var5.isEmpty()) {
         var2.addUnsafeString("version", var5);
      }

      var1.remove("version");
      String var6 = (String)var1.get("gameVersion");
      if (var6 != null && !var6.isEmpty()) {
         var2.addUnsafeString("gameVersion", var6);
      }

      var1.remove("gameVersion");
      String var7 = (String)var1.get("author");
      if (var7 != null && !var7.isEmpty()) {
         var2.addSafeString("author", var7);
      }

      var1.remove("author");
      String var8 = (String)var1.get("description");
      if (var8 != null && !var8.isEmpty()) {
         var2.addSafeString("description", var8);
      }

      var1.remove("description");
      String var9 = (String)var1.get("clientside");
      if (var9 != null && !var9.isEmpty()) {
         var2.addUnsafeString("clientside", var9);
      }

      var1.remove("clientside");
      String var10 = (String)var1.get("dependencies");
      if (var10 != null && !var10.isEmpty()) {
         var2.addUnsafeString("dependencies", var10);
      }

      var1.remove("dependencies");
      String var11 = (String)var1.get("optionalDependencies");
      if (var11 != null && !var11.isEmpty()) {
         var2.addUnsafeString("optionalDependencies", var11);
      }

      var1.remove("optionalDependencies");
      Iterator var12 = var1.entrySet().iterator();

      while(var12.hasNext()) {
         Map.Entry var13 = (Map.Entry)var12.next();
         String var14 = (String)var13.getKey();
         String var15 = (String)var13.getValue();
         if (!var15.isEmpty()) {
            if (var15.startsWith("unsafe:")) {
               var2.addSafeString(var14, var15.substring("unsafe:".length()));
            } else {
               var2.addSafeString(var14, var15);
            }
         }
      }

      var2.saveScript(var0);
   }

   private String loadUnsafeString(LoadData var1, String var2) {
      try {
         String var3 = var1.getUnsafeString(var2).trim();
         if (var3.isEmpty()) {
            throw new IllegalStateException();
         } else {
            return var3;
         }
      } catch (NullPointerException var4) {
         throw new IllegalArgumentException("mod.info missing " + var2 + " data");
      } catch (IllegalStateException var5) {
         throw new IllegalArgumentException("mod.info " + var2 + " cannot be empty");
      } catch (Exception var6) {
         throw new IllegalArgumentException("Could not load mod info " + var2);
      }
   }

   private String loadSafeString(LoadData var1, String var2) {
      try {
         String var3 = var1.getSafeString(var2).trim();
         if (var3.isEmpty()) {
            throw new IllegalStateException();
         } else {
            return var3;
         }
      } catch (NullPointerException var4) {
         throw new IllegalArgumentException("mod.info missing " + var2 + " data");
      } catch (IllegalStateException var5) {
         throw new IllegalArgumentException("mod.info " + var2 + " cannot be empty");
      } catch (Exception var6) {
         throw new IllegalArgumentException("Could not load mod info " + var2);
      }
   }

   private String[] loadOptionalArray(LoadData var1, String var2) {
      String var3 = var1.getUnsafeString(var2, "[]", false);
      if (var3.startsWith("[") && var3.endsWith("]")) {
         var3 = var3.substring(1, var3.length() - 1);
         return this.removeEmptyStrings(var3.split(","));
      } else {
         return new String[]{var3.trim()};
      }
   }

   private String[] removeEmptyStrings(String[] var1) {
      LinkedList var2 = new LinkedList();
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var6 = var6.trim();
         if (!var6.isEmpty()) {
            var2.add(var6);
         }
      }

      return (String[])var2.toArray(new String[0]);
   }
}
