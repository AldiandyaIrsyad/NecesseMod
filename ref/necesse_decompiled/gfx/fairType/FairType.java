package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.engine.util.GameMath;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.fairType.parsers.TypeParserResult;
import necesse.gfx.gameFont.FontOptions;

public class FairType {
   private ArrayList<FairGlyph> glyphs;

   public FairType() {
      this.glyphs = new ArrayList();
   }

   public FairType(ArrayList<FairGlyph> var1) {
      this.glyphs = var1;
   }

   public FairType(FairGlyph... var1) {
      this(new ArrayList(Arrays.asList(var1)));
   }

   public FairType(FairType var1) {
      this.glyphs = new ArrayList(var1.glyphs);
   }

   public FairType append(String var1, Function<Character, FairCharacterGlyph> var2) {
      return this.append(FairCharacterGlyph.fromString(var1, var2));
   }

   public FairType append(FontOptions var1, String var2) {
      return this.append(FairCharacterGlyph.fromString(var1, var2));
   }

   public FairType append(FairGlyph... var1) {
      Objects.requireNonNull(var1);
      this.glyphs.addAll(Arrays.asList(var1));
      return this;
   }

   public FairType insert(int var1, FairGlyph... var2) {
      Objects.requireNonNull(var2);
      this.glyphs.addAll(var1, Arrays.asList(var2));
      return this;
   }

   public FairGlyph remove(int var1) {
      return (FairGlyph)this.glyphs.remove(var1);
   }

   public boolean remove(FairGlyph var1) {
      return this.glyphs.remove(var1);
   }

   public int getLength() {
      return this.glyphs.size();
   }

   public FairGlyph get(int var1) {
      return (FairGlyph)this.glyphs.get(var1);
   }

   public FairGlyph[] getGlyphsArray() {
      return (FairGlyph[])this.glyphs.toArray(new FairGlyph[0]);
   }

   public int indexOf(FairGlyph var1) {
      return this.glyphs.indexOf(var1);
   }

   public int indexOf(String var1) {
      return this.getCharacterString().indexOf(var1);
   }

   public FairType getTextBoxCopy() {
      ArrayList var1 = new ArrayList(this.glyphs.size());
      Iterator var2 = this.glyphs.iterator();

      while(var2.hasNext()) {
         FairGlyph var3 = (FairGlyph)var2.next();
         var1.add(var3.getTextBoxCharacter());
      }

      return new FairType(var1);
   }

   public String getParseString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.glyphs.iterator();

      while(var2.hasNext()) {
         FairGlyph var3 = (FairGlyph)var2.next();
         var1.append(var3.getParseString());
      }

