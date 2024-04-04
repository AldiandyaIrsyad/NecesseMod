package necesse.inventory.item.placeableItem.consumableItem.spawnItems;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.network.server.ServerClient;
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

public class EvilsProtectorSpawnItem extends ConsumableItem {
   public EvilsProtectorSpawnItem() {
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
      if (var1.isClient()) {
         return null;
      } else if (var1 instanceof IncursionLevel) {
         return "inincursion";
      } else if (!var1.isIslandPosition()) {
         return "notisland";
      } else if (var1.getIslandDimension() != 0) {
         return "notsurface";
      } else if (!var1.getServer().world.worldEntity.isNight()) {
         return "notnight";
      } else {
         ArrayList var7 = new ArrayList();
         Mob var8 = MobRegistry.getMob("evilsprotector", var1);
         int var9 = var4.getX() / 32;
         int var10 = var4.getY() / 32;

         for(int var11 = -10; var11 <= 10; ++var11) {
            for(int var12 = -10; var12 <= 10; ++var12) {
               int var13 = var9 + var11;
               int var14 = var10 + var12;
               if (!var1.isLiquidTile(var13, var14) && !var1.isShore(var13, var14) && !var8.collidesWith(var1, var13 * 32 + 16, var14 * 32 + 16)) {
                  var7.add(new Point(var13, var14));
               }
            }
         }

         if (var7.size() == 0) {
            return "nospace";
         } else {
            return null;
         }
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         if (var1 instanceof IncursionLevel) {
            GameMessage var7 = ((IncursionLevel)var1).canSummonBoss("evilsprotector");
            if (var7 != null) {
               if (var4 != null && var4.isServerClient()) {
                  var4.getServerClient().sendChatMessage(var7);
               }

               return var5;
            }
         }

         ArrayList var15 = new ArrayList();
         Mob var8 = MobRegistry.getMob("evilsprotector", var1);
         int var9 = var4.getX() / 32;
         int var10 = var4.getY() / 32;

         for(int var11 = -10; var11 <= 10; ++var11) {
            for(int var12 = -10; var12 <= 10; ++var12) {
               int var13 = var9 + var11;
               int var14 = var10 + var12;
               if (!var1.isLiquidTile(var13, var14) && !var1.isShore(var13, var14) && !var8.collidesWith(var1, var13 * 32 + 16, var14 * 32 + 16)) {
                  var15.add(new Point(var13, var14));
               }
            }
         }

         System.out.println("Evil's Protector has been summoned at " + var1.getIdentifier() + ".");
         Point var16;
         if (!var15.isEmpty()) {
            var16 = (Point)GameRandom.globalRandom.getOneOf((List)var15);
         } else {
            var16 = new Point(var4.getTileX() + GameRandom.globalRandom.getIntBetween(-8, 8), var4.getTileY() + GameRandom.globalRandom.getIntBetween(-8, 8));
         }

         var1.entityManager.addMob(var8, (float)(var16.x * 32 + 16), (float)(var16.y * 32 + 16));
         var1.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", var8.getLocalization())), (Level)var1);
         if (var1 instanceof IncursionLevel) {
            ((IncursionLevel)var1).onBossSummoned(var8);
         }
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      if (var1.isServer() && var4 != null && var4.isServerClient() && var7.equals("inincursion")) {
         var4.getServerClient().sendChatMessage((GameMessage)(new LocalMessage("misc", "cannotsummoninincursion")));
         return var5;
      } else {
         if (var1.isServer()) {
            String var8;
            if (var7.equals("alreadyspawned")) {
               var8 = null;
            } else if (var7.equals("notsurface")) {
               var8 = "portalnotonsurface";
            } else if (var7.equals("notnight")) {
               var8 = "portalnotnight";
            } else if (var7.equals("nospace")) {
               var8 = "portalnospace";
            } else {
               var8 = "portalerror";
            }

            if (var8 != null) {
               var1.getServer().network.sendPacket(new PacketMobChat(var4.getUniqueID(), "itemtooltip", var8), (ServerClient)var4.getServerClient());
            }
         }

         return var5;
      }
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "evilsevent"));
      return var4;
   }
}
