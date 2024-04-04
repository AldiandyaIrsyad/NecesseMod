package necesse.gfx.drawOptions.texture;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import necesse.engine.world.GameClock;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.light.GameLight;

public class SharedTextureDrawOptions {
   public final GameTexture texture;
   private ArrayList<TextureDrawOptionsObj> options = new ArrayList();

   public SharedTextureDrawOptions(GameTexture var1) {
      this.texture = var1;
   }

   public Wrapper add(GameTextureSection var1) {
      if (var1.getTexture() != this.texture) {
         throw new IllegalStateException("Invalid texture");
      } else {
         TextureDrawOptionsObj var2 = var1.initDraw().opts;
         synchronized(this) {
            this.options.add(var2);
         }

         return new Wrapper(var2);
      }
   }

   public Wrapper addFull() {
      return this.add(new GameTextureSection(this.texture));
   }

   public Wrapper addSprite(int var1, int var2, int var3, int var4) {
      return this.add((new GameTextureSection(this.texture)).sprite(var1, var2, var3, var4));
   }

   public Wrapper addSprite(int var1, int var2, int var3) {
      return this.add((new GameTextureSection(this.texture)).sprite(var1, var2, var3));
   }

   public Wrapper addSection(int var1, int var2, int var3, int var4) {
      return this.add(new GameTextureSection(this.texture, var1, var2, var3, var4));
   }

   public Wrapper addSpriteSection(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      return this.add((new GameTextureSection(this.texture)).sprite(var1, var2, var3, var4).section(var5, var6, var7, var8));
   }

   public Wrapper addSpriteSection(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return this.add((new GameTextureSection(this.texture)).sprite(var1, var2, var3).section(var4, var5, var6, var7));
   }

   public SharedTextureDrawOptions forEachDraw(Consumer<Wrapper> var1) {
      synchronized(this) {
         Iterator var3 = this.options.iterator();

         while(var3.hasNext()) {
            TextureDrawOptionsObj var4 = (TextureDrawOptionsObj)var3.next();
            var1.accept(new Wrapper(var4));
         }

         return this;
      }
   }

   public void draw() {
      synchronized(this) {
         Iterator var2 = this.options.iterator();
         TextureDrawOptionsObj var3 = null;
         boolean var4 = true;

         while(var2.hasNext()) {
            TextureDrawOptionsObj var5 = (TextureDrawOptionsObj)var2.next();
            if (var5 != null) {
               var5.draw(var4, !var2.hasNext());
               var3 = var5;
               var4 = false;
            } else {
               if (var3 != null) {
                  var3.glEnd();
               }

               var4 = true;
            }
         }

      }
   }

   public static class Wrapper extends TextureDrawOptionsMods {
      public Wrapper(TextureDrawOptionsObj var1) {
         super(var1);
      }

      public Wrapper rotate(float var1, int var2, int var3) {
         super.rotate(var1, var2, var3);
         return this;
      }

      public Wrapper rotate(float var1) {
         super.rotate(var1);
         return this;
      }

      public Wrapper addRotation(float var1, int var2, int var3) {
         super.addRotation(var1, var2, var3);
         return this;
      }

      public Wrapper rotateTexture(int var1, int var2, int var3) {
         super.rotateTexture(var1, var2, var3);
         return this;
      }

      public Wrapper rotateTexture(int var1) {
         super.rotateTexture(var1);
         return this;
      }

      public Wrapper size(int var1, int var2) {
         super.size(var1, var2);
         return this;
      }

      public Wrapper size(Dimension var1) {
         super.size(var1);
         return this;
      }

      public Wrapper shrinkWidth(int var1, boolean var2) {
         super.shrinkWidth(var1, var2);
         return this;
      }

      public Wrapper shrinkHeight(int var1, boolean var2) {
         super.shrinkHeight(var1, var2);
         return this;
      }

      public Wrapper size(int var1, boolean var2) {
         super.size(var1, var2);
         return this;
      }

      public Wrapper size(int var1) {
         super.size(var1);
         return this;
      }

