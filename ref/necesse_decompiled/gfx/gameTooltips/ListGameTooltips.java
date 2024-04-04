package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;

public class ListGameTooltips extends LinkedList<GameTooltips> implements GameTooltips {
   public int yMargin;
   public int drawOrder;

   public ListGameTooltips() {
      this.drawOrder = 0;
      this.yMargin = 0;
   }

   public ListGameTooltips(GameTooltips var1) {
      this();
      if (var1 != null) {
         this.add((Object)var1);
      }

   }

   public ListGameTooltips(String var1) {
      this();
      if (var1 != null) {
         this.add(var1);
      }

   }

   public ListGameTooltips(GameMessage var1) {
      this();
      if (var1 != null) {
         this.add(var1);
      }

   }

   public void add(String var1, int var2) {
      this.add((Object)(new StringTooltips(var1, var2)));
   }

   public void add(GameMessage var1, int var2) {
      this.add(var1.translate(), var2);
   }

   public void add(String var1) {
      this.add((Object)(new StringTooltips(var1)));
   }

   public void add(GameMessage var1) {
      this.add(var1.translate());
   }

   public void addFirst(String var1) {
      this.addFirst(new StringTooltips(var1));
   }

   public int getHeight() {
      int var1 = 0;

      GameTooltips var3;
      for(Iterator var2 = this.iterator(); var2.hasNext(); var1 += var3.getHeight() + this.yMargin) {
         var3 = (GameTooltips)var2.next();
      }

      return var1 - this.yMargin;
   }

   public int getWidth() {
      int var1 = 0;

      GameTooltips var3;
      for(Iterator var2 = this.iterator(); var2.hasNext(); var1 = Math.max(var1, var3.getWidth() + var3.getDrawXOffset())) {
         var3 = (GameTooltips)var2.next();
      }

      return var1;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      this.sort(Comparator.comparing(GameTooltips::getDrawOrder, Comparator.reverseOrder()));

      GameTooltips var5;
      for(Iterator var4 = this.iterator(); var4.hasNext(); var2 += var5.getHeight() + this.yMargin) {
         var5 = (GameTooltips)var4.next();
         int var6 = var5.getDrawXOffset();
         int var7 = Math.max(0, var1 + var6);
         var5.draw(var7, var2, var3);
      }

   }

   public int getDrawOrder() {
      return this.drawOrder;
   }
}
