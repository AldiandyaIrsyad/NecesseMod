package necesse.gfx.drawOptions.texture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import necesse.engine.util.GameMath;
import necesse.gfx.ImpossibleDrawException;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.ShaderState;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

class TextureDrawOptionsObj {
   public final Runnable textureBinder;
   protected ArrayList<ShaderTexture> shaderTextures;
   protected float drawDepth;
   protected int translateX;
   protected int translateY;
   protected float red;
   protected float green;
   protected float blue;
   protected float alpha;
   protected float[] advCol;
   protected float spriteX1;
   protected float spriteX2;
   protected float spriteX3;
   protected float spriteX4;
   protected float spriteY1;
   protected float spriteY2;
   protected float spriteY3;
   protected float spriteY4;
   protected int width;
   protected int height;
   protected boolean useRotation;
   protected int rotTranslateX;
   protected int rotTranslateY;
   protected float rotation;
   protected PointRotate pointRotate;
   protected LinkedList<PointRotate> addedRotations;
   protected boolean mirrorX;
   protected boolean mirrorY;
   protected boolean setBlend;
   protected int blendSourceRGB;
   protected int blendDestinationRGB;
   protected int blendSourceAlpha;
   protected int blendDestinationAlpha;
   protected LinkedList<ShaderState> shaderStates;
   private float x1;
   private float y1;
   private float x2;
   private float y2;
   private float x3;
   private float y3;
   private float x4;
   private float y4;

   TextureDrawOptionsObj(Runnable var1, int var2, int var3) {
      this.shaderTextures = new ArrayList();
      this.drawDepth = 0.0F;
      this.addedRotations = new LinkedList();
      this.shaderStates = new LinkedList();
      this.textureBinder = var1;
      this.red = 1.0F;
      this.green = 1.0F;
      this.blue = 1.0F;
      this.alpha = 1.0F;
      this.advCol = null;
      this.spriteX1 = 0.0F;
      this.spriteX2 = 1.0F;
      this.spriteX3 = 1.0F;
      this.spriteX4 = 0.0F;
      this.spriteY1 = 0.0F;
      this.spriteY2 = 0.0F;
      this.spriteY3 = 1.0F;
      this.spriteY4 = 1.0F;
      this.width = var2;
      this.height = var3;
      this.useRotation = false;
      this.rotTranslateX = 0;
      this.rotTranslateY = 0;
      this.rotation = 0.0F;
      this.pointRotate = null;
      this.mirrorX = false;
      this.mirrorY = false;
      this.setBlend = false;
   }

   TextureDrawOptionsObj(GameTexture var1, int var2, int var3) {
      Objects.requireNonNull(var1);
      this(var1::bind, var2, var3);
   }

   TextureDrawOptionsObj(TextureDrawOptionsObj var1) {
      this.shaderTextures = new ArrayList();
      this.drawDepth = 0.0F;
      this.addedRotations = new LinkedList();
      this.shaderStates = new LinkedList();
      this.textureBinder = var1.textureBinder;
      this.shaderTextures.addAll(var1.shaderTextures);
      this.drawDepth = var1.drawDepth;
      this.translateX = var1.translateX;
      this.translateY = var1.translateY;
      this.red = var1.red;
      this.green = var1.green;
      this.blue = var1.blue;
      this.alpha = var1.alpha;
      this.advCol = var1.advCol;
      this.spriteX1 = var1.spriteX1;
      this.spriteX2 = var1.spriteX2;
      this.spriteX3 = var1.spriteX3;
      this.spriteX4 = var1.spriteX4;
      this.spriteY1 = var1.spriteY1;
      this.spriteY2 = var1.spriteY2;
      this.spriteY3 = var1.spriteY3;
      this.spriteY4 = var1.spriteY4;
      this.width = var1.width;
      this.height = var1.height;
      this.useRotation = var1.useRotation;
      this.rotTranslateX = var1.rotTranslateX;
      this.rotTranslateY = var1.rotTranslateY;
      this.rotation = var1.rotation;
      if (var1.pointRotate != null) {
         this.pointRotate = new PointRotate(var1.pointRotate);
      } else {
         this.pointRotate = null;
      }

      this.addedRotations.addAll(var1.addedRotations);
      this.mirrorX = var1.mirrorX;
      this.mirrorY = var1.mirrorY;
      this.setBlend = var1.setBlend;
      this.blendSourceRGB = var1.blendSourceRGB;
      this.blendDestinationRGB = var1.blendDestinationRGB;
      this.blendSourceAlpha = var1.blendSourceAlpha;
      this.blendDestinationAlpha = var1.blendDestinationAlpha;
      this.shaderStates.addAll(var1.shaderStates);
      this.x1 = var1.x1;
      this.y1 = var1.y1;
      this.x2 = var1.x2;
      this.y2 = var1.y2;
      this.x3 = var1.x3;
      this.y3 = var1.y3;
      this.x4 = var1.x4;
      this.y4 = var1.y4;
   }

