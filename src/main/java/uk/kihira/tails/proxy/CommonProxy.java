package uk.kihira.tails.proxy;

import uk.kihira.tails.common.LibraryManager;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.Tails;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonProxy {

    protected final HashMap<UUID, Outfit> activeOutfits = new HashMap<>(); // todo move this out of proxy? Will be removed in cloud save stuff most likely
    protected LibraryManager libraryManager;

    public void setActiveOutfit(UUID uuid, Outfit outfit) {
        this.activeOutfits.put(uuid, outfit);
        Tails.logger.debug(String.format("Added outfit for %s: %s", uuid.toString(), outfit));
    }

    public void removeActiveOutfit(UUID uuid) {
        if (hasActiveOutfit(uuid)) {
            if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
                //todo Tell client to remove textures
                //Tails.networkWrapper.sendToAll(new PlayerDataMessage(uuid, this.activeOutfits.get(uuid), true));
            }
            this.activeOutfits.remove(uuid);
            Tails.logger.debug(String.format("Removed part data for %s", uuid.toString()));
        }
    }

    public void clearAllPartsData() {
        this.activeOutfits.clear();
        Tails.logger.debug("Clearing parts data");
    }

    public boolean hasActiveOutfit(UUID uuid) {
        return this.activeOutfits.containsKey(uuid);
    }

    public Outfit getActiveOutfit(UUID uuid) {
        return this.activeOutfits.get(uuid);
    }

    public Map<UUID, Outfit> getActiveOutfits() {
        return this.activeOutfits;
    }

    public LibraryManager getLibraryManager() {
        return libraryManager;
    }
}