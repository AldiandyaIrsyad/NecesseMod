package necesse.gfx.forms.components.lists;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.shader.FormShader;

public abstract class FormGeneralGridList<E extends FormListGridElement> extends FormGeneralList<E> {
   public final int elementWidth;

   public FormGeneralGridList(int var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var6);
      this.elementWidth = var5;
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      var5 = var5.intersection(new Rectangle(var2 + this.getX(), var3 + this.getY(), this.width, this.height));
      if (var6) {
         Screen.drawShape(var5, false, 0.0F, 1.0F, 1.0F, 1.0F);
      }

      int var7 = Math.max(1, this.width / this.elementWidth);
      int var8 = Math.max(0, (int)Math.ceil((double)((float)this.scroll / (float)this.elementHeight)));
      int var9 = var8 * var7;
      int var10 = this.height - 32;
      int var11 = (int)Math.floor((double)((float)(this.scroll + var10) / (float)this.elementHeight));
      int var12 = Math.min(this.elements.size(), var11 * var7);
      int var13 = this.width % this.elementWidth / 2;

      for(int var14 = var9; var14 < var12; ++var14) {
         FormListGridElement var15 = (FormListGridElement)this.elements.get(var14);
         int var16 = var14 / var7;
         int var17 = var16 * this.elementHeight - this.scroll + 16;
         int var18 = (var14 - var16 * var7) * this.elementWidth + var13;
         ControllerFocus.add(var1, var5, var15, new Rectangle(this.elementWidth, this.elementHeight), var2 + this.getX() + var18, var3 + this.getY() + var17, 0, this.getControllerNavigationHandler(var2, var3));
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isButton && var1.buttonState || this.acceptMouseRepeatEvents && var1.getState() == ControllerInput.REPEAT_EVENT) {
         ControllerFocus var4 = this.getManager().getCurrentFocus();
         if (var4 != null && var4.handler instanceof FormListElement) {
            int var5 = Math.max(1, this.width / this.elementWidth);
            int var6 = Math.max(0, (int)Math.ceil((double)((float)this.scroll / (float)this.elementHeight)) - 1);
            int var7 = var6 * var5;
            int var8 = this.height - 32;
            int var9 = (int)Math.floor((double)((float)(this.scroll + var8) / (float)this.elementHeight)) + 1;
            int var10 = Math.min(this.elements.size(), var9 * var5);

            for(int var11 = var7; var11 < var10; ++var11) {
               FormListGridElement var12 = (FormListGridElement)this.elements.get(var11);
               if (var12 == var4.handler) {
                  var12.onControllerEvent(this, var11, var1, var2, var3);
               }
            }
         }
      }

   }

   protected ControllerNavigationHandler getControllerNavigationHandler(int var1, int var2) {
      return (var3, var4, var5, var6) -> {
         LinkedList var7 = new LinkedList();
         Rectangle var8 = new Rectangle(var1 + this.getX(), var2 + this.getY() - this.elementHeight, this.width, this.height + this.elementHeight * 2);
         int var9 = Math.max(1, this.width / this.elementWidth);
         int var10 = Math.max(0, (int)Math.ceil((double)((float)this.scroll / (float)this.elementHeight)) - 1);
         int var11 = var10 * var9;
         int var12 = this.height - 32;
         int var13 = (int)Math.floor((double)((float)(this.scroll + var12) / (float)this.elementHeight)) + 1;
         int var14 = Math.min(this.elements.size(), var13 * var9);
         int var15 = this.width % this.elementWidth / 2;

         int var19;
         int var20;
         for(int var16 = var11; var16 < var14; ++var16) {
            FormListGridElement var17 = (FormListGridElement)this.elements.get(var16);
            int var18 = var16 / var9;
            var19 = var18 * this.elementHeight - this.scroll + 16;
            var20 = (var16 - var18 * var9) * this.elementWidth + var15;
            ControllerFocus.add(var7, var8, var17, new Rectangle(this.elementWidth, this.elementHeight), var1 + this.getX() + var20, var2 + this.getY() + var19, 0, this.getControllerNavigationHandler(var1, var2));
         }

         ControllerFocus var23 = ControllerFocus.getNext(var3, this.getManager(), var7);
         if (var23 != null) {
            int var24 = this.scroll;
            Rectangle var25 = new Rectangle(var23.boundingBox.x - (var1 + this.getX()), var23.boundingBox.y - (var2 + this.getY() - this.scroll), var23.boundingBox.width, var23.boundingBox.height);
            var19 = this.elementHeight;
            var20 = var25.y - var19;
            int var21 = var25.y + var25.height + var19 - this.height;
            this.scroll = Math.max(0, GameMath.limit(this.scroll, var21, var20));
            this.limitMaxScroll();
            int var22 = this.scroll - var24;
            this.getManager().setControllerFocus(new ControllerFocus(var23, 0, -var22));
            return true;
         } else {
            return false;
         }
      };
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.handleDrawScroll(var1);
      if (this.elements.size() == 0) {
         this.drawEmptyMessage(var1);
      } else {
         int var4 = Math.max(1, this.width / this.elementWidth);
         int var5 = Math.max(0, this.scroll / this.elementHeight);
         int var6 = var5 * var4;
         int var7 = this.height - 32;
         int var8 = var7 / this.elementHeight + (var7 % this.elementHeight == 0 ? 0 : 1);
         int var9 = Math.min(this.elements.size(), var6 + (var8 + (this.scroll % this.elementHeight == 0 ? 0 : 1)) * var4);
         int var10 = this.width % this.elementWidth / 2;

         for(int var11 = var6; var11 < var9; ++var11) {
            int var12 = var11 / var4;
            int var13 = var12 * this.elementHeight - this.scroll + 16;
            int var14 = (var11 - var12 * var4) * this.elementWidth + var10;
            int var15 = this.getX() + var14;
            int var16 = this.getY() + var13;
            int var17 = Math.max(0, 16 - var13);
            int var18 = Math.min(this.elementHeight, this.height - var13 - 16) - var17;
            int var19 = Math.min(this.elementWidth, this.width - var14);
            FormShader.FormShaderState var20 = GameResources.formShader.startState(new Point(var15, var16), new Rectangle(0, var17, var19, var18));

            try {
               ((FormListGridElement)this.elements.get(var11)).draw(this, var1, var2, var11);
            } finally {
               var20.end();
            }
         }
      }

      this.drawScrollButtons(var1);
   }

   protected FormGeneralList<E>.MouseOverObject getMouseOverObj(InputEvent var1) {
      if (!this.isMouseOverElementSpace(var1)) {
         return null;
      } else {
         int var2 = Math.max(1, this.width / this.elementWidth);
         int var3 = Math.max(0, this.scroll / this.elementHeight);
         int var4 = var3 * var2;
         int var5 = this.height - 32;
         int var6 = var5 / this.elementHeight + (var5 % this.elementHeight == 0 ? 0 : 1);
         int var7 = Math.min(this.elements.size(), var4 + (var6 + (this.scroll % this.elementHeight == 0 ? 0 : 1)) * var2);

         for(int var8 = var4; var8 < var7; ++var8) {
            FormGeneralList.MouseOverObject var9 = this.getMouseOffset(var8, var1);
            if (var9.xOffset != -1 && var9.yOffset != -1) {
               return var9;
            }
         }

         return null;
      }
   }

   protected FormGeneralList<E>.MouseOverObject getMouseOffset(int var1, InputEvent var2) {
      int var3 = Math.max(1, this.width / this.elementWidth);
      int var4 = this.width % this.elementWidth / 2;
      int var5 = var1 / var3;
      int var6 = var5 * this.elementHeight - this.scroll + 16;
      int var7 = (var1 - var5 * var3) * this.elementWidth + var4;
      int var8 = this.getX() + var7;
      int var9 = this.getY() + var6;
      return this.getMouseOffset(var1, var2, var8, var9);
   }

   private FormGeneralList<E>.MouseOverObject getMouseOffset(int var1, InputEvent var2, int var3, int var4) {
      int var5 = var2.pos.hudX - var3;
      int var6 = var2.pos.hudY - var4;
      if (var5 < 0 || var5 >= this.elementWidth) {
         var3 = -1;
      }

      if (var6 < 0 || var6 >= this.elementHeight) {
         var4 = -1;
      }

      return new FormGeneralList.MouseOverObject(var1, var3, var4);
   }

   public void limitMaxScroll() {
      int var1 = Math.max(1, this.width / this.elementWidth);
      int var2 = (this.elements.size() - 1) / var1;
      int var3 = Math.max(0, var2 * this.elementHeight - (this.height - 32 - this.elementHeight));
      if (this.scroll > var3) {
         this.scroll = var3;
      }

   }
}
