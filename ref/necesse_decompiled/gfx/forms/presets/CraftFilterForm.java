package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.recipe.RecipeFilter;

public class CraftFilterForm extends Form {
   private final RecipeFilter filter;
   private final Supplier<Boolean> hasUpdated;
   private final FormTextInput search;
   private final FormCheckBox hidden;
   private final FormCheckBox craftable;
   private HashMap<String, FormCheckBox> categories = new HashMap();

   public CraftFilterForm(String var1, int var2, int var3, int var4, RecipeFilter var5) {
      super(var1, var2, var3);
      this.filter = var5;
      this.hasUpdated = var5.addMonitor(this);
      this.search = (FormTextInput)this.addComponent(new FormTextInput(4, 4, FormInputSize.SIZE_24, this.getWidth() - 8, 100));
      this.search.rightClickToClear = true;
      this.search.placeHolder = new LocalMessage("ui", "searchtip");
      this.search.setText(var5.getSearchFilter());
      this.search.onChange((var2x) -> {
         var5.setSearchFilter(this.search.getText());
      });
      FormContentBox var6 = (FormContentBox)this.addComponent(new FormContentBox(0, 32, this.getWidth(), this.getHeight() - 24 - 16));
      FormFlow var7 = new FormFlow(5);
      this.hidden = ((<undefinedtype>)var6.addComponent(new FormLocalCheckBox("ui", "showhidden", 4, var7.next(20), var5.showHidden()) {
         public GameTooltips getTooltip() {
            return new StringTooltips(Localization.translate("ui", "craftshowhiddentip"), 400);
         }
      })).onClicked((var1x) -> {
         var5.setShowHidden(((FormCheckBox)var1x.from).checked);
      });
      this.search.controllerDownFocus = this.hidden;
      this.craftable = ((FormLocalCheckBox)var6.addComponent(new FormLocalCheckBox("ui", "filteronlycraftable", 4, var7.next(20), var5.craftableOnly()))).onClicked((var1x) -> {
         var5.setCraftableOnly(((FormCheckBox)var1x.from).checked);
      });
      ItemCategory.masterCategory.streamChildren().sorted().forEach((var4x) -> {
         FormLocalCheckBox var5x = (FormLocalCheckBox)var6.addComponent((FormLocalCheckBox)var7.nextY(new FormLocalCheckBox(var4x.displayName, 4, var7.next(), var5.containsCategoryFilter(var4x.stringID), this.getWidth() - 10), 4));
         var5x.onClicked((var2) -> {
            if (((FormCheckBox)var2.from).checked) {
               var5.addCategoryFilter(var4x.stringID);
            } else {
               var5.removeCategoryFilter(var4x.stringID);
            }

         });
         this.categories.put(var4x.stringID, var5x);
      });
      int var8 = var7.next() + 5 + var6.getY();
      this.setHeight(GameMath.limit(var8, var3, var4));
      var6.setHeight(this.getHeight() - 24 - 8);
      var6.setContentBox(new Rectangle(0, 0, this.getWidth(), var7.next() + 5));
   }

   public void updateFilter() {
      if (!this.search.isTyping()) {
         this.search.setText(this.filter.getSearchFilter());
      }

      this.hidden.checked = this.filter.showHidden();
      this.craftable.checked = this.filter.craftableOnly();

      Map.Entry var2;
      for(Iterator var1 = this.categories.entrySet().iterator(); var1.hasNext(); ((FormCheckBox)var2.getValue()).checked = this.filter.containsCategoryFilter((String)var2.getKey())) {
         var2 = (Map.Entry)var1.next();
      }

   }

   public void selectAllTextAndSetTypingTrue() {
      this.search.setTyping(true);
      this.search.selectAll();
   }

   public void setHidden(boolean var1) {
      super.setHidden(var1);
      if (var1 && this.search.isTyping()) {
         this.search.setTyping(false);
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if ((Boolean)this.hasUpdated.get()) {
         this.updateFilter();
      }

      super.draw(var1, var2, var3);
   }

   public void dispose() {
      super.dispose();
      this.filter.removeMonitor(this);
   }
}
