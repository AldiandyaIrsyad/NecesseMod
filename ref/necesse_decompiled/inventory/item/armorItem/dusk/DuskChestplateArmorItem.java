package necesse.inventory.item.armorItem.dusk;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DuskChestplateArmorItem extends ChestArmorItem {
   public GameTexture brokenTexture;
   public GameTexture brokenLeftArmTexture;
   public GameTexture brokenRightArmTexture;

   public DuskChestplateArmorItem() {
      super(29, 1400, Item.Rarity.EPIC, "duskchestplate", "duskarms");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 2), new ModifierValue(BuffModifiers.MAGIC_CRIT_CHANCE, 0.3F), new ModifierValue(BuffModifiers.SUMMON_CRIT_CHANCE, 0.3F)});
   }

   protected void loadArmorTexture() {
      super.loadArmorTexture();
      this.brokenTexture = GameTexture.fromFile("player/armor/" + this.textureName + "_broken");
      this.brokenLeftArmTexture = GameTexture.fromFile("player/armor/duskarms_left_broken");
      this.brokenRightArmTexture = GameTexture.fromFile("player/armor/duskarms_right_broken");
   }

   public GameTexture getArmorLeftArmsTexture(InventoryItem var1, Level var2, PlayerMob var3) {
      return var2 != null && !var2.getWorldEntity().isNight() ? this.brokenLeftArmTexture : super.getArmorLeftArmsTexture(var1, var2, var3);
   }

   public GameTexture getArmorRightArmsTexture(InventoryItem var1, Level var2, PlayerMob var3) {
      return var2 != null && !var2.getWorldEntity().isNight() ? this.brokenRightArmTexture : super.getArmorRightArmsTexture(var1, var2, var3);
   }

   public GameSprite getAttackArmSprite(InventoryItem var1, Level var2, PlayerMob var3) {
      if (var2 != null && !var2.getWorldEntity().isNight()) {
         return this.brokenTexture == null ? null : new GameSprite(this.brokenTexture, 0, 8, 32);
      } else {
         return super.getAttackArmSprite(var1, var2, var3);
      }
   }

   public DrawOptions getArmorDrawOptions(InventoryItem var1, Level var2, PlayerMob var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, boolean var12, GameLight var13, float var14, GameTexture var15) {
      return (DrawOptions)(var2 != null && !var2.getWorldEntity().isNight() ? this.brokenTexture.initDraw().sprite(var4, var5, var6).alpha(var14).size(var9, var10).mirror(var11, var12).addShaderTextureFit(var15, 1).pos(var7, var8).light(var13) : super.getArmorDrawOptions(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15));
   }
}
