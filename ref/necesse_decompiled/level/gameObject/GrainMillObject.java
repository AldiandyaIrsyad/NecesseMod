package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.GrainMillObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class GrainMillObject extends GrainMillExtraObject implements SettlementWorkstationObject {
   public GameTexture texture;
   public GameTexture bladeTexture;
   protected int counterIDTopRight;
   protected int counterIDBotLeft;
   protected int counterIDBotRight;

   public GrainMillObject() {
   }

   protected Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 5, var3 * 32 + 12, 27, 20);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32, var3 * 32 + 12, 27, 20);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32, var3 * 32, 27, 22) : new Rectangle(var2 * 32 + 5, var3 * 32, 27, 22);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }

   protected void setCounterIDs(int var1, int var2, int var3, int var4) {
      this.counterIDTopRight = var2;
      this.counterIDBotLeft = var3;
      this.counterIDBotRight = var4;
   }

   public MultiTile getMultiTile(int var1) {
      return new StaticMultiTile(0, 0, 2, 2, var1, true, new int[]{this.getID(), this.counterIDTopRight, this.counterIDBotLeft, this.counterIDBotRight});
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/grainmill");
      this.bladeTexture = GameTexture.fromFile("objects/grainmillblade");
   }

   public GrainMillObjectEntity getGrainMillObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof GrainMillObjectEntity ? (GrainMillObjectEntity)var4 : null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      float var10 = 0.0F;
      GrainMillObjectEntity var11 = this.getGrainMillObjectEntity(var3, var4, var5);
      if (var11 != null) {
         var10 = var11.bladeRotation;
      }

      byte var12 = var3.getObjectRotation(var4, var5);
      if (var12 == 1) {
         --var4;
      } else if (var12 == 2) {
         --var4;
         --var5;
      } else if (var12 == 3) {
         --var5;
      }

      int var13 = var7.getTileDrawX(var4);
      int var14 = var7.getTileDrawY(var5);
      final TextureDrawOptionsEnd var15 = this.texture.initDraw().light(var9).pos(var13, var14 - (this.texture.getHeight() - 64));
      final TextureDrawOptionsEnd var16 = this.bladeTexture.initDraw().light(var9).rotate(var10, this.bladeTexture.getWidth() / 2, this.bladeTexture.getHeight() / 2).posMiddle(var13 + this.texture.getWidth() / 2, var14 - 16);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 40;
         }

         public void draw(TickManager var1) {
            var15.draw();
            var16.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      if (var4 == 1) {
         --var2;
      } else if (var4 == 2) {
         --var2;
         --var3;
      } else if (var4 == 3) {
         --var3;
      }

      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().alpha(var5).draw(var8, var9 - (this.texture.getHeight() - 64));
      this.bladeTexture.initDraw().alpha(var5).posMiddle(var8 + this.texture.getWidth() / 2, var9 - 16).draw();
   }

   public ProcessingTechInventoryObjectEntity getProcessingObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof ProcessingTechInventoryObjectEntity ? (ProcessingTechInventoryObjectEntity)var4 : null;
   }

   public Stream<Recipe> streamSettlementRecipes(Level var1, int var2, int var3) {
      ProcessingTechInventoryObjectEntity var4 = this.getProcessingObjectEntity(var1, var2, var3);
      return var4 != null ? Recipes.streamRecipes(var4.techs) : Stream.empty();
   }

   public boolean isProcessingInventory(Level var1, int var2, int var3) {
      return true;
   }

   public boolean canCurrentlyCraft(Level var1, int var2, int var3, Recipe var4) {
      ProcessingTechInventoryObjectEntity var5 = this.getProcessingObjectEntity(var1, var2, var3);
      if (var5 != null) {
         return var5.getExpectedResults().crafts < 10;
      } else {
         return false;
      }
   }

   public int getMaxCraftsAtOnce(Level var1, int var2, int var3, Recipe var4) {
      return 5;
   }

   public InventoryRange getProcessingInputRange(Level var1, int var2, int var3) {
      ProcessingTechInventoryObjectEntity var4 = this.getProcessingObjectEntity(var1, var2, var3);
      return var4 != null ? var4.getInputInventoryRange() : null;
   }

   public InventoryRange getProcessingOutputRange(Level var1, int var2, int var3) {
      ProcessingTechInventoryObjectEntity var4 = this.getProcessingObjectEntity(var1, var2, var3);
      return var4 != null ? var4.getOutputInventoryRange() : null;
   }

   public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level var1, int var2, int var3) {
      ProcessingTechInventoryObjectEntity var4 = this.getProcessingObjectEntity(var1, var2, var3);
      return var4 != null ? var4.getCurrentAndExpectedResults().items : new ArrayList();
   }

   public static int[] registerGrainMill() {
      GrainMillObject var0;
      int var4 = ObjectRegistry.registerObject("grainmill", var0 = new GrainMillObject(), 10.0F, true);
      GrainMillObject2 var1;
      int var5 = ObjectRegistry.registerObject("grainmill2", var1 = new GrainMillObject2(), 0.0F, false);
      GrainMillObject3 var2;
      int var6 = ObjectRegistry.registerObject("grainmill3", var2 = new GrainMillObject3(), 0.0F, false);
      GrainMillObject4 var3;
      int var7 = ObjectRegistry.registerObject("grainmill4", var3 = new GrainMillObject4(), 0.0F, false);
      var0.setCounterIDs(var4, var5, var6, var7);
      var1.setCounterIDs(var4, var5, var6, var7);
      var2.setCounterIDs(var4, var5, var6, var7);
      var3.setCounterIDs(var4, var5, var6, var7);
      return new int[]{var4, var5, var6, var7};
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      return super.getItemTooltips(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return super.getNewObjectEntity(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      super.interact(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return super.canInteract(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return super.getInteractTip(var1, var2, var3, var4, var5);
   }
}
