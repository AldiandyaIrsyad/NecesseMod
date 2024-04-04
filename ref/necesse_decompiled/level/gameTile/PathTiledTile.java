package necesse.level.gameTile;

import java.awt.Color;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.LevelTileDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class PathTiledTile extends EdgedTiledTexture {
   protected GameTextureSection texture;

   public PathTiledTile(String var1, Color var2) {
      super(true, var1);
      this.mapColor = var2;
      this.canBeMined = true;
      this.tilesHeight = 2;
   }

   protected void loadTextures() {
      super.loadTextures();
      this.texture = tileTextures.addTexture(GameTexture.fromFile("tiles/" + this.textureName));
   }

   public void addBridgeDrawables(LevelTileDrawOptions var1, List<LevelSortedDrawable> var2, Level var3, int var4, int var5, GameCamera var6, TickManager var7) {
      int var8 = var6.getTileDrawX(var4);
      int var9 = var6.getTileDrawY(var5);
      if (var4 != 0 && var3.isLiquidTile(var4 - 1, var5) && var3.getTileID(var4 - 1, var5 - 1) == this.getID()) {
         var1.add(this.texture.sprite(6, 2, 16, 32)).pos(var8, var9);
      } else {
         var1.add(this.texture.sprite(4, 2, 16, 32)).pos(var8, var9);
      }

      if (var4 != var3.width - 1 && var3.isLiquidTile(var4 + 1, var5) && var3.getTileID(var4 + 1, var5 - 1) == this.getID()) {
         var1.add(this.texture.sprite(5, 2, 16, 32)).pos(var8 + 16, var9);
      } else {
         var1.add(this.texture.sprite(7, 2, 16, 32)).pos(var8 + 16, var9);
      }

   }

   protected boolean isMergeTile(Level var1, int var2, int var3) {
      if (super.isMergeTile(var1, var2, var3)) {
         return true;
      } else {
         GameObject var4 = var1.getObject(var2, var3);
         return var4.isWall && var4.isDoor;
      }
   }

   public ModifierValue<Float> getSpeedModifier(Mob var1) {
      return var1.isFlying() ? super.getSpeedModifier(var1) : new ModifierValue(BuffModifiers.SPEED, 0.1F);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "stonepathtip"));
      return var3;
   }
}
