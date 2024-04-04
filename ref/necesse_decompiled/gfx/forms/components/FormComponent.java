package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.ComponentPriorityManager;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameTexture.GameSprite;

public abstract class FormComponent implements Comparable<FormComponent>, ControllerFocusHandler {
   public int zIndex;
   private long priorityKey;
   private boolean isDisposed;
   private FormManager manager;
   private ArrayList<ControllerFocusHandler> nextFocus = new ArrayList();
   private ArrayList<ControllerFocusHandler> prioritizeFocus = new ArrayList();
   private ArrayList<ControllerFocusHandler> tryPrioritizeFocus = new ArrayList();
   private ComponentPriorityManager priorityManager;
   public ControllerFocusHandler controllerUpFocus;
   public ControllerFocusHandler controllerDownFocus;
   public ControllerFocusHandler controllerLeftFocus;
   public ControllerFocusHandler controllerRightFocus;
   public Object controllerFocusHashcode;
   public int controllerInitialFocusPriority;

   public FormComponent() {
   }

   public final void setManager(FormManager var1) {
      if (var1 != null && this.manager != var1) {
         if (this.manager != null) {
            throw new IllegalStateException("Cannot change component manager");
         } else {
            this.manager = var1;
            this.init();
         }
      }
   }

   public FormManager getManager() {
      return this.manager;
   }

   public final void setPriorityManager(ComponentPriorityManager var1) {
      if (var1 != null && this.priorityManager != var1) {
         if (this.priorityManager != null) {
            throw new IllegalStateException("Cannot change component priority manager");
         } else {
            this.priorityManager = var1;
         }
      }
   }

   public boolean tryPutOnTop() {
      if (this.priorityManager != null) {
         this.priorityKey = this.priorityManager.getNextPriorityKey();
         return true;
      } else {
         return false;
      }
   }

   protected void init() {
      if (!this.nextFocus.isEmpty()) {
         this.getManager().setNextControllerFocus((ControllerFocusHandler[])this.nextFocus.toArray(new ControllerFocusHandler[0]));
         this.nextFocus.clear();
      }

      if (!this.prioritizeFocus.isEmpty()) {
         this.getManager().prioritizeControllerFocus((ControllerFocusHandler[])this.prioritizeFocus.toArray(new ControllerFocusHandler[0]));
         this.prioritizeFocus.clear();
      }

      if (!this.tryPrioritizeFocus.isEmpty()) {
         this.getManager().tryPrioritizeControllerFocus((ControllerFocusHandler[])this.tryPrioritizeFocus.toArray(new ControllerFocusHandler[0]));
         this.tryPrioritizeFocus.clear();
      }

   }

