package necesse.inventory.item.armorItem.spiderite;

import java.awt.Color;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpideriteHoodArmorItem extends SetHelmetArmorItem {
   public GameTexture lightTexture;

   public SpideriteHoodArmorItem() {
      super(23, DamageTypeRegistry.RANGED, 1400, Item.Rarity.EPIC, "spideritehood", "spideritechestplate", "spideritegreaves", "spideritesetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.RANGED_ATTACK_SPEED, 0.15F), new ModifierValue(BuffModifiers.ARMOR_PEN_FLAT, 15)});
   }

   protected void loadArmorTexture() {
      super.loadArmorTexture();
      this.lightTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_light");
   }

   public DrawOptions getArmorDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, boolean var12, GameLight var13, float var14, GameTexture var15) {
      DrawOptionsList var16 = new DrawOptionsList();
      var16.add(super.getArmorDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15));
      Color var17 = this.getDrawColor(var1, var3);
      var16.add(this.lightTexture.initDraw().sprite(var4, var5, var6).colorLight(var17, var13.minLevelCopy(150.0F)).alpha(var14).size(var9, var10).mirror(var11, var12).addShaderTextureFit(var15, 1).pos(var7, var8));
      return var16;
   }
}