      public Wrapper color(float var1, float var2, float var3, float var4) {
         super.color(var1, var2, var3, var4);
         return this;
      }

      public Wrapper color(float var1) {
         super.color(var1);
         return this;
      }

      public Wrapper brightness(float var1) {
         super.brightness(var1);
         return this;
      }

      public Wrapper color(float var1, float var2, float var3) {
         super.color(var1, var2, var3);
         return this;
      }

      public Wrapper color(Color var1, boolean var2) {
         super.color(var1, var2);
         return this;
      }

      public Wrapper color(Color var1) {
         super.color(var1);
         return this;
      }

      public Wrapper alpha(float var1) {
         super.alpha(var1);
         return this;
      }

      public Wrapper light(GameLight var1) {
         super.light(var1);
         return this;
      }

      public Wrapper colorLight(float var1, float var2, float var3, GameLight var4) {
         super.colorLight(var1, var2, var3, var4);
         return this;
      }

      public Wrapper colorLight(float var1, float var2, float var3, float var4, GameLight var5) {
         super.colorLight(var1, var2, var3, var4, var5);
         return this;
      }

      public Wrapper colorLight(Color var1, GameLight var2) {
         super.colorLight(var1, var2);
         return this;
      }

      public Wrapper colorLight(Color var1, boolean var2, GameLight var3) {
         super.colorLight(var1, var2, var3);
         return this;
      }

      public Wrapper colorMult(Color var1) {
         super.colorMult(var1);
         return this;
      }

      public Wrapper spelunkerColorLight(float var1, float var2, float var3, float var4, GameLight var5, boolean var6, long var7, GameClock var9, long var10, float var12, int var13) {
         super.spelunkerColorLight(var1, var2, var3, var4, var5, var6, var7, var9, var10, var12, var13);
         return this;
      }

      public Wrapper spelunkerColorLight(float var1, float var2, float var3, GameLight var4, boolean var5, long var6, GameClock var8, long var9, float var11, int var12) {
         super.spelunkerColorLight(var1, var2, var3, var4, var5, var6, var8, var9, var11, var12);
         return this;
      }

      public Wrapper spelunkerColorLight(Color var1, boolean var2, GameLight var3, boolean var4, long var5, GameClock var7, long var8, float var10, int var11) {
         super.spelunkerColorLight(var1, var2, var3, var4, var5, var7, var8, var10, var11);
         return this;
      }

      public Wrapper spelunkerColorLight(Color var1, GameLight var2, boolean var3, long var4, GameClock var6, long var7, float var9, int var10) {
         super.spelunkerColorLight(var1, var2, var3, var4, var6, var7, var9, var10);
         return this;
      }

      public Wrapper spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5, long var6, float var8, int var9) {
         super.spelunkerLight(var1, var2, var3, var5, var6, var8, var9);
         return this;
      }

