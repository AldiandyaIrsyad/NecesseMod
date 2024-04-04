package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.Screen;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public abstract class SelectedSettlersHandler {
   private static final HashMap<LevelIdentifier, HashSet<Integer>> levelsSelectedSettlers = new HashMap();
   private static HashSet<Integer> lastSelected = new HashSet();
   public final Client client;
   public final Level level;
   private HashSet<Integer> selectedSettlers = new HashSet();
   private HudDrawElement selectedSettlersHudElement;

   public SelectedSettlersHandler(Client var1) {
      this.client = var1;
      this.level = var1.getLevel();
   }

   public void init() {
      if (this.selectedSettlersHudElement != null) {
         this.selectedSettlersHudElement.remove();
      }

      this.selectedSettlersHudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            if (!SelectedSettlersHandler.this.selectedSettlers.isEmpty()) {
               final DrawOptionsList var4 = new DrawOptionsList();
               synchronized(SelectedSettlersHandler.this) {
                  Iterator var6 = SelectedSettlersHandler.this.selectedSettlers.iterator();

                  while(true) {
                     if (!var6.hasNext()) {
                        break;
                     }

                     int var7 = (Integer)var6.next();
                     Mob var8 = (Mob)this.getLevel().entityManager.mobs.get(var7, false);
                     if (var8 instanceof CommandMob && ((CommandMob)var8).canBeCommanded(SelectedSettlersHandler.this.client)) {
                        Rectangle var9 = var8.getSelectBox();
                        if (var2.getBounds().intersects(var9)) {
                           var4.add(HUD.levelBoundOptions(var2, new Color(255, 255, 255, 150), true, var9));
                        }
                     }
                  }
               }

               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return -1000000;
                  }

                  public void draw(TickManager var1) {
                     var4.draw();
                  }
               });
            }

         }
      };
      this.level.hudManager.addElement(this.selectedSettlersHudElement);
      this.selectedSettlers = (HashSet)levelsSelectedSettlers.compute(this.level.getIdentifier(), (var0, var1) -> {
         return var1 == null ? new HashSet() : var1;
      });
      this.selectedSettlers.addAll(lastSelected);
      lastSelected = this.selectedSettlers;
   }

   public void cleanUp(Predicate<Integer> var1) {
      synchronized(this) {
         if (this.selectedSettlers.removeIf((var1x) -> {
            return !var1.test(var1x);
         })) {
            this.updateSelectedSettlers(false);
         }

      }
   }

   public Collection<Integer> get() {
      return this.selectedSettlers;
   }

   public int getSize() {
      return this.selectedSettlers.size();
   }

   public boolean isEmpty() {
      return this.selectedSettlers.isEmpty();
   }

   public boolean contains(int var1) {
      return this.selectedSettlers.contains(var1);
   }

   public void clear() {
      this.selectSettlers(false);
   }

   public static boolean isShiftDown() {
      return Screen.input().isKeyDown(340) || Screen.input().isKeyDown(344);
   }

   public void selectOrDeselectSettler(int var1) {
      this.selectOrDeselectSettler(!isShiftDown(), var1);
   }

   public void selectOrDeselectSettler(boolean var1, int var2) {
      if (this.selectedSettlers.contains(var2) && this.selectedSettlers.size() > 1) {
         if (isShiftDown()) {
            this.deselectSettlers(var1, var2);
         } else {
            this.selectSettlers(var1, var2);
         }
      } else {
         this.selectSettlers(var1, var2);
      }

   }

   public void deselectSettlers(Integer... var1) {
      this.deselectSettlers(!isShiftDown(), var1);
   }

   public void deselectSettlers(boolean var1, Integer... var2) {
      this.deselectSettlers(var1, () -> {
         return GameUtils.arrayIterator(var2);
      });
   }

   public void deselectSettlers(Iterable<Integer> var1) {
      this.deselectSettlers(!isShiftDown(), var1);
   }

   public void deselectSettlers(boolean var1, Iterable<Integer> var2) {
      synchronized(this) {
         boolean var4 = false;

         int var6;
         for(Iterator var5 = var2.iterator(); var5.hasNext(); var4 = this.selectedSettlers.remove(var6) || var4) {
            var6 = (Integer)var5.next();
         }

         if (var4) {
            this.updateSelectedSettlers(var1);
         }

      }
   }

   public void selectSettlers(Integer... var1) {
      this.selectSettlers(!isShiftDown(), var1);
   }

   public void selectSettlers(boolean var1, Integer... var2) {
      this.selectSettlers(var1, () -> {
         return GameUtils.arrayIterator(var2);
      });
   }

   public void selectSettlers(Iterable<Integer> var1) {
      this.selectSettlers(!isShiftDown(), var1);
   }

   public void selectSettlers(boolean var1, Iterable<Integer> var2) {
      synchronized(this) {
         if (!isShiftDown()) {
            this.selectedSettlers.clear();
         }

         Iterator var4 = var2.iterator();

         while(true) {
            if (!var4.hasNext()) {
               break;
            }

            int var5 = (Integer)var4.next();
            this.selectedSettlers.add(var5);
         }
      }

      this.updateSelectedSettlers(var1);
   }

   public abstract void updateSelectedSettlers(boolean var1);

   public void dispose() {
      if (this.selectedSettlersHudElement != null) {
         this.selectedSettlersHudElement.remove();
      }

      this.selectedSettlersHudElement = null;
   }
}
