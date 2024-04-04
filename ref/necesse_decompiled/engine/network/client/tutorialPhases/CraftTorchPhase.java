package necesse.engine.network.client.tutorialPhases;

import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.control.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.Form;
import necesse.level.maps.hudManager.HudDrawElement;

public class CraftTorchPhase extends TutorialPhase {
   private LocalMessage openInv;
   private LocalMessage craftTorch;
   private HudDrawElement drawElement;

   public CraftTorchPhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void start() {
      super.start();
      this.openInv = new LocalMessage("tutorials", "openinv");
      this.craftTorch = new LocalMessage("tutorials", "crafttorch");
      this.drawElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, final PlayerMob var3) {
            if (!var3.isInventoryExtended()) {
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return Integer.MAX_VALUE;
                  }

                  public void draw(TickManager var1) {
                     FairTypeDrawOptions var2x = CraftTorchPhase.this.getTextDrawOptions(CraftTorchPhase.this.getTutorialText(CraftTorchPhase.this.openInv.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.INVENTORY)));
                     DrawOptions var3x = CraftTorchPhase.this.getLevelTextDrawOptions(var2x, var3.getX(), var3.getY() - 50, var2, var3, false);
                     var3x.draw();
                  }
               });
            }

         }
      };
      this.client.getLevel().hudManager.addElement(this.drawElement);
   }

   public void end() {
      super.end();
      if (this.drawElement != null) {
         this.drawElement.remove();
      }

      this.drawElement = null;
   }

   public void updateObjective(MainGame var1) {
      this.setObjective(var1, "treestorch");
   }

   public void tick() {
      if (this.client.getPlayer().getInv().getAmount(ItemRegistry.getItem("torch"), false, false, false, "tutorial") > 0) {
         this.over();
      }

   }

   public void drawOverForm(PlayerMob var1) {
      if (var1.isInventoryExtended()) {
         Form var2 = ((MainGame)GlobalData.getCurrentState()).formManager.crafting;
         FairTypeDrawOptions var3 = this.getTextDrawOptions(this.getTutorialText(this.craftTorch.translate()));
         this.getTextDrawOptions(var3, (FairTypeDrawOptions)null, var2.getX() + var2.getWidth() / 2 - 40, var2.getY(), true).draw();
      }

   }
}
