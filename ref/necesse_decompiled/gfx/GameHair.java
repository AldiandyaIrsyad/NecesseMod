package necesse.gfx;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import necesse.engine.GameCache;
import necesse.engine.GameLog;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.GameTextureData;

public class GameHair {
   public static boolean printDebugs = false;
   private static ArrayList<GameHair> gameHairs;
   private static ArrayList<HairColor> hairColors;
   public final int weight;
   private ArrayList<GameTexture> textures;
   private ArrayList<GameTexture> backTextures;
   private ArrayList<GameTexture> wigTextures;
   private int hairNum;
   private HairGender gender;

   public static void loadHair() {
      gameHairs = new ArrayList();
      gameHairs.add(new GameHair(100, 0, GameHair.HairGender.NEUTRAL));
      gameHairs.add(new GameHair(100, 1, GameHair.HairGender.MALE));
      gameHairs.add(new GameHair(100, 2, GameHair.HairGender.MALE));
      gameHairs.add(new GameHair(100, 3, GameHair.HairGender.FEMALE));
      gameHairs.add(new GameHair(100, 4, GameHair.HairGender.MALE));
      gameHairs.add(new GameHair(100, 5, GameHair.HairGender.FEMALE));
      gameHairs.add(new GameHair(100, 6, GameHair.HairGender.MALE));
      gameHairs.add(new GameHair(100, 7, GameHair.HairGender.FEMALE));
      gameHairs.add(new GameHair(100, 8, GameHair.HairGender.MALE));
      gameHairs.add(new GameHair(100, 9, GameHair.HairGender.MALE));
      gameHairs.add(new GameHair(100, 10, GameHair.HairGender.FEMALE));
      gameHairs.add(new GameHair(100, 11, GameHair.HairGender.MALE));
      gameHairs.add(new GameHair(100, 12, GameHair.HairGender.FEMALE));
      gameHairs.add(new GameHair(10, 13, GameHair.HairGender.FEMALE));
   }

   public static void loadHairTextures() {
      if (gameHairs == null) {
         throw new NullPointerException("Load hair before loading hair textures.");
      } else {
         Iterator var0 = gameHairs.iterator();

         while(var0.hasNext()) {
            GameHair var1 = (GameHair)var0.next();
            var1.loadTextures();
         }

      }
   }

   public static void loadHairColors() {
      hairColors = new ArrayList();
      String var0 = "player/hair/haircolors";

      try {
         GameTexture var1 = GameTexture.fromFileRaw(var0, true);
         HairColor var2 = null;

         for(int var3 = 0; var3 < var1.getHeight(); ++var3) {
            Color[] var4 = new Color[var1.getWidth()];
            Color var5 = var1.getColor(0, var3);
            int var6 = (GameMath.max(var5.getRed(), var5.getGreen(), var5.getBlue()) + GameMath.min(var5.getRed(), var5.getGreen(), var5.getBlue())) / 2;

            for(int var7 = 1; var7 < var4.length; ++var7) {
               var4[var7 - 1] = var1.getColor(var7, var3);
            }

            if (var3 == 0) {
               var2 = new HairColor(var6, (HairColor)null, var4);
            } else {
               hairColors.add(new HairColor(var6, var2, var4));
            }
         }

      } catch (FileNotFoundException var8) {
         throw new RuntimeException("Could not find hair colors texture file at " + var0);
      }
   }

   public GameHair(int var1, int var2, HairGender var3) {
      this.weight = var1;
      this.hairNum = var2;
      this.gender = var3;
   }

   public GameTexture getHairTexture(int var1) {
      return this.hairNum == 0 ? null : (GameTexture)this.textures.get(var1 % this.textures.size());
   }

   public GameTexture getBackHairTexture(int var1) {
      return this.hairNum != 0 && this.backTextures != null ? (GameTexture)this.backTextures.get(var1 % this.backTextures.size()) : null;
   }

   public GameTexture getWigTexture(int var1) {
      return this.hairNum == 0 ? (GameTexture)this.wigTextures.get(0) : (GameTexture)this.wigTextures.get(var1 % this.wigTextures.size());
   }

   public static GameHair getHair(int var0) {
      return (GameHair)gameHairs.get(var0 % gameHairs.size());
   }

   public static HairGender getHairGender(int var0) {
      return ((GameHair)gameHairs.get(var0 % gameHairs.size())).gender;
   }

   public static int getTotalHair() {
      return gameHairs.size();
   }

   public static int getTotalHairColors() {
      return hairColors.size();
   }

   public static int getRandomHairColor(GameRandom var0) {
      if (hairColors != null && !hairColors.isEmpty()) {
         TicketSystemList var1 = new TicketSystemList();

         for(int var2 = 0; var2 < hairColors.size(); ++var2) {
            var1.addObject(((HairColor)hairColors.get(var2)).weight, var2);
         }

         return (Integer)var1.getRandomObject(var0);
      } else {
         return var0.nextInt();
      }
   }

