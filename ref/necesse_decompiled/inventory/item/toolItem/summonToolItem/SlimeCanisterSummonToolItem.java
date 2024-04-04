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

public class SlimeCanisterSummonToolItem extends SummonToolItem {
   public SlimeCanisterSummonToolItem() {
      super("playerpoisonslime", FollowPosition.WALK_CLOSE, 1.0F, 700);
      this.summonType = "playerpoisonslime";
      this.rarity = Item.Rarity.RARE;
      this.attackAnimTime.setBaseValue(400);
      this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 50.0F);
      this.knockback.setBaseValue(0);
      this.attackXOffset = 10;
      this.attackYOffset = 16;
      this.drawMaxSummons = false;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "slimecanistertip"));
      var4.add(Localization.translate("itemtooltip", "secondarysummon"));
      return var4;
   }

   public int getMaxSummons(InventoryItem var1, PlayerMob var2) {
      return 10;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt4, SoundEffect.effect(var4).volume(0.3F).pitch(GameRandom.globalRandom.getFloatBetween(1.6F, 1.8F)));
      }

   }
}
