package necesse.engine.network.client.tutorialPhases;

import java.util.Iterator;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.control.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientTutorial;
import necesse.engine.registries.MobRegistry;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairControlKeyGlyph;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class InteractElderPhase extends TutorialPhase {
   private Mob elderMob;
   private long findElderCooldown;
   private boolean elderFocus;
   private LocalMessage walk;
   private HudDrawElement drawElement;

   public InteractElderPhase(ClientTutorial var1, Client var2) {
      super(var1, var2);
   }

   public void start() {
      super.start();
      this.elderMob = null;
      this.findElderCooldown = 0L;
      this.elderFocus = false;
      this.walk = new LocalMessage("tutorials", "elderwalk");
      this.drawElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, final PlayerMob var3) {
            if (!InteractElderPhase.this.elderFocus) {
               final Mob var4 = InteractElderPhase.this.elderMob;
               if (var4 != null) {
                  var1.add(new SortedDrawable() {
                     public int getPriority() {
                        return Integer.MAX_VALUE;
                     }

                     public void draw(TickManager var1) {
                        FairTypeDrawOptions var2x = InteractElderPhase.this.getTextDrawOptions(InteractElderPhase.this.getTutorialText(InteractElderPhase.this.walk.translate()).replaceAll("<key>", new FairControlKeyGlyph(Control.MOUSE2)));
                        DrawOptions var3x = InteractElderPhase.this.getLevelTextDrawOptions(var2x, var4.getX(), var4.getY() - 32, var2, var3, true);
                        var3x.draw();
                     }
                  });
               }
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
      if (!this.elderFocus) {
         this.setObjective(var1, "talkelder");
      } else {
         this.setObjective(var1, new LocalMessage("tutorials", "talkelderstop", "key", "[input=" + Control.INVENTORY.id + "]"));
      }

   }

   public void tick() {
      if (!this.elderFocus) {
         if (this.elderMob != null && (this.elderMob.removed() || this.client.getLevel().entityManager.mobs.get(this.elderMob.getUniqueID(), false) != this.elderMob)) {
            this.elderMob = null;
         }

         this.findElder();
      } else if (!this.client.hasFocusForm()) {
         this.over();
      }

   }

   public void findElder() {
      if (this.findElderCooldown <= this.client.worldEntity.getLocalTime()) {
         if (this.client.getLevel() != null) {
            PlayerMob var1 = this.client.getPlayer();
            if (var1 != null) {
               this.elderMob = null;
               float var2 = -1.0F;
               int var3 = MobRegistry.getMobID("elderhuman");
               Iterator var4 = this.client.getLevel().entityManager.mobs.iterator();

               while(true) {
                  Mob var5;
                  float var6;
                  do {
                     do {
                        if (!var4.hasNext()) {
                           this.findElderCooldown = this.client.worldEntity.getLocalTime() + 2000L;
                           return;
                        }

                        var5 = (Mob)var4.next();
                     } while(var5.getID() != var3);

                     var6 = var5.getDistance(this.client.getPlayer());
                  } while(this.elderMob != null && !(var6 < var2));

                  var2 = var6;
                  this.elderMob = var5;
               }
            }
         }
      }
   }

   public void elderInteracted() {
      this.elderFocus = true;
      if (GlobalData.getCurrentState() instanceof MainGame) {
         this.updateObjective((MainGame)GlobalData.getCurrentState());
      }

   }
}
