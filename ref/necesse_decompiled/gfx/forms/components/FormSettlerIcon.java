package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HUD;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class FormSettlerIcon extends FormButton implements FormPositionContainer {
   private FormPosition position;
   private Settler settler;
   private Mob mob;
   private SettlementContainerForm<?> containerForm;
   public Supplier<GameTooltips> extraTooltips;

   public FormSettlerIcon(int var1, int var2, Settler var3, Mob var4, SettlementContainerForm<?> var5) {
      this.position = new FormFixedPosition(var1, var2);
      this.settler = var3;
      this.mob = var4;
      this.containerForm = var5;
      if (var5 != null) {
         this.onClicked((var2x) -> {
            var5.selectedSettlers.selectOrDeselectSettler(var4.getUniqueID());
         });
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.settler.getSettlerFaceDrawOptions(this.getX() + 16, this.getY() + 16, this.mob).draw();
      if (this.containerForm != null && this.containerForm.selectedSettlers.contains(this.mob.getUniqueID())) {
         HUD.selectBoundOptions(new Color(255, 255, 255), false, this.getX(), this.getY(), this.getX() + 32, this.getY() + 32).draw();
      }

      if (this.isHovering()) {
         ListGameTooltips var4 = new ListGameTooltips(this.settler.getGenericMobName());
         if (this.containerForm != null) {
            if (Input.lastInputIsController) {
               var4.add((Object)(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("ui", "settlementselectsettler"))));
            } else {
               var4.add((Object)(new InputTooltip(-100, Localization.translate("ui", "settlementselectsettler"))));
            }
         }

         if (this.extraTooltips != null) {
            GameTooltips var5 = (GameTooltips)this.extraTooltips.get();
            if (var5 != null) {
               var4.add((Object)var5);
            }
         }

         Screen.addTooltip(var4, TooltipLocation.FORM_FOCUS);
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), 32, 32));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
