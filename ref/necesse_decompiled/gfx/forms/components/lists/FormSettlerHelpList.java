package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class FormSettlerHelpList extends FormGeneralGridList<ListSettler> {
   public FormSettlerHelpList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 36, 36);
      this.reset();
   }

   public void reset() {
      super.reset();
      Iterator var1 = SettlerRegistry.getSettlers().iterator();

      while(var1.hasNext()) {
         Settler var2 = (Settler)var1.next();
         this.elements.add(new ListSettler(var2));
      }

   }

   protected static class ListSettler extends FormListGridElement<FormSettlerHelpList> {
      public final Settler settler;

      public ListSettler(Settler var1) {
         this.settler = var1;
      }

      void draw(FormSettlerHelpList var1, TickManager var2, PlayerMob var3, int var4) {
         if (this.isMouseOver(var1)) {
            Settings.UI.inventoryslot_small.highlighted.initDraw().color(Settings.UI.highlightElementColor).draw(2, 2);
            ListGameTooltips var5 = new ListGameTooltips(this.settler.getGenericMobName());
            GameMessage var6 = this.settler.getAcquireTip();
            if (var6 != null) {
               var5.add((Object)(new StringTooltips(var6.translate(), 300)));
            }

            Screen.addTooltip(var5, TooltipLocation.FORM_FOCUS);
         } else {
            Settings.UI.inventoryslot_small.active.initDraw().color(Settings.UI.activeElementColor).draw(2, 2);
         }

         this.settler.getSettlerFaceDrawOptions(18, 18, Color.WHITE, (Mob)null).draw();
      }

      void onClick(FormSettlerHelpList var1, int var2, InputEvent var3, PlayerMob var4) {
      }

      void onControllerEvent(FormSettlerHelpList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormSettlerHelpList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormSettlerHelpList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormSettlerHelpList)var1, var2, var3, var4);
      }
   }
}
