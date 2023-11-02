package dcq.mods;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class References {
    public static final String MOD_ID = "dcq_bundles";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier SYNC_CONFIG_PACKET = Identifier.of(MOD_ID, "sync_config");
}