   public abstract void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3);

   public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
      switch (var1) {
         case 0:
            if (this.controllerUpFocus != null) {
               if (this.controllerUpFocus != this) {
                  this.manager.setNextControllerFocus(this.controllerUpFocus);
               }

               return true;
            }
            break;
         case 1:
            if (this.controllerRightFocus != null) {
               if (this.controllerRightFocus != this) {
                  this.manager.setNextControllerFocus(this.controllerRightFocus);
               }

               return true;
            }
            break;
         case 2:
            if (this.controllerDownFocus != null) {
               if (this.controllerDownFocus != this) {
                  this.manager.setNextControllerFocus(this.controllerDownFocus);
               }

               return true;
            }
            break;
         case 3:
            if (this.controllerLeftFocus != null) {
               if (this.controllerLeftFocus != this) {
                  this.manager.setNextControllerFocus(this.controllerLeftFocus);
               }

               return true;
            }
      }

      return false;
   }

   public abstract void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6);

   public abstract void draw(TickManager var1, PlayerMob var2, Rectangle var3);

   public abstract List<Rectangle> getHitboxes();

   protected static List<Rectangle> singleBox(Rectangle var0) {
      return Collections.singletonList(var0);
   }

   protected static List<Rectangle> multiBox(Rectangle... var0) {
      return Arrays.asList(var0);
   }

   public Rectangle getBoundingBox() {
      List var1 = this.getHitboxes();
      if (var1.isEmpty()) {
         return new Rectangle();
      } else {
         Rectangle var2 = null;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Rectangle var4 = (Rectangle)var3.next();
            if (!var4.isEmpty()) {
               if (var2 == null) {
                  var2 = var4;
               } else {
                  var2 = var2.union(var4);
               }
            }
         }

         if (var2 == null) {
            return new Rectangle();
         } else {
            return var2;
         }
      }
   }

   public boolean shouldUseMouseEvents() {
      return true;
   }

   public boolean shouldDraw() {
      return true;
   }

   public boolean isMouseOver(InputEvent var1) {
      return var1.isMoveUsed() ? false : this.getHitboxes().stream().filter(Objects::nonNull).anyMatch((var1x) -> {
         return var1x.contains(var1.pos.hudX, var1.pos.hudY);
      });
   }

   public void setNextControllerFocus(ControllerFocusHandler... var1) {
      if (var1 != null) {
         FormManager var2 = this.getManager();
         if (var2 != null) {
            var2.setNextControllerFocus(var1);
         } else {
            this.nextFocus.addAll(Arrays.asList(var1));
         }

      }
   }

   public void prioritizeControllerFocus(ControllerFocusHandler... var1) {
      if (var1 != null) {
         FormManager var2 = this.getManager();
         if (var2 != null) {
            var2.prioritizeControllerFocus(var1);
         } else {
            this.prioritizeFocus.addAll(Arrays.asList(var1));
         }

      }
   }

   public FormComponent prioritizeControllerFocus() {
      this.prioritizeControllerFocus(this);
      return this;
   }

   public void tryPrioritizeControllerFocus(ControllerFocusHandler... var1) {
      if (var1 != null) {
         FormManager var2 = this.getManager();
         if (var2 != null) {
            var2.tryPrioritizeControllerFocus(var1);
         } else {
            this.tryPrioritizeFocus.addAll(Arrays.asList(var1));
         }

      }
   }

   public FormComponent tryPrioritizeControllerFocus() {
      this.tryPrioritizeControllerFocus(this);
      return this;
   }

   public boolean isControllerFocus(ControllerFocusHandler var1) {
      FormManager var2 = this.getManager();
      return var2 != null && var2.isControllerFocus(var1);
   }

   public ControllerFocus getControllerFocus() {
      FormManager var1 = this.getManager();
      return var1 != null ? var1.getCurrentFocus() : null;
   }

   public ControllerFocusHandler getControllerFocusHandler() {
      ControllerFocus var1 = this.getControllerFocus();
      return var1 != null ? var1.handler : null;
   }

   public boolean isControllerFocus() {
      return this.isControllerFocus(this);
   }

   public int getControllerFocusHashcode() {
      return this.controllerFocusHashcode != null ? this.controllerFocusHashcode.hashCode() : ControllerFocusHandler.super.getControllerFocusHashcode();
   }

   public void playTickSound() {
      Screen.playSound(GameResources.tick, SoundEffect.ui());
   }

   public void onWindowResized() {
   }

   public void dispose() {
      if (!this.isDisposed) {
         this.isDisposed = true;
         FormManager var1 = this.getManager();
         if (var1 != null) {
            var1.onComponentDispose(this);
         }
      }

   }

   public final boolean isDisposed() {
      return this.isDisposed;
   }

   public int compareTo(FormComponent var1) {
      int var2 = Integer.compare(this.zIndex, var1.zIndex);
      return var2 == 0 ? Long.compare(this.priorityKey, var1.priorityKey) : var2;
   }

   public static void drawWidthComponent(GameSprite var0, GameSprite var1, int var2, int var3, int var4, Color var5, boolean var6) {
      if (var6) {
         var2 += var0.width;
      }

      int var7;
      if (var4 <= var0.width * 2) {
         var7 = var4 / 2;
         float var8 = (float)var7 / (float)var0.width;
         int var9 = (int)((float)var0.spriteWidth * var8);
         var0.initDrawSection(0, var9, 0, var0.spriteHeight).size(var7, var0.height).mirror(false, var6).color(var5).rotate(var6 ? 90.0F : 0.0F, 0, 0).draw(var2, var3);
         var0.initDrawSection(0, var9, 0, var0.spriteHeight).size(var7, var0.height).mirror(true, var6).color(var5).rotate(var6 ? 90.0F : 0.0F, 0, 0).draw(var6 ? var2 : var2 + var7, var6 ? var3 + var7 : var3);
      } else {
         var0.initDraw().mirror(false, var6).color(var5).rotate(var6 ? 90.0F : 0.0F, 0, 0).draw(var2, var3);
         var0.initDraw().mirror(true, var6).color(var5).rotate(var6 ? 90.0F : 0.0F, 0, 0).draw(var6 ? var2 : var2 + var4 - var0.width, var6 ? var3 + var4 - var0.width : var3);

         for(var7 = var0.width; var7 < var4 - var0.width; var7 += var1.width) {
            int var11 = Math.min(var1.width, var4 - var0.width - var7);
            float var12 = (float)var11 / (float)var1.width;
            int var10 = (int)((float)var1.spriteWidth * var12);
            var1.initDrawSection(0, var10, 0, var1.spriteHeight).size(var11, var1.height).mirror(false, var6).color(var5).rotate(var6 ? 90.0F : 0.0F, 0, 0).draw(var6 ? var2 : var2 + var7, var6 ? var3 + var7 : var3);
         }
      }

   }

   public static void drawWidthComponent(GameSprite var0, GameSprite var1, int var2, int var3, int var4, Color var5) {
      drawWidthComponent(var0, var1, var2, var3, var4, var5, false);
   }

   public static void drawWidthComponent(GameSprite var0, GameSprite var1, int var2, int var3, int var4, boolean var5) {
      drawWidthComponent(var0, var1, var2, var3, var4, Color.WHITE, var5);
   }

   public static void drawWidthComponent(GameSprite var0, GameSprite var1, int var2, int var3, int var4) {
      drawWidthComponent(var0, var1, var2, var3, var4, Color.WHITE);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((FormComponent)var1);
   }
}
