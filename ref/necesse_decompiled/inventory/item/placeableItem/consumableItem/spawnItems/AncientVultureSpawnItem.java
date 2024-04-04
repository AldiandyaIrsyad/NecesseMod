package necesse.inventory.item.placeableItem.consumableItem.spawnItems;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.desert.DesertBiome;

public class AncientVultureSpawnItem extends ConsumableItem {
   public AncientVultureSpawnItem() {
      super(1, true);
      this.itemCooldownTime.setBaseValue(2000);
      this.setItemCategory(new String[]{"consumable", "bossitems"});
      this.dropsAsMatDeathPenalty = true;
      this.keyWords.add("boss");
      this.rarity = Item.Rarity.LEGENDARY;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1 instanceof IncursionLevel) {
         return "inincursion";
      } else {
         return !(var1.biome instanceof DesertBiome) ? "notdesert" : null;
      }
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      if (var1.isServer() && var4 != null && var4.isServerClient() && var7.equals("inincursion")) {
         var4.getServerClient().sendChatMessage((GameMessage)(new LocalMessage("misc", "cannotsummoninincursion")));
      }

      return super.onAttemptPlace(var1, var2, var3, var4, var5, var6, var7);
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         if (var1 instanceof IncursionLevel) {
            GameMessage var7 = ((IncursionLevel)var1).canSummonBoss("ancientvulture");
            if (var7 != null) {
               if (var4 != null && var4.isServerClient()) {
                  var4.getServerClient().sendChatMessage(var7);
               }

               return var5;
            }
         }

         System.out.println("Ancient Vulture has been summoned at " + var1.getIdentifier() + ".");
         float var12 = (float)GameRandom.globalRandom.nextInt(360);
         float var8 = (float)Math.cos(Math.toRadians((double)var12));
         float var9 = (float)Math.sin(Math.toRadians((double)var12));
         float var10 = 960.0F;
         Mob var11 = MobRegistry.getMob("ancientvulture", var1);
         var1.entityManager.addMob(var11, (float)(var4.getX() + (int)(var8 * var10)), (float)(var4.getY() + (int)(var9 * var10)));
         var1.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", var11.getLocalization())), (Level)var1);
         if (var1 instanceof IncursionLevel) {
            ((IncursionLevel)var1).onBossSummoned(var11);
         }
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "ancientstatuetip1"));
      var4.add(Localization.translate("itemtooltip", "ancientstatuetip2"));
      return var4;
   }
}
