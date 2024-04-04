package necesse.engine.screenHudManager;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class ScreenFloatTextFade extends ScreenFloatText {
   public int riseTime;
   public int hoverTime;
   public int fadeTime;
   private long spawnTime;
   private String text;
   private final FontOptions fontOptions;
   private int x;
   private int y;
   private int width;
   private int height;
   protected int heightIncrease;
   public boolean avoidOtherText;
   public Predicate<ScreenFloatTextFade> avoidOtherTextFilter;
   public int expandTime;

   public ScreenFloatTextFade(int var1, int var2, FontOptions var3) {
      this.fontOptions = var3;
      this.x = var1;
      this.y = var2;
      this.expandTime = 150;
      this.riseTime = 500;
      this.hoverTime = 0;
      this.fadeTime = 1000;
      this.heightIncrease = 50;
   }

   public ScreenFloatTextFade(int var1, int var2, String var3, FontOptions var4) {
      this(var1, var2, var4);
      this.setText(var3);
   }

   public void resetSpawnTime() {
      this.spawnTime = this.getTime();
   }

   public boolean isAlive() {
      return this.getTime() - this.spawnTime <= (long)(this.riseTime + this.hoverTime + this.fadeTime);
   }

   public float getLifeProgress() {
      return (float)(this.getTime() - this.spawnTime) / (float)(this.riseTime + this.hoverTime + this.fadeTime);
   }

   public String getText() {
      return this.text;
   }

   public void setText(String var1) {
      this.text = var1;
      this.width = FontManager.bit.getWidthCeil(var1, this.fontOptions);
      this.height = FontManager.bit.getHeightCeil(var1, this.fontOptions);
   }

   public void draw(TickManager var1) {
      if (!this.isRemoved()) {
         if (!this.isAlive()) {
            this.remove();
         } else {
            long var2 = this.getTime() - this.spawnTime;
            int var4 = this.x;
            int var5 = this.getY();
            float var6 = 1.0F;
            if (var2 >= (long)(this.riseTime + this.hoverTime)) {
               var6 = Math.abs((float)(var2 - (long)(this.riseTime + this.hoverTime)) / (float)this.fadeTime - 1.0F);
            }

            float var7 = 1.0F;
            if (var2 < (long)this.expandTime) {
               var7 = GameMath.limit((float)var2 / (float)this.expandTime, 0.0F, 1.0F);
            }

            FontOptions var8 = (new FontOptions(this.fontOptions)).size((int)((float)this.fontOptions.getSize() * var7)).alphaf(var6);
            FontManager.bit.drawString((float)(var4 - FontManager.bit.getWidthCeil(this.text, var8) / 2), (float)var5, this.text, var8);
         }
      }
   }

   public int getX() {
      return this.x - this.width / 2;
   }

   public int getRealX() {
      return this.x;
   }

   public int getY() {
      return this.y - this.getCurrentHeightIncrease();
   }

   public int getRealY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setX(int var1) {
      this.x = var1 + this.width / 2;
   }

   public void setY(int var1) {
      this.y = var1 + this.getCurrentHeightIncrease();
   }

   private int getCurrentHeightIncrease() {
      float var1 = Math.min(1.0F, (float)(this.getTime() - this.spawnTime) / (float)this.riseTime);
      return (int)((float)this.heightIncrease * GameMath.sin(var1 * 90.0F));
   }

   public void setPos(int var1, int var2) {
      this.setX(var1);
      this.setY(var2);
   }

   public Rectangle getCollision() {
      return new Rectangle(this.x, this.y - this.height, this.getWidth(), this.getHeight());
   }

   public void addThis(ArrayList<ScreenHudElement> var1) {
      super.addThis(var1);
      this.resetSpawnTime();
      if (this.avoidOtherText) {
         while(this.checkCollision(var1)) {
         }
      }

   }

   public boolean checkCollision(ArrayList<ScreenHudElement> var1) {
      Iterator var2 = var1.iterator();

      ScreenFloatTextFade var4;
      do {
         do {
            ScreenHudElement var3;
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        return false;
                     }

                     var3 = (ScreenHudElement)var2.next();
                  } while(var3.isRemoved());
               } while(var3 == this);
            } while(!(var3 instanceof ScreenFloatTextFade));

            var4 = (ScreenFloatTextFade)var3;
         } while(this.avoidOtherTextFilter != null && !this.avoidOtherTextFilter.test(var4));
      } while(!var4.collidesWith(this));

      int var5 = this.getHeight() + 2 - (var4.y - this.y);
      var5 = Math.max(var5, 2);
      this.setY(this.getY() - var5);
      return true;
   }
}