   public static int getRandomHair(GameRandom var0) {
      if (gameHairs != null && !gameHairs.isEmpty()) {
         TicketSystemList var1 = new TicketSystemList();

         for(int var2 = 0; var2 < gameHairs.size(); ++var2) {
            var1.addObject(((GameHair)gameHairs.get(var2)).weight, var2);
         }

         return (Integer)var1.getRandomObject(var0);
      } else {
         return var0.nextInt();
      }
   }

   public void loadTextures() {
      this.loadFrontTextures();
      this.loadBackTextures();
      this.loadWigTextures();
   }

   public void loadFrontTextures() {
      if (this.hairNum != 0) {
         GameTexture var1 = GameTexture.fromFile("player/hair/hair" + this.hairNum, true);
         ArrayList var2 = new ArrayList();
         Iterator var3 = hairColors.iterator();

         int var5;
         while(var3.hasNext()) {
            HairColor var4 = (HairColor)var3.next();
            var5 = var1.hashCode() * GameRandom.prime(15) * var4.hashCode();
            var2.add(var5);
         }

         GameHairCache var12;
         try {
            var12 = (GameHairCache)GameCache.getObject("version/hairtex" + this.hairNum);
         } catch (Exception var10) {
            var12 = null;
         }

         this.textures = new ArrayList();
         boolean var13 = false;

         for(var5 = 0; var5 < hairColors.size(); ++var5) {
            HairColor var6 = (HairColor)hairColors.get(var5);
            if (var12 != null && var5 < var12.data.size()) {
               ColorAndTextureHash var7 = (ColorAndTextureHash)var12.data.get(var5);
               int var8 = (Integer)var2.get(var5);
               if (var7 != null && var7.hash == var8) {
                  try {
                     GameTexture var9 = new GameTexture("cachedHairFront" + this.hairNum + " color" + var5, var7.textureData);
                     this.textures.add(var9);
                     continue;
                  } catch (Exception var11) {
                     GameLog.warn.println("Could not load front hair cache for " + this.hairNum + " for color " + var5);
                  }
               } else if (printDebugs) {
                  GameLog.debug.println("Detected invalid front hair cache for" + this.hairNum + " for color " + var5);
               }
            }

            this.textures.add(var6.applyColorToTexture(var1));
            if (printDebugs) {
               GameLog.debug.println("Generating new front hair for " + this.hairNum + " for color " + var5);
            }

            var13 = true;
         }

         var1.makeFinal();
         if (var13) {
            ArrayList var14 = new ArrayList(hairColors.size());

            for(int var15 = 0; var15 < hairColors.size(); ++var15) {
               var14.add(new ColorAndTextureHash((Integer)var2.get(var15), (GameTexture)this.textures.get(var15)));
            }

            GameCache.cacheObject(new GameHairCache(var14), "version/hairtex" + this.hairNum);
         }

         this.textures.forEach(GameTexture::makeFinal);
      }
   }

   public void loadBackTextures() {
      if (this.hairNum != 0) {
         GameTexture var1;
         try {
            var1 = GameTexture.fromFileRaw("player/hair/hair" + this.hairNum + "_back", true);
         } catch (FileNotFoundException var11) {
            return;
         }

         ArrayList var2 = new ArrayList();
         Iterator var3 = hairColors.iterator();

         int var5;
         while(var3.hasNext()) {
            HairColor var4 = (HairColor)var3.next();
            var5 = var1.hashCode() * GameRandom.prime(15) * var4.hashCode();
            var2.add(var5);
         }

         GameHairCache var13;
         try {
            var13 = (GameHairCache)GameCache.getObject("version/hairtexb" + this.hairNum);
         } catch (Exception var10) {
            var13 = null;
         }

         this.backTextures = new ArrayList();
         boolean var14 = false;

         for(var5 = 0; var5 < hairColors.size(); ++var5) {
            HairColor var6 = (HairColor)hairColors.get(var5);
            if (var13 != null && var5 < var13.data.size()) {
               ColorAndTextureHash var7 = (ColorAndTextureHash)var13.data.get(var5);
               int var8 = (Integer)var2.get(var5);
               if (var7 != null && var7.hash == var8) {
                  try {
                     GameTexture var9 = new GameTexture("cachedHairBack" + this.hairNum + " color" + var5, var7.textureData);
                     this.backTextures.add(var9);
                     continue;
                  } catch (Exception var12) {
                     GameLog.warn.println("Could not load back hair cache for " + this.hairNum + " for color " + var5);
                  }
               } else if (printDebugs) {
                  GameLog.debug.println("Detected invalid back hair cache for" + this.hairNum + " for color " + var5);
               }
            }

            if (printDebugs) {
               GameLog.debug.println("Generating new back hair for " + this.hairNum + " for color " + var5);
            }

            this.backTextures.add(var6.applyColorToTexture(var1));
            var14 = true;
         }

         var1.makeFinal();
         if (var14) {
            ArrayList var15 = new ArrayList(hairColors.size());

            for(int var16 = 0; var16 < hairColors.size(); ++var16) {
               var15.add(new ColorAndTextureHash((Integer)var2.get(var16), (GameTexture)this.backTextures.get(var16)));
            }

            GameCache.cacheObject(new GameHairCache(var15), "version/hairtexb" + this.hairNum);
         }

         this.backTextures.forEach(GameTexture::makeFinal);
      }
   }

