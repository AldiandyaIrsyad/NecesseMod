package necesse.inventory.item.toolItem.summonToolItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class EmpressCommandToolItem extends SummonToolItem {
   public EmpressCommandToolItem() {
      super("babyspiderkinarcher", FollowPosition.newPyramid(64, 64), 1.0F, 1900);
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 22.0F);
   }

   public GameTooltips getSpaceTakenTooltip(InventoryItem var1, PlayerMob var2) {
      return null;
   }

   public void runSummon(Level var1, int var2, int var3, ServerClient var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      List var11 = (List)((Stream)var4.streamFollowers().sequential()).filter((var1x) -> {
         return var1x.summonType.equals(this.summonType);
      }).map((var0) -> {
         return var0.mob;
      }).filter((var0) -> {
         return var0.getStringID().equals("babyspiderkinwarrior") || var0.getStringID().equals("babyspiderkinarcher");
      }).collect(Collectors.toList());
      String var12;
      if (var11.isEmpty()) {
         var12 = "babyspiderkinwarrior";
      } else {
         Mob var13 = (Mob)var11.get(var11.size() - 1);
         if (var13.getStringID().equals("babyspiderkinwarrior")) {
            var12 = "babyspiderkinarcher";
         } else {
            var12 = "babyspiderkinwarrior";
         }
      }

      AttackingFollowingMob var14 = (AttackingFollowingMob)MobRegistry.getMob(var12, var1);
      this.summonMob(var4, var14, var2, var3, var5, var6);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "empresscommandtip"));
      return var4;
   }
}
