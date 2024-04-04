package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormDialogueOption;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.mob.ExplorerContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.level.maps.biomes.Biome;

public class ExplorerContainerForm<T extends ExplorerContainer> extends ShopContainerForm<T> {
   public DialogueForm findIsland;
   public DialogueForm findIslandFocus;
   public DialogueForm foundBiome;
   public int islandBuyCost;
   public FormDialogueOption islandBuyButton;

   public ExplorerContainerForm(Client var1, T var2) {
      super(var1, var2, 408, 170, 240);
      GameMessage var3 = MobRegistry.getLocalization(var2.humanShop.getID());
      this.findIsland = (DialogueForm)this.addComponent(new DialogueForm("findIsland", 408, 220, var3, new LocalMessage("ui", "explorerselectisland")));
      this.findIsland.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.dialogueForm);
      });
      List var4 = BiomeRegistry.getBiomes();
      var4.sort(Comparator.comparing((var0) -> {
         return var0.getLocalization().translate();
      }));
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         Biome var6 = (Biome)var5.next();
         if (var6 != BiomeRegistry.UNKNOWN && var2.getFindIslandPrice(var6) >= 0) {
            this.findIsland.addDialogueOption(var6.getLocalization(), () -> {
               this.focusOnIsland(var6);
            });
         }
      }

      this.foundBiome = (DialogueForm)this.addComponent(new DialogueForm("foundBiome", 408, 160, var3, (GameMessage)null));
      if (var2.knowBiomeAlready) {
         this.foundBiome.reset(var3, (GameMessage)(new LocalMessage("ui", "explorerknowalready", "biome", var2.foundBiome.getLocalization())));
         DialogueForm var10000 = this.foundBiome;
         LocalMessage var10001 = new LocalMessage("ui", "explorerfindnew");
         EmptyCustomAction var10002 = var2.findNewIslandButton;
         Objects.requireNonNull(var10002);
         var10000.addDialogueOption(var10001, var10002::runAndSend);
         this.foundBiome.addDialogueOption(new LocalMessage("ui", "cancelbutton"), () -> {
            var2.acceptFoundIslandButton.runAndSend();
            if (var2.humanShop.missionFailed) {
               this.makeCurrent(this.missionFailedForm);
            } else {
               this.makeCurrent(this.dialogueForm);
            }

         });
      } else {
         LocalMessage var7;
         if (var2.foundIslandCoord != null) {
            var7 = new LocalMessage("ui", "explorerfoundisland", new Object[]{"biome", var2.foundBiome.getLocalization(), "dir", TravelDir.getDeltaDir(var1.getLevel().getIslandX(), var1.getLevel().getIslandY(), var2.foundIslandCoord.x, var2.foundIslandCoord.y).dirMessage});
         } else {
            var7 = new LocalMessage("ui", "explorerfoundfail");
         }

         this.foundBiome.reset(var3, (GameMessage)var7);
         this.foundBiome.addDialogueOption(new LocalMessage("ui", "acceptbutton"), () -> {
            var2.acceptFoundIslandButton.runAndSend();
            if (var2.humanShop.missionFailed) {
               this.makeCurrent(this.missionFailedForm);
            } else {
               this.makeCurrent(this.dialogueForm);
            }

         });
      }

      this.findIslandFocus = (DialogueForm)this.addComponent(new DialogueForm("findIslandFocus", 408, 160, (GameMessage)null, (GameMessage)null));
      this.onWindowResized();
      if (var2.foundIsland) {
         this.makeCurrent(this.foundBiome);
      }

   }

   private void focusOnIsland(Biome var1) {
      this.islandBuyCost = ((ExplorerContainer)this.container).getFindIslandPrice(var1);
      this.findIslandFocus.reset(MobRegistry.getLocalization(((ExplorerContainer)this.container).humanShop.getID()), (GameMessage)(new LocalMessage("ui", "explorerfindcost", new Object[]{"biome", var1.getLocalization(), "cost", this.islandBuyCost})));
      this.islandBuyButton = this.findIslandFocus.addDialogueOption(new LocalMessage("ui", "buybutton"), () -> {
         ((ExplorerContainer)this.container).buyFindIslandButton.runAndSend(var1.getID());
      });
      this.findIslandFocus.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.findIsland);
         this.islandBuyButton = null;
      });
      this.updateIslandBuyButton(this.client.getPlayer());
      this.makeCurrent(this.findIslandFocus);
   }

   protected void setupExtraDialogueOptions() {
      super.setupExtraDialogueOptions();
      if (((ExplorerContainer)this.container).humanShop != null && ((ExplorerContainer)this.container).humanShop.isSettlerOnCurrentLevel()) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "explorerfindisland"), () -> {
            this.makeCurrent(this.findIsland);
         });
      }

   }

   public void updateIslandBuyButton(PlayerMob var1) {
      if (this.islandBuyButton != null) {
         int var2 = var1.getInv().getAmount(ItemRegistry.getItem("coin"), true, false, false, "buy");
         this.islandBuyButton.setActive(var2 >= this.islandBuyCost);
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.findIsland);
      ContainerComponent.setPosFocus(this.findIslandFocus);
      ContainerComponent.setPosFocus(this.foundBiome);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.updateIslandBuyButton(var2);
      super.draw(var1, var2, var3);
   }

   public boolean shouldOpenInventory() {
      return true;
   }
}