   public void setRotation(float var1, int var2, int var3) {
      this.pointRotate = new PointRotate(var1, var2, var3);
   }

   public void addRotation(float var1, int var2, int var3) {
      this.addedRotations.add(new PointRotate(var1, var2, var3));
   }

   public void pos(int var1, int var2) {
      this.x1 = (float)(var1 + this.translateX);
      this.y1 = (float)(var2 + this.translateY);
      this.x2 = this.x1 + (float)this.width;
      this.y2 = this.y1;
      this.x3 = this.x2;
      this.y3 = this.y1 + (float)this.height;
      this.x4 = this.x1;
      this.y4 = this.y3;
      float var3 = 0.0F;
      if (this.pointRotate != null) {
         var3 = this.pointRotate.apply(this.x1, this.y1, var3);
      }

      PointRotate var5;
      for(Iterator var4 = this.addedRotations.iterator(); var4.hasNext(); var3 = var5.apply(this.x1, this.y1, var3)) {
         var5 = (PointRotate)var4.next();
      }

   }

   public void draw(boolean var1, boolean var2) {
      try {
         if (!this.shaderTextures.isEmpty()) {
            this.drawMultiTexture(var1, var2);
         } else {
            this.drawSingleTexture(var1, var2);
         }
      } catch (Exception var4) {
         ImpossibleDrawException.submitDrawError(var4);
      }

   }

