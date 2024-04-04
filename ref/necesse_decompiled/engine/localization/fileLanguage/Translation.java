package necesse.engine.localization.fileLanguage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameUtils;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeItemParser;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;

public class Translation {
   public static String MISSING_TRANSLATION_PREPEND = "MISSING_TRANSLATION:";
   public static String SAME_TRANSLATION_PREPEND = "SAME_TRANSLATION:";
   public final String fileName;
   private Translation compareCoverage;
   private int totalUniqueTranslations;
   private HashMap<String, LinkedList<LineData>> linesBeforeCategory;
   private HashMap<String, LinkedList<LineData>> linesBeforeTranslation;
   private HashMap<String, LinkedList<LineData>> linesAfterCategory;
   private HashMap<String, LinkedList<LineData>> linesAfterTranslation;
   private ArrayList<LineData> lines;
   private HashMap<String, TranslationCategory> categories;

   public Translation(String var1, Translation var2) {
      this.linesBeforeCategory = new HashMap();
      this.linesBeforeTranslation = new HashMap();
      this.linesAfterCategory = new HashMap();
      this.linesAfterTranslation = new HashMap();
      this.lines = new ArrayList();
      this.fileName = var1;
      this.compareCoverage = var2;
      this.loadLanguageFile();
   }

   public Translation(String var1) {
      this(var1, (Translation)null);
   }

