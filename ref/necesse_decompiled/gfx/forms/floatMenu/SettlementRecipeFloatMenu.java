package necesse.gfx.forms.floatMenu;

import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormSettlementWorkstationRecipeList;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;

public abstract class SettlementRecipeFloatMenu extends FormFloatMenu {
   public SettlementRecipeFloatMenu(FormComponent var1, int var2, int var3, PlayerMob var4, SettlementWorkstationLevelObject var5) {
      super(var1);
      Form var6 = new Form(var2, var3);
      FormSettlementWorkstationRecipeList var7 = (FormSettlementWorkstationRecipeList)var6.addComponent(new FormSettlementWorkstationRecipeList(0, 28, var6.getWidth(), var6.getHeight() - 32, var5) {
         public void onRecipeClicked(Recipe var1, PlayerMob var2) {
            SettlementRecipeFloatMenu.this.onRecipeClicked(var1, var2);
         }
      });
      FormTextInput var8 = (FormTextInput)var6.addComponent(new FormTextInput(4, 4, FormInputSize.SIZE_24, var6.getWidth() - 8, 50), 1000);
      var8.placeHolder = new LocalMessage("ui", "searchtip");
      var8.rightClickToClear = true;
      var8.onChange((var2x) -> {
         var7.setFilter((var2) -> {
            return var2.resultItem.item.matchesSearch(var2.resultItem, var4, ((FormTextInput)var2x.from).getText());
         });
      });
      this.setForm(var6);
   }

   public abstract void onRecipeClicked(Recipe var1, PlayerMob var2);
}
