package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class DeepIceTile extends TerrainSplatterTile {
   private final GameRandom drawRandom;

   public DeepIceTile() {
      super(false, "deepice");
      this.mapColor = new Color(68, 104, 154);
      this.canBeMined = true;
      this.drawRandom = new GameRandom();
   }

   public Point getTerrainSprite(GameTextureSection var1, Level var2, int var3, int var4) {
      int var5;
      synchronized(this.drawRandom) {
         var5 = this.drawRandom.seeded(this.getTileSeed(var3, var4)).nextInt(var1.getHeight() / 32);
      }

      return new Point(0, var5);
   }

   public int getTerrainPriority() {
      return 200;
   }

   public ModifierValue<Float> getSpeedModifier(Mob var1) {
      return var1.isFlying() ? super.getSpeedModifier(var1) : new ModifierValue(BuffModifiers.SPEED, 0.25F);
   }

   public ModifierValue<Float> getFrictionModifier(Mob var1) {
      return var1.isFlying() ? super.getFrictionModifier(var1) : new ModifierValue(BuffModifiers.FRICTION, -0.85F);
   }

   public String canPlace(Level var1, int var2, int var3) {
      if (var1.getTileID(var2, var3) != TileRegistry.waterID) {
         return "notwater";
      } else {
         boolean var4 = !var1.getTile(var2 - 1, var3).isLiquid;
         var4 = var4 || !var1.getTile(var2 + 1, var3).isLiquid;
         var4 = var4 || !var1.getTile(var2, var3 - 1).isLiquid;
         var4 = var4 || !var1.getTile(var2, var3 + 1).isLiquid;
         return !var4 ? "notshore" : null;
      }
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add(Localization.translate("itemtooltip", "icetip"));
      return var3;
   }

   public int getDestroyedTile() {
      return TileRegistry.waterID;
   }

   public boolean canBePlacedOn(Level var1, int var2, int var3, GameTile var4) {
      return false;
   }
}
