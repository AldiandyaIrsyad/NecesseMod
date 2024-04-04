package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;

public class ComparingGameTooltips implements GameTooltips {
   public int padding;
   protected ArrayList<GameTooltips> tooltips;
   protected int height;
   protected int width;

   public ComparingGameTooltips(Collection<GameTooltips> var1) {
      this.padding = 5;
      this.tooltips = new ArrayList(var1.size());
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         GameTooltips var3 = (GameTooltips)var2.next();
         this.addTooltips(var3);
      }

   }

   public ComparingGameTooltips(GameTooltips... var1) {
      this((Collection)Arrays.asList(var1));
   }

   public void addTooltips(GameTooltips var1) {
      this.height = Math.max(var1.getHeight(), this.height);
      this.width += var1.getWidth();
      this.tooltips.add(var1);
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width + this.padding * Math.max(0, this.tooltips.size() - 1);
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      GameTooltips var5;
      for(Iterator var4 = this.tooltips.iterator(); var4.hasNext(); var1 += var5.getWidth() + this.padding) {
         var5 = (GameTooltips)var4.next();
         var5.draw(var1, var2, var3);
      }

   }

   public int getDrawOrder() {
      return 0;
   }
}
