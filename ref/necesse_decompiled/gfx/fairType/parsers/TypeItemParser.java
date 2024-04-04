package necesse.gfx.fairType.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveSyntaxException;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class TypeItemParser extends TypeParser<ItemParserResult> {
   public static final Pattern ITEM_PATTERN = Pattern.compile("\\[item=(\\w+)](\\{.+})?");
   public static final Pattern ITEMS_PATTERN = Pattern.compile("\\[items=(.+)]");
   public static final Pattern ITEM_IN_ITEMS_PATTERN = Pattern.compile("(\\w+)(\\{.+})?");
   public final int size;
   public final boolean allowGND;

   public TypeItemParser(int var1, boolean var2) {
      this.size = var1;
      this.allowGND = var2;
   }

   public TypeItemParser(int var1) {
      this(var1, true);
   }

   public ItemParserResult getMatchResult(FairGlyph[] var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      FairGlyph[] var4 = var1;
      int var5 = var1.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         FairGlyph var7 = var4[var6];
         var3.append(var7.getCharacter());
      }

      Matcher var20 = ITEMS_PATTERN.matcher(var3.toString());
      String var21;
      if (!var20.find(var2)) {
         var20 = ITEM_PATTERN.matcher(var3.toString());
         if (var20.find(var2)) {
            var21 = var20.group(1);
            var6 = var20.end(1) + 1;
            String var23 = null;
            if (this.allowGND && var20.groupCount() > 1 && var20.group(2) != null) {
               String var24 = var20.group(2);

               try {
                  int var26 = SaveComponent.getSectionStop(var24, '{', '}', 0);
                  var23 = var24.substring(0, var26 + 1);
                  var6 = var20.start(2) + var26 + 1;
               } catch (SaveSyntaxException var19) {
               }
            }

            InventoryItem var25 = this.constructItem(var21, var23);
            return new ItemParserResult(var20.start(), var6, var25);
         } else {
            return null;
         }
      } else {
         var21 = var20.group(1);
         var6 = var20.end(1) + 1;

         try {
            int var22 = SaveComponent.getSectionStop(var21, '[', ']', 0);
            var21 = var21.substring(0, var22 + 1);
            var6 = var20.start(2) + var22 + 1;
         } catch (SaveSyntaxException var18) {
         }

         Matcher var8 = ITEM_IN_ITEMS_PATTERN.matcher(var21);
         ArrayList var9 = new ArrayList();
         int var10 = 0;

         while(var8.find(var10)) {
            String var11 = var8.group(1);
            int var12 = var8.end(1) + 1;
            String var13 = null;
            if (this.allowGND && var8.groupCount() > 1 && var8.group(2) != null) {
               String var14 = var8.group(2);

               try {
                  int var15 = SaveComponent.getSectionStop(var14, '{', '}', 0);
                  var13 = var14.substring(0, var15 + 1);
                  var12 = var8.start(2) + var15 + 1;
               } catch (SaveSyntaxException var17) {
               }
            }

            InventoryItem var27 = this.constructItem(var11, var13);
            if (var27 != null) {
               var9.add(var27);
            }

            var10 = var12;
            if (var12 >= var21.length()) {
               break;
            }
         }

         return new ItemParserResult(var20.start(), var6, var9);
      }
   }

   public FairGlyph[] parse(ItemParserResult var1, FairGlyph[] var2) {
      return var1.items.isEmpty() ? var2 : new FairGlyph[]{new FairItemGlyph(this.size, var1.items)};
   }

   public InventoryItem constructItem(String var1, String var2) {
      Item var3 = ItemRegistry.getItem(var1);
      if (var3 == null) {
         return null;
      } else {
         InventoryItem var4 = new InventoryItem(var3);
         if (var2 != null) {
            try {
               LoadData var5 = new LoadData(var2);
               GNDItemMap var6 = new GNDItemMap(var5);
               if (var6 != null) {
                  var4.setGndData(var6);
               }
            } catch (Exception var7) {
            }
         }

         return var4;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FairGlyph[] parse(TypeParserResult var1, FairGlyph[] var2) {
      return this.parse((ItemParserResult)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public TypeParserResult getMatchResult(FairGlyph[] var1, int var2) {
      return this.getMatchResult(var1, var2);
   }

   public static class ItemParserResult extends TypeParserResult {
      public final List<InventoryItem> items;

      public ItemParserResult(int var1, int var2, List<InventoryItem> var3) {
         super(var1, var2);
         this.items = var3;
      }

      public ItemParserResult(int var1, int var2, InventoryItem var3) {
         this(var1, var2, var3 == null ? Collections.emptyList() : Collections.singletonList(var3));
      }
   }
}
