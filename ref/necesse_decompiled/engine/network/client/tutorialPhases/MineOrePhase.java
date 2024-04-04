package necesse.engine.network.client.tutorialPhases;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.AreaFinder;
import necesse.engine.GlobalData;
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
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class MineOrePhase extends TutorialPhase {
   private static final String[] oreItems = new String[]{"copperore", "ironore", "goldore"};
   private Point oreCoord;
   private long findOreCooldown;
   private boolean isUnderground;
   private LocalMessage mineOre;
   private LocalMessage findOre;
   private HudDrawElement drawElement;

   public MineOrePhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void start() {
      this.oreCoord = null;
      this.findOreCooldown = 0L;
      this.isUnderground = false;
      this.mineOre = new LocalMessage("tutorials", "mineore");
      this.findOre = new LocalMessage("tutorials", "findorefloat");
      this.drawElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, final PlayerMob var3) {
            if (MineOrePhase.this.isUnderground) {
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return Integer.MAX_VALUE;
                  }

                  public void draw(TickManager var1) {
                     Point var3x = MineOrePhase.this.oreCoord;
                     DrawOptions var2x;
                     FairTypeDrawOptions var4;
                     if (var3x != null) {
                        var4 = MineOrePhase.this.getTextDrawOptions(MineOrePhase.this.getTutorialText(MineOrePhase.this.mineOre.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE1)));
                        var2x = MineOrePhase.this.getLevelTextDrawOptions(var4, var3x.x * 32 + 16, var3x.y * 32 + 8, var2, var3, true);
                     } else {
                        var4 = MineOrePhase.this.getTextDrawOptions(MineOrePhase.this.getTutorialText(MineOrePhase.this.findOre.translate()));
                        var2x = MineOrePhase.this.getLevelTextDrawOptions(var4, var3.getX(), var3.getY() - 50, var2, var3, false);
                     }

                     var2x.draw();
                  }
               });
            }

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
      this.isUnderground = this.client.getLevel().getIslandDimension() < 0;
      if (this.isUnderground) {
         this.setObjective(var1, "findore");
      } else {
         this.setObjective(var1, "gounder");
      }

   }

   public void tick() {
      boolean var1 = this.client.getLevel().getIslandDimension() < 0;
      if (var1 != this.isUnderground && GlobalData.getCurrentState() instanceof MainGame) {
         this.updateObjective((MainGame)GlobalData.getCurrentState());
      }

      if (var1) {
         this.findOre();
         if (this.oreCoord != null && !this.isOre(this.oreCoord.x, this.oreCoord.y)) {
            this.over();
         }

         String[] var2 = oreItems;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            if (this.client.getPlayer().getInv().getAmount(ItemRegistry.getItem(var5), false, false, false, "tutorial") > 0) {
               this.over();
            }
         }
      } else {
         this.oreCoord = null;
      }

   }

   private void findOre() {
      if (this.findOreCooldown <= this.client.worldEntity.getLocalTime()) {
         if (this.client.getLevel() != null) {
            PlayerMob var1 = this.client.getPlayer();
            if (var1 != null) {
               this.oreCoord = null;
               final AtomicBoolean var2 = new AtomicBoolean();
               (new AreaFinder(var1, 30) {
                  public boolean checkPoint(int var1, int var2x) {
                     if (MineOrePhase.this.client.getLevel().getTileID(var1, var2x) == TileRegistry.emptyID) {
                        var2.set(true);
                        return true;
                     } else if (MineOrePhase.this.isOre(var1, var2x)) {
                        MineOrePhase.this.oreCoord = new Point(var1, var2x);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }).runFinder();
               this.findOreCooldown = this.client.worldEntity.getLocalTime() + (long)(var2.get() ? 1000 : 5000);
            }
         }
      }
   }

   private boolean isOre(int var1, int var2) {
      return this.client.getLevel().getObject(var1, var2).isOre;
   }
}
