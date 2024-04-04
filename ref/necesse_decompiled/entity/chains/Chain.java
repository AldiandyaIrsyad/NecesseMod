package necesse.entity.chains;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import org.lwjgl.opengl.GL11;

public class Chain {
   public ChainLocation pos1;
   public ChainLocation pos2;
   public float height;
   private boolean removed;
   private int drawStart;
   private float m;
   private boolean calculatedM;
   public boolean drawOnTop;
   public GameSprite sprite;

   public Chain(ChainLocation var1, ChainLocation var2) {
      this.sprite = new GameSprite(GameResources.chains, 1, 0, 32);
      this.pos1 = var1;
      this.pos2 = var2;
   }

   public Chain(int var1, int var2, int var3, int var4) {
      this(new StaticChainLocation(var1, var2), new StaticChainLocation(var3, var4));
   }

   public int getMaxY() {
      return Math.max(this.pos1.getY(), this.pos2.getY());
   }

   public int getMinY() {
      return Math.min(this.pos1.getY(), this.pos2.getY());
   }

   public float getXPos(float var1) {
      if (!(var1 < (float)this.getMinY()) && !(var1 > (float)this.getMaxY())) {
         if (!this.calculatedM) {
            float var2 = (float)(this.pos2.getX() - this.pos1.getX());
            float var3 = (float)(this.pos2.getY() - this.pos1.getY());
            if (var2 == 0.0F) {
               this.m = 0.0F;
            } else {
               this.m = var3 / var2;
            }

            this.calculatedM = true;
         }

         return this.m == 0.0F ? (float)this.pos1.getX() : (var1 - (float)this.pos1.getY() + this.m * (float)this.pos1.getX()) / this.m;
      } else {
         return -1.0F;
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, int var2, int var3, Level var4, TickManager var5, GameCamera var6) {
      this.resetDraw();

      for(int var7 = var2; var7 < var3; ++var7) {
         int var8 = var7 * 32;
         int var9 = var8 + 32;
         if ((var8 >= this.getMinY() || var9 >= this.getMinY()) && (var8 <= this.getMaxY() || var9 <= this.getMaxY())) {
            final int var10 = var8 + this.getDrawY();
            final DrawOptions var11 = this.getDrawSection(var8, var9, var4, var6);
            var1.add(new LevelSortedDrawable(this) {
               public int getSortY() {
                  return var10;
               }

               public void draw(TickManager var1) {
                  DrawOptions var10002 = var11;
                  Objects.requireNonNull(var10002);
                  Performance.record(var1, "chainDraw", (Runnable)(var10002::draw));
               }
            });
         }
      }

   }

   private DrawOptions getDrawSection(int var1, int var2, Level var3, GameCamera var4) {
      int var5 = (int)this.getXPos((float)var1);
      int var6 = (int)this.getXPos((float)var2);
      if (var6 == -1) {
         var2 = this.getMaxY();
         var6 = (int)this.getXPos((float)var2);
      }

      if (var5 == -1) {
         var1 = this.getMinY();
         var5 = (int)this.getXPos((float)var1);
      }

      if (this.pos1.getY() == this.pos2.getY()) {
         var5 = this.pos1.getX();
         var6 = this.pos2.getX();
      }

      DrawOptionsList var7 = new DrawOptionsList();
      this.drawStart = this.addSpriteChainLightStartTextLevelDrawOptions(var7, this.sprite, var5, var1, var6, var2, this.drawStart, this.height, var3, var4);
      return var7;
   }

   protected int addSpriteChainLightStartTextLevelDrawOptions(List<DrawOptions> var1, GameSprite var2, int var3, int var4, int var5, int var6, int var7, float var8, Level var9, GameCamera var10) {
      Point2D.Float var11 = new Point2D.Float((float)(var5 - var3), (float)(var6 - var4));
      float var12 = (float)var11.distance(0.0, 0.0);
      float var13 = var11.x / var12;
      float var14 = var11.y / var12;
      float var15 = (float)var2.width / 2.0F;
      int var16 = var2.height;
      float var17 = (float)(-var7);
      float var18 = (float)var3;

      for(float var19 = (float)var4; var17 < var12; var7 = 0) {
         if (var17 > var12 - (float)var2.height) {
            var16 = Math.abs((int)(var17 - var12));
         }

         var17 += (float)var2.height;
         int var20 = Math.abs(var7 - var16);
         float var21 = var18 + var13 * (float)var20;
         float var22 = var19 + var14 * (float)var20;
         float var23 = var18 + var13 * (float)var20 / 2.0F;
         float var24 = var19 + var14 * (float)var20 / 2.0F;
         GameLight var25 = new GameLight(150.0F);
         if (var9 != null) {
            var25 = var9.getLightLevel((int)(var23 / 32.0F), (int)(var24 / 32.0F));
         }

         Runnable var26 = var25.getGLColorSetter(1.0F, 1.0F, 1.0F, 1.0F);
         Point2D.Float var27 = new Point2D.Float((float)var10.getDrawX(var18), (float)var10.getDrawY(var19) - var8);
         Point2D.Float var28 = new Point2D.Float((float)var10.getDrawX(var21), (float)var10.getDrawY(var22) - var8);
         Point2D.Float var29 = GameMath.getPerpendicularPoint(var27, var15, var13, var14);
         Point2D.Float var30 = GameMath.getPerpendicularPoint(var27, -var15, var13, var14);
         Point2D.Float var31 = GameMath.getPerpendicularPoint(var28, -var15, var13, var14);
         Point2D.Float var32 = GameMath.getPerpendicularPoint(var28, var15, var13, var14);
         float var33 = TextureDrawOptions.pixel(var2.spriteX, var2.spriteWidth, var2.texture.getWidth());
         float var34 = TextureDrawOptions.pixel(var2.spriteX + 1, var2.spriteWidth, var2.texture.getWidth());
         float var35 = TextureDrawOptions.pixel(var2.spriteY, var7, var2.spriteHeight, var2.texture.getHeight());
         float var36 = TextureDrawOptions.pixel(var2.spriteY, var16, var2.spriteHeight, var2.texture.getHeight());
         var1.add(() -> {
            var2.texture.bind();
            var26.run();
            GL11.glBegin(7);
            GL11.glTexCoord2f(var33, var35);
            GL11.glVertex2f(var29.x, var29.y - 1.0F);
            GL11.glTexCoord2f(var34, var35);
            GL11.glVertex2f(var30.x, var30.y - 1.0F);
            GL11.glTexCoord2f(var34, var36);
            GL11.glVertex2f(var31.x, var31.y);
            GL11.glTexCoord2f(var33, var36);
            GL11.glVertex2f(var32.x, var32.y);
            GL11.glEnd();
         });
         var18 = var21;
         var19 = var22;
      }

      return var16;
   }

   public void remove() {
      this.removed = true;
   }

   public boolean isRemoved() {
      return this.pos1.removed() || this.pos2.removed() || this.removed;
   }

   public int getDrawY() {
      return (int)this.height;
   }

   public void resetDraw() {
      this.drawStart = 0;
      this.calculatedM = false;
   }
}
