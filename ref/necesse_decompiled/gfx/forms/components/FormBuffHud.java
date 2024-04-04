package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormBuffHud extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   public int columns;
   public FairType.TextAlign align;
   private Mob owner;
   private ArrayList<BuffHudElement> buffs = new ArrayList();
   private BuffHudElement hovering;
   protected Predicate<ActiveBuff> filter;

   public FormBuffHud(int var1, int var2, int var3, FairType.TextAlign var4, Mob var5, Predicate<ActiveBuff> var6) {
      this.position = new FormFixedPosition(var1, var2);
      this.columns = var3;
      this.align = var4;
      this.filter = var6;
      this.setOwner(var5);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      int var4;
      if (var1.isMouseMoveEvent()) {
         this.hovering = null;
         this.getBuffs();
         var4 = this.getMouseOverIndex(var1.pos.hudX, var1.pos.hudY, this.getWidth());
         if (var4 >= 0 && var4 < this.buffs.size()) {
            this.hovering = (BuffHudElement)this.buffs.get(var4);
         }

      } else if (var1.state && !var1.isKeyboardEvent()) {
         if (var1.getID() == -99 && this.isMouseOver(var1)) {
            this.getBuffs();
            var4 = this.getMouseOverIndex(var1.pos.hudX, var1.pos.hudY, this.getWidth());
            if (var4 >= 0 && var4 < this.buffs.size()) {
               BuffHudElement var5 = (BuffHudElement)this.buffs.get(var4);
               if (var5.activeBuff.canCancel() || GlobalData.debugCheatActive()) {
                  var5.activeBuff.owner.buffManager.removeBuff(var5.activeBuff.buff.getID(), true);
                  var1.use();
                  this.playTickSound();
               }
            }
         }

      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.getState() == ControllerInput.MENU_SELECT && var1.buttonState) {
         ControllerFocusHandler var4 = this.getControllerFocusHandler();
         if (var4 instanceof BuffHudElement && ((BuffHudElement)var4).hud == this) {
            BuffHudElement var5 = (BuffHudElement)var4;
            if (var5.activeBuff.canCancel() || GlobalData.debugCheatActive()) {
               var5.activeBuff.owner.buffManager.removeBuff(var5.activeBuff.buff.getID(), true);
               var1.use();
               this.playTickSound();
            }
         }
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      int var7;
      if (this.align == FairType.TextAlign.LEFT) {
         var7 = 0;
      } else if (this.align == FairType.TextAlign.RIGHT) {
         var7 = -this.getWidth();
      } else {
         var7 = -this.getWidth() / 2;
      }

      for(int var8 = 0; var8 < this.buffs.size(); ++var8) {
         int var9 = var8 % this.columns;
         int var10 = var8 / this.columns;
         BuffHudElement var11 = (BuffHudElement)this.buffs.get(var8);
         ControllerFocus.add(var1, var5, var11, new Rectangle(32, 32), var2 + this.getX() + var7 + var9 * 40 + 2, var3 + this.getY() + var10 * 40, 0, var4);
      }

   }

   public void setOwner(Mob var1) {
      this.owner = var1;
   }

   public Mob getOwner() {
      return this.owner;
   }

   public void getBuffs() {
      this.buffs = (ArrayList)this.getOwner().buffManager.getBuffs().values().stream().filter((var0) -> {
         return var0.isVisible() || GlobalData.debugActive();
      }).filter(this.filter).map((var1x) -> {
         return new BuffHudElement(this, var1x);
      }).collect(Collectors.toCollection(ArrayList::new));
      Comparator var1 = Comparator.comparing((var0) -> {
         return var0.activeBuff.isVisible();
      });
      Comparator var2 = Comparator.comparing((var0) -> {
         return var0.activeBuff.canCancel();
      });
      Comparator var3 = Comparator.comparing((var0) -> {
         return var0.activeBuff.shouldDrawDuration();
      });
      Comparator var4 = Comparator.comparingInt((var0) -> {
         return var0.activeBuff.getDurationLeft();
      });
      Comparator var5 = Comparator.comparingInt((var0) -> {
         return var0.activeBuff.buff.getID();
      });
      this.buffs.sort(var1.reversed().thenComparing(var3).thenComparing(var2.reversed()).thenComparing(var4.reversed()).thenComparing(var5));
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      BuffHudElement var4 = this.hovering;
      ControllerFocusHandler var5 = this.getControllerFocusHandler();
      if (var5 instanceof BuffHudElement && ((BuffHudElement)var5).hud == this) {
         var4 = (BuffHudElement)var5;
      }

      if (var4 != null) {
         if (!var4.activeBuff.isRemoved() && var4.activeBuff.owner.buffManager.hasBuff(var4.activeBuff.buff)) {
            Screen.addTooltip(var4.getTooltips(), TooltipLocation.FORM_FOCUS);
         } else {
            ControllerInput.submitNextRefreshFocusEvent();
            Screen.input().submitNextMoveEvent();
         }
      }

      this.getBuffs();
      int var6;
      if (this.align == FairType.TextAlign.LEFT) {
         var6 = 0;
      } else if (this.align == FairType.TextAlign.RIGHT) {
         var6 = -this.getWidth();
      } else {
         var6 = -this.getWidth() / 2;
      }

      for(int var7 = 0; var7 < this.buffs.size(); ++var7) {
         int var8 = var7 % this.columns;
         int var9 = var7 / this.columns;
         BuffHudElement var10 = (BuffHudElement)this.buffs.get(var7);
         var10.activeBuff.drawIcon(this.getX() + var6 + var8 * 40 + 2, this.getY() + var9 * 40);
      }

   }

   public int getColumns() {
      return Math.min(this.buffs.size(), this.columns);
   }

   public int getWidth() {
      return this.getColumns() * 40;
   }

   public int getRows() {
      int var1 = this.getColumns();
      return var1 == 0 ? 0 : (this.buffs.size() + var1 - 1) / var1;
   }

   public int getHeight() {
      return this.getRows() * 40;
   }

   public List<Rectangle> getHitboxes() {
      LinkedList var1 = new LinkedList();
      int var2 = this.getWidth();

      for(int var3 = 0; var3 < this.buffs.size(); ++var3) {
         int var4 = var3 % this.columns;
         int var5 = var3 / this.columns;
         var1.add(this.getMouseOverRect(var4, var5, var2));
      }

      return var1;
   }

   public Rectangle getMouseOverRect(int var1, int var2, int var3) {
      int var4 = var1 * 40 + 2;
      int var5 = var2 * 40;
      if (this.align == FairType.TextAlign.LEFT) {
         return new Rectangle(this.getX() + var4, this.getY() + var5, 32, 32);
      } else {
         return this.align == FairType.TextAlign.RIGHT ? new Rectangle(this.getX() - var3 + var4, this.getY() + var5, 32, 32) : new Rectangle(this.getX() - var3 / 2 + var4, this.getY() + var5, 32, 32);
      }
   }

   public boolean checkMouseOver(InputEvent var1, int var2, int var3, int var4) {
      return var1.isMoveUsed() ? false : this.getMouseOverRect(var2, var3, var4).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public int getMouseOverIndex(int var1, int var2, int var3) {
      int var4;
      if (this.align == FairType.TextAlign.LEFT) {
         var4 = 0;
      } else if (this.align == FairType.TextAlign.RIGHT) {
         var4 = -this.getWidth();
      } else {
         var4 = -this.getWidth() / 2;
      }

      var1 -= this.getX() + var4;
      var2 -= this.getY();
      int var5 = var1 / 40;
      if (var5 >= 0 && var5 <= this.columns) {
         int var6 = var2 / 40;
         return (new Rectangle(var5 * 40 + 2, var6 * 40, 32, 32)).contains(var1, var2) ? var6 * this.columns + var5 : -1;
      } else {
         return -1;
      }
   }

   public boolean isMouseOver(InputEvent var1) {
      if (var1.isMoveUsed()) {
         return false;
      } else {
         int var2 = this.getMouseOverIndex(var1.pos.hudX, var1.pos.hudY, this.getWidth());
         return var2 >= 0 && var2 < this.buffs.size();
      }
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   protected static class BuffHudElement implements ControllerFocusHandler {
      public final FormBuffHud hud;
      public final ActiveBuff activeBuff;

      public BuffHudElement(FormBuffHud var1, ActiveBuff var2) {
         this.hud = var1;
         this.activeBuff = var2;
      }

      public GameTooltips getTooltips() {
         ListGameTooltips var1 = this.activeBuff.getTooltips(new GameBlackboard());
         if (this.activeBuff.canCancel()) {
            if (Input.lastInputIsController) {
               var1.add((Object)(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("bufftooltip", "canceltip")) {
                  public int getDrawOrder() {
                     return 0;
                  }
               }));
            } else {
               var1.add((Object)(new InputTooltip(-99, Localization.translate("bufftooltip", "canceltip")) {
                  public int getDrawOrder() {
                     return 0;
                  }
               }));
            }
         }

         if (Screen.isKeyDown(340)) {
            LinkedList var2 = this.activeBuff.getModifierTooltips();
            if (var2.isEmpty()) {
               var1.add(Localization.translate("bufftooltip", "nomodifiers"));
            } else {
               Stream var10000 = this.activeBuff.getModifierTooltips().stream().map((var0) -> {
                  return var0.toTooltip(true);
               });
               Objects.requireNonNull(var1);
               var10000.forEach(var1::add);
            }
         }

         return var1;
      }

      public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      }

      public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
         return false;
      }

      public int getControllerFocusHashcode() {
         return this.hud.hashCode() + this.activeBuff.buff.getID();
      }
   }
}
