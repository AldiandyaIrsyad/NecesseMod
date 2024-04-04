package necesse.engine.localization.fileLanguage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.modLoader.LoadedMod;

public class TranslationCategory {
   public final String name;
   private HashMap<String, String> translations = new HashMap();
   private HashMap<String, Boolean> sameTranslations = new HashMap();
   private HashMap<String, Boolean> missingTranslations = new HashMap();

   public TranslationCategory(String var1) {
      this.name = var1;
   }

   public int getTotalTranslations() {
      return this.translations.size();
   }

   public void addTranslation(String var1, String var2, String var3, boolean var4, boolean var5, LoadedMod var6) {
      if (this.translations.containsKey(var2)) {
         if (var6 == null) {
            GameLog.warn.println(var1 + ": Overwrote duplicate translation " + this.name + "." + var2);
         } else {
            System.out.println(var1 + ": " + this.name + "." + var2 + " localisation overwriting by mod " + var6.id);
         }
      }

      this.translations.put(var2, var3);
      if (var4) {
         this.sameTranslations.put(var2, true);
      }

      if (var5) {
         this.missingTranslations.put(var2, true);
      }

   }

   public String translate(String var1) {
      return (String)this.translations.get(var1);
   }

   public boolean isSameAsEnglish(String var1) {
      return (Boolean)this.sameTranslations.getOrDefault(var1, false);
   }

   public boolean isMissing(String var1) {
      return (Boolean)this.missingTranslations.getOrDefault(var1, false);
   }

   public void printTranslations() {
      this.printTranslations("");
   }

   public void printTranslations(String var1) {
      Iterator var2 = this.translations.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         System.out.println(var1 + var3 + "=" + (String)this.translations.get(var3));
      }

   }

   public Stream<Map.Entry<String, String>> streamTranslations() {
      return this.translations.entrySet().stream();
   }

   public void forEachTranslations(BiConsumer<String, String> var1) {
      this.translations.forEach(var1);
   }
}
