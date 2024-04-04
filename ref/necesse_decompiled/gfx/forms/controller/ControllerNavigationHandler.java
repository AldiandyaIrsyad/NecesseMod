package necesse.gfx.forms.controller;

import necesse.engine.control.ControllerEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

public interface ControllerNavigationHandler {
   boolean handleNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4);
}
