package necesse.gfx.forms.presets;

import java.util.ArrayList;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.ContinueComponent;

public abstract class ContinueForm extends Form implements ContinueComponent {
   private ArrayList<Runnable> continueEvents = new ArrayList();
   private boolean isContinued = false;

   public ContinueForm(String var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public final void onContinue(Runnable var1) {
      if (var1 != null) {
         this.continueEvents.add(var1);
      }

   }

   public final void applyContinue() {
      if (this.canContinue()) {
         this.continueEvents.forEach(Runnable::run);
         this.isContinued = true;
      }

   }

   public boolean canContinue() {
      return true;
   }

   public final boolean isContinued() {
      return this.isContinued;
   }
}
