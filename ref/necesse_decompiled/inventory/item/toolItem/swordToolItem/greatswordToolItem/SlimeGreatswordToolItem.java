package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.SlimeGreatswordAttackHandler;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class SlimeGreatswordToolItem extends GreatswordToolItem {
   public SlimeGreatswordToolItem() {
      super(1800, getThreeChargeLevels(500, 600, 700));
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(140.0F).setUpgradedValue(1.0F, 150.0F);
      this.attackRange.setBaseValue(130);
      this.knockback.setBaseValue(150);
      this.attackXOffset = 12;
      this.attackYOffset = 14;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "slimegreatswordchargetip1"));
      var4.add(Localization.translate("itemtooltip", "slimegreatswordchargetip2"));
      return var4;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      var4.startAttackHandler(new SlimeGreatswordAttackHandler(this, var4, var7, var6, this, var9, var2, var3, this.chargeLevels));
      return var6;
   }

   public GameSprite getAttackSprite(InventoryItem var1, PlayerMob var2) {
      byte var3 = 100;
      int var4 = this.attackTexture.getHeight();
      int var5 = this.attackTexture.getWidth() / var4;
      int var6 = GameUtils.getAnim(var2.getLocalTime(), var5, var5 * var3);
      return new GameSprite(this.attackTexture, var6, 0, var4);
   }
}
