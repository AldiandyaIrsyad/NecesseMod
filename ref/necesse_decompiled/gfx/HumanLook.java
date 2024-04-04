package necesse.gfx;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.light.GameLight;

public class HumanLook {
   private byte hair;
   private byte hairColor;
   private byte skin;
   private byte eyeColor;
   private Color shirtColor;
   private Color shoesColor;

   public HumanLook() {
      this.resetDefault();
   }

   public HumanLook(GameRandom var1) {
      this.randomizeLook(var1);
   }

   public HumanLook(int var1, int var2, int var3, int var4, Color var5, Color var6) {
      this.setHair(var1);
      this.setHairColor(var2);
      this.setSkin(var3);
      this.setEyeColor(var4);
      this.setShirtColor(var5);
      this.setShoesColor(var6);
   }

   public HumanLook(HumanLook var1) {
      this.copy(var1);
   }

   public HumanLook(PacketReader var1) {
      this.resetDefault();
      this.applyContentPacket(var1);
   }

   public void copy(HumanLook var1) {
      this.hair = var1.hair;
      this.hairColor = var1.hairColor;
      this.skin = var1.skin;
      this.eyeColor = var1.eyeColor;
      this.shirtColor = new Color(var1.shirtColor.getRGB());
      this.shoesColor = new Color(var1.shoesColor.getRGB());
   }

   public void resetDefault() {
      this.hair = 1;
      this.hairColor = 0;
      this.skin = 0;
      this.eyeColor = 0;
      this.shirtColor = new Color(110, 110, 200);
      this.shoesColor = new Color(110, 110, 200);
   }

   public void randomizeLook() {
      this.randomizeLook(GameRandom.globalRandom);
   }

   public void randomizeLook(boolean var1, boolean var2) {
      this.randomizeLook(GameRandom.globalRandom, var1, var2);
   }

   public void randomizeLook(GameRandom var1) {
      this.randomizeLook(var1, true, true);
   }

   public void randomizeLook(GameRandom var1, boolean var2, boolean var3) {
      this.setHair(GameHair.getRandomHair(var1));
      this.setHairColor(GameHair.getRandomHairColor(var1));
      if (var2) {
         this.setSkin(GameSkin.getRandomSkinColor(var1));
      }

      if (var3) {
         this.setEyeColor(GameEyes.getRandomEyeColor(var1));
      }

      this.shirtColor = new Color(var1.getIntBetween(50, 200), var1.getIntBetween(50, 200), var1.getIntBetween(50, 200));
      this.shoesColor = new Color(var1.getIntBetween(50, 200), var1.getIntBetween(50, 200), var1.getIntBetween(50, 200));
   }

   public void setupContentPacket(PacketWriter var1, boolean var2) {
      var1.putNextBoolean(var2);
      var1.putNextByte(this.hair);
      var1.putNextByte(this.hairColor);
      var1.putNextByte(this.skin);
      var1.putNextByte(this.eyeColor);
      if (var2) {
         var1.putNextByteUnsigned(this.shirtColor.getRed());
         var1.putNextByteUnsigned(this.shirtColor.getGreen());
         var1.putNextByteUnsigned(this.shirtColor.getBlue());
         var1.putNextByteUnsigned(this.shoesColor.getRed());
         var1.putNextByteUnsigned(this.shoesColor.getGreen());
         var1.putNextByteUnsigned(this.shoesColor.getBlue());
      }

   }

   public HumanLook applyContentPacket(PacketReader var1) {
      boolean var2 = var1.getNextBoolean();
      this.hair = var1.getNextByte();
      this.hairColor = var1.getNextByte();
      this.skin = var1.getNextByte();
      this.eyeColor = var1.getNextByte();
      if (var2) {
         this.shirtColor = new Color(var1.getNextByteUnsigned(), var1.getNextByteUnsigned(), var1.getNextByteUnsigned());
         this.shoesColor = new Color(var1.getNextByteUnsigned(), var1.getNextByteUnsigned(), var1.getNextByteUnsigned());
      }

      return this;
   }

   public void addSaveData(SaveData var1) {
      var1.addColor("shirtColor", this.getShirtColor());
      var1.addColor("shoesColor", this.getShoesColor());
      var1.addInt("skin", this.getSkin());
      var1.addInt("hair", this.getHair());
      var1.addInt("hairColor", this.getHairColor());
      var1.addInt("eyeColor", this.getEyeColor());
   }

   public void applyLoadData(LoadData var1) {
      this.setShirtColor(var1.getColor("shirtColor", this.getShirtColor()));
      if (var1.hasLoadDataByName("bootsColor")) {
         this.setShoesColor(var1.getColor("bootsColor", this.getShoesColor()));
      } else {
         this.setShoesColor(var1.getColor("shoesColor", this.getShoesColor()));
      }

      this.setSkin(var1.getInt("skin", this.getSkin()));
      this.setHair(var1.getInt("hair", this.getHair()));
      this.setHairColor(var1.getInt("hairColor", this.getHairColor()));
      this.setEyeColor(var1.getInt("eyeColor", this.getEyeColor()));
   }

   public DrawOptions getEyesDrawOptions(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8, float var9, GameLight var10, GameTexture var11) {
      Color var12 = GameEyes.getEyeColor(this.getEyeColor());
      return GameEyes.eyesTexture.initDraw().sprite(var3, var4, 64).colorLight(var12, var10).alpha(var9).size(var5, var6).mirror(var7, var8).addShaderTextureFit(var11, 1).pos(var1, var2);
   }

   public void setHair(int var1) {
      this.hair = (byte)var1;
   }

   public void setHairColor(int var1) {
      this.hairColor = (byte)var1;
   }

   public void setSkin(int var1) {
      this.skin = (byte)var1;
   }

   public void setEyeColor(int var1) {
      this.eyeColor = (byte)var1;
   }

   public void setShirtColor(Color var1) {
      this.shirtColor = var1;
   }

   public void setShoesColor(Color var1) {
      this.shoesColor = var1;
   }

   public int getHair() {
      return this.hair & 255;
   }

   public int getHairColor() {
      return this.hairColor & 255;
   }

   public int getSkin() {
      return this.skin & 255;
   }

   public int getEyeColor() {
      return this.eyeColor & 255;
   }

   public Color getShirtColor() {
      return this.shirtColor;
   }

   public Color getShoesColor() {
      return this.shoesColor;
   }

   public GameSkin getGameSkin(boolean var1) {
      return GameSkin.getSkin(this.getSkin(), var1);
   }

   public GameTexture getHairTexture() {
      return GameHair.getHair(this.getHair()).getHairTexture(this.getHairColor());
   }

   public GameTexture getBackHairTexture() {
      return GameHair.getHair(this.getHair()).getBackHairTexture(this.getHairColor());
   }

   public GameTexture getWigTexture() {
      return GameHair.getHair(this.getHair()).getWigTexture(this.getHairColor());
   }

   public static void loadTextures() {
      GameSkin.loadSkinTextures();
      GameEyes.loadTextures();
      GameHair.loadHairColors();
      GameHair.loadHairTextures();
   }

   public static Color limitClothesColor(Color var0) {
      return new Color(GameMath.limit(var0.getRed(), 50, 200), GameMath.limit(var0.getGreen(), 50, 200), GameMath.limit(var0.getBlue(), 50, 200));
   }
}
