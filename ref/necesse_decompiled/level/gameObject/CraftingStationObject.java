package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;

public class CraftingStationObject extends GameObject implements SettlementWorkstationObject {
   public CraftingStationObject() {
      this.displayMapTooltip = true;
      this.setItemCategory(new String[]{"objects", "craftingstations"});
      this.replaceCategories.add("workstation");
      this.canReplaceCategories.add("workstation");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("furniture");
   }

   public CraftingStationObject(Rectangle var1) {
      super(var1);
      this.displayMapTooltip = true;
      this.setItemCategory(new String[]{"objects", "craftingstations"});
      this.replaceCategories.add("workstation");
      this.canReplaceCategories.add("workstation");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("furniture");
   }

   public Tech[] getCraftingTechs() {
      return new Tech[]{RecipeTechRegistry.NONE};
   }

   public GameMessage getCraftingHeader() {
      return this.getLocalization();
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         CraftingStationContainer.openAndSendContainer(ContainerRegistry.CRAFTING_STATION_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public int getCraftingObjectID() {
      return this.getMultiTile(0).getMasterObject().getID();
   }

   public Stream<Recipe> streamSettlementRecipes(Level var1, int var2, int var3) {
      Tech[] var4 = this.getCraftingTechs();
      return Recipes.streamRecipes().filter((var1x) -> {
         return var1x.matchesTechs(var4);
      });
   }
}
