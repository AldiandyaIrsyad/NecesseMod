package necesse.gfx.forms.components;

import java.awt.Color;
import java.util.Objects;
import java.util.function.Predicate;
import necesse.engine.MouseDraggingElement;
import necesse.engine.Screen;
import necesse.engine.control.Input;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class FormContentIconValueButton<T> extends FormContentButton {
   protected T value;
   protected ButtonIcon icon;
   protected boolean mirrorX;
   protected boolean mirrorY;
   protected GameMessage[] tooltips;

   public FormContentIconValueButton(int var1, int var2, int var3, FormInputSize var4, ButtonColor var5) {
      super(var1, var2, var3, var4, var5);
   }

   public FormContentIconValueButton(int var1, int var2, FormInputSize var3, ButtonColor var4) {
      this(var1, var2, var3.height, var3, var4);
   }

   protected int getIconDrawX(ButtonIcon var1, int var2, int var3) {
      return var2 + var3 / 2 - var1.texture.getWidth() / 2;
   }

   protected int getIconDrawY(ButtonIcon var1, int var2, int var3) {
      return var2 + var3 / 2 - var1.texture.getHeight() / 2;
   }

   public Color getContentColor() {
      return (Color)this.icon.colorGetter.apply(this.getButtonState());
   }

   protected void drawContent(int var1, int var2, int var3, int var4) {
      if (this.icon != null) {
         this.icon.texture.initDraw().color(this.getContentColor()).mirror(this.mirrorX, this.mirrorY).draw(this.getIconDrawX(this.icon, var1, var3), this.getIconDrawY(this.icon, var2, var4));
      }
   }

   protected void addTooltips(PlayerMob var1) {
      super.addTooltips(var1);
      GameTooltips var2 = this.getTooltips();
      if (var2 != null) {
         Screen.addTooltip(var2, TooltipLocation.FORM_FOCUS);
      }

   }

   public GameTooltips getTooltips() {
      if (this.tooltips.length == 0) {
         return null;
      } else {
         StringTooltips var1 = new StringTooltips();
         GameMessage[] var2 = this.tooltips;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            GameMessage var5 = var2[var4];
            var1.add(var5.translate(), 350);
         }

         return var1;
      }
   }

   public T getValue() {
      return this.value;
   }

   public FormContentIconValueButton<T> setCurrent(T var1, ButtonIcon var2, boolean var3, boolean var4, GameMessage... var5) {
      this.value = var1;
      this.icon = var2;
      this.mirrorX = var3;
      this.mirrorY = var4;
      this.tooltips = var5;
      return this;
   }

   public FormContentIconValueButton<T> setCurrent(T var1, ButtonIcon var2, GameMessage... var3) {
      return this.setCurrent(var1, var2, false, false, var3);
   }

   public FormContentIconValueButton<T> setCurrent(T var1, ButtonIcon var2) {
      return this.setCurrent(var1, var2, false, false);
   }

   protected boolean acceptsEvents() {
      return super.acceptsEvents() && !this.isDraggingThis();
   }

   public void setupDragToOtherButtons(Object var1, boolean var2, Predicate<T> var3) {
      this.onDragStarted((var3x) -> {
         final int var4 = var3x.draggingStartedEvent.isUsed() ? var3x.draggingStartedEvent.getLastID() : var3x.draggingStartedEvent.getID();
         Screen.setMouseDraggingElement(new FormContentIconStateDraggingElement(this, var1) {
            public boolean isKeyDown(Input var1) {
               return var1.isKeyDown(var4);
            }
         });
         if (var2) {
            FormInputEvent var5 = new FormInputEvent(this, var3x.draggingStartedEvent);
            this.clickedEvents.onEvent(var5);
            if (!var5.hasPreventedDefault()) {
               this.pressed(var3x.event);
               this.startCooldown();
               var3x.event.use();
            }
         }

      });
      this.onChangedHover((var3x) -> {
         if (this.isHovering() && this.isActive()) {
            MouseDraggingElement var4 = Screen.getMouseDraggingElement();
            if (var4 instanceof FormContentIconStateDraggingElement) {
               FormContentIconStateDraggingElement var5 = (FormContentIconStateDraggingElement)var4;
               if (var5.component != this && !Objects.equals(this.value, var5.component.value) && Objects.equals(var5.sameObject, var1)) {
                  try {
                     if (var3.test(var5.component.value)) {
                        this.setCurrent(var5.component.value, var5.component.icon, var5.component.mirrorX, var5.component.mirrorY, var5.component.tooltips);
                        this.playTickSound();
                        var3x.event.use();
                     }
                  } catch (ClassCastException var7) {
                  }
               }
            }
         }

      });
   }

   protected boolean isDraggingThis() {
      MouseDraggingElement var1 = Screen.getMouseDraggingElement();
      if (var1 instanceof FormContentIconStateDraggingElement) {
         FormContentIconStateDraggingElement var2 = (FormContentIconStateDraggingElement)var1;
         return var2.component == this;
      } else {
         return false;
      }
   }

   public void drawDraggingElement(int var1, int var2) {
   }

   protected static class FormContentIconStateDraggingElement implements MouseDraggingElement {
      public final FormContentIconValueButton<?> component;
      public final Object sameObject;

      public FormContentIconStateDraggingElement(FormContentIconValueButton<?> var1, Object var2) {
         this.component = var1;
         this.sameObject = var2;
      }

      public boolean draw(int var1, int var2) {
         this.component.drawDraggingElement(var1, var2);
         return true;
      }
   }
}
