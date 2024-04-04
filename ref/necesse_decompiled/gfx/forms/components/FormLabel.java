package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameFont.GameFontHandler;

public class FormLabel extends FormComponent implements FormPositionContainer {
   public static final int ALIGN_LEFT = -1;
   public static final int ALIGN_MID = 0;
   public static final int ALIGN_RIGHT = 1;
   private FormPosition position;
   private ArrayList<GameMessage> lines;
   private int maxWidth;
   private int align;
   private FontOptions fontOptions;

   public FormLabel(String var1, FontOptions var2, int var3, int var4, int var5, int var6) {
      this.fontOptions = var2.defaultColor(Settings.UI.activeTextColor);
      this.align = var3;
      this.position = new FormFixedPosition(var4, var5);
      this.setText(var1, var6);
   }

   public FormLabel(String var1, FontOptions var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, -1);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      GameFontHandler var4 = FontManager.bit;

      for(int var5 = 0; var5 < this.lines.size(); ++var5) {
         int var6 = this.getX();
         if (this.align == 0) {
            var6 = this.getX() - var4.getWidthCeil(((GameMessage)this.lines.get(var5)).translate(), this.fontOptions) / 2;
         } else if (this.align == 1) {
            var6 = this.getX() - var4.getWidthCeil(((GameMessage)this.lines.get(var5)).translate(), this.fontOptions);
         }

         var4.drawString((float)var6, (float)(this.getY() + var5 * this.fontOptions.getSize()), ((GameMessage)this.lines.get(var5)).translate(), this.fontOptions);
      }

   }

   public List<Rectangle> getHitboxes() {
      int var1 = this.getX();
      if (this.align == 0) {
         var1 = this.getX() - this.maxWidth / 2;
      } else if (this.align == 1) {
         var1 = this.getX() - this.maxWidth;
      }

      return singleBox(new Rectangle(var1, this.getY(), this.maxWidth, this.getLinesCount() * this.fontOptions.getSize()));
   }

   public int getHeight() {
      return this.lines.size() * this.fontOptions.getSize();
   }

   public void setText(GameMessage var1, int var2) {
      this.lines = new ArrayList();
      this.maxWidth = 0;
      Iterator var3 = var1.breakMessage(this.fontOptions, var2 > 0 ? var2 : Integer.MAX_VALUE).iterator();

      while(var3.hasNext()) {
         GameMessage var4 = (GameMessage)var3.next();
         this.addBrokenLine(var4);
      }

   }

   public void setColor(Color var1) {
      this.fontOptions.defaultColor(var1);
   }

   public void setText(GameMessage var1) {
      this.setText((GameMessage)var1, -1);
   }

   public void setText(String var1) {
      this.setText((GameMessage)(new StaticMessage(var1)));
   }

   public void setText(String var1, int var2) {
      this.setText((GameMessage)(new StaticMessage(var1)), var2);
   }

   public void addLine(GameMessage var1, int var2) {
      Iterator var3 = var1.breakMessage(this.fontOptions, var2 > 0 ? var2 : Integer.MAX_VALUE).iterator();

      while(var3.hasNext()) {
         GameMessage var4 = (GameMessage)var3.next();
         this.addBrokenLine(var4);
      }

   }

   public void addLine(String var1, int var2) {
      this.addLine((GameMessage)(new StaticMessage(var1)), var2);
   }

   public void addLine(GameMessage var1) {
      this.addLine((GameMessage)var1, -1);
   }

   public void addLine(String var1) {
      this.addLine((GameMessage)(new StaticMessage(var1)));
   }

   private void addBrokenLine(GameMessage var1) {
      this.lines.add(var1);
      int var2 = FontManager.bit.getWidthCeil(var1.translate(), this.fontOptions);
      if (var2 > this.maxWidth) {
         this.maxWidth = var2;
      }

   }

   public int getLinesCount() {
      return this.lines.size();
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
