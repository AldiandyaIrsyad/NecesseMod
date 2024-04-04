package necesse.engine.screenHudManager;

import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.tickManager.TickManager;

public class ScreenHudManager {
   private ArrayList<ScreenHudElement> list = new ArrayList();

   public ScreenHudManager() {
   }

   public void cleanUp() {
      for(int var1 = 0; var1 < this.list.size(); ++var1) {
         ScreenHudElement var2 = (ScreenHudElement)this.list.get(var1);
         if (var2.isRemoved()) {
            this.list.remove(var1);
            var2.onRemove();
            --var1;
         }
      }

   }

   public void draw(TickManager var1) {
      this.list.sort(Comparator.comparingInt(ScreenHudElement::getDrawPriority).reversed());
      ScreenHudElement[] var2 = (ScreenHudElement[])this.list.toArray(new ScreenHudElement[0]);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ScreenHudElement var5 = var2[var4];
         if (var5 != null && !var5.isRemoved()) {
            var5.draw(var1);
         }
      }

   }

   public <T extends ScreenHudElement> T addElement(T var1) {
      var1.addThis(this.list);
      return var1;
   }
}
