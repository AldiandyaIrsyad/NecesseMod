package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.network.packet.PacketPlayerBuff;
import necesse.engine.network.packet.PacketPlayerBuffs;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class PotionConsumableItem extends ConsumableItem implements AdventurePartyConsumableItem {
   protected String buffType;
   protected ArrayList<String> overrideBuffs;
   protected ArrayList<String> overrideThis;
   protected int playerBuffDuration;
   protected int settlerBuffDuration;
   protected boolean obeysBuffPotionPolicy;

   public PotionConsumableItem(int var1, String var2, int var3, int var4) {
      super(var1, true);
      this.overrideBuffs = new ArrayList();
      this.overrideThis = new ArrayList();
      this.obeysBuffPotionPolicy = true;
      this.buffType = var2;
      this.playerBuffDuration = var3;
      this.settlerBuffDuration = var4;
      this.isPotion = true;
      this.setItemCategory(new String[]{"consumable", "potions"});
      this.keyWords.add("potion");
      this.incinerationTimeMillis = 10000;
   }

   public PotionConsumableItem(int var1, String var2, int var3) {
      this(var1, var2, var3, var3);
   }

   public PotionConsumableItem overridePotion(String var1) {
      Item var2 = ItemRegistry.getItem(var1);
      if (var2 instanceof PotionConsumableItem) {
         PotionConsumableItem var3 = (PotionConsumableItem)var2;
         this.overrideBuffs.add(var3.buffType);
         var3.overrideThis.add(this.buffType);
      } else {
         GameLog.warn.println("Could not find override potion with stringID " + var1);
      }

      return this;
   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotationInv(var5);
   }

   public boolean shouldSendToOtherClients(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, String var6, PacketReader var7) {
      return var6 == null;
   }

   public void onOtherPlayerPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      Screen.playSound(GameResources.drink, SoundEffect.effect(var4));
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (this.buffType != null && this.playerBuffDuration > 0) {
         ActiveBuff var7 = new ActiveBuff(this.buffType, var4, (float)this.playerBuffDuration, (Attacker)null);
         var4.addBuff(var7, false);
         if (var1.isServer()) {
            ServerClient var8 = var4.getServerClient();
            if (var8 != null) {
               var1.getServer().network.sendToClientsAt(new PacketPlayerBuff(var8.slot, var7), (ServerClient)var8);
            }
         }
      }

      Iterator var9 = this.overrideBuffs.iterator();

      while(var9.hasNext()) {
         String var11 = (String)var9.next();
         var4.buffManager.removeBuff(var11, var1.isServer());
      }

      if (var1.isServer()) {
         ServerClient var10 = var4.getServerClient();
         if (var10 != null) {
            var10.newStats.potions_consumed.add((Item)this);
         }
      } else {
         Screen.playSound(GameResources.drink, SoundEffect.effect(var4));
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      if (var1.isServer() && !var4.buffManager.hasBuff(this.buffType)) {
         var1.getServer().network.sendPacket(new PacketPlayerBuffs(var4.getServerClient()), (ServerClient)var4.getServerClient());
      }

      return var5;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var4.buffManager.hasBuff(this.buffType)) {
         return "buffactive";
      } else {
         Iterator var7 = this.overrideThis.iterator();

         String var8;
         do {
            if (!var7.hasNext()) {
               return null;
            }

            var8 = (String)var7.next();
         } while(!var4.buffManager.hasBuff(var8));

         return "overrideactive";
      }
   }

   public boolean canAndShouldPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      if (var2.buffManager.hasBuff(this.buffType)) {
         return false;
      } else {
         Iterator var6 = this.overrideThis.iterator();

         String var7;
         do {
            if (!var6.hasNext()) {
               if (this.obeysBuffPotionPolicy) {
                  AdventureParty.BuffPotionPolicy var8 = var3.adventureParty.getBuffPotionPolicy();
                  switch (var8) {
                     case ALWAYS:
                        return true;
                     case IN_COMBAT:
                        return var2.isInCombat();
                     case SAME_AS_ME:
                        return var3.playerMob.buffManager.hasBuff(this.buffType);
                     case ON_HOTKEY:
                        return var5.equals("usebuffpotion");
                     case NEVER:
                        return false;
                  }
               }

               return true;
            }

            var7 = (String)var6.next();
         } while(!var2.buffManager.hasBuff(var7));

         return false;
      }
   }

   public InventoryItem onPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5) {
      if (this.buffType != null && this.settlerBuffDuration > 0) {
         ActiveBuff var6 = new ActiveBuff(this.buffType, var2, (float)this.settlerBuffDuration, (Attacker)null);
         var2.addBuff(var6, false);
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsAt(new PacketMobBuff(var2.getUniqueID(), var6), (Level)var1);
            var2.playConsumeSound.runAndSend(true);
         }
      }

      Iterator var8 = this.overrideBuffs.iterator();

      while(var8.hasNext()) {
         String var7 = (String)var8.next();
         var2.buffManager.removeBuff(var7, var1.isServer());
      }

      InventoryItem var9 = var4.copy();
      if (this.singleUse) {
         var4.setAmount(var4.getAmount() - 1);
      }

      return var9;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "consumetip"));
      return var4;
   }

   public GameMessage getDurationMessage() {
      return getBuffDurationMessage(this.playerBuffDuration);
   }

   public ComparableSequence<Integer> getInventoryPriority(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, String var6) {
      return !this.overrideBuffs.isEmpty() && var6.equals("usebuffpotion") ? super.getInventoryPriority(var1, var2, var3, var4, var5, var6).beforeBy((Comparable)(-this.overrideBuffs.size())) : super.getInventoryPriority(var1, var2, var3, var4, var5, var6);
   }

   public ItemUsed useBuffPotion(Level var1, PlayerMob var2, InventoryItem var3) {
      String var4 = this.canPlace(var1, 0, 0, var2, var3, (PacketReader)null);
      return var4 == null ? new ItemUsed(true, this.onPlace(var1, 0, 0, var2, var3, (PacketReader)null)) : new ItemUsed(false, this.onAttemptPlace(var1, 0, 0, var2, var3, (PacketReader)null, var4));
   }
}
