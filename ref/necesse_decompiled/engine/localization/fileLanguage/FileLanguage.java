package necesse.engine.localization.fileLanguage;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class FileLanguage extends Language {
   public static final String langsPath = "locale/";
   private Translation translation;

   public FileLanguage(String var1, String var2, Translation var3, String var4, String var5) {
      super(var1, var2, var4, var5, var3.translate("lang", "credits"));
      this.translation = var3;
   }

   public String getFileName() {
      return this.translation.fileName;
   }

   public String getFilePath() {
      return GlobalData.rootPath() + "locale/" + "/" + this.translation.fileName;
   }

   public void reload(List<LoadedMod> var1) {
      this.translation.loadLanguageFile();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         LoadedMod var3 = (LoadedMod)var2.next();
         this.translation.loadModLanguageFile(var3);
      }

   }

   public void loadMod(LoadedMod var1) {
      this.translation.loadModLanguageFile(var1);
   }

   public TranslationCategory getCategory(String var1) {
      return this.translation.getCategory(var1);
   }

   public String translate(String var1, String var2) {
      return this.translation.translate(var1, var2);
   }

   public int countTranslationWords() {
      return this.translation.countTranslationWords();
   }

   public void exportTranslationCSV(String var1) {
      this.translation.exportTranslationCSV(GlobalData.rootPath() + var1 + "/" + GameUtils.removeFileExtension(this.translation.fileName) + ".csv");
   }

   public void importTranslationCSV(String var1) {
      this.translation.importTranslationCSV(this.getFilePath(), var1);
   }

   public String getCharactersUsed() {
      LinkedHashSet var1 = new LinkedHashSet();
      this.translation.streamTranslations(true, true).forEach((var1x) -> {
         var1x.translation.chars().forEach((var1xx) -> {
            if (!var1.contains(var1xx)) {
               var1.add(var1xx);
            }
         });
      });
      StringBuilder var2 = new StringBuilder(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         var2.append((char)var4);
      }

      return var2.toString();
   }

   public void addTooltips(ListGameTooltips var1) {
      super.addTooltips(var1);
      this.translation.addCoverageTooltips(var1);
   }

   public void fixAndPrintLanguageFile(FileLanguage var1, String var2, boolean var3) {
      this.translation.fixAndPrintLanguageFile(var1.translation, var2, var3);
   }

   public static String loadFileLanguage(String var0, String var1) {
      try {
         if (Localization.getLanguageStringID(var0) != null) {
            return var0 + " language already registered";
         } else {
            File var2 = new File(GlobalData.rootPath() + "locale/" + "/" + var1);
            if (var2.exists()) {
               Translation var3 = new Translation(var1, Localization.EnglishTranslation);
               String var4 = var3.translate("lang", "localname");
               String var5 = var3.translate("lang", "engname");
               if (var4 == null) {
                  return "Missing lang.localname translation";
               } else if (var5 == null) {
                  return "Missing lang.engname translation";
               } else {
                  Localization.registerLanguage(new FileLanguage(var0, (String)null, var3, var5, var4));
                  return null;
               }
            } else {
               return var1 + " does not exist";
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
         return var6.getMessage();
      }
   }
}
