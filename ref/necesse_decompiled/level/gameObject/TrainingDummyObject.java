package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrainingDummyObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.shader.WaveShader;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.SemiRegion;

public class TrainingDummyObject extends GameObject {
   public static final int weaveTime = 200;
   public GameTexture base;
   public GameTexture body;
   protected GameRandom drawRandom;

   public TrainingDummyObject() {
      super(new Rectangle(6, 18, 20, 10));
      this.mapColor = new Color(150, 119, 70);
      this.displayMapTooltip = true;
      this.objectHealth = 50;
      this.toolType = ToolType.ALL;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
      this.regionType = SemiRegion.RegionType.SUMMON_IGNORED;
   }

   public List<Rectangle> getProjectileCollisions(Level var1, int var2, int var3, int var4) {
      return Collections.emptyList();
   }

   public void loadTextures() {
      super.loadTextures();
      this.base = GameTexture.fromFile("objects/trainingdummy_base");
      this.body = GameTexture.fromFile("objects/trainingdummy_body");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4) - 16;
      int var11 = var7.getTileDrawY(var5) - 32;
      int var12 = var3.getObjectRotation(var4, var5) % 4;
      final WaveShader.WaveState var13 = GameResources.waveShader.setupGrassWave(var3, var4, var5, 200L, 0.025F, 1.0F, this.drawRandom, this.getTileSeed(var4, var5));
      final TextureDrawOptionsEnd var14 = this.body.initDraw().sprite(var12, 0, 64).light(var9).pos(var10, var11 - 8);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 19;
         }

         public void draw(TickManager var1) {
            if (var13 != null) {
               var13.start();
            }

            var14.draw();
            if (var13 != null) {
               var13.end();
            }

         }
      });
      TextureDrawOptionsEnd var15 = this.base.initDraw().sprite(var12, 0, 64).light(var9).pos(var10, var11 + 2);
      var2.add((var1x) -> {
         var15.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2) - 16;
      int var9 = var7.getTileDrawY(var3) - 32;
      this.base.initDraw().sprite(var4, 0, 64).alpha(var5).draw(var8, var9 + 2);
      this.body.initDraw().sprite(var4, 0, 64).alpha(var5).draw(var8, var9 - 8);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new TrainingDummyObjectEntity(var1, var2, var3);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "trainingdummytip"));
      return var3;
   }
}
