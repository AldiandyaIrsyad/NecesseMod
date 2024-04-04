package necesse.inventory.recipe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.MapIterator;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairColorChangeGlyph;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairSpacerGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class Ingredient {
   public final String ingredientStringID;
   private int ingredientID;
   private int itemAmount;
   private boolean requiredToShow;
   private boolean isGlobalIngredient;

   public Ingredient(String var1, int var2, boolean var3) {
      this.ingredientStringID = var1;
      this.itemAmount = var2;
      this.requiredToShow = var3;
      int var4 = ItemRegistry.getItemID(var1);
      this.ingredientID = -1;
      if (var4 == -1) {
         this.isGlobalIngredient = true;
         this.ingredientID = GlobalIngredientRegistry.getGlobalIngredientID(var1);
      } else {
         this.isGlobalIngredient = false;
         this.ingredientID = var4;
      }

   }

   public Ingredient(String var1, int var2) {
      this(var1, var2, false);
   }

   public Ingredient(PacketReader var1) {
      this.isGlobalIngredient = var1.getNextBoolean();
      this.ingredientID = var1.getNextShort();
      if (this.isGlobalIngredient) {
         this.ingredientStringID = GlobalIngredientRegistry.getGlobalIngredient(this.ingredientID).getStringID();
      } else {
         this.ingredientStringID = ItemRegistry.getItem(this.ingredientID).getStringID();
      }

      this.itemAmount = var1.getNextShort();
      this.requiredToShow = var1.getNextBoolean();
   }

   public void writePacket(PacketWriter var1) {
      var1.putNextBoolean(this.isGlobalIngredient());
      var1.putNextShortUnsigned(this.getIngredientID());
      var1.putNextShortUnsigned(this.getIngredientAmount());
      var1.putNextBoolean(this.requiredToShow());
   }

   public int getIngredientID() {
      return this.ingredientID;
   }

   public String getDisplayName() {
      return this.isGlobalIngredient() ? this.getGlobalIngredient().displayName.translate() : ItemRegistry.getDisplayName(this.getIngredientID());
   }

   public int getIngredientAmount() {
      return this.itemAmount;
   }

   public boolean requiredToShow() {
      return this.requiredToShow;
   }

   public boolean isGlobalIngredient() {
      return this.isGlobalIngredient;
   }

   public boolean matchesItem(Item var1) {
      if (this.isGlobalIngredient()) {
         return var1.isGlobalIngredient(this.getGlobalIngredient());
      } else {
         return var1.getID() == this.getIngredientID();
      }
   }

   public boolean equals(Ingredient var1) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.isGlobalIngredient != var1.isGlobalIngredient) {
         return false;
      } else if (this.ingredientID != var1.ingredientID) {
         return false;
      } else {
         return this.itemAmount == var1.itemAmount;
      }
   }

   public GlobalIngredient getGlobalIngredient() {
      return !this.isGlobalIngredient() ? null : GlobalIngredientRegistry.getGlobalIngredient(this.getIngredientID());
   }

   public boolean matchesSearch(PlayerMob var1, String var2) {
      if (this.isGlobalIngredient()) {
         GlobalIngredient var4 = this.getGlobalIngredient();
         return var4.getRegisteredItemIDs().stream().map(ItemRegistry::getItem).filter(Objects::nonNull).anyMatch((var2x) -> {
            return var2x.matchesSearch(var2x.getDefaultItem(var1, 1), var1, var2);
         });
      } else {
         Item var3 = ItemRegistry.getItem(this.getIngredientID());
         return var3 == null ? false : var3.matchesSearch(var3.getDefaultItem(var1, 1), var1, var2);
      }
   }

   public boolean hasIngredient(Level var1, PlayerMob var2, Inventory var3) {
      return this.hasIngredient(var1, var2, (Iterable)Collections.singletonList(var3));
   }

   public boolean hasIngredient(Level var1, PlayerMob var2, Iterable<Inventory> var3) {
      return this.hasIngredientRange(var1, var2, () -> {
         return new MapIterator(var3.iterator(), InventoryRange::new);
      });
   }

   public boolean hasIngredientRange(Level var1, PlayerMob var2, InventoryRange var3) {
      return this.hasIngredientRange(var1, var2, (Iterable)Collections.singletonList(var3));
   }

   public boolean hasIngredientRange(Level var1, PlayerMob var2, Iterable<InventoryRange> var3) {
      AtomicInteger var4 = new AtomicInteger();
      AtomicBoolean var5 = new AtomicBoolean();
      boolean var6 = this.itemAmount > 0;
      HashMapSet var7 = new HashMapSet();
      Iterator var8 = var3.iterator();

      while(var8.hasNext()) {
         InventoryRange var9 = (InventoryRange)var8.next();
         if (var9.inventory.canBeUsedForCrafting()) {
            var9.inventory.countIngredientAmount(var1, var2, var9.startSlot, var9.endSlot, (var4x, var5x, var6x) -> {
               if (var4x == null || var4x.canBeUsedForCrafting()) {
                  if (!((HashSet)var7.get(var4x)).contains(var5x)) {
                     if (this.matchesItem(var6x.item)) {
                        var5.set(true);
                        var4.addAndGet(var6x.getAmount());
                     }

                     var7.add(var4x, var5x);
                  }
               }
            });
            if (var6) {
               if (var4.get() >= this.getIngredientAmount()) {
                  break;
               }
            } else if (var5.get()) {
               break;
            }
         }
      }

      if (var6) {
         return var5.get();
      } else {
         return var4.get() >= this.getIngredientAmount();
      }
   }

   public int getIngredientHash() {
      int var1 = 1;
      var1 = var1 * 17 + this.ingredientStringID.hashCode();
      var1 = var1 * 31 + this.itemAmount;
      return var1;
   }

   public Item getDisplayItem() {
      if (this.isGlobalIngredient()) {
         GlobalIngredient var1 = this.getGlobalIngredient();
         ArrayList var2 = var1.getObtainableRegisteredItemIDs();
         if (var2.isEmpty()) {
            var2 = var1.getRegisteredItemIDs();
         }

         int var3 = (int)(System.currentTimeMillis() / 1000L % (long)var2.size());
         return ItemRegistry.getItem((Integer)var2.get(var3));
      } else {
         return ItemRegistry.getItem(this.getIngredientID());
      }
   }

   public GameColor getCanCraftColor(int var1) {
      Item var2 = this.getDisplayItem();
      boolean var3 = this.getIngredientAmount() == 0 ? var1 == -1 : var1 >= this.getIngredientAmount();
      if (var3) {
         if (this.isGlobalIngredient()) {
            return GameColor.WHITE;
         } else {
            return var2 == null ? GameColor.WHITE : var2.getRarityColor(var2.getDefaultItem((PlayerMob)null, 1));
         }
      } else {
         return GameColor.RED;
      }
   }

   public GameTooltips getTooltips() {
      return this.getTooltips(this.getIngredientAmount(), false);
   }

   public GameTooltips getTooltips(int var1, boolean var2) {
      return new FairTypeTooltip(this.getTooltipText((new FontOptions(Settings.tooltipTextSize)).outline(), var1, new FairColorChangeGlyph(this.getCanCraftColor(var1)), var2), 10);
   }

   public FairType getTooltipText(FontOptions var1, int var2, Color var3, Color var4, boolean var5) {
      boolean var7 = this.getIngredientAmount() == 0 ? var2 == -1 : var2 >= this.getIngredientAmount();
      Color var6;
      if (var7) {
         var6 = var3;
      } else {
         var6 = var4;
      }

      return this.getTooltipText(var1, var2, new FairColorChangeGlyph(var6), var5);
   }

   public FairType getTooltipText(FontOptions var1, int var2, FairColorChangeGlyph var3, boolean var4) {
      FairType var5 = new FairType();
      Item var6 = this.getDisplayItem();
      if (var3 != null) {
         var5.append(var3);
      }

      if (this.getIngredientAmount() > 0) {
         if (var6 != null) {
            var5.append(new FairItemGlyph(var1.getSize(), var6.getDefaultItem((PlayerMob)null, 1)));
            var5.append(new FairSpacerGlyph(5.0F, 2.0F));
         }

         var5.append(var1, GameUtils.formatNumber((long)this.getIngredientAmount()));
         if (var4) {
            FontOptions var7 = var1.copy().size(var1.getSize() - 4);
            var5.append("/" + GameUtils.metricNumber((long)var2), (var1x) -> {
               FairCharacterGlyph var2 = new FairCharacterGlyph(var7, var1x);
               var2.drawYOffset = -1;
               return var2;
            });
         }

         var5.append(var1, " " + this.getDisplayName());
      } else if (var6 != null) {
         FairCharacterGlyph[] var9 = FairCharacterGlyph.fromString(var1, this.getDisplayName());
         FairGlyph[] var8 = (FairGlyph[])GameUtils.concat(new FairGlyph[]{new FairItemGlyph(var1.getSize(), var6.getDefaultItem((PlayerMob)null, 1)), new FairSpacerGlyph(5.0F, 2.0F)}, var9);
         var5.append(var1, Localization.translate("misc", "ingredientmusthave"));
         var5.replaceAll("<ingredient>", var8);
      } else {
         var5.append(var1, Localization.translate("misc", "ingredientmusthave", "ingredient", this.getDisplayName()));
      }

      if (this.requiredToShow()) {
         var5.append(var1, "*");
      }

      return var5;
   }
}
