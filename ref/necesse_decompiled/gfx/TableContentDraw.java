package necesse.gfx;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class TableContentDraw {
   public final ColumnWidths colWidths;
   private int totalHeight;
   private final LinkedList<TableRow> rows;

   public TableContentDraw(ColumnWidths var1) {
      this.rows = new LinkedList();
      this.colWidths = var1;
   }

   public TableContentDraw() {
      this(new ColumnWidths());
   }

   public void setMinimumColumnWidth(int var1, int var2) {
      this.colWidths.setMinimumWidth(var1, var2);
   }

   public TableRow newRow() {
      TableRow var1 = new TableRow();
      this.rows.add(var1);
      return var1;
   }

   public int getColumnWidth(int var1) {
      return this.colWidths.getWidth(var1);
   }

   public int getWidth() {
      return this.colWidths.getTotalWidth();
   }

   public int getHeight() {
      return this.totalHeight;
   }

   public void draw(int var1, int var2) {
      TableRow var4;
      for(Iterator var3 = this.rows.iterator(); var3.hasNext(); var2 += var4.draw(var1, var2)) {
         var4 = (TableRow)var3.next();
      }

   }

   public static void drawSeries(int var0, int var1, Iterable<TableContentDraw> var2) {
      TableContentDraw var4;
      for(Iterator var3 = var2.iterator(); var3.hasNext(); var1 += var4.getHeight()) {
         var4 = (TableContentDraw)var3.next();
         var4.draw(var0, var1);
      }

   }

   public static void drawSeries(int var0, int var1, TableContentDraw... var2) {
      drawSeries(var0, var1, (Iterable)Arrays.asList(var2));
   }

   public static class ColumnWidths {
      private int totalWidth;
      private int[] widths = new int[0];

      public ColumnWidths() {
      }

      public void setMinimumWidth(int var1, int var2) {
         if (var1 >= this.widths.length) {
            this.widths = Arrays.copyOf(this.widths, var1 + 1);
         }

         if (var2 > this.widths[var1]) {
            this.totalWidth += var2 - this.widths[var1];
            this.widths[var1] = var2;
         }

      }

      public int getWidth(int var1) {
         return var1 >= this.widths.length ? 0 : this.widths[var1];
      }

      public int getTotalWidth() {
         return this.totalWidth;
      }
   }

   public class TableRow {
      private final LinkedList<Field> fields;
      private int maxHeight;

      private TableRow() {
         this.fields = new LinkedList();
      }

      public TableRow setMinimumHeight(int var1) {
         if (var1 > this.maxHeight) {
            TableContentDraw.this.totalHeight = var1 - this.maxHeight;
            this.maxHeight = var1;
         }

         return this;
      }

      public TableRow addColumn(BiConsumer<Integer, Integer> var1, int var2, int var3, boolean var4, boolean var5) {
         int var6 = this.fields.size();
         TableContentDraw.this.setMinimumColumnWidth(var6, var2);
         this.fields.add(new Field(var1, var2, var3, var4, var5));
         return this.setMinimumHeight(var3);
      }

      public TableRow addColumn(BiConsumer<Integer, Integer> var1, int var2, int var3) {
         return this.addColumn(var1, var2, var3, false, false);
      }

      public TableRow addTextColumn(String var1, FontOptions var2, boolean var3, boolean var4, int var5, int var6) {
         return this.addColumn((var2x, var3x) -> {
            FontManager.bit.drawString((float)var2x, (float)var3x, var1, var2);
         }, FontManager.bit.getWidthCeil(var1, var2) + var5, FontManager.bit.getHeightCeil(var1, var2) + var6, var3, var4);
      }

      public TableRow addTextColumn(String var1, FontOptions var2, boolean var3, boolean var4) {
         return this.addTextColumn(var1, var2, var3, var4, 0, 0);
      }

      public TableRow addTextColumn(String var1, FontOptions var2, int var3, int var4) {
         return this.addTextColumn(var1, var2, false, false, var3, var4);
      }

      public TableRow addTextColumn(String var1, FontOptions var2) {
         return this.addTextColumn(var1, var2, false, false);
      }

      public TableRow addEmptyColumn() {
         return this.addColumn((var0, var1) -> {
         }, 0, 0);
      }

      private int draw(int var1, int var2) {
         int var3 = 0;

         for(Iterator var4 = this.fields.iterator(); var4.hasNext(); ++var3) {
            Field var5 = (Field)var4.next();
            int var6 = TableContentDraw.this.getColumnWidth(var3);
            var5.draw(var1, var2, var6, this.maxHeight);
            var1 += var6;
         }

         return this.maxHeight;
      }

      // $FF: synthetic method
      TableRow(Object var2) {
         this();
      }
   }

   private static class Field {
      private final BiConsumer<Integer, Integer> drawLogic;
      private final int drawWidth;
      private final int drawHeight;
      private final boolean centeredX;
      private final boolean centeredY;

      public Field(BiConsumer<Integer, Integer> var1, int var2, int var3, boolean var4, boolean var5) {
         this.drawLogic = var1;
         this.drawWidth = var2;
         this.drawHeight = var3;
         this.centeredX = var4;
         this.centeredY = var5;
      }

      public void draw(int var1, int var2, int var3, int var4) {
         int var5 = this.centeredX ? var3 / 2 - this.drawWidth / 2 : 0;
         int var6 = this.centeredY ? var4 / 2 - this.drawHeight / 2 : 0;
         this.drawLogic.accept(var1 + var5, var2 + var6);
      }
   }
}
