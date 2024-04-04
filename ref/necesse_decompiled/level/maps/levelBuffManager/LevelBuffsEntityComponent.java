package necesse.level.maps.levelBuffManager;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.manager.EntityComponent;

public interface LevelBuffsEntityComponent extends EntityComponent {
   Stream<ModifierValue<?>> getLevelModifiers();
}