   protected void drawSingleTexture(boolean var1, boolean var2) {
      this.shaderStates.iterator().forEachRemaining(ShaderState::use);
      if (var1) {
         this.textureBinder.run();
         GL11.glLoadIdentity();
         if (this.useRotation) {
            GL11.glTranslatef((float)this.rotTranslateX + this.x1, (float)this.rotTranslateY + this.y1, 0.0F);
            GL11.glRotatef(this.rotation, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef((float)(-this.rotTranslateX) - this.x1, (float)(-this.rotTranslateY) - this.y1, 0.0F);
         }

         if (this.setBlend) {
            GL14.glBlendFuncSeparate(this.blendSourceRGB, this.blendDestinationRGB, this.blendSourceAlpha, this.blendDestinationAlpha);
         }

         GL11.glBegin(7);
      }

      GL11.glColor4f(this.red, this.green, this.blue, this.alpha);
      float var3 = this.spriteX1;
      float var4 = this.spriteX2;
      float var5 = this.spriteX3;
      float var6 = this.spriteX4;
      float var7 = this.spriteY1;
      float var8 = this.spriteY2;
      float var9 = this.spriteY3;
      float var10 = this.spriteY4;
      if (this.mirrorX) {
         var3 = this.spriteX2;
         var4 = this.spriteX1;
         var5 = this.spriteX4;
         var6 = this.spriteX3;
      }

      if (this.mirrorY) {
         var7 = this.spriteY4;
         var8 = this.spriteY3;
         var9 = this.spriteY2;
         var10 = this.spriteY1;
      }

      if (this.advCol != null) {
         GL11.glColor4f(this.advCol[0], this.advCol[1], this.advCol[2], this.advCol[3]);
      }

      GL11.glTexCoord2f(var3, var7);
      GL11.glVertex3f(this.x1, this.y1, this.drawDepth);
      if (this.advCol != null) {
         GL11.glColor4f(this.advCol[4], this.advCol[5], this.advCol[6], this.advCol[7]);
      }

      GL11.glTexCoord2f(var4, var8);
      GL11.glVertex3f(this.x2, this.y2, this.drawDepth);
      if (this.advCol != null) {
         GL11.glColor4f(this.advCol[8], this.advCol[9], this.advCol[10], this.advCol[11]);
      }

      GL11.glTexCoord2f(var5, var9);
      GL11.glVertex3f(this.x3, this.y3, this.drawDepth);
      if (this.advCol != null) {
         GL11.glColor4f(this.advCol[12], this.advCol[13], this.advCol[14], this.advCol[15]);
      }

      GL11.glTexCoord2f(var6, var10);
      GL11.glVertex3f(this.x4, this.y4, this.drawDepth);
      if (var2) {
         this.glEnd();
      }

      this.shaderStates.descendingIterator().forEachRemaining(ShaderState::stop);
   }

   protected void drawMultiTexture(boolean var1, boolean var2) {
      this.shaderStates.iterator().forEachRemaining(ShaderState::use);
      if (var1) {
         this.textureBinder.run();
         this.shaderTextures.forEach(ShaderTexture::bind);
         GL11.glLoadIdentity();
         if (this.useRotation) {
            GL11.glTranslatef((float)this.rotTranslateX + this.x1, (float)this.rotTranslateY + this.y1, 0.0F);
            GL11.glRotatef(this.rotation, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef((float)(-this.rotTranslateX) - this.x1, (float)(-this.rotTranslateY) - this.y1, 0.0F);
         }

         GL11.glBegin(7);
      }

      if (this.setBlend) {
         GL14.glBlendFuncSeparate(this.blendSourceRGB, this.blendDestinationRGB, this.blendSourceAlpha, this.blendDestinationAlpha);
      }

      GL11.glColor4f(this.red, this.green, this.blue, this.alpha);
      float var3 = this.spriteX1;
      float var4 = this.spriteX2;
      float var5 = this.spriteX3;
      float var6 = this.spriteX4;
      float var7 = this.spriteY1;
      float var8 = this.spriteY2;
      float var9 = this.spriteY3;
      float var10 = this.spriteY4;
      if (this.mirrorX) {
         var3 = this.spriteX2;
         var4 = this.spriteX1;
         var5 = this.spriteX4;
         var6 = this.spriteX3;
      }

      if (this.mirrorY) {
         var7 = this.spriteY4;
         var8 = this.spriteY3;
         var9 = this.spriteY2;
         var10 = this.spriteY1;
      }

      if (this.advCol != null) {
         GL11.glColor4f(this.advCol[0], this.advCol[1], this.advCol[2], this.advCol[3]);
      }

      GL13.glMultiTexCoord2f(33984, var3, var7);
      this.shaderTextures.forEach(ShaderTexture::startTopLeft);
      GL11.glVertex3f(this.x1, this.y1, this.drawDepth);
      if (this.advCol != null) {
         GL11.glColor4f(this.advCol[4], this.advCol[5], this.advCol[6], this.advCol[7]);
      }

      GL13.glMultiTexCoord2f(33984, var4, var8);
      this.shaderTextures.forEach(ShaderTexture::startTopRight);
      GL11.glVertex3f(this.x2, this.y2, this.drawDepth);
      if (this.advCol != null) {
         GL11.glColor4f(this.advCol[8], this.advCol[9], this.advCol[10], this.advCol[11]);
      }

      GL13.glMultiTexCoord2f(33984, var5, var9);
      this.shaderTextures.forEach(ShaderTexture::startBotRight);
      GL11.glVertex3f(this.x3, this.y3, this.drawDepth);
      if (this.advCol != null) {
         GL11.glColor4f(this.advCol[12], this.advCol[13], this.advCol[14], this.advCol[15]);
      }

      GL13.glMultiTexCoord2f(33984, var6, var10);
      this.shaderTextures.forEach(ShaderTexture::startBotLeft);
      GL11.glVertex3f(this.x4, this.y4, this.drawDepth);
      if (var2) {
         this.glEnd();
      }

      this.shaderStates.descendingIterator().forEachRemaining(ShaderState::stop);
   }

   public void glEnd() {
      GL11.glEnd();
      if (this.useRotation) {
         GL11.glLoadIdentity();
      }

      if (this.setBlend) {
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
      }

   }

   protected class PointRotate {
      protected final float pointRotate;
      protected final int pointRotateX;
      protected final int pointRotateY;

      public PointRotate(float var2, int var3, int var4) {
         this.pointRotate = var2;
         this.pointRotateX = var3;
         this.pointRotateY = var4;
      }

      public PointRotate(PointRotate var2) {
         this.pointRotate = var2.pointRotate;
         this.pointRotateX = var2.pointRotateX;
         this.pointRotateY = var2.pointRotateY;
      }

      public float apply(float var1, float var2, float var3) {
         float var4 = this.pointRotate + var3;
         if (this.pointRotate != 0.0F) {
            float var5 = GameMath.cos(this.pointRotate);
            float var6 = GameMath.sin(this.pointRotate);
            float var7 = var1 + (float)this.pointRotateX;
            float var8 = var2 + (float)this.pointRotateY;
            float var9;
            float var10;
            float var11;
            if (var3 != 0.0F) {
               var9 = GameMath.cos(var3);
               var10 = GameMath.sin(var3);
               var11 = (float)this.pointRotateX * var9 - (float)this.pointRotateY * var10 + var1;
               var8 = (float)this.pointRotateX * var10 + (float)this.pointRotateY * var9 + var2;
               var7 = var11;
            }

            var9 = (TextureDrawOptionsObj.this.x1 - var7) * var5 - (TextureDrawOptionsObj.this.y1 - var8) * var6 + var7;
            TextureDrawOptionsObj.this.y1 = (TextureDrawOptionsObj.this.x1 - var7) * var6 + (TextureDrawOptionsObj.this.y1 - var8) * var5 + var8;
            TextureDrawOptionsObj.this.x1 = var9;
            var10 = (TextureDrawOptionsObj.this.x2 - var7) * var5 - (TextureDrawOptionsObj.this.y2 - var8) * var6 + var7;
            TextureDrawOptionsObj.this.y2 = (TextureDrawOptionsObj.this.x2 - var7) * var6 + (TextureDrawOptionsObj.this.y2 - var8) * var5 + var8;
            TextureDrawOptionsObj.this.x2 = var10;
            var11 = (TextureDrawOptionsObj.this.x3 - var7) * var5 - (TextureDrawOptionsObj.this.y3 - var8) * var6 + var7;
            TextureDrawOptionsObj.this.y3 = (TextureDrawOptionsObj.this.x3 - var7) * var6 + (TextureDrawOptionsObj.this.y3 - var8) * var5 + var8;
            TextureDrawOptionsObj.this.x3 = var11;
            float var12 = (TextureDrawOptionsObj.this.x4 - var7) * var5 - (TextureDrawOptionsObj.this.y4 - var8) * var6 + var7;
            TextureDrawOptionsObj.this.y4 = (TextureDrawOptionsObj.this.x4 - var7) * var6 + (TextureDrawOptionsObj.this.y4 - var8) * var5 + var8;
            TextureDrawOptionsObj.this.x4 = var12;
         }

         return var4;
      }
   }
}