      public Wrapper spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5) {
         super.spelunkerLight(var1, var2, var3, var5);
         return this;
      }

      public Wrapper advColor(float[] var1) {
         super.advColor(var1);
         return this;
      }

      public Wrapper translatePos(int var1, int var2) {
         super.translatePos(var1, var2);
         return this;
      }

      public Wrapper mirrorX() {
         super.mirrorX();
         return this;
      }

      public Wrapper mirrorY() {
         super.mirrorY();
         return this;
      }

      public Wrapper mirror(boolean var1, boolean var2) {
         super.mirror(var1, var2);
         return this;
      }

      public Wrapper depth(float var1) {
         super.depth(var1);
         return this;
      }

      public Wrapper blendFunc(int var1, int var2, int var3, int var4) {
         super.blendFunc(var1, var2, var3, var4);
         return this;
      }

      public Wrapper pos(int var1, int var2) {
         super.pos(var1, var2);
         return this;
      }

      public Wrapper pos(int var1, int var2, boolean var3) {
         super.pos(var1, var2, var3);
         return this;
      }

      public Wrapper posMiddle(int var1, int var2, boolean var3) {
         super.posMiddle(var1, var2, var3);
         return this;
      }

      public Wrapper posMiddle(int var1, int var2) {
         super.posMiddle(var1, var2);
         return this;
      }

      public int getWidth() {
         return super.getWidth();
      }

      public int getHeight() {
         return super.getHeight();
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods posMiddle(int var1, int var2) {
         return this.posMiddle(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods posMiddle(int var1, int var2, boolean var3) {
         return this.posMiddle(var1, var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods pos(int var1, int var2, boolean var3) {
         return this.pos(var1, var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods pos(int var1, int var2) {
         return this.pos(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods blendFunc(int var1, int var2, int var3, int var4) {
         return this.blendFunc(var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods depth(float var1) {
         return this.depth(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods mirror(boolean var1, boolean var2) {
         return this.mirror(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods mirrorY() {
         return this.mirrorY();
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods mirrorX() {
         return this.mirrorX();
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods translatePos(int var1, int var2) {
         return this.translatePos(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods advColor(float[] var1) {
         return this.advColor(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5) {
         return this.spelunkerLight(var1, var2, var3, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods spelunkerLight(GameLight var1, boolean var2, long var3, GameClock var5, long var6, float var8, int var9) {
         return this.spelunkerLight(var1, var2, var3, var5, var6, var8, var9);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods spelunkerColorLight(Color var1, GameLight var2, boolean var3, long var4, GameClock var6, long var7, float var9, int var10) {
         return this.spelunkerColorLight(var1, var2, var3, var4, var6, var7, var9, var10);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods spelunkerColorLight(Color var1, boolean var2, GameLight var3, boolean var4, long var5, GameClock var7, long var8, float var10, int var11) {
         return this.spelunkerColorLight(var1, var2, var3, var4, var5, var7, var8, var10, var11);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods spelunkerColorLight(float var1, float var2, float var3, GameLight var4, boolean var5, long var6, GameClock var8, long var9, float var11, int var12) {
         return this.spelunkerColorLight(var1, var2, var3, var4, var5, var6, var8, var9, var11, var12);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods spelunkerColorLight(float var1, float var2, float var3, float var4, GameLight var5, boolean var6, long var7, GameClock var9, long var10, float var12, int var13) {
         return this.spelunkerColorLight(var1, var2, var3, var4, var5, var6, var7, var9, var10, var12, var13);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods colorMult(Color var1) {
         return this.colorMult(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods colorLight(Color var1, boolean var2, GameLight var3) {
         return this.colorLight(var1, var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods colorLight(Color var1, GameLight var2) {
         return this.colorLight(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods colorLight(float var1, float var2, float var3, float var4, GameLight var5) {
         return this.colorLight(var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods colorLight(float var1, float var2, float var3, GameLight var4) {
         return this.colorLight(var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods light(GameLight var1) {
         return this.light(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods alpha(float var1) {
         return this.alpha(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods color(Color var1) {
         return this.color(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods color(Color var1, boolean var2) {
         return this.color(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods color(float var1, float var2, float var3) {
         return this.color(var1, var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods brightness(float var1) {
         return this.brightness(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods color(float var1) {
         return this.color(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods color(float var1, float var2, float var3, float var4) {
         return this.color(var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods size(int var1) {
         return this.size(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods size(int var1, boolean var2) {
         return this.size(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods shrinkHeight(int var1, boolean var2) {
         return this.shrinkHeight(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods shrinkWidth(int var1, boolean var2) {
         return this.shrinkWidth(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods size(Dimension var1) {
         return this.size(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods size(int var1, int var2) {
         return this.size(var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods rotateTexture(int var1) {
         return this.rotateTexture(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods rotateTexture(int var1, int var2, int var3) {
         return this.rotateTexture(var1, var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods addRotation(float var1, int var2, int var3) {
         return this.addRotation(var1, var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods rotate(float var1) {
         return this.rotate(var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public TextureDrawOptionsMods rotate(float var1, int var2, int var3) {
         return this.rotate(var1, var2, var3);
      }
   }
}
