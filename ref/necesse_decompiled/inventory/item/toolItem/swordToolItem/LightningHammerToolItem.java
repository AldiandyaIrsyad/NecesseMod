package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.LightningTrailEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class LightningHammerToolItem extends SwordToolItem {
   public LightningHammerToolItem() {
      super(600);
      this.rarity = Item.Rarity.UNCOMMON;
      this.attackAnimTime.setBaseValue(200);
      this.attackDamage.setBaseValue(17.0F).setUpgradedValue(1.0F, 65.0F);
      this.attackRange.setBaseValue(50);
      this.knockback.setBaseValue(75);
      this.resilienceGain.setBaseValue(1.0F);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "lightninghammertip1"));
      var4.add(Localization.translate("itemtooltip", "lightninghammertip2"));
      return var4;
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      var6 = super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      LightningTrailEvent var11 = new LightningTrailEvent(var4, this.getAttackDamage(var6), this.getResilienceGain(var6), var4.getX(), var4.getY(), var2, var3 + var5, var9);
      var1.entityManager.addLevelEventHidden(var11);
      if (var1.isServer()) {
         var1.getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(var11), var11, var4.getServerClient());
      }

      return var6;
   }

   public InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6) {
      var6 = super.onSettlerAttack(var1, var2, var3, var4, var5, var6);
      LightningTrailEvent var7 = new LightningTrailEvent(var2, this.getAttackDamage(var6), this.getResilienceGain(var6), var2.getX(), var2.getY(), var3.getX(), var3.getY() + var4, var5);
      var1.entityManager.addLevelEventHidden(var7);
      var1.getServer().network.sendToClientsWithEntity(new PacketLevelEvent(var7), var7);
      return var6;
   }
}
