package necesse.gfx;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.GameCache;
import necesse.engine.GameLog;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.GameTextureData;

public class GameSkin {
   public static boolean printDebugs = false;
   private static ArrayList<SkinColor> skinColors = new ArrayList();
   private static ArrayList<GameSkin> skins = new ArrayList();
   private static ArrayList<GameSkin> skinsHumanlike = new ArrayList();
   public GameTexture head;
   public GameTexture body;
   public GameTexture leftArms;
   public GameTexture rightArms;
   public GameTexture feet;
   public SkinColor color;
   public final boolean isHumanlike;

   public static void loadSkinTextures() {
      loadSkinColors();
      loadTextures();
   }

   private static void loadSkinColors() {
      skinColors.clear();
      String var0 = "player/skin/skincolors";

      try {
         GameTexture var1 = GameTexture.fromFileRaw(var0, true);
         SkinColor var2 = null;

         for(int var3 = 0; var3 < var1.getHeight(); ++var3) {
            Color[] var4 = new Color[var1.getWidth()];
            Color var5 = var1.getColor(0, var3);
            int var6 = (GameMath.max(var5.getRed(), var5.getGreen(), var5.getBlue()) + GameMath.min(var5.getRed(), var5.getGreen(), var5.getBlue())) / 2;

            for(int var7 = 1; var7 < var4.length; ++var7) {
               var4[var7 - 1] = var1.getColor(var7, var3);
            }

            if (var3 == 0) {
               var2 = new SkinColor(var6, (SkinColor)null, var4);
            } else {
               skinColors.add(new SkinColor(var6, var2, var4));
            }
         }

      } catch (FileNotFoundException var8) {
         throw new RuntimeException("Could not find skin colors texture file at " + var0);
      }
   }

   private static void loadTextures() {
      byte var0 = 4;

      for(int var1 = 0; var1 < skinColors.size(); ++var1) {
         GameSkin var2 = new GameSkin((SkinColor)skinColors.get(var1), var1 < var0);
         skins.add(var2);
         if (var2.isHumanlike) {
            skinsHumanlike.add(var2);
         }

         var2.loadTextures(var1);
      }

   }

   private GameSkin(SkinColor var1, boolean var2) {
      this.color = var1;
      this.isHumanlike = var2;
   }

   public void loadTextures(int var1) {
      this.head = this.loadTexture(var1, "head");
      this.body = this.loadTexture(var1, "body");
      this.leftArms = this.loadTexture(var1, "arms_left");
      this.rightArms = this.loadTexture(var1, "arms_right");
      this.feet = this.loadTexture(var1, "feet");
   }

   public GameTexture loadTexture(int var1, String var2) {
      GameTexture var3 = GameTexture.fromFile("player/skin/" + var2, true);
      int var4 = var3.hashCode() * GameRandom.prime(15) * this.color.hashCode();

      try {
         GameSkinCache var5 = (GameSkinCache)GameCache.getObject("version/skin" + var2 + var1);
         if (var5 != null && var5.hash == var4) {
            try {
               GameTexture var10 = new GameTexture("cachedSkin " + var2 + var1, var5.textureData);
               var10.makeFinal();
               return var10;
            } catch (Exception var7) {
               GameLog.warn.println("Could not load skin cache for " + var2 + " with id: " + var1);
            }
         } else if (printDebugs) {
            GameLog.debug.println("Detected invalid " + var2 + " with id: " + var1);
         }
      } catch (Exception var8) {
      }

      if (printDebugs) {
         GameLog.debug.println("Generating new " + var2 + " with id: " + var1);
      }

      GameTexture var9 = this.color.applyColorToTexture(var3);
      GameSkinCache var6 = new GameSkinCache(var4, var9);
      GameCache.cacheObject(var6, "version/skin" + var2 + var1);
      var9.makeFinal();
      return var9;
   }

   public GameTexture getHeadTexture() {
      return this.head;
   }

   public GameTexture getBodyTexture() {
      return this.body;
   }

   public GameTexture getLeftArmsTexture() {
      return this.leftArms;
   }

   public GameTexture getRightArmsTexture() {
      return this.rightArms;
   }

   public GameTexture getFeetTexture() {
      return this.feet;
   }

   public static int getTotalSkins() {
      return skins.size();
   }

   public static GameSkin getSkin(int var0, boolean var1) {
      return var1 ? (GameSkin)skinsHumanlike.get(var0 % skinsHumanlike.size()) : (GameSkin)skins.get(var0 % skins.size());
   }

   public static int getRandomSkinColor(GameRandom var0) {
      if (skinColors != null && !skinColors.isEmpty()) {
         TicketSystemList var1 = new TicketSystemList();

         for(int var2 = 0; var2 < skinColors.size(); ++var2) {
            var1.addObject(((SkinColor)skinColors.get(var2)).weight, var2);
         }

         return (Integer)var1.getRandomObject(var0);
      } else {
         return var0.nextInt();
      }
   }

   private static class SkinColor {
      public final int weight;
      private final Color[] colors;
      private final SkinColor toneColors;

      public SkinColor(int var1, SkinColor var2, Color[] var3) {
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

   private static class GameSkinCache implements Serializable {
      public int hash;
      public GameTextureData textureData;

      public GameSkinCache(int var1, GameTexture var2) {
         this.hash = var1;
         this.textureData = var2.getData();
      }
   }
}
