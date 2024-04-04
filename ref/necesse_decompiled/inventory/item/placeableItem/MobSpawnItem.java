package necesse.inventory.item.placeableItem;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class MobSpawnItem extends PlaceableItem {
   private String mobType;

   public MobSpawnItem(int var1, boolean var2, String var3) {
      super(var1, var2);
      this.dropsAsMatDeathPenalty = true;
      this.keyWords.add("spawn");
      this.mobType = var3;
   }

   public boolean getConstantUse(InventoryItem var1) {
      return false;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var1.isServer()) {
         Mob var7 = MobRegistry.getMob(this.mobType, var1);
         this.beforeSpawned(var1, var2, var3, var4, var5, var6, var7);
         Point var8 = this.findSpawnLocation(var1, var7, var4.getX() / 32, var4.getY() / 32, 1);
         var1.entityManager.addMob(var7, (float)var8.x, (float)var8.y);
      }

      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      if (var1.isClient()) {
         Screen.playSound(GameResources.pop, SoundEffect.effect(var4).pitch(0.8F));
      }

      return var5;
   }

   protected void beforeSpawned(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, Mob var7) {
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
      return null;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((Object)(new StringTooltips(Localization.translate("itemtooltip", "spawnmob", "mob", MobRegistry.getDisplayName(MobRegistry.getMobID(this.mobType))))));
      if (this.singleUse) {
         var4.add(Localization.translate("itemtooltip", "singleuse"));
      } else {
         var4.add(Localization.translate("itemtooltip", "infiniteuse"));
      }

      return var4;
   }
}