   public void loadWigTextures() {
      this.wigTextures = new ArrayList();
      GameTexture var1 = new GameTexture(GameTexture.fromFile("player/hair/wigs", true), 32 * this.hairNum, 0, 32, 32);
      if (this.hairNum == 0) {
         this.wigTextures.add(var1);
      } else {
         ArrayList var2 = new ArrayList();
         Iterator var3 = hairColors.iterator();

         int var5;
         while(var3.hasNext()) {
            HairColor var4 = (HairColor)var3.next();
            var5 = var1.hashCode() * GameRandom.prime(15) * var4.hashCode();
            var2.add(var5);
         }

         GameHairCache var12;
         try {
            var12 = (GameHairCache)GameCache.getObject("version/hairtexw" + this.hairNum);
         } catch (Exception var10) {
            var12 = null;
         }

         this.wigTextures = new ArrayList();
         boolean var13 = false;

         for(var5 = 0; var5 < hairColors.size(); ++var5) {
            HairColor var6 = (HairColor)hairColors.get(var5);
            if (var12 != null && var5 < var12.data.size()) {
               ColorAndTextureHash var7 = (ColorAndTextureHash)var12.data.get(var5);
               int var8 = (Integer)var2.get(var5);
               if (var7 != null && var7.hash == var8) {
                  try {
                     GameTexture var9 = new GameTexture("cachedHairWig" + this.hairNum + " color" + var5, var7.textureData);
                     this.wigTextures.add(var9);
                     continue;
                  } catch (Exception var11) {
                     GameLog.warn.println("Could not load wig cache for " + this.hairNum + " for color " + var5);
                  }
               } else if (printDebugs) {
                  GameLog.debug.println("Detected invalid wig cache for " + this.hairNum + " for color " + var5);
               }
            }

            if (printDebugs) {
               GameLog.debug.println("Generating new wig for " + this.hairNum + " for color " + var5);
            }

            this.wigTextures.add(var6.applyColorToTexture(var1));
            var13 = true;
         }

         var1.makeFinal();
         if (var13) {
            ArrayList var14 = new ArrayList(hairColors.size());

            for(int var15 = 0; var15 < hairColors.size(); ++var15) {
               var14.add(new ColorAndTextureHash((Integer)var2.get(var15), (GameTexture)this.wigTextures.get(var15)));
            }

            GameCache.cacheObject(new GameHairCache(var14), "version/hairtexw" + this.hairNum);
         }

         this.wigTextures.forEach(GameTexture::makeFinal);
      }
   }

   public static enum HairGender {
      NEUTRAL,
      MALE,
      FEMALE;

      private HairGender() {
      }

      // $FF: synthetic method
      private static HairGender[] $values() {
         return new HairGender[]{NEUTRAL, MALE, FEMALE};
      }
   }

   private static class HairColor {
      public final int weight;
      private final Color[] colors;
      private final HairColor toneColors;

      public HairColor(int var1, HairColor var2, Color[] var3) {
         this.weight = var1;
         this.toneColors = var2;
         this.colors = var3;
      }

      public GameTexture applyColorToTexture(GameTexture var1) {
         GameTexture var2 = new GameTexture(var1);

         for(int var3 = 0; var3 < this.colors.length; ++var3) {
            var2.replaceColor(this.toneColors.colors[var3], this.colors[var3]);
         }

         return var2;
      }

      public int hashCode() {
         return Arrays.hashCode(this.colors);
      }
   }

   private static class GameHairCache implements Serializable {
      public ArrayList<ColorAndTextureHash> data;

      public GameHairCache(ArrayList<ColorAndTextureHash> var1) {
         this.data = var1;
      }
   }

   private static class ColorAndTextureHash implements Serializable {
      public int hash;
      public GameTextureData textureData;

      public ColorAndTextureHash(int var1, GameTexture var2) {
         this.hash = var1;
         this.textureData = var2.getData();
      }
   }
}
