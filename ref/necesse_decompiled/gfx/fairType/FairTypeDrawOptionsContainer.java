package necesse.gfx.fairType;

import java.util.LinkedList;
import java.util.function.Supplier;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;

public class FairTypeDrawOptionsContainer {
   private final Supplier<FairTypeDrawOptions> generator;
   private FairTypeDrawOptions drawOptions;
   protected Language currentLang = null;
   protected LinkedList<Runnable> updateEvents = new LinkedList();
   protected LinkedList<Runnable> resetEvents = new LinkedList();

   public FairTypeDrawOptionsContainer(Supplier<FairTypeDrawOptions> var1) {
      this.generator = var1;
   }

   public FairTypeDrawOptionsContainer updateOnLanguageChange() {
      this.currentLang = Localization.getCurrentLang();
      return this;
   }

   public FairTypeDrawOptionsContainer onUpdate(Runnable var1) {
      this.updateEvents.add(var1);
      return this;
   }

   public FairTypeDrawOptionsContainer onReset(Runnable var1) {
      this.resetEvents.add(var1);
      return this;
   }

   public FairTypeDrawOptions get() {
      if (this.drawOptions == null || this.drawOptions.shouldUpdate() || this.currentLang != null && this.currentLang != Localization.getCurrentLang()) {
         if (this.currentLang != null) {
            this.currentLang = Localization.getCurrentLang();
         }

         this.drawOptions = (FairTypeDrawOptions)this.generator.get();
         this.updateEvents.forEach(Runnable::run);
      }

      return this.drawOptions;
   }

   public FairTypeDrawOptionsContainer reset() {
      if (this.drawOptions != null) {
         this.drawOptions = null;
         this.resetEvents.forEach(Runnable::run);
      }

      return this;
   }
}
