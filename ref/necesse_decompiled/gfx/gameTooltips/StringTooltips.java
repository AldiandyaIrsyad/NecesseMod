package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;

public class StringTooltips implements GameTooltips {
   private int currentWidth;
   private int currentHeight;
   private LinkedList<PriorityString> list;

   public StringTooltips() {
      this.list = new LinkedList();
   }

   public StringTooltips(String var1) {
      this();
      this.add(var1);
   }

   public StringTooltips(String var1, int var2) {
      this();
      this.add(var1, var2);
   }

   public StringTooltips(String var1, GameColor var2) {
      this();
      this.add(var1, var2);
   }

   public StringTooltips(String var1, GameColor var2, int var3) {
      this();
      this.add(var1, var2, var3);
   }

   public StringTooltips(String var1, Color var2) {
      this();
      this.add(var1, var2);
   }

   public StringTooltips(String var1, Color var2, int var3) {
      this();
      this.add(var1, var2, var3);
   }

   public StringTooltips(String var1, Supplier<Color> var2, int var3) {
      this();
      this.add(var1, (Supplier)var2, var3, 0);
   }

   public StringTooltips(String... var1) {
      this();
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.add(var5);
      }

   }

   public StringTooltips(List<String> var1) {
      this();
      var1.forEach(this::add);
   }

   private StringTooltips add(PriorityString var1) {
      GameUtils.insertSortedList((List)this.list, var1, Comparator.comparingInt((var0) -> {
         return var0.priority;
      }));
      Rectangle var2 = var1.drawOptions.getBoundingBox();
      this.currentWidth = Math.max(this.currentWidth, var2.x + var2.width);
      this.currentHeight += var2.height;
      return this;
   }

   public StringTooltips add(String var1, Supplier<Color> var2, int var3, int var4) {
      FontOptions var5 = (new FontOptions(Settings.tooltipTextSize)).outline();
      FairType var6 = (new FairType()).append(var5, var1).applyParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(var5.getSize()), TypeParsers.InputIcon(var5));
      PriorityString var7 = new PriorityString(var6, var2, var3, var4);
      return this.add(var7);
   }

   public StringTooltips add(String var1, GameColor var2, int var3, int var4) {
      return this.add(var1, var2.color, var3, var4);
   }

   public StringTooltips add(String var1, Color var2, int var3, int var4) {
      return this.add(var1, () -> {
         return var2;
      }, var3, var4);
   }

   public StringTooltips add(String var1, GameColor var2, int var3) {
      return this.add(var1, (Supplier)var2.color, var3, 0);
   }

   public StringTooltips add(String var1, Color var2, int var3) {
      return this.add(var1, (Supplier)(() -> {
         return var2;
      }), var3, 0);
   }

   public StringTooltips add(String var1, int var2) {
      return this.add(var1, (Supplier)((Supplier)null), var2, 0);
   }

   public StringTooltips add(String var1, Color var2) {
      return this.add(var1, (Supplier)(() -> {
         return var2;
      }), -1, 0);
   }

   public StringTooltips add(String var1, GameColor var2) {
      return this.add(var1, (Supplier)var2.color, -1, 0);
   }

   public StringTooltips add(String var1) {
      return this.add(var1, (Supplier)((Supplier)null), -1, 0);
   }

   public StringTooltips addAll(StringTooltips var1) {
      var1.list.forEach(this::add);
      return this;
   }

   public StringTooltips addAll(StringTooltips var1, int var2) {
      var1.list.forEach((var2x) -> {
         this.add(new PriorityString(var2x.drawOptions, var2x.color, var2));
      });
      return this;
   }

   public void clear() {
      this.list.clear();
      this.currentWidth = 0;
      this.currentHeight = 0;
   }

   public int getSize() {
      return this.list.size();
   }

   public int getHeight() {
      return this.currentHeight;
   }

   public int getWidth() {
      return this.currentWidth;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      if (!this.list.isEmpty()) {
         int var4 = var2;

         PriorityString var6;
         for(Iterator var5 = this.list.iterator(); var5.hasNext(); var4 += var6.drawOptions.getBoundingBox().height) {
            var6 = (PriorityString)var5.next();
            var6.draw(var1, var4, var3);
         }

      }
   }

   public int getDrawOrder() {
      return 0;
   }

   private class PriorityString {
      public Supplier<Color> color;
      public FairTypeDrawOptions drawOptions;
      public int priority;

      public PriorityString(FairTypeDrawOptions var2, Supplier<Color> var3, int var4) {
         this.drawOptions = var2;
         this.color = var3;
         this.priority = var4;
      }

      public PriorityString(FairType var2, Supplier<Color> var3, int var4, int var5) {
         this(var2.getDrawOptions(FairType.TextAlign.LEFT, var4, true, false), var3, var5);
      }

      public int getWidth() {
         return this.drawOptions.getBoundingBox().width;
      }

      public void draw(int var1, int var2, Supplier<Color> var3) {
         this.drawOptions.draw(var1, var2, this.color == null ? (Color)var3.get() : (Color)this.color.get());
      }
   }
}
