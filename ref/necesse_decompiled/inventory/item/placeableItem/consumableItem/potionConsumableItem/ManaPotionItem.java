package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem;

import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobManaChangeEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemUsed;
import necesse.level.maps.Level;

public class ManaPotionItem extends PotionConsumableItem {
   public int restoreMana;

   public ManaPotionItem(Item.Rarity var1, int var2) {
      super(100, "manapotionfatigue", 30);
      this.rarity = var1;
      this.restoreMana = var2;
      this.obeysBuffPotionPolicy = false;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "manaflatpot", "mana", (Object)this.restoreMana));
      var4.add(Localization.translate("itemtooltip", "manapottip", "key", TypeParsers.getInputParseString(Control.MANA_POT)));
      return var4;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      InventoryItem var7 = super.onPlace(var1, var2, var3, var4, var5, var6);
      float var8 = (float)this.restoreMana;
      var4.isManaExhausted = false;
      if (var4.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION)) {
         var4.buffManager.removeBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, false);
      }

      if (var1.isServer()) {
         MobManaChangeEvent var9 = new MobManaChangeEvent(var4, var8);
         var1.entityManager.addLevelEvent(var9);
      }

      return var7;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return var4.getMana() >= (float)var4.getMaxMana() ? "fullmana" : super.canPlace(var1, var2, var3, var4, var5, var6);
   }

   public boolean canAndShouldPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      if (!super.canAndShouldPartyConsume(var1, var2, var3, var4, var5)) {
         return false;
      } else {
         float var6 = var2.getMana() / (float)var2.getMaxMana();
         return var2.getMana() <= (float)(var2.getMaxMana() - this.restoreMana) && var6 <= 0.75F || var6 <= 0.1F;
      }
   }

   public InventoryItem onPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      InventoryItem var6 = super.onPartyConsume(var1, var2, var3, var4, var5);
      if (var1.isServer()) {
         MobManaChangeEvent var7 = new MobManaChangeEvent(var2, (float)this.restoreMana);
         var1.entityManager.addLevelEvent(var7);
      }

      return var6;
   }

   public ComparableSequence<Integer> getPartyPriority(Level var1, HumanMob var2, ServerClient var3, Inventory var4, int var5, InventoryItem var6, String var7) {
      return super.getPartyPriority(var1, var2, var3, var4, var5, var6, var7).beforeBy((Comparable)(-this.restoreMana));
   }

   public ComparableSequence<Integer> getInventoryPriority(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, String var6) {
      return var6.equals("usemanapotion") ? super.getInventoryPriority(var1, var2, var3, var4, var5, var6).beforeBy((Comparable)(-this.restoreMana)) : super.getInventoryPriority(var1, var2, var3, var4, var5, var6);
   }

   public ItemUsed useManaPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      String var4 = this.canPlace(var1, 0, 0, var2, var3, (PacketReader)null);
      return var4 == null ? new ItemUsed(true, this.onPlace(var1, 0, 0, var2, var3, (PacketReader)null)) : new ItemUsed(false, this.onAttemptPlace(var1, 0, 0, var2, var3, (PacketReader)null, var4));
   }

   public ItemUsed useBuffPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      return new ItemUsed(false, var3);
   }
}