   public void loadLanguageFile() {
      this.categories = new HashMap();

      try {
         File var1 = new File(GlobalData.rootPath() + "locale/" + "/" + this.fileName);
         InputStreamReader var2 = new InputStreamReader(new FileInputStream(var1), StandardCharsets.UTF_8);
         BufferedReader var3 = new BufferedReader(var2);
         this.loadLanguageFile(var3, (LoadedMod)null);
         var3.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public void loadModLanguageFile(LoadedMod var1) {
      try {
         Enumeration var2 = var1.jarFile.entries();

         while(var2.hasMoreElements()) {
            JarEntry var3 = (JarEntry)var2.nextElement();
            String var4 = var3.getName();
            if (var4.startsWith("resources/") && var4.endsWith(this.fileName)) {
               InputStreamReader var5 = new InputStreamReader(var1.jarFile.getInputStream(var3), StandardCharsets.UTF_8);
               BufferedReader var6 = new BufferedReader(var5);
               this.loadLanguageFile(var6, var1);
               var6.close();
               break;
            }
         }
      } catch (IOException var7) {
         System.err.println("Could not load mod " + var1.id + " language file " + this.fileName);
         var7.printStackTrace();
      }

   }

   private void loadLanguageFile(BufferedReader var1, LoadedMod var2) throws IOException {
      TranslationCategory var3 = (TranslationCategory)this.categories.get("null");
      if (var3 == null) {
         var3 = new TranslationCategory("null");
      }

      LinkedList var5 = new LinkedList();
      boolean var6 = false;
      SetTranslationLineData var7 = null;

      while(true) {
         String var4;
         while((var4 = var1.readLine()) != null) {
            if (var4.length() == 0) {
               if (var6) {
                  if (!var5.isEmpty()) {
                     this.linesAfterCategory.put(var3.name, var5);
                  }

                  var5 = new LinkedList();
               } else if (var7 != null) {
                  if (!var5.isEmpty()) {
                     this.linesAfterTranslation.put(var7.category + "." + var7.key, var5);
                  }

                  var5 = new LinkedList();
               }

               var5.add(new LineData(Translation.LineType.EMPTY, var4));
               this.lines.add(new LineData(Translation.LineType.EMPTY, var4));
               var6 = false;
               var7 = null;
            } else if (var4.startsWith("//")) {
               var5.add(new LineData(Translation.LineType.COMMENT, var4));
               this.lines.add(new LineData(Translation.LineType.COMMENT, var4));
            } else {
               int var8;
               String var9;
               if (var4.startsWith("[")) {
                  var8 = var4.indexOf("]");
                  if (var8 != -1) {
                     if (var3.getTotalTranslations() > 0) {
                        this.categories.put(var3.name, var3);
                     }

                     var9 = var4.substring(1, var8);
                     if (this.categories.containsKey(var9)) {
                        var3 = (TranslationCategory)this.categories.get(var9);
                     } else {
                        var3 = new TranslationCategory(var9);
                     }

                     if (!var5.isEmpty()) {
                        this.linesBeforeCategory.put(var9, var5);
                     }

                     var5 = new LinkedList();
                     this.lines.add(new SetCategoryLineData(var4, var9));
                     var7 = null;
                     var6 = true;
                     continue;
                  }
               }

               var8 = var4.indexOf(61);
               if (var8 == -1) {
                  this.lines.add(new LineData(Translation.LineType.UNKNOWN, var4));
               } else {
                  var9 = var4.substring(0, var8);
                  boolean var10 = false;
                  boolean var11 = false;
                  if (var9.startsWith(MISSING_TRANSLATION_PREPEND)) {
                     var9 = var9.substring(MISSING_TRANSLATION_PREPEND.length());
                     var11 = true;
                  } else if (var9.startsWith(SAME_TRANSLATION_PREPEND)) {
                     var10 = true;
                     var9 = var9.substring(SAME_TRANSLATION_PREPEND.length());
                  }

                  if (var3.name.equals("lang")) {
                     var10 = true;
                  }

                  String var12 = var4.substring(var8 + 1);
                  var12 = var12.replace("\\n", "\n");
                  var3.addTranslation(this.fileName, var9, var12, var10, var11, var2);
                  if (this.compareCoverage == null) {
                     ++this.totalUniqueTranslations;
                  } else {
                     String var13 = this.compareCoverage.translate(var3.name, var9);
                     if (var13 != null && (var10 || !var13.equals(var12))) {
                        ++this.totalUniqueTranslations;
                     }
                  }

                  if (!var5.isEmpty()) {
                     this.linesBeforeTranslation.put(var3.name + "." + var9, var5);
                  }

                  var5 = new LinkedList();
                  this.lines.add(var7 = new SetTranslationLineData(var4, var3.name, var9, var12));
                  var6 = false;
               }
            }
         }

         if (var3.getTotalTranslations() > 0) {
            this.categories.put(var3.name, var3);
         }

         return;
      }
   }

   public TranslationCategory getCategory(String var1) {
      return (TranslationCategory)this.categories.get(var1);
   }

   public String translate(String var1, String var2) {
      TranslationCategory var3 = this.getCategory(var1);
      return var3 != null ? var3.translate(var2) : null;
   }

   public boolean isSameAsEnglish(String var1, String var2) {
      TranslationCategory var3 = (TranslationCategory)this.categories.get(var1);
      return var3 != null ? var3.isSameAsEnglish(var2) : false;
   }

   public Stream<TranslationData> streamTranslations(boolean var1, boolean var2) {
      return this.categories.entrySet().stream().flatMap((var2x) -> {
         Stream var3 = ((TranslationCategory)var2x.getValue()).streamTranslations();
         if (!var1) {
            var3 = var3.filter((var1x) -> {
               return !((TranslationCategory)var2x.getValue()).isSameAsEnglish((String)var1x.getKey());
            });
         }

         if (!var2) {
            var3 = var3.filter((var1x) -> {
               return !((TranslationCategory)var2x.getValue()).isMissing((String)var1x.getKey());
            });
         }

         return var3.map((var1x) -> {
            return new TranslationData((String)var2x.getKey(), (String)var1x.getKey(), (String)var1x.getValue());
         });
      });
   }

   public Stream<TranslationData> streamStrippedTranslations(boolean var1, boolean var2) {
      return this.streamTranslations(var1, var2).map((var0) -> {
         String var1 = var0.translation.replaceAll("<([^>])+>", "").replaceAll("\u00a7([!0-9a-z]|#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}))", "").replace("\\n", " ").replaceAll(TypeParsers.INPUT_PATTERN.pattern(), " ").replaceAll(TypeItemParser.ITEM_PATTERN.pattern(), " ").replaceAll(TypeItemParser.ITEMS_PATTERN.pattern(), " ").replaceAll("\\s+", " ").trim();
         return new TranslationData(var0.category, var0.key, var1);
      });
   }

   public void forEachTranslation(Consumer<TranslationData> var1, boolean var2, boolean var3) {
      this.categories.forEach((var3x, var4) -> {
         var4.forEachTranslations((var5, var6) -> {
            if ((var2 || !var4.isSameAsEnglish(var5)) && (var3 || !var4.isMissing(var5))) {
               var1.accept(new TranslationData(var3x, var5, var6));
            }

         });
      });
   }

   public void debugReload() {
      this.loadLanguageFile();
   }

   public void printTranslations() {
      System.out.println("Translations in file:");
      System.out.println(this.fileName);
      System.out.println("--------------------------------");
      Iterator var1 = this.categories.keySet().iterator();

      while(var1.hasNext()) {
         String var2 = (String)var1.next();
         System.out.println("[" + var2 + "]");
         ((TranslationCategory)this.categories.get(var2)).printTranslations(" ");
      }

   }

   public void addCoverageTooltips(ListGameTooltips var1) {
      if (this.compareCoverage != null) {
         var1.add((Object)(new SpacerGameTooltip(5)));
         double var2 = (double)this.totalUniqueTranslations / (double)this.compareCoverage.totalUniqueTranslations;
         int var4 = (int)Math.floor(var2 * 100.0);
         var1.add(Localization.translate("settingsui", "translationcoverage", "percent", var4 + "%"));
      }

   }

   public void fixAndPrintLanguageFile(Translation var1, String var2, boolean var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < this.lines.size(); ++var5) {
         LineData var6 = (LineData)this.lines.get(var5);
         if (var6.type != Translation.LineType.COMMENT && var6.type != Translation.LineType.EMPTY && var6.type != Translation.LineType.UNKNOWN) {
            LinkedList var8;
            Iterator var9;
            LineData var10;
            if (var6.type == Translation.LineType.SET_CATEGORY) {
               SetCategoryLineData var21 = (SetCategoryLineData)var6;
               var8 = (LinkedList)var1.linesBeforeCategory.get(var21.category);
               if (var8 == null) {
                  var8 = (LinkedList)this.linesBeforeCategory.getOrDefault(var21.category, new LinkedList());
               }

               var9 = var8.iterator();

               while(var9.hasNext()) {
                  var10 = (LineData)var9.next();
                  var4.add(var10.fullLine);
               }

               var4.add(var6.fullLine);
               LinkedList var23 = (LinkedList)var1.linesAfterCategory.get(var21.category);
               if (var23 == null) {
                  var23 = (LinkedList)this.linesAfterCategory.getOrDefault(var21.category, new LinkedList());
               }

               Iterator var28 = var23.iterator();

               while(var28.hasNext()) {
                  LineData var27 = (LineData)var28.next();
                  var4.add(var27.fullLine);
               }
            } else if (var6.type == Translation.LineType.SET_TRANSLATION) {
               SetTranslationLineData var7 = (SetTranslationLineData)var6;
               var8 = (LinkedList)var1.linesBeforeTranslation.get(var7.category + "." + var7.key);
               if (var8 == null) {
                  var8 = (LinkedList)this.linesAfterTranslation.getOrDefault(var7.category + "." + var7.key, new LinkedList());
               }

               var9 = var8.iterator();

               while(var9.hasNext()) {
                  var10 = (LineData)var9.next();
                  var4.add(var10.fullLine);
               }

               String var22 = var1.translate(var7.category, var7.key);
               if (var22 == null || !var1.isSameAsEnglish(var7.category, var7.key) && var7.translation.equals(var22)) {
                  if (var3) {
                     var4.add(MISSING_TRANSLATION_PREPEND + var7.fullLine);
                  }
               } else {
                  String var24 = var7.translation;
                  boolean var11 = var7.category.equals("lang");
                  if (var11) {
                     var4.add(var7.key + "=" + var22.replace("\n", "\\n"));
                  } else {
                     HashSet var12 = new HashSet();
                     HashSet var13 = new HashSet();
                     GameUtils.forEachMatcherResult(LocalMessage.replaceRegex, var24, (var1x) -> {
                        var12.add(var1x.group(1));
                     });
                     GameUtils.forEachMatcherResult(LocalMessage.replaceRegex, var22, (var1x) -> {
                        var13.add(var1x.group(1));
                     });
                     if (!var12.equals(var13)) {
                        if (var3) {
                           var4.add(MISSING_TRANSLATION_PREPEND + var7.fullLine);
                        }
                     } else {
                        boolean var14 = var1.isSameAsEnglish(var7.category, var7.key);
                        String var15 = (var14 ? SAME_TRANSLATION_PREPEND : "") + var7.key + "=" + var22.replace("\n", "\\n");
                        var4.add(var15);
                     }
                  }
               }

               LinkedList var25 = (LinkedList)var1.linesAfterTranslation.get(var7.category + "." + var7.key);
               if (var25 == null) {
                  var25 = (LinkedList)this.linesAfterTranslation.getOrDefault(var7.category + "." + var7.key, new LinkedList());
               }

               Iterator var26 = var25.iterator();

               while(var26.hasNext()) {
                  LineData var29 = (LineData)var26.next();
                  var4.add(var29.fullLine);
               }
            }
         }
      }

      try {
         BufferedWriter var19 = Files.newBufferedWriter(Paths.get(var2), StandardCharsets.UTF_8);

         try {
            Iterator var20 = var4.iterator();

            while(var20.hasNext()) {
               var19.write((String)var20.next());
               if (var20.hasNext()) {
                  var19.newLine();
               }
            }
         } catch (Throwable var17) {
            if (var19 != null) {
               try {
                  var19.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }
            }

            throw var17;
         }

         if (var19 != null) {
            var19.close();
         }
      } catch (IOException var18) {
         var18.printStackTrace();
      }

   }

   public int countTranslationWords() {
      return this.streamStrippedTranslations(true, false).mapToInt((var0) -> {
         return var0.translation.split(" ").length;
      }).sum();
   }

   public void exportTranslationCSV(String var1) {
      File var2 = new File(var1);

      try {
         GameUtils.mkDirs(var2);
         FileWriter var3 = new FileWriter(var2);
         this.forEachTranslation((var1x) -> {
            try {
               var3.append("\"").append(var1x.category).append(".").append(var1x.key).append("\"").append(",").append("\"").append(var1x.translation.replace("\n", "\\n")).append("\"").append("\n");
            } catch (IOException var3x) {
               throw new RuntimeException(var3x);
            }
         }, true, false);
         var3.close();
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public void importTranslationCSV(String var1, String var2) {
      HashMap var3 = new HashMap();
      Pattern var4 = Pattern.compile("(?:^\"|,\")(\"\"|[\\w\\W]*?)(?=\",|\"$)|(?:^(?!\")|,(?!\"))([^,]*?)(?=$|,)|(\\r\\n|\\n)");

      try {
         BufferedReader var5 = Files.newBufferedReader(Paths.get(var2), StandardCharsets.UTF_8);

         String var6;
         try {
            while((var6 = var5.readLine()) != null) {
               while(!var6.endsWith("\"")) {
                  String var7 = var5.readLine();
                  if (var7 == null) {
                     break;
                  }

                  var6 = var6 + "\\n" + var7;
               }

               Matcher var22 = var4.matcher(var6);
               if (var22.find()) {
                  String var8 = var22.group(1);
                  if (var22.find()) {
                     String var9 = var22.group(1);
                     var9 = var9.replace("\"\"", "\"");
                     int var10 = var8.indexOf(".");
                     String var11;
                     if (var10 != -1) {
                        var11 = var8.substring(0, var10);
                        var8 = var8.substring(var10 + 1);
                     } else {
                        var11 = "null";
                     }

                     var3.compute(var11, (var4x, var5x) -> {
                        if (var5x == null) {
                           var5x = new TranslationCategory(var11);
                        }

                        var5x.addTranslation(this.fileName, var8, var9, false, false, (LoadedMod)null);
                        return var5x;
                     });
                  } else {
                     System.out.println("COULD NOT FIND TRANSLATION IN " + var6 + " FOR " + var1);
                  }
               } else {
                  System.out.println("COULD NOT FIND KEY IN " + var6 + " FOR " + var1);
               }
            }
         } catch (Throwable var18) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var14) {
                  var18.addSuppressed(var14);
               }
            }

            throw var18;
         }

         if (var5 != null) {
            var5.close();
         }
      } catch (IOException var19) {
         throw new RuntimeException(var19);
      }

      ArrayList var20 = new ArrayList();

      for(int var21 = 0; var21 < this.lines.size(); ++var21) {
         LineData var24 = (LineData)this.lines.get(var21);
         if (var24.type == Translation.LineType.SET_TRANSLATION) {
            SetTranslationLineData var26 = (SetTranslationLineData)var24;
            TranslationCategory var27 = (TranslationCategory)var3.get(var26.category);
            if (var27 == null) {
               var20.add(var26.fullLine);
            } else {
               String var28 = var27.translate(var26.key);
               if (var28 == null || var28.equals(var26.translation) && !((TranslationCategory)this.categories.get(var26.category)).isMissing(var26.key)) {
                  var20.add(var26.fullLine);
               } else {
                  var20.add(var26.key + "=" + var28);
               }
            }
         } else {
            var20.add(var24.fullLine);
         }
      }

      try {
         BufferedWriter var23 = Files.newBufferedWriter(Paths.get(var1), StandardCharsets.UTF_8);

         try {
            Iterator var25 = var20.iterator();

            while(var25.hasNext()) {
               var23.write((String)var25.next());
               if (var25.hasNext()) {
                  var23.newLine();
               }
            }
         } catch (Throwable var16) {
            if (var23 != null) {
               try {
                  var23.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }
            }

            throw var16;
         }

         if (var23 != null) {
            var23.close();
         }
      } catch (IOException var17) {
         var17.printStackTrace();
      }

   }

