package necesse.level.maps.hudManager.floatText;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.components.chat.ChatMessage;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class ChatBubbleText extends FloatText {
   static int maxWidth = 200;
   private final FontOptions fontOptions;
   private int x;
   private int y;
   private Mob mob;
   private String message;
   private FairTypeDrawOptions drawOptions;
   private int width;
   private int height;
   private long removeTime;

   public ChatBubbleText(int var1, int var2, String var3) {
      this.fontOptions = new FontOptions(16);
      this.x = var1;
      this.y = var2;
      this.message = var3;
      this.mob = null;
   }

   public ChatBubbleText(Mob var1, String var2) {
      this(var1.getX(), var1.getY(), var2);
      this.mob = var1;
      this.x = var1.getX();
      this.y = var1.getY() - 50;
   }

   public void setMob(Mob var1) {
      this.mob = var1;
   }

   public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
      if (!this.isRemoved()) {
         if (var2.getBounds().intersects(this.getCollision())) {
            if (this.getTime() >= this.removeTime) {
               this.remove();
            } else {
               if (this.mob != null) {
                  Point var4 = this.mob.getDrawPos();
                  this.x = var4.x;
                  this.y = var4.y - 50;
               }

               int var10 = var2.getDrawX(this.x) - 32;
               int var5 = var2.getDrawY(this.y);
               int var6 = this.width < 50 ? 50 - this.width : 0;
               final LinkedList var7 = new LinkedList();
               Rectangle var8 = this.drawOptions.getBoundingBox();
               int var9 = var8.y + var8.height;
               var7.add(Screen.initQuadDraw(this.width + 4, var9 + 4).pos(var10 + var6, var5 - var9 - 26));
               var7.add(Settings.UI.chatbubble.initDraw().sprite(0, 0, 16).pos(var10 - 4 + var6, var5 - var9 - 30));
               var7.add(Settings.UI.chatbubble.initDraw().sprite(1, 0, 16).pos(var10 + this.width + var6, var5 - var9 - 30));
               var7.add(Screen.initQuadDraw(this.width - 4, 2).color(new Color(40, 40, 40)).pos(var10 + 4 + var6, var5 - var9 - 30));
               var7.add(Screen.initQuadDraw(this.width - 4, 2).pos(var10 + 4 + var6, var5 - var9 - 28));
               var7.add(Settings.UI.chatbubble.initDraw().sprite(0, 1, 16).pos(var10 - 4 + var6, var5 - 26));
               var7.add(Settings.UI.chatbubble.initDraw().sprite(1, 1, 16).pos(var10 + this.width + var6, var5 - 26));
               var7.add(Screen.initQuadDraw(this.width + 4 - 8, 2).color(new Color(40, 40, 40)).pos(var10 + 4 + var6, var5 - 20));
               var7.add(Screen.initQuadDraw(this.width - 4, 2).pos(var10 + 4 + var6, var5 - 22));
               var7.add(Screen.initQuadDraw(2, var9 - 4).color(new Color(40, 40, 40)).pos(var10 - 4 + var6, var5 - var9 - 22));
               var7.add(Screen.initQuadDraw(2, var9 - 4).pos(var10 - 2 + var6, var5 - var9 - 22));
               var7.add(Screen.initQuadDraw(2, var9 - 4).color(new Color(40, 40, 40)).pos(var10 + this.width + 6 + var6, var5 - var9 - 22));
               var7.add(Screen.initQuadDraw(2, var9 - 4).pos(var10 + this.width + 4 + var6, var5 - var9 - 22));
               var7.add(Settings.UI.chatbubble.initDraw().sprite(1, 0, 32).pos(var10 + 24, var5 - 34));
               var7.add(() -> {
                  this.drawOptions.draw(var10 + 2 + var6, var5 - this.height + 4, Color.BLACK);
               });
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return 1000;
                  }

                  public void draw(TickManager var1) {
                     var7.forEach(DrawOptions::draw);
                  }
               });
            }
         }
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Rectangle getCollision() {
      return new Rectangle(this.x - 38, this.y - this.height - 2, this.width + 16, this.height);
   }

   public void addThis(Level var1, ArrayList<HudDrawElement> var2) {
      super.addThis(var1, var2);
      this.drawOptions = (new FairType()).append(this.fontOptions, this.message).applyParsers(ChatMessage.getParsers(this.fontOptions)).getDrawOptions(FairType.TextAlign.LEFT, maxWidth, true, true);
      Rectangle var3 = this.drawOptions.getBoundingBox();
      this.width = Math.max(20, var3.x + var3.width);
      this.height = var3.y + var3.height + 30;
      long var4 = (long)(Math.max((var3.width * this.drawOptions.getLineCount() + 200) / 100, 3) * 1000);
      this.removeTime = this.getTime() + var4;
      if (this.mob != null) {
         for(int var6 = 0; var6 < var2.size(); ++var6) {
            HudDrawElement var7 = (HudDrawElement)var2.get(var6);
            if (var7 != this && var7 instanceof ChatBubbleText) {
               ChatBubbleText var8 = (ChatBubbleText)var7;
               if (var8.mob == this.mob) {
                  var2.remove(var6);
                  --var6;
               }
            }
         }
      }

   }
}
