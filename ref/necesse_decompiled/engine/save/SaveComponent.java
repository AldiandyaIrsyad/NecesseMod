package necesse.engine.save;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.PerformanceTimerManager;
import necesse.engine.tickManager.PerformanceWrapper;
import necesse.engine.util.GameUtils;
import necesse.engine.world.WorldFile;

public class SaveComponent {
   public static final int TYPE_UNKNOWN = 0;
   public static final int TYPE_DATA = 1;
   public static final int TYPE_ARRAY = 2;
   public static final char[][] escapeUnsafeCharacters = new char[][]{{'{', '{'}, {'}', '}'}, {'[', '['}, {']', ']'}, {'"', '"'}, {'\'', '\''}, {'/', '/'}, {',', ','}, {'=', '='}, {'\n', 'n'}, {'\r', 'r'}, {'\t', 't'}, {'\b', 'b'}, {'\\', '\\'}};
   private PerformanceTimerManager performanceManager;
   private static final char[][] dataSplitters = new char[][]{{'[', ']'}, {'"', '"'}, {'\'', '\''}};
   private String name;
   private String data;
   private String comment;
   private List<SaveComponent> components;
   private int type;
   private boolean isCompiled;
   private String script;
   public static Charset[] decodeCharsets;

   public static String toSafeData(String var0) {
      for(int var1 = escapeUnsafeCharacters.length - 1; var1 >= 0; --var1) {
         var0 = var0.replace(Character.toString(escapeUnsafeCharacters[var1][0]), "\\" + escapeUnsafeCharacters[var1][1]);
      }

      return var0;
   }

