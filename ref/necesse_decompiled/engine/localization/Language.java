package necesse.engine.localization;

import java.util.List;
import java.util.Objects;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;

public abstract class Language {
   public int updateUnique;
   public final String stringID;
   public final String steamAPICode;
   public final String englishDisplayName;
   public final String localDisplayName;
   public final String credits;

   public void updateUnique() {
      this.updateUnique = GameRandom.globalRandom.nextInt();
   }

   public Language(String var1, String var2, String var3, String var4, String var5) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var3);
      Objects.requireNonNull(var4);
      this.stringID = var1;
      this.steamAPICode = var2;
      this.englishDisplayName = var3;
      this.localDisplayName = var4;
      this.credits = var5;
      this.updateUnique();
   }

   public void reload(List<LoadedMod> var1) {
   }

   public abstract String translate(String var1, String var2);

   public void loadMod(LoadedMod var1) {
   }

   public void setCurrent() {
      Localization.setCurrentLang(this);
   }

   public boolean isDebugOnly() {
      return false;
   }

   public abstract String getCharactersUsed();

   public abstract int countTranslationWords();

   public abstract void exportTranslationCSV(String var1);

   public abstract void importTranslationCSV(String var1);

   public void addTooltips(ListGameTooltips var1) {
      if (!this.englishDisplayName.equals(this.localDisplayName)) {
         var1.add(this.englishDisplayName);
      }

      if (this.credits != null && !this.credits.isEmpty()) {
         var1.add((Object)(new SpacerGameTooltip(5)));
         var1.add((String)this.credits, 500);
      }

   }
}
