package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.quest.Quest;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormQuestComponent extends FormComponent implements FormPositionContainer {
   protected FormPosition position;
   protected int width;
   public final Quest quest;
   public boolean showTitle = true;
   protected FairTypeDrawOptions descDrawOptions = null;
   protected boolean isHovering;
   public Color nameColor;
   public Color descColor;

   public FormQuestComponent(int var1, int var2, int var3, Quest var4) {
      this.nameColor = Settings.UI.highlightTextColor;
      this.descColor = Settings.UI.activeTextColor;
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.quest = var4;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   protected FairTypeDrawOptions getDescDrawOptions() {
      GameMessage var1 = this.quest.getDescription();
      if (var1 == null) {
         return null;
      } else {
         if (this.descDrawOptions == null || var1.hasUpdated()) {
            FairType var2 = new FairType();
            var2.append(new FontOptions(this.showTitle ? 12 : 16), var1.translate());
            this.descDrawOptions = var2.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
         }

         return this.descDrawOptions;
      }
   }

   public void updateDrawables() {
      this.descDrawOptions = null;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      int var5 = this.getY();
      if (this.showTitle) {
         String var6 = this.quest.getTitle().translate();
         FontOptions var7 = (new FontOptions(16)).color(this.nameColor);
         String var8 = GameUtils.maxString(var6, var7, this.width - 20);
         FontManager.bit.drawString((float)(this.getX() + 10), (float)(this.getY() + 4), var8, var7);
         if (this.isHovering && !var6.equals(var8)) {
            var4.add(var6);
         }

         var5 += 22;
      }

      FairTypeDrawOptions var9 = this.getDescDrawOptions();
      if (var9 != null) {
         var9.draw(this.getX() + 10, var5, this.descColor);
         var5 += var9.getBoundingBox().height + 2;
      }

      this.quest.drawProgress(var2, this.getX() + 10, var5, this.width - 20, this.descColor, false);
      if (!var4.isEmpty()) {
         Screen.addTooltip(var4, TooltipLocation.FORM_FOCUS);
      }

   }

   public List<Rectangle> getHitboxes() {
      FairTypeDrawOptions var1 = this.getDescDrawOptions();
      int var2 = var1 == null ? 0 : var1.getBoundingBox().height;
      int var3 = this.quest.getDrawProgressHeight(this.width - 20, false);
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, (this.showTitle ? 22 : 0) + var2 + 2 + var3));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
