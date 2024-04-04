package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.ArachnidWebBowAttackHandler;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class ArachnidWebBowToolItem extends BowProjectileToolItem {
   public ArachnidWebBowToolItem() {
      super(2000);
      this.attackAnimTime.setBaseValue(450);
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(45.0F).setUpgradedValue(1.0F, 50.0F);
      this.velocity.setBaseValue(300);
      this.attackRange.setBaseValue(800);
      this.attackXOffset = 12;
      this.attackYOffset = 31;
      this.resilienceGain.setBaseValue(0.5F);
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
   }

   protected void addExtraBowTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      var1.add(Localization.translate("itemtooltip", "arachnidwebbowtip"));
   }

   public GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return new LocalMessage("ui", "settlercantuseitem");
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      var4.startAttackHandler(new ArachnidWebBowAttackHandler(var4, var7, var6, this, var9, var2, var3));
      return var6;
   }

   public InventoryItem superOnAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      return super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }
}