   public static String fromSafeData(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         char var2 = var0.charAt(var1);
         if (var2 == '\\' && var1 < var0.length() - 1) {
            char var3 = var0.charAt(var1 + 1);
            char var4 = 0;
            char[][] var5 = escapeUnsafeCharacters;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               char[] var8 = var5[var7];
               if (var8[1] == var3) {
                  var4 = var8[0];
                  break;
               }
            }

            if (var4 != 0) {
               var0 = var0.substring(0, var1) + var4 + var0.substring(var1 + 2);
            } else {
               ++var1;
            }
         }
      }

      return var0;
   }

   public SaveComponent(String var1) {
      this.name = var1;
      this.components = new ArrayList();
      this.comment = "";
      this.type = 2;
      this.isCompiled = true;
   }

   public SaveComponent(String var1, String var2) {
      this.name = var1;
      this.components = new ArrayList();
      this.comment = var2;
      this.type = 2;
      this.isCompiled = true;
   }

   private SaveComponent(String var1, Object var2, String var3) {
      this.name = var1;
      this.data = var2.toString();
      this.comment = var3;
      this.type = 1;
      this.isCompiled = true;
   }

   public SaveComponent(PerformanceTimerManager var1, String var2, boolean var3, boolean var4) {
      this.performanceManager = var1;
      this.script = var2;
      this.isCompiled = false;
      this.comment = "";
      this.type = 0;
      if (var3) {
         this.compile(var4);
      }

   }

   public String getName() {
      if (!this.isCompiled) {
         this.compile(false);
      }

      return this.name;
   }

   public String getData() {
      if (!this.isCompiled) {
         this.compile(false);
      }

      return this.data;
   }

   public int getType() {
      if (!this.isCompiled) {
         this.compile(false);
      }

      return this.type;
   }

   public List<SaveComponent> getComponents() {
      if (!this.isCompiled) {
         this.compile(false);
      }

      return this.components;
   }

   public boolean isEmpty() {
      return this.type == 2 && this.components.size() == 0;
   }

   public void addData(String var1, Object var2, String var3) {
      this.components.add(new SaveComponent(var1, var2, var3));
   }

   public void addData(String var1, Object var2) {
      this.addData(var1, var2, "");
   }

   public void addData(String var1, Object var2, String var3, int var4) {
      this.components.add(var4, new SaveComponent(var1, var2, var3));
   }

   public void addData(String var1, Object var2, int var3) {
      this.addData(var1, var2, "", var3);
   }

   public void addComponent(SaveComponent var1) {
      this.components.add(var1);
   }

   public List<SaveComponent> getComponentsByName(String var1) {
      if (!this.isCompiled) {
         this.compile(false);
      }

      return (List)this.getComponents().stream().filter((var1x) -> {
         return var1x.getName().equals(var1);
      }).collect(Collectors.toList());
   }

   public SaveComponent getFirstComponentByName(String var1) {
      if (!this.isCompiled) {
         this.compile(false);
      }

      return (SaveComponent)this.getComponents().stream().filter((var1x) -> {
         return var1x.getName().equals(var1);
      }).findFirst().orElse((Object)null);
   }

   public String getFirstDataByName(String var1) {
      return this.getFirstComponentByName(var1).getData();
   }

   public boolean hasComponentByName(String var1) {
      return this.getFirstComponentByName(var1) != null;
   }

   public void removeComponentsByName(String var1) {
      if (!this.isCompiled) {
         this.compile(false);
      }

      for(int var2 = 0; var2 < this.components.size(); ++var2) {
         if (((SaveComponent)this.components.get(var2)).getName().equals(var1)) {
            this.components.remove(var2);
            --var2;
         }
      }

   }

   public boolean removeFirstComponentByName(String var1) {
      if (!this.isCompiled) {
         this.compile(false);
      }

      for(int var2 = 0; var2 < this.components.size(); ++var2) {
         if (((SaveComponent)this.components.get(var2)).getName().equals(var1)) {
            this.components.remove(var2);
            return true;
         }
      }

      return false;
   }

   private String getScript(String var1, String var2, boolean var3, Set<SaveComponent> var4) {
      if (var4.contains(this)) {
         throw new IllegalArgumentException("Script recursive");
      } else {
         var4.add(this);
         String var5 = var3 ? "" : "\n";
         String var6 = var3 ? "" : "\t";
         String var7 = var3 ? "=" : " = ";
         String var8 = var3 ? "" : (this.comment.isEmpty() ? "" : " // " + this.comment);
         String var9 = this.getName();
         if (!this.isCompiled) {
            return var1 + this.script + var2 + var5;
         } else {
            StringBuilder var10 = new StringBuilder();
            if (this.type == 1) {
               String var11 = this.getData();
               if (var9.equals("")) {
                  var10.append(var1).append(var11).append(var2).append(var3 ? "" : var8).append(var5);
               } else {
                  var10.append(var1).append(var9).append(var7).append(var11).append(var2).append(var3 ? "" : var8).append(var5);
               }
            } else {
               var10.append(var1).append(var9).append(var9.equals("") ? "" : var7).append("{").append(var8).append(var5);

               for(int var12 = 0; var12 < this.components.size(); ++var12) {
                  if (((SaveComponent)this.components.get(var12)).getType() == 2) {
                     var10.append(((SaveComponent)this.components.get(var12)).getScript(var1 + var6, var2, var3, var4));
                     if (var12 == this.components.size() - 1) {
                        var10 = new StringBuilder(var10.substring(0, var10.lastIndexOf(",")) + var10.substring(var10.lastIndexOf(",") + 1, var10.length()));
                     }
                  } else {
                     var10.append(((SaveComponent)this.components.get(var12)).getScript(var1 + var6, var12 == this.components.size() - 1 ? "" : var2, var3, var4));
                  }
               }

               var10.append(var1).append("}").append(var2).append(var5);
            }

            return var10.toString();
         }
      }
   }

   public String getScript(boolean var1) {
      String var2 = this.getScript("", ",", var1, new HashSet());
      int var3 = var2.lastIndexOf(",");
      if (var3 != -1) {
         var2 = var2.substring(0, var3);
      }

      return var2;
   }

   public String getScript() {
      return this.getScript(false);
   }

   public void printScript() {
      System.out.println(this.getScript());
   }

   public void printDebug() {
      this.printDebug("");
   }

   private void printDebug(String var1) {
      switch (this.getType()) {
         case 1:
            System.out.println(var1 + "[DATA] \"" + this.getName() + "\" = \"" + this.getData() + "\"");
            break;
         case 2:
            System.out.println(var1 + "[ARRAY] \"" + this.getName() + "\" = {");
            Iterator var2 = this.getComponents().iterator();

            while(var2.hasNext()) {
               SaveComponent var3 = (SaveComponent)var2.next();
               var3.printDebug(var1 + "\t");
            }

            System.out.println(var1 + "}");
            break;
         default:
            System.out.println(var1 + "[UNKNOWN] \"" + this.getName() + "\" = \"" + this.getData() + "\"");
      }

   }

   public static void saveScriptRaw(WorldFile var0, SaveComponent var1, boolean var2) throws IOException {
      byte[] var3 = var1.getScript(var2).getBytes();
      if (var2) {
         var3 = GameUtils.compressData(var3);
      }

      var0.write(var3);
   }

   public static void saveScript(WorldFile var0, SaveComponent var1, boolean var2) {
      try {
         saveScriptRaw(var0, var1, var2);
      } catch (IllegalArgumentException var4) {
         System.err.println("Could not create folder to save script: " + var0.toString());
         var4.printStackTrace();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public static void saveScript(WorldFile var0, SaveComponent var1) {
      saveScript(var0, var1, false);
   }

   public void saveScript(WorldFile var1) {
      saveScript(var1, this);
   }

   public static SaveComponent loadScriptRaw(WorldFile var0, boolean var1) throws IOException, DataFormatException {
      return loadScriptRaw((PerformanceTimerManager)null, (WorldFile)var0, var1);
   }

   public static SaveComponent loadScriptRaw(PerformanceTimerManager var0, WorldFile var1, boolean var2) throws IOException, DataFormatException {
      Charset[] var3 = decodeCharsets;
      int var4 = var3.length;
      int var5 = 0;

      while(var5 < var4) {
         Charset var6 = var3[var5];

         try {
            if (var2) {
               byte[] var7 = GameUtils.decompressData(var1.read());
               return loadScript(new String(var7, var6));
            }

            return loadScript(var0, var1.reader(var6));
         } catch (MalformedInputException var8) {
            ++var5;
         }
      }

      throw new RuntimeException("Could not load file " + var1.getFileName() + ": Unknown encoding");
   }

   public static SaveComponent loadScript(WorldFile var0, boolean var1) {
      return loadScript((PerformanceTimerManager)null, (WorldFile)var0, var1);
   }

   public static SaveComponent loadScript(PerformanceTimerManager var0, WorldFile var1, boolean var2) {
      try {
         return loadScriptRaw(var0, var1, var2);
      } catch (IOException var4) {
         var4.printStackTrace();
      } catch (DataFormatException var5) {
         System.err.println("Could not decompress script file: " + var1.toString());
         var5.printStackTrace();
      }

      return null;
   }

   public static SaveComponent loadScript(WorldFile var0) {
      return loadScript((PerformanceTimerManager)null, (WorldFile)var0);
   }

   public static SaveComponent loadScript(PerformanceTimerManager var0, WorldFile var1) {
      return loadScript(var0, var1, false);
   }

   public static void saveScriptRaw(File var0, SaveComponent var1, boolean var2) throws IOException {
      byte[] var3 = var1.getScript(var2).getBytes();
      if (var2) {
         var3 = GameUtils.compressData(var3);
      }

      GameUtils.saveByteFile(var3, var0);
   }

   public static void saveScript(File var0, SaveComponent var1, boolean var2) {
      try {
         saveScriptRaw(var0, var1, var2);
      } catch (IllegalArgumentException var4) {
         System.err.println("Could not create folder to save script: " + var0.getPath());
         var4.printStackTrace();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public static void saveScript(File var0, SaveComponent var1) {
      saveScript(var0, var1, false);
   }

   public void saveScript(File var1) {
      saveScript(var1, this);
   }

   public static SaveComponent loadScriptRaw(File var0, boolean var1) throws IOException, DataFormatException {
      return loadScriptRaw((PerformanceTimerManager)null, (File)var0, var1);
   }

   public static SaveComponent loadScriptRaw(PerformanceTimerManager var0, File var1, boolean var2) throws IOException, DataFormatException {
      Charset[] var3 = decodeCharsets;
      int var4 = var3.length;
      int var5 = 0;

      while(var5 < var4) {
         Charset var6 = var3[var5];

         try {
            byte[] var7 = GameUtils.loadByteFile(var1);
            if (var2) {
               var7 = GameUtils.decompressData(var7);
               return loadScript(new String(var7, var6));
            }

            InputStreamReader var8 = new InputStreamReader(new ByteArrayInputStream(var7), var6);
            BufferedReader var9 = new BufferedReader(var8);
            return loadScript(var0, var9);
         } catch (MalformedInputException var10) {
            ++var5;
         }
      }

      throw new RuntimeException("Could not load file " + var1.getName() + ": Unknown encoding");
   }

   public static SaveComponent loadScript(File var0, boolean var1) {
      return loadScript((PerformanceTimerManager)null, (File)var0, var1);
   }

   public static SaveComponent loadScript(PerformanceTimerManager var0, File var1, boolean var2) {
      try {
         return loadScriptRaw(var0, var1, var2);
      } catch (IOException var4) {
         var4.printStackTrace();
      } catch (DataFormatException var5) {
         System.err.println("Could not decompress script file: " + var1.getPath());
         var5.printStackTrace();
      }

      return null;
   }

   public static SaveComponent loadScript(File var0) {
      return loadScript((PerformanceTimerManager)null, (File)var0);
   }

   public static SaveComponent loadScript(PerformanceTimerManager var0, File var1) {
      return loadScript(var0, var1, false);
   }

   public static SaveComponent loadScript(String var0) {
      return loadScript((PerformanceTimerManager)null, (String)var0);
   }

   public static SaveComponent loadScript(PerformanceTimerManager var0, String var1) {
      try {
         return loadScript(var0, new BufferedReader(new StringReader(var1)));
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   private static SaveComponent loadScript(PerformanceTimerManager var0, BufferedReader var1) throws IOException {
      StringBuilder var2 = new StringBuilder();

      String var3;
      while((var3 = var1.readLine()) != null) {
         int var4 = var3.indexOf("//");
         if (var4 != -1) {
            var3 = var3.substring(0, var4);
         }

         var3 = var3.trim();
         if (var3.length() != 0) {
            var2.append(var3);
            if (var3.charAt(var3.length() - 1) != ',') {
               var2.append(",");
            }
         }
      }

      var1.close();
      return new SaveComponent(var0, var2.toString(), true, false);
   }

   public static int getSectionStop(String var0, char var1, char var2, int var3) {
      int var4 = indexOf(var0, var1, var3);
      if (var4 == -1) {
         throw new SaveSyntaxException("SYNTAX ERROR: Missing section start \"" + var1 + "\" at " + var0);
      } else {
         int var5 = 1;

         while(true) {
            while(true) {
               int var6 = indexOf(var0, var2, var4 + 1);
               if (var6 == -1) {
                  throw new SaveSyntaxException("SYNTAX ERROR: Missing section stop \"" + var2 + "\" at " + var0);
               }

               int var7 = indexOf(var0, var1, var4 + 1);
               if (var7 != -1 && var7 <= var6) {
                  ++var5;
                  var4 = var7;
               } else {
                  --var5;
                  if (var5 == 0) {
                     return var6;
                  }

                  var4 = var6;
               }
            }
         }
      }
   }

   public static int indexOf(String var0, char var1, int var2) {
      int var3 = var0.indexOf(var1, var2);
      if (var3 >= 0) {
         return isCharEscaped(var0, var3) ? indexOf(var0, var1, var3 + 1) : var3;
      } else {
         return -1;
      }
   }

   private static boolean isCharEscaped(String var0, int var1) {
      boolean var2;
      for(var2 = false; var1 > 0 && var0.charAt(var1 - 1) == '\\'; --var1) {
         var2 = !var2;
      }

      return var2;
   }

   private static int indexOfValid(String var0, char var1, int var2) {
      int var3 = var0.indexOf(var1, var2);
      if (var3 >= 0) {
         return isCharEscaped(var0, var3) ? indexOfValid(var0, var1, var3 + 1) : var3;
      } else {
         throw new SaveSyntaxException("SYNTAX ERROR: Missing valid data section \"" + var1 + "\" character at " + var0);
      }
   }

   private static int getDataSectionStop(String var0, char var1, char var2, int var3) {
      int var4 = indexOfValid(var0, var1, var3);
      if (var4 == -1) {
         throw new SaveSyntaxException("SYNTAX ERROR: Missing start data section \"" + var1 + "\" character at " + var0);
      } else {
         int var5 = indexOfValid(var0, var2, var4 + 1);
         if (var5 == -1) {
            throw new SaveSyntaxException("SYNTAX ERROR: Missing end data section \"" + var2 + "\" character at " + var0);
         } else {
            return var5 + 1;
         }
      }
   }

   private void compile(boolean var1) {
      if (!this.isCompiled) {
         PerformanceWrapper var2 = Performance.wrapTimer(this.performanceManager, "getType");
         int var3 = indexOf(this.script, '{', 0);
         int var4 = indexOf(this.script, ',', 0);
         var2.end();
         if (var3 != -1 && (var4 == -1 || var4 >= var3)) {
            PerformanceWrapper var5 = Performance.wrapTimer(this.performanceManager, "parseArrayType");
            this.type = 2;
            this.components = new ArrayList();
            PerformanceWrapper var6 = Performance.wrapTimer(this.performanceManager, "contentDefine");
            int var7 = indexOf(this.script, '=', 0);
            int var8 = getSectionStop(this.script, '{', '}', 0);
            String var9 = this.script.substring(var3 + 1, var8);
            if (var7 != -1 && var7 < var3) {
               this.name = this.script.substring(0, var7).trim();
            } else {
               this.name = this.script.substring(0, var3).trim();
            }

            var6.end();
            int var10 = 0;

            while(true) {
               PerformanceWrapper var11 = Performance.wrapTimer(this.performanceManager, "arrayStart");
               var3 = indexOf(var9, '{', var10);
               var4 = indexOf(var9, ',', var10);
               var11.end();
               PerformanceWrapper var23;
               if (var3 != -1 && var4 > var3) {
                  var23 = Performance.wrapTimer(this.performanceManager, "contentEnd");
                  var4 = indexOf(var9, ',', getSectionStop(var9, '{', '}', var10));
                  var23.end();
               } else {
                  int var12 = -1;
                  int var13 = -1;
                  char var14 = '[';
                  char var15 = ']';
                  PerformanceWrapper var16 = Performance.wrapTimer(this.performanceManager, "dataSplitters");
                  char[][] var17 = dataSplitters;
                  int var18 = var17.length;
                  int var19 = 0;

                  while(true) {
                     if (var19 >= var18) {
                        var16.end();
                        if (var12 != -1 && var13 != -1 && var4 > var12) {
                           PerformanceWrapper var28 = Performance.wrapTimer(this.performanceManager, "contentEnd");
                           var4 = indexOf(var9, ',', getDataSectionStop(var9, var14, var15, var10));
                           var28.end();
                        }
                        break;
                     }

                     char[] var20 = var17[var19];
                     int var21 = indexOf(var9, var20[0], var10);
                     int var22 = indexOf(var9, var20[1], var10);
                     if (var12 == -1 || var21 != -1 && var22 != -1 && var21 < var12) {
                        var12 = var21;
                        var13 = var22;
                        var14 = var20[0];
                        var15 = var20[1];
                     }

                     ++var19;
                  }
               }

               var23 = Performance.wrapTimer(this.performanceManager, "trimEnd1");
               if (var4 == -1) {
                  var4 = var9.length();
               }

               String var24 = var9.substring(var10, var4).trim();
               var23.end();
               if (var24.length() != 0) {
                  var5.end();
                  this.components.add(new SaveComponent(this.performanceManager, var24, var1, var1));
                  var5 = Performance.wrapTimer(this.performanceManager, "parseArrayType");
               }

               if (var4 == var9.length()) {
                  var5.end();
                  break;
               }

               PerformanceWrapper var25 = Performance.wrapTimer(this.performanceManager, "trimEnd2");
               var10 = var4 + 1;
               var25.end();
               PerformanceWrapper var26 = Performance.wrapTimer(this.performanceManager, "trimEnd3");

               for(int var27 = var9.length(); var10 < var27 && var9.charAt(var10) <= ' '; ++var10) {
               }

               var26.end();
            }
         } else {
            Performance.record(this.performanceManager, "parseDataType", () -> {
               this.type = 1;
               this.components = new ArrayList();
               int var1 = indexOf(this.script, '=', 0);
               boolean var2 = this.script.endsWith(",") && !isCharEscaped(this.script, this.script.length() - 1);
               if (var1 == -1) {
                  this.name = "";
                  this.data = this.script.substring(0, this.script.length() - (var2 ? 1 : 0));
               } else {
                  this.name = this.script.substring(0, var1).trim();
                  this.data = this.script.substring(var1 + 1, this.script.length() - (var2 ? 1 : 0)).trim();
               }

            });
         }

         this.isCompiled = true;
         this.script = null;
      }
   }

   static {
      decodeCharsets = new Charset[]{Charset.defaultCharset(), StandardCharsets.ISO_8859_1};
   }
}
