package necesse.inventory.item.placeableItem.followerSummonItem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class FollowerSummonPlaceableItem extends PlaceableItem {
   protected String mobType;
   protected String summonType;
   protected String buffType;
   protected FollowPosition followPosition;
   protected int maxSummons;

   public FollowerSummonPlaceableItem(int var1, boolean var2, String var3, FollowPosition var4, String var5, String var6, int var7) {
      super(var1, var2);
      this.keyWords.add("follower");
      this.mobType = var3;
      this.followPosition = var4;
      this.summonType = var5;
      this.buffType = var6;
      this.maxSummons = var7;
      this.worldDrawSize = 32;
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }

   public int getMaxSummons(PlayerMob var1) {
      return this.maxSummons;
   }

   public int getSummonSpaceTaken() {
      return 1;
   }

   protected String canSummon(Level var1, PlayerMob var2, InventoryItem var3, PacketReader var4) {
      return this.getMaxSummons(var2) < this.getSummonSpaceTaken() ? "nosummonspace" : null;
   }

   public final void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);
      Packet var7 = this.getSummonContent();
      if (var7 != null) {
         var1.putNextContentPacket(var7);
      }

   }

   protected Packet getSummonContent() {
      return null;
   }

   protected void summonMob(Level var1, PlayerMob var2, InventoryItem var3, PacketReader var4) {
      ServerClient var5 = var2.getServerClient();
      Mob var6 = MobRegistry.getMob(this.mobType, var1);
      var5.addFollower(this.summonType, var6, this.followPosition, this.buffType, (float)this.getSummonSpaceTaken(), this::getMaxSummons, (BiConsumer)null, false);
      Point var7 = this.findSpawnLocation(var1, var6, var2.getX() / 32, var2.getY() / 32, 1);
      this.beforeSpawn(var6, var3);
      var1.entityManager.addMob(var6, (float)var7.x, (float)var7.y);
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         this.summonMob(var1, var4, var5, var6);
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      return var5;
   }

   protected void beforeSpawn(Mob var1, InventoryItem var2) {
   }

   protected Point findSpawnLocation(Level var1, Mob var2, int var3, int var4, int var5) {
      ArrayList var6 = new ArrayList();

      for(int var7 = var3 - var5; var7 <= var3 + var5; ++var7) {
         for(int var8 = var4 - var5; var8 <= var4 + var5; ++var8) {
            if (var7 != var3 || var8 != var4) {
               int var9 = var7 * 32 + 16;
               int var10 = var8 * 32 + 16;
               if (!var2.collidesWith(var1, var9, var10)) {
                  var6.add(new Point(var9, var10));
               }
            }
         }
      }

      if (var6.size() > 0) {
         return (Point)var6.get(GameRandom.globalRandom.nextInt(var6.size()));
      } else {
         return new Point(var3 * 32 + 16, var4 * 32 + 16);
      }
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return this.canSummon(var1, var4, var5, var6);
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.addAll(this.getAnimalTooltips(var1, var2));
      int var5 = this.getSummonSpaceTaken();
      if (var5 != 1) {
         var4.add(Localization.translate("itemtooltip", "summonuseslots", "count", (Object)var5));
      }

      int var6 = this.getMaxSummons(var2);
      if (var6 != 1) {
         var4.add(Localization.translate("itemtooltip", "summonslots", "count", (Object)var6));
      }

      if (this.singleUse) {
         var4.add(Localization.translate("itemtooltip", "singleuse"));
      } else {
         var4.add(Localization.translate("itemtooltip", "infiniteuse"));
      }

      return var4;
   }

   public void draw(InventoryItem var1, PlayerMob var2, int var3, int var4, boolean var5) {
      super.draw(var1, var2, var3, var4, var5);
      if (var5) {
         int var6 = this.getMaxSummons(var2);
         if (var6 > 999) {
            var6 = 999;
         }

         if (var6 != 1) {
            String var7 = String.valueOf(var6);
            int var8 = FontManager.bit.getWidthCeil(var7, tipFontOptions);
            FontManager.bit.drawString((float)(var3 + 28 - var8), (float)(var4 + 16), var7, tipFontOptions);
         }
      }

   }

   protected ListGameTooltips getAnimalTooltips(InventoryItem var1, Attacker var2) {
      return new ListGameTooltips(Localization.translate("itemtooltip", "summontip", "mob", MobRegistry.getDisplayName(MobRegistry.getMobID(this.mobType))));
   }
}
