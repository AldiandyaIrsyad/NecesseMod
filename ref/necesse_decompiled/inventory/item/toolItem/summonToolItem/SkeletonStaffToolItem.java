package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class SkeletonStaffToolItem extends SummonToolItem {
   public SkeletonStaffToolItem() {
      super("babyskeleton", FollowPosition.PYRAMID, 0.5F, 1350);
      this.rarity = Item.Rarity.RARE;
      this.attackDamage.setBaseValue(22.0F).setUpgradedValue(1.0F, 23.0F);
   }

   public GameTooltips getSpaceTakenTooltip(InventoryItem var1, PlayerMob var2) {
      return null;
   }

   public void runSummon(Level var1, int var2, int var3, ServerClient var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      AttackingFollowingMob var11 = (AttackingFollowingMob)MobRegistry.getMob("babyskeleton", var1);
      this.summonMob(var4, var11, var2, var3, var5, var6);
      AttackingFollowingMob var12 = (AttackingFollowingMob)MobRegistry.getMob("babyskeletonmage", var1);
      this.summonMob(var4, var12, var2, var3, var5, var6);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "skeletonstafftip"));
      return var4;
   }
}
