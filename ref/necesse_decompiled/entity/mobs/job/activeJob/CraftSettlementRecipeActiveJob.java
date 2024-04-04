package necesse.entity.mobs.job.activeJob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Supplier;
import necesse.engine.util.GameObjectReservable;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.SettlementRecipeCraftedEvent;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class CraftSettlementRecipeActiveJob extends TileActiveJob {
   public SettlementWorkstation workstation;
   public SettlementWorkstationRecipe recipe;
   public float workSeconds;
   public GameObjectReservable reservable;
   public Supplier<Boolean> isRemovedCheck;
   public LinkedListJobSequence dropOffSequence;
   public float performedWork;

   public CraftSettlementRecipeActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, int var3, int var4, SettlementWorkstation var5, SettlementWorkstationRecipe var6, float var7, GameObjectReservable var8, Supplier<Boolean> var9, LinkedListJobSequence var10) {
      super(var1, var2, var3, var4);
      this.workstation = var5;
      this.recipe = var6;
      this.workSeconds = var7;
      this.reservable = var8;
      this.isRemovedCheck = var9;
      this.dropOffSequence = var10;
   }

   public JobMoveToTile getMoveToTile(JobMoveToTile var1) {
      return new JobMoveToTile(this.tileX, this.tileY, true);
   }

   public void tick(boolean var1, boolean var2) {
      this.reservable.reserve(this.worker.getMobWorker());
   }

   public boolean isValid(boolean var1) {
      if ((this.isRemovedCheck == null || !(Boolean)this.isRemovedCheck.get()) && this.reservable.isAvailable(this.worker.getMobWorker())) {
         SettlementWorkstationLevelObject var2 = this.workstation.getWorkstationObject();
         if (var2 != null && var2.canCurrentlyCraft(this.recipe.recipe)) {
            if (!var1) {
               return true;
            } else {
               Ingredient[] var3 = this.recipe.recipe.ingredients;
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Ingredient var6 = var3[var5];
                  int var7 = 0;
                  Iterator var8 = this.worker.getWorkInventory().items().iterator();

                  while(var8.hasNext()) {
                     InventoryItem var9 = (InventoryItem)var8.next();
                     if (this.recipe.canUseItem(var6, var9)) {
                        var7 += var9.getAmount();
                     }

                     if (var7 >= var6.getIngredientAmount()) {
                        break;
                     }
                  }

                  if (var7 < var6.getIngredientAmount()) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public ActiveJobResult perform() {
      SettlementWorkstationLevelObject var1 = this.workstation.getWorkstationObject();
      if (var1 != null) {
         var1.tickCrafting(this.recipe.recipe);
      }

      float var2 = 0.05F;
      this.performedWork += var2;
      ArrayList var3;
      Iterator var16;
      if (this.performedWork < this.workSeconds) {
         var3 = new ArrayList(Arrays.asList(this.recipe.recipe.ingredients));

         while(!var3.isEmpty()) {
            int var13 = GameRandom.globalRandom.nextInt(var3.size());
            Ingredient var15 = (Ingredient)var3.remove(var13);
            var16 = this.worker.getWorkInventory().items().iterator();

            while(var16.hasNext()) {
               InventoryItem var18 = (InventoryItem)var16.next();
               if (this.recipe.canUseItem(var15, var18)) {
                  this.worker.showWorkAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, var18.item, 1000);
                  return ActiveJobResult.PERFORMING;
               }
            }
         }

         this.worker.showWorkAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, this.recipe.recipe.resultItem.item, 1000);
         return ActiveJobResult.PERFORMING;
      } else if (this.worker.isInWorkAnimation()) {
         return ActiveJobResult.PERFORMING;
      } else {
         var3 = new ArrayList();
         Ingredient[] var4 = this.recipe.recipe.ingredients;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Ingredient var7 = var4[var6];
            ListIterator var8 = this.worker.getWorkInventory().listIterator();
            int var9 = var7.getIngredientAmount();

            while(var8.hasNext()) {
               final InventoryItem var10 = (InventoryItem)var8.next();
               if (this.recipe.canUseItem(var7, var10)) {
                  final int var11 = Math.min(var9, var10.getAmount());
                  var9 -= var11;
                  var10.setAmount(var10.getAmount() - var11);
                  var3.add(new InventoryItemsRemoved((Inventory)null, -1, var10, var11) {
                     public void revert() {
                        CraftSettlementRecipeActiveJob.this.worker.getWorkInventory().add(var10.copy(var11));
                     }
                  });
                  if (var10.getAmount() <= 0) {
                     var8.remove();
                  }
               }

               if (var9 <= 0) {
                  break;
               }
            }
         }

         SettlementRecipeCraftedEvent var12 = new SettlementRecipeCraftedEvent(this.recipe.recipe, var3, this);
         this.recipe.recipe.submitCraftedEvent(var12);
         this.worker.getWorkInventory().add(var12.resultItem);
         this.worker.showPickupAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, var12.resultItem.item, 250);
         this.recipe.onCrafted(this.workstation, 1);
         if (this.dropOffSequence != null) {
            ArrayList var14 = HasStorageLevelJob.findDropOffLocation(this.worker, var12.resultItem);
            var16 = var14.iterator();

            while(var16.hasNext()) {
               HasStorageLevelJob.DropOffFind var17 = (HasStorageLevelJob.DropOffFind)var16.next();
               this.dropOffSequence.addLast(var17.getActiveJob(this.worker, this.priority, false));
            }
         }

         return ActiveJobResult.FINISHED;
      }
   }
}
