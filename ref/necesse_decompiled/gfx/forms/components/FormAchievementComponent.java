package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.achievements.Achievement;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
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

public class FormAchievementComponent extends FormComponent implements FormPositionContainer {
   protected FormPosition position;
   protected int width;
   public final Achievement achievement;
   protected FairTypeDrawOptions descDrawOptions = null;
   protected boolean isHovering;
   public Color nameColor;
   public Color descColor;

   public FormAchievementComponent(int var1, int var2, int var3, Achievement var4) {
      this.nameColor = Settings.UI.highlightTextColor;
      this.descColor = Settings.UI.activeTextColor;
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.achievement = var4;
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
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   protected FairTypeDrawOptions getDescDrawOptions() {
      if (this.descDrawOptions == null) {
         FairType var1 = new FairType();
         var1.append(new FontOptions(12), this.achievement.description.translate());
         this.descDrawOptions = var1.getDrawOptions(FairType.TextAlign.LEFT, this.width - 50, true, true);
      }

      return this.descDrawOptions;
   }

   public void updateDrawables() {
      this.descDrawOptions = null;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      ListGameTooltips var4 = new ListGameTooltips();
      this.achievement.drawIcon(this.getX() + 2, this.getY() + 4);
      String var5 = this.achievement.name.translate();
      FontOptions var6 = (new FontOptions(16)).color(this.nameColor);
      String var7 = GameUtils.maxString(var5, var6, this.width - 50);
      FontManager.bit.drawString((float)(this.getX() + 50), (float)(this.getY() + 4), var7, var6);
      if (this.isHovering && !var5.equals(var7)) {
         var4.add(var5);
      }

      int var8 = this.getDescDrawOptions().getBoundingBox().height;
      this.getDescDrawOptions().draw(this.getX() + 50, this.getY() + 20, this.descColor);
      int var9 = this.getY() + 20 + var8 + 2;
      this.achievement.drawProgress(this.getX() + 55, var9, this.width - 60, false);
      if (this.achievement.isCompleted() && this.isHovering && this.achievement.getTimeCompleted() > 0L) {
         DateTimeFormatter var10 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
         LocalDateTime var11 = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.achievement.getTimeCompleted()), ZoneId.systemDefault());
         String var12 = var10.format(var11);
         var4.add(Localization.translate("achievement", "completedon", "date", var12));
      }

      if (!var4.isEmpty()) {
         Screen.addTooltip(var4, TooltipLocation.FORM_FOCUS);
      }

   }

   public List<Rectangle> getHitboxes() {
      int var1 = this.getDescDrawOptions().getBoundingBox().height;
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, 20 + var1 + 2 + 20));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
