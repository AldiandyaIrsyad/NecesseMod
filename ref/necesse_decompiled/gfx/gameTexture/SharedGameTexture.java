package necesse.gfx.gameTexture;

import java.awt.Color;
import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class SharedGameTexture {
   private String debugName;
   private ArrayList<AddedTexture> textures = new ArrayList();

   public SharedGameTexture(String var1) {
      this.debugName = var1;
   }

   public GameTexture generate() {
      return this.generate(false);
   }

   public GameTexture generate(boolean var1) {
      int var2 = 0;
      int var3 = 0;

      AddedTexture var5;
      for(Iterator var4 = this.textures.iterator(); var4.hasNext(); var3 = Math.max(var3, var5.w)) {
         var5 = (AddedTexture)var4.next();
         var2 += var5.w * var5.h;
      }

      Comparator var23 = Comparator.comparingInt((var0) -> {
         return var0.h;
      });
      this.textures.sort(var23.reversed());
      int var24 = (int)Math.max(Math.ceil(Math.sqrt((double)var2 / 0.95)), (double)var3);
      ArrayList var6 = new ArrayList();
      var6.add(new Rectangle(0, 0, var24, Integer.MAX_VALUE));
      ArrayList var7 = new ArrayList();
      Iterator var8 = this.textures.iterator();

      while(true) {
         while(true) {
            Rectangle var11;
            Rectangle var12;
            while(var8.hasNext()) {
               AddedTexture var9 = (AddedTexture)var8.next();

               for(int var10 = var6.size() - 1; var10 >= 0; --var10) {
                  var11 = (Rectangle)var6.get(var10);
                  if (var9.w <= var11.width && var9.h <= var11.height) {
                     var7.add(new PackedTexture(var11.x, var11.y, var9));
                     if (var9.w == var11.width && var9.h == var11.height) {
                        var12 = (Rectangle)var6.remove(var6.size() - 1);
                        if (var10 < var6.size()) {
                           var6.remove(var10);
                           var6.add(var10, var12);
                        }
                        break;
                     }

                     if (var9.h == var11.height) {
                        var11.x += var9.w;
                        var11.width -= var9.w;
                     } else if (var9.w == var11.width) {
                        var11.y += var9.h;
                        var11.height -= var9.h;
                     } else {
                        var6.add(new Rectangle(var11.x + var9.w, var11.y, var11.width - var9.w, var9.h));
                        var11.y += var9.h;
                        var11.height -= var9.h;
                     }
                     break;
                  }
               }
            }

            int var25 = 0;
            int var26 = 0;

            Iterator var27;
            PackedTexture var28;
            for(var27 = var7.iterator(); var27.hasNext(); var26 = Math.max(var26, var28.y + var28.texture.h)) {
               var28 = (PackedTexture)var27.next();
               var25 = Math.max(var25, var28.x + var28.texture.w);
            }

            for(var27 = var6.iterator(); var27.hasNext(); var25 = Math.max(var25, var11.x + var11.width)) {
               var11 = (Rectangle)var27.next();
            }

            if (var25 == 0 && var26 == 0) {
               return null;
            }

            GameTexture var29 = new GameTexture(this.debugName, var25, var26);
            Iterator var30 = var7.iterator();

            while(true) {
               int var20;
               GameTextureSection var33;
               do {
                  int var13;
                  int var14;
                  int var16;
                  int var17;
                  if (!var30.hasNext()) {
                     if (var1) {
                        var30 = var6.iterator();

                        label99:
                        while(true) {
                           int var32;
                           do {
                              do {
                                 if (!var30.hasNext()) {
                                    break label99;
                                 }

                                 var12 = (Rectangle)var30.next();
                                 var13 = var12.x;
                                 var14 = var13 + var12.width;
                                 var32 = var12.y;
                              } while(var32 >= var29.getHeight());

                              var16 = var32 + var12.height;
                           } while(var16 >= var29.getHeight());

                           for(var17 = var13; var17 < var14; ++var17) {
                              var29.setPixel(var17, var32, new Color(255, 100, 100, 150));
                              var29.setPixel(var17, var16 - 1, new Color(255, 100, 100, 150));
                           }

                           for(var17 = var32; var17 < var16; ++var17) {
                              var29.setPixel(var13, var17, new Color(255, 100, 100, 150));
                              var29.setPixel(var14 - 1, var17, new Color(255, 100, 100, 150));
                           }
                        }
                     }

                     var29.makeFinal();
                     return var29;
                  }

                  PackedTexture var31 = (PackedTexture)var30.next();
                  var13 = var31.x;
                  var14 = var31.y;
                  GameTexture var15 = var31.texture.texture;
                  var16 = var15.getHeight();
                  var17 = var15.getWidth();
                  ByteBuffer var18;
                  if (!var15.isFinal()) {
                     var18 = var15.buffer;
                  } else {
                     var18 = BufferUtils.createByteBuffer(var17 * var16 * 4);
                     var15.bind();
                     GL11.glGetTexImage(3553, 0, 6408, 5121, var18);
                  }

                  for(int var19 = 0; var19 < var16; ++var19) {
                     var20 = (var13 + (var14 + var19) * var25) * 4;
                     int var21 = var19 * var17 * 4;
                     var29.buffer.position(var20);
                     var18.position(var21);

                     for(int var22 = 0; var22 < var17 * 4; ++var22) {
                        var29.buffer.put(var18.get());
                     }
                  }

                  var33 = new GameTextureSection(var29, var13, var13 + var17, var14, var14 + var16);
                  var31.texture.section.texture = var29;
                  var31.texture.section.startX = var13;
                  var31.texture.section.endX = var13 + var17;
                  var31.texture.section.startY = var14;
                  var31.texture.section.endY = var14 + var16;
               } while(!var1);

               for(var20 = var33.startX; var20 < var33.endX; ++var20) {
                  var29.setPixel(var20, var33.startY, new Color(255, 100, 255));
                  var29.setPixel(var20, var33.endY - 1, new Color(255, 100, 255));
               }

               for(var20 = var33.startY; var20 < var33.endY; ++var20) {
                  var29.setPixel(var33.startX, var20, new Color(255, 100, 255));
                  var29.setPixel(var33.endX - 1, var20, new Color(255, 100, 255));
               }
            }
         }
      }
   }

   public GameTextureSection addTexture(GameTexture var1) {
      if (this.textures == null) {
         throw new IllegalStateException("Shared texture closed");
      } else {
         GameTextureSection var2 = new GameTextureSection();
         this.textures.add(new AddedTexture(var1, var2));
         var1.makeFinal();
         return var2;
      }
   }

   public GameTextureSection addBlankQuad(int var1, int var2) {
      GameTexture var3 = new GameTexture(this.debugName + " blank" + var1 + "x" + var2, var1, var2);
      var3.fill(255, 255, 255, 255);
      return this.addTexture(var3);
   }

   public void close() {
      this.textures = null;
   }

   protected static class AddedTexture {
      public int w;
      public int h;
      public final GameTexture texture;
      public final GameTextureSection section;

      public AddedTexture(GameTexture var1, GameTextureSection var2) {
         this.w = var1.getWidth();
         this.h = var1.getHeight();
         this.texture = var1;
         this.section = var2;
      }
   }

   protected static class PackedTexture {
      public int x;
      public int y;
      public final AddedTexture texture;

      public PackedTexture(int var1, int var2, AddedTexture var3) {
         this.x = var1;
         this.y = var2;
         this.texture = var3;
      }
   }
}
