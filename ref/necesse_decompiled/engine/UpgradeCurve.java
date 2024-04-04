package necesse.engine;

import java.util.Map;
import java.util.TreeMap;

public class UpgradeCurve {
   private TreeMap<Float, Float> steps = new TreeMap();

   public UpgradeCurve() {
   }

   public UpgradeCurve addStep(float var1, float var2) {
      if (this.steps.isEmpty()) {
         this.steps.put(var1, var2);
      } else {
         Map.Entry var3 = this.steps.lastEntry();
         float var4 = (Float)var3.getKey();
         float var5 = (Float)var3.getValue();
      }

      return this;
   }
}
