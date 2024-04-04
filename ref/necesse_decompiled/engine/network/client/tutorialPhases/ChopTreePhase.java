package necesse.engine.network.client.tutorialPhases;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.AreaFinder;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class ChopTreePhase extends TutorialPhase {
   public static String[] treeItems = new String[]{"oaklog", "sprucelog", "pinelog", "palmlog", "willowlog", "oaksapling", "sprucesapling", "pinesapling", "palmsapling", "willowsapling", "cactussapling"};
   private Point chopTreeCoord;
   private long chopTreeFindCooldown;
   private LocalMessage hitTree;
   private LocalMessage findTree;
   private HudDrawElement drawElement;

   public ChopTreePhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void start() {
      super.start();
      this.hitTree = new LocalMessage("tutorials", "hittree");
      this.findTree = new LocalMessage("tutorials", "findtree");
      this.chopTreeFindCooldown = 0L;
      this.chopTreeCoord = null;
      this.drawElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, final PlayerMob var3) {
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return Integer.MAX_VALUE;
               }

               public void draw(TickManager var1) {
                  DrawOptionsList var2x = new DrawOptionsList();
                  Point var3x = ChopTreePhase.this.chopTreeCoord;
                  FairTypeDrawOptions var4;
                  if (var3x == null) {
                     var4 = ChopTreePhase.this.getTextDrawOptions(ChopTreePhase.this.getTutorialText(ChopTreePhase.this.findTree.translate()));
                     var2x.add(ChopTreePhase.this.getLevelTextDrawOptions(var4, var3.getX(), var3.getY() - 50, var2, var3, false));
                  } else {
                     var4 = ChopTreePhase.this.getTextDrawOptions(ChopTreePhase.this.getTutorialText(ChopTreePhase.this.hitTree.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE1)));
                     var2x.add(ChopTreePhase.this.getLevelTextDrawOptions(var4, var3x.x * 32 + 16, var3x.y * 32 + 8, var2, var3, true));
                     var2x.add(Settings.UI.select_outline.initDraw().sprite(0, 0, 16).color(TutorialPhase.TUTORIAL_TEXT_COLOR).pos(var2.getTileDrawX(var3x.x), var2.getTileDrawY(var3x.y)));
                     var2x.add(Settings.UI.select_outline.initDraw().sprite(1, 0, 16).color(TutorialPhase.TUTORIAL_TEXT_COLOR).pos(var2.getTileDrawX(var3x.x) + 16, var2.getTileDrawY(var3x.y)));
                     var2x.add(Settings.UI.select_outline.initDraw().sprite(0, 1, 16).color(TutorialPhase.TUTORIAL_TEXT_COLOR).pos(var2.getTileDrawX(var3x.x), var2.getTileDrawY(var3x.y) + 16));
                     var2x.add(Settings.UI.select_outline.initDraw().sprite(1, 1, 16).color(TutorialPhase.TUTORIAL_TEXT_COLOR).pos(var2.getTileDrawX(var3x.x) + 16, var2.getTileDrawY(var3x.y) + 16));
                  }

                  var2x.draw();
               }
            });
         }
      };
      this.client.getLevel().hudManager.addElement(this.drawElement);
   }

   public void end() {
      super.end();
      if (this.drawElement != null) {
         this.drawElement.remove();
      }

      this.drawElement = null;
   }

   public void updateObjective(MainGame var1) {
      this.setObjective(var1, "treestorch");
   }

   public void tick() {
      this.findNewTree();
      if (this.chopTreeCoord != null && !this.checkForTree(this.chopTreeCoord.x, this.chopTreeCoord.y)) {
         this.over();
      }

      String[] var1 = treeItems;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         if (this.client.getPlayer().getInv().getAmount(ItemRegistry.getItem(var4), false, false, false, "tutorial") > 0) {
            this.over();
         }
      }

   }

   private void findNewTree() {
      if (this.chopTreeFindCooldown <= this.client.worldEntity.getLocalTime()) {
         if (this.client.getLevel() != null) {
            PlayerMob var1 = this.client.getPlayer();
            if (var1 != null) {
               this.chopTreeCoord = null;
               final AtomicBoolean var2 = new AtomicBoolean();
               (new AreaFinder(var1, 20) {
                  public boolean checkPoint(int var1, int var2x) {
                     if (ChopTreePhase.this.client.getLevel().getTileID(var1, var2x) == TileRegistry.emptyID) {
                        var2.set(true);
                        return true;
                     } else if (ChopTreePhase.this.checkForTree(var1, var2x)) {
                        ChopTreePhase.this.chopTreeCoord = new Point(var1, var2x);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }).runFinder();
               this.chopTreeFindCooldown = this.client.worldEntity.getLocalTime() + (long)(var2.get() ? 1000 : 4000);
            }
         }
      }
   }

   private boolean checkForTree(int var1, int var2) {
      return this.client.getLevel().getObject(var1, var2).isTree;
   }
}