      return var1.toString();
   }

   public String getCharString() {
      char[] var1 = new char[this.getLength()];

      for(int var2 = 0; var2 < this.glyphs.size(); ++var2) {
         var1[var2] = ((FairGlyph)this.glyphs.get(var2)).getCharacter();
      }

      return new String(var1);
   }

   public FairType applyParsers(Function<Parse, Boolean> var1, Consumer<Parse> var2, TypeParser... var3) {
      TypeParser[] var4 = var3;
      int var5 = var3.length;

      label54:
      for(int var6 = 0; var6 < var5; ++var6) {
         TypeParser var7 = var4[var6];
         int var8 = 0;

         while(true) {
            while(true) {
               if (var8 > this.getLength()) {
                  continue label54;
               }

               TypeParserResult var9 = var7.getMatchResult((FairGlyph[])this.glyphs.toArray(new FairGlyph[0]), var8);
               if (var9 == null) {
                  continue label54;
               }

               FairGlyph[] var10 = (FairGlyph[])this.glyphs.subList(var9.start, var9.end).toArray(new FairGlyph[0]);
               FairGlyph[] var11 = var7.parse(var9, var10);
               Parse var12 = new Parse(var7, var10, var9.start, var9.end, var11);
               if (var11 != null && var11 != var10 && (var1 == null || (Boolean)var1.apply(var12))) {
                  int var13 = var9.end - var9.start;

                  for(int var14 = 0; var14 < var13; ++var14) {
                     this.glyphs.remove(var9.start);
                  }

                  if (var11 != null) {
                     this.glyphs.addAll(var9.start, Arrays.asList(var11));
                     var8 = var9.start + var11.length;
                  }

                  if (var2 != null) {
                     var2.accept(var12);
                  }
               } else {
                  var8 = var9.end + 1;
               }
            }
         }
      }

      return this;
   }

   public FairType applyParsers(TypeParser... var1) {
      return this.applyParsers((Function)null, (Consumer)null, var1);
   }

   public FairType replaceAll(String var1, FairGlyph... var2) {
      this.applyParsers(TypeParsers.replaceParser(var1, var2));
      return this;
   }

   private String getCharacterString() {
      char[] var1 = new char[this.glyphs.size()];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = ((FairGlyph)this.glyphs.get(var2)).getCharacter();
      }

      return new String(var1);
   }

   public void updateGlyphDimensions() {
      Iterator var1 = this.glyphs.iterator();

      while(var1.hasNext()) {
         FairGlyph var2 = (FairGlyph)var1.next();
         var2.updateDimensions();
      }

   }

   public FairTypeDrawOptions getDrawOptions(TextAlign var1) {
      return this.getDrawOptions(var1, -1, false);
   }

   public FairTypeDrawOptions getDrawOptions(TextAlign var1, int var2, boolean var3) {
      return this.getDrawOptions(var1, var2, var3, true);
   }

   public FairTypeDrawOptions getDrawOptions(TextAlign var1, int var2, boolean var3, boolean var4) {
      return this.getDrawOptions(var1, var2, var3, -1, false, (FontOptions)null, var4, var4);
   }

   public FairTypeDrawOptions getDrawOptions(TextAlign var1, int var2, boolean var3, int var4, boolean var5, FontOptions var6, boolean var7) {
      return this.getDrawOptions(var1, var2, var3, var4, var5, var6, var7, var7);
   }

   public FairTypeDrawOptions getDrawOptions(TextAlign var1, int var2, boolean var3, int var4, boolean var5, FontOptions var6, boolean var7, boolean var8) {
      final DrawVars var9 = new DrawVars();
      final boolean var10 = true;

      int var11;
      for(var11 = 0; var11 < this.glyphs.size(); ++var11) {
         FairGlyph var12 = (FairGlyph)this.glyphs.get(var11);
         if (var12.isNewLineGlyph()) {
            var9.submitLine();
            if (var4 > 0 && var9.lines.size() >= var4) {
               if (var11 < this.glyphs.size() - 1) {
                  var10 = false;
               }
               break;
            }
         }

         FloatDimension var13 = var12.getDimensions();
         if (var2 > 0 && var3 && var9.currentLine.currentWord.width + var13.width > (float)var2) {
            var9.currentLine.submitWord();
         }

         var9.currentLine.currentWord.glyphs.add(new GlyphIndex(var11, var12));
         DrawWord var10000 = var9.currentLine.currentWord;
         var10000.width += var13.width;
         var9.currentLine.currentWord.height = Math.max(var9.currentLine.currentWord.height, var13.height);
         if (var12.isWhiteSpaceGlyph() && !var12.isNewLineGlyph()) {
            var9.currentLine.submitWord();
         }
      }

      if (var4 <= 0 || var9.lines.size() < var4) {
         var9.submitLine();
      }

      DrawLine var28;
      final LinkedList var30;
      if (var2 > 0) {
         for(var11 = 0; var11 < var9.lines.size(); ++var11) {
            var28 = (DrawLine)var9.lines.get(var11);
            if (var28.width > (float)var2) {
               var30 = var28.words;
               var28.words = new LinkedList();
               var28.width = 0.0F;
               var28.height = 0.0F;

               DrawWord var15;
               for(Iterator var14 = var30.iterator(); var14.hasNext(); var28.height = Math.max(var28.height, var15.height)) {
                  var15 = (DrawWord)var14.next();
                  if (!var28.words.isEmpty() && var28.width + var15.width > (float)var2) {
                     if (var4 > 0 && var11 >= var4 - 1 && var5) {
                        DrawWord var16 = new DrawWord();

                        while(!var15.glyphs.isEmpty()) {
                           GlyphIndex var17 = (GlyphIndex)var15.glyphs.getFirst();
                           FloatDimension var18 = var17.glyph.getDimensions();
                           if (!(var28.width + var16.width + var18.width <= (float)var2)) {
                              break;
                           }

                           var16.glyphs.add(var17);
                           var16.width += var18.width;
                           var16.height = Math.max(var16.height, var18.height);
                           var15.glyphs.removeFirst();
                        }

                        if (!var16.glyphs.isEmpty()) {
                           var15.updateDimensions();
                           var28.words.add(var16);
                           var28.width += var16.width;
                           var28.height = Math.max(var28.height, var16.height);
                        }
                     }

                     var9.lines.add(var11 + 1, var28 = new DrawLine());
                     if (var4 > 0 && var11 >= var4 - 1) {
                        if (var11 < var9.lines.size() - 1) {
                           var10 = false;
                        }
                        break;
                     }

                     ++var11;
                  }

                  var28.words.add(var15);
                  var28.width += var15.width;
               }

               if (var4 > 0 && var11 >= var4) {
                  break;
               }
            }
         }
      }

      if (var4 > 0) {
         while(var9.lines.size() > var4) {
            var10 = false;
            var9.lines.remove(var9.lines.size() - 1);
         }
      }

      FairCharacterGlyph[] var29 = null;
      if (!var10 && var6 != null) {
         var29 = FairCharacterGlyph.fromString(var6, "...");
         if (var2 > 0) {
            var28 = (DrawLine)var9.lines.get(var9.lines.size() - 1);
            float var32 = (Float)Arrays.stream(var29).reduce(0.0F, (var0, var1x) -> {
               return var0 + var1x.getDimensions().width;
            }, Float::sum);

            while(!var28.words.isEmpty() && !(var28.width + var32 <= (float)var2)) {
               DrawWord var33 = (DrawWord)var28.words.getLast();
               if (!var33.glyphs.isEmpty()) {
                  var33.glyphs.removeLast();
                  var33.updateDimensions();
                  var28.updateDimensions();
               }

               if (var33.glyphs.isEmpty()) {
                  var28.words.removeLast();
                  var28.updateDimensions();
               }
            }
         }
      }

      var9.lines.forEach((var2x) -> {
         var2x.drawWidth = var2x.width;
         if (var7) {
            var2x.stripWhitespaceLeft();
         }

         if (var8) {
            var2x.stripWhitespaceRight();
         }

      });
      Supplier var31 = null;
      var30 = new LinkedList();
      float var34 = 0.0F;
      float var35 = 0.0F;
      float var36 = 0.0F;
      float var37 = 0.0F;
      float var38 = 0.0F;
      final LinkedList var19 = new LinkedList();

      for(int var20 = 0; var20 < var9.lines.size(); ++var20) {
         DrawLine var21 = (DrawLine)var9.lines.get(var20);
         float var22 = 0.0F;
         if (var1 == FairType.TextAlign.CENTER) {
            var22 = -var21.drawWidth / 2.0F;
         } else if (var1 == FairType.TextAlign.RIGHT) {
            var22 = -var21.drawWidth;
         }

         var34 = Math.min(var34 == 0.0F ? 2.14748365E9F : var34, var22);
         var35 = Math.max(var35, var21.drawWidth);
         var36 += var21.height;
         Iterator var23 = var21.words.iterator();

         while(var23.hasNext()) {
            DrawWord var24 = (DrawWord)var23.next();

            GlyphIndex var26;
            for(Iterator var25 = var24.glyphs.iterator(); var25.hasNext(); var37 += var26.glyph.getDimensions().width) {
               var26 = (GlyphIndex)var25.next();
               Supplier var27 = var26.glyph.getDefaultColor();
               if (var27 != null) {
                  var31 = (Supplier)var27.get();
               }

               var30.add(new GlyphContainer(var26.glyph, var26.index, var20, var21.height, var37 + var22 + var21.drawOffsetX, var38 + var21.height, var31));
            }
         }

         if (var20 == var9.lines.size() - 1 && var29 != null) {
            float var42 = 0.0F;
            FairCharacterGlyph[] var43 = var29;
            int var44 = var29.length;

            for(int var45 = 0; var45 < var44; ++var45) {
               FairCharacterGlyph var46 = var43[var45];
               var19.add(new GlyphContainer(var46, -1, var20, var21.height, var37 + var42, var38 + var21.height, var31));
               var42 += var46.getDimensions().width;
            }

            var35 = Math.max(var35, var21.drawWidth + var42);
         }

         var38 += var21.height;
         var37 = 0.0F;
      }

      final FairType var39 = new FairType(this);
      final Rectangle var40 = new Rectangle((int)var34, 0, GameMath.ceil((double)var35), GameMath.ceil((double)var36));
      final boolean var41 = Settings.pixelFont;
      return new FairTypeDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8) {
         public void handleInputEvent(int var1, int var2, InputEvent var3) {
            Iterator var4 = var30.iterator();

            while(var4.hasNext()) {
               GlyphContainer var5 = (GlyphContainer)var4.next();
               if (var3.isUsed()) {
                  break;
               }

               var5.handleInputEvent(var1, var2, var3);
            }

         }

         public Rectangle getBoundingBox(int var1, int var2) {
            return new Rectangle(var40.x + var1, var40.y + var2, var40.width, var40.height);
         }

         public void draw(int var1, int var2, Color var3) {
            var30.forEach((var3x) -> {
               var3x.draw(var1, var2, var3);
            });
            var19.forEach((var3x) -> {
               var3x.draw(var1, var2, var3);
            });
         }

         public LinkedList<GlyphContainer> getDrawList() {
            return var30;
         }

         public int getLineCount() {
            return var9.lines.size();
         }

         public boolean displaysEverything() {
            return var10;
         }

         public FairType getType() {
            return var39;
         }

         public boolean shouldUpdate() {
            return var41 != Settings.pixelFont;
         }
      };
   }

   public static class Parse {
      public final TypeParser<?> parser;
      public final FairGlyph[] oldGlyphs;
      public final int start;
      public final int end;
      public final FairGlyph[] newGlyphs;

      private Parse(TypeParser<?> var1, FairGlyph[] var2, int var3, int var4, FairGlyph[] var5) {
         this.parser = var1;
         this.oldGlyphs = var2;
         this.start = var3;
         this.end = var4;
         this.newGlyphs = var5;
      }

      // $FF: synthetic method
      Parse(TypeParser var1, FairGlyph[] var2, int var3, int var4, FairGlyph[] var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }

   public static enum TextAlign {
      LEFT,
      CENTER,
      RIGHT;

      private TextAlign() {
      }

      // $FF: synthetic method
      private static TextAlign[] $values() {
         return new TextAlign[]{LEFT, CENTER, RIGHT};
      }
   }

   private class DrawVars {
      public ArrayList<DrawLine> lines;
      public DrawLine currentLine;

      private DrawVars() {
         this.lines = new ArrayList();
         this.currentLine = FairType.this.new DrawLine();
      }

      public void submitLine() {
         this.currentLine.submitWord();
         this.lines.add(this.currentLine);
         this.currentLine = FairType.this.new DrawLine();
      }

      // $FF: synthetic method
      DrawVars(Object var2) {
         this();
      }
   }

   private class DrawLine {
      public LinkedList<DrawWord> words;
      public DrawWord currentWord;
      public float drawWidth;
      public float drawOffsetX;
      public float width;
      public float height;

      private DrawLine() {
         this.words = new LinkedList();
         this.currentWord = FairType.this.new DrawWord();
      }

      public void submitWord() {
         this.words.add(this.currentWord);
         this.width += this.currentWord.width;
         this.height = Math.max(this.height, this.currentWord.height);
         this.currentWord = FairType.this.new DrawWord();
      }

      public void stripWhitespaceLeft() {
         if (!this.words.isEmpty()) {
            float var1 = this.whiteSpaceSize(this.words.iterator(), (var0) -> {
               return var0.glyphs.iterator();
            });
            this.drawOffsetX -= var1;
            this.drawWidth -= var1;
         }

      }

      public void stripWhitespaceRight() {
         if (!this.words.isEmpty()) {
            this.drawWidth -= this.whiteSpaceSize(this.words.descendingIterator(), (var0) -> {
               return var0.glyphs.descendingIterator();
            });
         }

      }

      public void updateDimensions() {
         this.width = 0.0F;
         this.height = 0.0F;

         DrawWord var2;
         for(Iterator var1 = this.words.iterator(); var1.hasNext(); this.height = Math.max(this.height, var2.height)) {
            var2 = (DrawWord)var1.next();
            this.width += var2.width;
         }

      }

      public float whiteSpaceSize(Iterator<DrawWord> var1, Function<DrawWord, Iterator<GlyphIndex>> var2) {
         float var3 = 0.0F;

         GlyphIndex var5;
         while(var1.hasNext()) {
            for(Iterator var4 = (Iterator)var2.apply((DrawWord)var1.next()); var4.hasNext(); var3 += var5.glyph.getDimensions().width) {
               var5 = (GlyphIndex)var4.next();
               if (!var5.glyph.isWhiteSpaceGlyph()) {
                  return var3;
               }
            }
         }

         return var3;
      }

      // $FF: synthetic method
      DrawLine(Object var2) {
         this();
      }
   }

   private class DrawWord {
      public LinkedList<GlyphIndex> glyphs;
      public float width;
      public float height;

      private DrawWord() {
         this.glyphs = new LinkedList();
      }

      public void updateDimensions() {
         this.width = 0.0F;
         this.height = 0.0F;

         FloatDimension var3;
         for(Iterator var1 = this.glyphs.iterator(); var1.hasNext(); this.height = Math.max(this.height, var3.height)) {
            GlyphIndex var2 = (GlyphIndex)var1.next();
            var3 = var2.glyph.getDimensions();
            this.width += var3.width;
         }

      }

      public String toString() {
         return Arrays.toString(this.glyphs.toArray());
      }

      // $FF: synthetic method
      DrawWord(Object var2) {
         this();
      }
   }

   private static class GlyphIndex {
      public final int index;
      public final FairGlyph glyph;

      public GlyphIndex(int var1, FairGlyph var2) {
         this.index = var1;
         this.glyph = var2;
      }

      public String toString() {
         return this.index + ":" + this.glyph.toString();
      }
   }
}
