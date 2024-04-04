package necesse.gfx.forms.presets.playerStats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemDisplayComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class PlayerStatsObtainedItemsForm extends Form implements PlayerStatsSubForm {
   private final PlayerStatsForm statsForm;
   private final FormContentBox itemList;
   private final FormDropdownSelectionButton<ListFilter> filterButton;
   private final Runnable backPressed;

   public PlayerStatsObtainedItemsForm(PlayerStatsForm var1, PlayerStats var2, int var3, Runnable var4) {
      super(var1.getWidth(), var1.getHeight());
      this.statsForm = var1;
      this.backPressed = var4;
      this.drawBase = false;
      LinkedList var5 = new LinkedList();
      Iterator var6 = var2.items_obtained.getStatItemsObtained().iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         var5.add(var7);
      }

      List var8 = ItemRegistry.getItems();
      var8.removeIf((var1x) -> {
         return !ItemRegistry.countsInStats(var1x.getID()) || var5.contains(var1x.getStringID());
      });
      if (var2.items_obtained.getTotalStatItems() >= ItemRegistry.getTotalStatItemsObtainable()) {
         this.addComponent(new FormLocalLabel("stats", "have_all_items", new FontOptions(20), 0, this.getWidth() / 2, var3, this.getWidth()));
         this.filterButton = null;
         this.itemList = null;
      } else {
         this.addComponent(new FormLocalLabel("stats", "filter", new FontOptions(16), -1, 5, var3));
         this.filterButton = (FormDropdownSelectionButton)this.addComponent(new FormDropdownSelectionButton(4, 20 + var3, FormInputSize.SIZE_20, ButtonColor.BASE, this.getWidth() - 8));
         this.filterButton.options.add(PlayerStatsObtainedItemsForm.ListFilter.ALL, PlayerStatsObtainedItemsForm.ListFilter.ALL.displayName);
         this.filterButton.options.add(PlayerStatsObtainedItemsForm.ListFilter.OBTAINED, PlayerStatsObtainedItemsForm.ListFilter.OBTAINED.displayName);
         this.filterButton.options.add(PlayerStatsObtainedItemsForm.ListFilter.NOT_OBTAINED, PlayerStatsObtainedItemsForm.ListFilter.NOT_OBTAINED.displayName);
         this.filterButton.setSelected(PlayerStatsObtainedItemsForm.ListFilter.ALL, PlayerStatsObtainedItemsForm.ListFilter.ALL.displayName);
         this.filterButton.onSelected((var2x) -> {
            this.updateList(var2, (ListFilter)var2x.value);
         });
         this.itemList = (FormContentBox)this.addComponent(new FormContentBox(0, 40 + var3, this.getWidth(), this.getHeight() - 40 - var3));
         this.updateList(var2, (ListFilter)this.filterButton.getSelected());
      }

   }

   public boolean backPressed() {
      this.backPressed.run();
      return true;
   }

   public void updateList(PlayerStats var1) {
      if (this.filterButton != null) {
         this.updateList(var1, (ListFilter)this.filterButton.getSelected());
      }
   }

   private void updateList(PlayerStats var1, ListFilter var2) {
      if (this.itemList != null) {
         this.itemList.clearComponents();
         byte var3 = 32;
         byte var4 = 2;
         int var5 = (this.getWidth() - var4) / (var3 + var4);
         int var6 = (this.getWidth() - var4) % (var3 + var4) / 2;
         ArrayList var7 = new ArrayList();
         Iterator var8 = ItemRegistry.getItems().iterator();

         while(var8.hasNext()) {
            Item var9 = (Item)var8.next();
            if (ItemRegistry.countsInStats(var9.getID()) && (Boolean)var2.filter.apply(var9, var1)) {
               var7.add(var9.getDefaultItem((PlayerMob)null, 1));
            }
         }

         var7.sort((Comparator)null);

         for(int var13 = 0; var13 < var7.size(); ++var13) {
            InventoryItem var14 = (InventoryItem)var7.get(var13);
            int var10 = var13 % var5;
            int var11 = var13 / var5;
            final boolean var12 = var1.items_obtained.isStatItemObtained(var14.item.getStringID());
            this.itemList.addComponent(new FormItemDisplayComponent(var10 * (var3 + var4), var11 * (var3 + var4), var14) {
               public TextureDrawOptionsEnd changeDrawOptions(TextureDrawOptionsEnd var1) {
                  return !var12 ? var1.color(0.1F) : super.changeDrawOptions(var1);
               }

               public GameTooltips getTooltip() {
                  StringTooltips var1 = new StringTooltips(this.item.getItemDisplayName(), this.item.item.getRarityColor(this.item));
                  if (var12) {
                     var1.add(Localization.translate("stats", "show_items_obtained"), GameColor.GREEN);
                  } else {
                     var1.add(Localization.translate("stats", "show_items_not_obtained"), GameColor.RED);
                  }

                  return var1;
               }
            });
         }

         this.itemList.fitContentBoxToComponents(var6, 0, var4, var4);
      }
   }

   public void updateDisabled(int var1) {
      this.setPosition(0, var1);
      this.setHeight(this.statsForm.getHeight() - var1);
      if (this.itemList != null) {
         this.itemList.setHeight(this.getHeight() - this.itemList.getY());
      }

   }

   private static enum ListFilter {
      ALL(new LocalMessage("stats", "show_items_all"), (var0, var1) -> {
         return true;
      }),
      OBTAINED(new LocalMessage("stats", "show_items_obtained"), (var0, var1) -> {
         return var1.items_obtained.isStatItemObtained(var0.getStringID());
      }),
      NOT_OBTAINED(new LocalMessage("stats", "show_items_not_obtained"), (var0, var1) -> {
         return !var1.items_obtained.isStatItemObtained(var0.getStringID());
      });

      public final GameMessage displayName;
      public final BiFunction<Item, PlayerStats, Boolean> filter;

      private ListFilter(GameMessage var3, BiFunction var4) {
         this.displayName = var3;
         this.filter = var4;
      }

      // $FF: synthetic method
      private static ListFilter[] $values() {
         return new ListFilter[]{ALL, OBTAINED, NOT_OBTAINED};
      }
   }
}
