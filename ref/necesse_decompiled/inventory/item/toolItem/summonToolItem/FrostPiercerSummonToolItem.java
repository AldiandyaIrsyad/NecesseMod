package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class FrostPiercerSummonToolItem extends SummonToolItem {
   public FrostPiercerSummonToolItem() {
      super("frostpiercer", FollowPosition.FLYING_CIRCLE_FAST, 1.0F, 700);
      this.summonType = "summonedmobtemp";
      this.attackAnimTime.setBaseValue(400);
      this.rarity = Item.Rarity.RARE;
      this.attackDamage.setBaseValue(16.0F).setUpgradedValue(1.0F, 50.0F);
      this.knockback.setBaseValue(0);
      this.attackXOffset = 5;
      this.attackYOffset = 24;
      this.drawMaxSummons = false;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "secondarysummon"));
      return var4;
   }

   public int getMaxSummons(InventoryItem var1, PlayerMob var2) {
      return 8;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.jingle, SoundEffect.effect(var4).volume(0.3F).pitch(GameRandom.globalRandom.getFloatBetween(1.1F, 1.2F)));
      }

   }
}