   private static class SetTranslationLineData extends LineData {
      public final String category;
      public final String key;
      public final String translation;

      public SetTranslationLineData(String var1, String var2, String var3, String var4) {
         super(Translation.LineType.SET_TRANSLATION, var1);
         this.category = var2;
         this.key = var3;
         this.translation = var4;
      }
   }

   private static class LineData {
      public final LineType type;
      public final String fullLine;

      public LineData(LineType var1, String var2) {
         this.type = var1;
         this.fullLine = var2;
      }
   }

   private static enum LineType {
      SET_CATEGORY(SetCategoryLineData.class),
      SET_TRANSLATION(SetTranslationLineData.class),
      COMMENT(LineData.class),
      EMPTY(LineData.class),
      UNKNOWN(LineData.class);

      public final Class<? extends LineData> typeClass;

      private LineType(Class var3) {
         this.typeClass = var3;
      }

      // $FF: synthetic method
      private static LineType[] $values() {
         return new LineType[]{SET_CATEGORY, SET_TRANSLATION, COMMENT, EMPTY, UNKNOWN};
      }
   }

   private static class SetCategoryLineData extends LineData {
      public final String category;

      public SetCategoryLineData(String var1, String var2) {
         super(Translation.LineType.SET_CATEGORY, var1);
         this.category = var2;
      }
   }

   public static class TranslationData {
      public final String category;
      public final String key;
      public final String translation;

      public TranslationData(String var1, String var2, String var3) {
         this.category = var1;
         this.key = var2;
         this.translation = var3;
      }
   }
}
