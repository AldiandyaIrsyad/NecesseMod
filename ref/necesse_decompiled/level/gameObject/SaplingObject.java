package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SaplingObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SaplingObject extends GameObject {
   public GameTexture texture;
   protected String textureName;
   public String[] validTiles;
   public String resultObjectStringID;
   public int minGrowTimeInSeconds;
   public int maxGrowTimeInSeconds;
   protected final GameRandom drawRandom;
   public boolean addAnySaplingIngredient;

   public SaplingObject(String var1, String var2, int var3, int var4, boolean var5, String... var6) {
      super(new Rectangle(0, 0));
      this.textureName = var1;
      this.resultObjectStringID = var2;
      this.minGrowTimeInSeconds = var3;
      this.maxGrowTimeInSeconds = var4;
      this.addAnySaplingIngredient = var5;
      this.validTiles = var6;
      this.setItemCategory(new String[]{"objects", "saplings"});
      this.mapColor = new Color(16, 147, 30);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
      this.replaceCategories.add("sapling");
      this.canReplaceCategories.add("sapling");
      this.canReplaceCategories.add("tree");
      this.canReplaceCategories.add("bush");
      this.replaceRotations = false;
   }

   public SaplingObject(String var1, String var2, int var3, int var4, boolean var5) {
      this(var1, var2, var3, var4, var5, "grasstile", "swampgrasstile", "dirttile", "farmland", "snowtile");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.grass, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      boolean var12;
      synchronized(this.drawRandom) {
         this.drawRandom.setSeed(this.getTileSeed(var4, var5));
         var12 = this.drawRandom.nextBoolean();
      }

      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(0, 0, 32).mirror(var12, false).light(var9).pos(var10, var11 - 8);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var13.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      boolean var10;
      synchronized(this.drawRandom) {
         this.drawRandom.setSeed(this.getTileSeed(var2, var3));
         var10 = this.drawRandom.nextBoolean();
      }

      this.texture.initDraw().sprite(0, 0, 32).mirror(var10, false).alpha(var5).draw(var8, var9 - 8);
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         boolean var6 = false;
         String[] var7 = this.validTiles;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = var7[var9];
            if (var1.getTileID(var2, var3) == TileRegistry.getTileID(var10)) {
               var6 = true;
               break;
            }
         }

         return !var6 ? "invalidtile" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      if (super.isValid(var1, var2, var3)) {
         String[] var4 = this.validTiles;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            if (var1.getTileID(var2, var3) == TileRegistry.getTileID(var7)) {
               return true;
            }
         }
      }

      return false;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new SaplingObjectEntity(var1, var2, var3, this.resultObjectStringID, this.minGrowTimeInSeconds, this.maxGrowTimeInSeconds);
   }

   public Item generateNewObjectItem() {
      Item var1 = super.generateNewObjectItem();
      if (this.addAnySaplingIngredient) {
         var1.addGlobalIngredient("anysapling");
      }

      var1.addGlobalIngredient("anycompostable");
      return var1;
   }
}
