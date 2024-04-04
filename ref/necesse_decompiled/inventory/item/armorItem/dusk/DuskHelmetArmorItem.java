package necesse.inventory.item.armorItem.dusk;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DuskHelmetArmorItem extends SetHelmetArmorItem {
   public GameTexture brokenTexture;

   public DuskHelmetArmorItem() {
      super(23, (DamageType)null, 1400, Item.Rarity.EPIC, "duskhelmet", "duskchestplate", "duskboots", "dusksetbonus");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAGIC_ATTACK_SPEED, 0.15F), new ModifierValue(BuffModifiers.SUMMON_ATTACK_SPEED, 0.15F)});
   }

   protected void loadArmorTexture() {
      super.loadArmorTexture();
      this.brokenTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_broken");
   }

   public DrawOptions getArmorDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, boolean var12, GameLight var13, float var14, GameTexture var15) {
      return (DrawOptions)(var2 != null && !var2.getWorldEntity().isNight() ? this.brokenTexture.initDraw().sprite(var4, var5, var6).alpha(var14).size(var9, var10).mirror(var11, var12).addShaderTextureFit(var15, 1).pos(var7, var8).light(var13) : super.getArmorDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15));
   }
}
