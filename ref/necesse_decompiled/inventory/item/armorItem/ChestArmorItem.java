package necesse.inventory.item.armorItem;

import java.awt.Color;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChestArmorItem extends ArmorItem {
   public final String armsTextureName;
   public GameTexture rightArmsTexture;
   public GameTexture leftArmsTexture;

   public ChestArmorItem(int var1, int var2, String var3, String var4) {
      super(ArmorItem.ArmorType.CHEST, var1, var2, var3);
      this.armsTextureName = var4;
   }

   public ChestArmorItem(int var1, int var2, Item.Rarity var3, String var4, String var5) {
      this(var1, var2, var4, var5);
      this.rarity = var3;
   }

   protected void loadArmorTexture() {
      super.loadArmorTexture();
      if (this.armsTextureName != null) {
         this.leftArmsTexture = GameTexture.fromFile("player/armor/" + this.armsTextureName + "_left");
         this.rightArmsTexture = GameTexture.fromFile("player/armor/" + this.armsTextureName + "_right");
      }

   }

   public GameTexture getArmorLeftArmsTexture(InventoryItem var1, Level var2, PlayerMob var3) {
      return this.leftArmsTexture;
   }

   public GameTexture getArmorRightArmsTexture(InventoryItem var1, Level var2, PlayerMob var3) {
      return this.rightArmsTexture;
   }

   public GameSprite getAttackArmSprite(InventoryItem var1, Level var2, PlayerMob var3) {
      GameTexture var4 = this.getArmorTexture(var1, var3);
      return var4 == null ? null : new GameSprite(var4, 0, 8, 32);
   }

   public DrawOptions getArmorLeftArmsDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, boolean var12, GameLight var13, float var14, GameTexture var15) {
      GameTexture var16 = this.getArmorLeftArmsTexture(var1, var2, var3);
      Color var17 = this.getDrawColor(var1, var3);
      return (DrawOptions)(var16 != null ? var16.initDraw().sprite(var4, var5, var6).colorLight(var17, var13).alpha(var14).size(var9, var10).mirror(var11, var12).addShaderTextureFit(var15, 1).pos(var7, var8) : () -> {
      });
   }

   public DrawOptions getArmorRightArmsDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, boolean var12, GameLight var13, float var14, GameTexture var15) {
      GameTexture var16 = this.getArmorRightArmsTexture(var1, var2, var3);
      Color var17 = this.getDrawColor(var1, var3);
      return (DrawOptions)(var16 != null ? var16.initDraw().sprite(var4, var5, var6).colorLight(var17, var13).alpha(var14).size(var9, var10).mirror(var11, var12).addShaderTextureFit(var15, 1).pos(var7, var8) : () -> {
      });
   }

   public final DrawOptions getArmorLeftArmsDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, GameLight var10, float var11, GameTexture var12) {
      return this.getArmorLeftArmsDrawOptions(var1, var2 == null ? null : var2.getLevel(), var2, var3, var4, var5, var6, var7, 64, 64, var8, var9, var10, var11, var12);
   }

   public final DrawOptions getArmorRightArmsDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, GameLight var10, float var11, GameTexture var12) {
      return this.getArmorRightArmsDrawOptions(var1, var2 == null ? null : var2.getLevel(), var2, var3, var4, var5, var6, var7, 64, 64, var8, var9, var10, var11, var12);
   }
}
