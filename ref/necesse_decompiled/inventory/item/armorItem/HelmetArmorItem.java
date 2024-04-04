package necesse.inventory.item.armorItem;

import java.awt.Color;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.light.GameLight;

public class HelmetArmorItem extends ArmorItem {
   public DamageType damageClass;
   public String headArmorBackTextureName;
   public GameTexture headArmorBackTexture;

   public HelmetArmorItem(int var1, DamageType var2, int var3, String var4) {
      super(ArmorItem.ArmorType.HEAD, var1, var3, var4);
      this.damageClass = var2;
      if (var1 > 0) {
         byte var5;
         if (var2 == DamageTypeRegistry.MELEE) {
            var5 = 28;
         } else if (var2 == DamageTypeRegistry.RANGED) {
            var5 = 24;
         } else if (var2 == DamageTypeRegistry.MAGIC) {
            var5 = 22;
         } else if (var2 == DamageTypeRegistry.SUMMON) {
            var5 = 17;
         } else {
            var5 = 24;
         }

         this.armorValue.setUpgradedValue(1.0F, var5);
         if (var1 > var5) {
            this.armorValue.setBaseValue(var5);
         }
      }

   }

   public HelmetArmorItem(int var1, DamageType var2, int var3, Item.Rarity var4, String var5) {
      this(var1, var2, var3, var5);
      this.rarity = var4;
   }

   public HelmetArmorItem headArmorBackTexture(String var1) {
      this.headArmorBackTextureName = var1;
      return this;
   }

   protected void loadArmorTexture() {
      super.loadArmorTexture();
      if (this.headArmorBackTextureName != null) {
         this.headArmorBackTexture = GameTexture.fromFile("player/armor/" + this.headArmorBackTextureName);
      }

   }

   public GameTexture getHeadArmorBackTexture(InventoryItem var1, PlayerMob var2) {
      return this.headArmorBackTexture;
   }

   public DrawOptions getHeadArmorBackDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, boolean var10, GameLight var11, float var12, GameTexture var13) {
      GameTexture var14 = this.getHeadArmorBackTexture(var1, var2);
      Color var15 = this.getDrawColor(var1, var2);
      return (DrawOptions)(var14 != null ? var14.initDraw().sprite(var3, var4, 64).colorLight(var15, var11).alpha(var12).size(var7, var8).mirror(var9, var10).addShaderTextureFit(var13, 1).pos(var5, var6) : () -> {
      });
   }

   public final DrawOptions getHeadArmorBackDrawOptions(InventoryItem var1, PlayerMob var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8, GameLight var9, float var10, GameTexture var11) {
      return this.getHeadArmorBackDrawOptions(var1, var2, var3, var4, var5, var6, 64, 64, var7, var8, var9, var10, var11);
   }
}
