package necesse.level.maps.hudManager.floatText;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class FloatTextFade extends FloatText {
   public int riseTime;
   public int hoverTime;
   public int fadeTime;
   private long spawnTime;
   private String[] lines;
   protected FontOptions fontOptions;
   private int x;
   private int y;
   private int width;
   private int height;
   protected int heightIncrease;
   public boolean avoidOtherText;
   public Predicate<FloatTextFade> avoidOtherTextFilter;
   public int expandTime;

   public FloatTextFade(int var1, int var2, FontOptions var3) {
      this.fontOptions = var3;
      this.x = var1;
      this.y = var2;
      this.expandTime = 150;
      this.riseTime = 500;
      this.hoverTime = 0;
      this.fadeTime = 1000;
      this.heightIncrease = 50;
   }

   public FloatTextFade(int var1, int var2, String var3, FontOptions var4) {
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

   public String[] getLines() {
      return this.lines;
   }

   public String getText() {
      return GameUtils.join(this.lines, "\n");
   }

   public void setText(String var1) {
      this.lines = var1.split("\\n");
      this.width = 0;
      this.height = 0;
      String[] var2 = this.lines;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         this.width = Math.max(this.width, FontManager.bit.getWidthCeil(var5, this.fontOptions));
         this.height += FontManager.bit.getHeightCeil(var5, this.fontOptions);
      }

   }

   public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
      if (!this.isRemoved()) {
         if (this.getLevel() != null) {
            if (var2.getBounds().intersects(this.getCollision())) {
               if (!this.isAlive()) {
                  this.remove();
               } else {
                  long var4 = this.getTime() - this.spawnTime;
                  int var6 = var2.getDrawX(this.getAnchorX());
                  int var7 = var2.getDrawY(this.getY() - this.getHeight());
                  float var8 = 1.0F;
                  if (var4 >= (long)(this.riseTime + this.hoverTime)) {
                     var8 = Math.abs((float)(var4 - (long)(this.riseTime + this.hoverTime)) / (float)this.fadeTime - 1.0F);
                  }

                  float var9 = 1.0F;
                  if (var4 < (long)this.expandTime) {
                     var9 = GameMath.limit((float)var4 / (float)this.expandTime, 0.0F, 1.0F);
                  }

                  FontOptions var10 = (new FontOptions(this.fontOptions)).size((int)((float)this.fontOptions.getSize() * var9)).alphaf(var8);
                  final DrawOptionsList var11 = new DrawOptionsList();

                  for(int var12 = 0; var12 < this.lines.length; ++var12) {
                     String var13 = this.lines[var12];
                     var11.add((new StringDrawOptions(var10, var13)).posCenterX(var6, var7));
                     if (var12 < this.lines.length - 1) {
                        var7 += FontManager.bit.getHeightCeil(var13, var10);
                     }
                  }

                  var1.add(new SortedDrawable() {
                     public int getPriority() {
                        return 0;
                     }

                     public void draw(TickManager var1) {
                        var11.draw();
                     }
                  });
               }
            }
         }
      }
   }

   public int getAnchorX() {
      return this.x;
   }

   public int getAnchorY() {
      return this.y;
   }

   public int getX() {
      return this.getAnchorX() - this.width / 2;
   }

   public int getY() {
      return this.getAnchorY() - this.getCurrentHeightIncrease();
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
      return new Rectangle(this.getAnchorX(), this.getAnchorY() - this.getHeight(), this.getWidth(), this.getHeight());
   }

   public void addThis(Level var1, ArrayList<HudDrawElement> var2) {
      super.addThis(var1, var2);
      this.resetSpawnTime();
      if (this.avoidOtherText) {
         while(this.checkCollision(var2)) {
         }
      }

   }

   public boolean checkCollision(ArrayList<HudDrawElement> var1) {
      Iterator var2 = var1.iterator();

      FloatTextFade var4;
      do {
         do {
            HudDrawElement var3;
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        return false;
                     }

                     var3 = (HudDrawElement)var2.next();
                  } while(var3.isRemoved());
               } while(var3 == this);
            } while(!(var3 instanceof FloatTextFade));

            var4 = (FloatTextFade)var3;
         } while(this.avoidOtherTextFilter != null && !this.avoidOtherTextFilter.test(var4));
      } while(!var4.collidesWith(this));

      int var5 = this.getHeight() + 2 - (var4.y - this.y);
      var5 = Math.max(var5, 2);
      this.setY(this.getY() - var5);
      return true;
   }
}
