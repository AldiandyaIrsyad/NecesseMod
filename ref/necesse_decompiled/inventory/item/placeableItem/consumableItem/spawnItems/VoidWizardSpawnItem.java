package necesse.inventory.item.placeableItem.consumableItem.spawnItems;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.dungeon.DungeonArenaLevel;

public class VoidWizardSpawnItem extends ConsumableItem {
   public VoidWizardSpawnItem() {
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
         return null;
      } else if (var1.getIslandDimension() == -101 && var1.biome == BiomeRegistry.DUNGEON_ISLAND) {
         return var1.entityManager.mobs.streamInRegionsShape(GameUtils.rangeTileBounds(var2, var3, 150), 0).anyMatch((var0) -> {
            return var0.getStringID().equals("voidwizard");
         }) ? "alreadyspawned" : null;
      } else {
         return "notinarena";
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         if (var1 instanceof IncursionLevel) {
            GameMessage var7 = ((IncursionLevel)var1).canSummonBoss("reaper");
            if (var7 != null) {
               if (var4 != null && var4.isServerClient()) {
                  var4.getServerClient().sendChatMessage(var7);
               }

               return var5;
            }
         }

         Mob var16 = MobRegistry.getMob("voidwizard", var1);
         ((VoidWizard)var16).makeItemSpawned();
         Point2D.Float var8;
         if (var1.getIslandDimension() == -101 && var1.biome == BiomeRegistry.DUNGEON_ISLAND) {
            var8 = DungeonArenaLevel.getBossPosition();
         } else {
            ArrayList var9 = new ArrayList();
            int var10 = var4.getX() / 32;
            int var11 = var4.getY() / 32;
            int var12 = -10;

            while(true) {
               if (var12 > 10) {
                  Point var17;
                  if (!var9.isEmpty()) {
                     var17 = (Point)GameRandom.globalRandom.getOneOf((List)var9);
                  } else {
                     var17 = new Point(var4.getTileX() + GameRandom.globalRandom.getIntBetween(-8, 8), var4.getTileY() + GameRandom.globalRandom.getIntBetween(-8, 8));
                  }

                  var8 = new Point2D.Float((float)(var17.x * 32 + 16), (float)(var17.y * 32 + 16));
                  break;
               }

               for(int var13 = -10; var13 <= 10; ++var13) {
                  int var14 = var10 + var12;
                  int var15 = var11 + var13;
                  if (!var1.isLiquidTile(var14, var15) && !var1.isShore(var14, var15) && !var16.collidesWith(var1, var14 * 32 + 16, var15 * 32 + 16)) {
                     var9.add(new Point(var14, var15));
                  }
               }

               ++var12;
            }
         }

         var1.entityManager.addMob(var16, var8.x, var8.y);
         System.out.println("Void Wizard has been summoned at " + var1.getIdentifier() + ".");
         if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }

         if (var1 instanceof IncursionLevel) {
            ((IncursionLevel)var1).onBossSummoned(var16);
         }
      }

      return var5;
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      if (var1.isServer() && var7.equals("notinarena")) {
         var1.getServer().network.sendPacket(new PacketMobChat(var4.getUniqueID(), "itemtooltip", "callernoarena"), (ServerClient)var4.getServerClient());
      }

      return var5;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "voidcallertip1"));
      var4.add(Localization.translate("itemtooltip", "voidcallertip2"));
      return var4;
   }
}
