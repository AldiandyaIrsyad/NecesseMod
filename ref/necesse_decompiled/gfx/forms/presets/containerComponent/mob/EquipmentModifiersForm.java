package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormTooltipsDraw;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public abstract class EquipmentModifiersForm extends Form {
   private FormContentBox content;
   private FormTooltipsDraw tooltips;

   public static ListGameTooltips getTooltips(Mob var0) {
      ListGameTooltips var1 = new ListGameTooltips();
      if (var0 != null) {
         var1.add(Localization.translate("buffmodifiers", "currentmodifiers"));
         List var2 = (List)var0.buffManager.getModifierTooltips().stream().map((var0x) -> {
            return var0x.toTooltip(true);
         }).collect(Collectors.toList());
         if (var2.isEmpty()) {
            var1.add((Object)(new StringTooltips(Localization.translate("bufftooltip", "nomodifiers"), GameColor.YELLOW)));
         } else {
            var1.addAll(var2);
         }
      }

      return var1;
   }

   public EquipmentModifiersForm(String var1, int var2, int var3) {
      super(var1, var2, var3);
      this.content = (FormContentBox)this.addComponent(new FormContentBox(0, 0, var2, var3));
      this.tooltips = (FormTooltipsDraw)this.content.addComponent(new FormTooltipsDraw(4, 4, (ListGameTooltips)null));
   }

   public abstract Mob getMob();

   public void update() {
      this.tooltips.setTooltips(getTooltips(this.getMob()));
      this.content.setContentBox(new Rectangle(this.tooltips.getWidth() + 8, this.tooltips.getHeight() + 8));
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.update();
      super.draw(var1, var2, var3);
   }
}
