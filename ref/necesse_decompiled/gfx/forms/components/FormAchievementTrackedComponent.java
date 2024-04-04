package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.achievements.Achievement;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormAchievementTrackedComponent extends FormComponent implements FormPositionContainer {
   protected FormPosition position;
   protected int width;
   protected int progressWidth;
   public final Achievement achievement;
   protected boolean isHovering;
   protected FairTypeDrawOptions titleDrawOptions;
   public Color nameColor = new Color(220, 220, 220);

   public FormAchievementTrackedComponent(int var1, int var2, int var3, int var4, Achievement var5) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.progressWidth = var4;
      this.achievement = var5;
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

   protected FairTypeDrawOptions getTitleDrawOptions() {
      GameMessage var1 = this.achievement.name;
      if (var1 == null) {
         return null;
      } else {
         if (this.titleDrawOptions == null || var1.hasUpdated()) {
            FairType var2 = new FairType();
            var2.append((new FontOptions(16)).outline(), var1.translate());
            this.titleDrawOptions = var2.getDrawOptions(FairType.TextAlign.LEFT, this.width - 20, true, true);
         }

         return this.titleDrawOptions;
      }
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      int var4 = this.getY() + 4;
      FairTypeDrawOptions var5 = this.getTitleDrawOptions();
      var5.draw(this.getX() + 10, var4, this.nameColor);
      var4 += var5.getBoundingBox().height + 2;
      this.achievement.drawProgress(this.getX() + 10, var4, this.progressWidth, true);
      if (this.isHovering && !var5.displaysEverything()) {
         Screen.addTooltip(new StringTooltips(this.achievement.name.translate()), TooltipLocation.FORM_FOCUS);
      }

   }

   public List<Rectangle> getHitboxes() {
      int var1 = 0;
      int var2 = 4;
      FairTypeDrawOptions var3 = this.getTitleDrawOptions();
      Rectangle var4 = var3.getBoundingBox();
      var2 += var4.height + 2;
      var1 = Math.max(var1, var4.width);
      var2 += 20;
      var1 = Math.max(var1, this.progressWidth);
      return singleBox(new Rectangle(this.getX(), this.getY(), var1, var2));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
