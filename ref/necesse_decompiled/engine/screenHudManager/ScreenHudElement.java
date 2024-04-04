package necesse.engine.screenHudManager;

import java.util.ArrayList;
import necesse.engine.tickManager.TickManager;

public abstract class ScreenHudElement {
   private boolean removed;

   public ScreenHudElement() {
   }

   public final void remove() {
      if (!this.removed) {
         this.removed = true;
      }
   }

   public final boolean isRemoved() {
      return this.removed;
   }

   protected void onRemove() {
   }

   public final long getTime() {
      return System.currentTimeMillis();
   }

   public int getDrawPriority() {
      return 0;
   }

   public abstract void draw(TickManager var1);

   public void addThis(ArrayList<ScreenHudElement> var1) {
      var1.add(this);
   }
}
