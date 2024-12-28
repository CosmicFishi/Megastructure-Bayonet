package pigeonpun.megastructureBayonet.intel;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.Set;

/**
 * Display Bayonet station information such as: repairing/functional/building
 */
public class bayonetStationStatusIntel extends BaseIntelPlugin {

    //todo: this
    public bayonetStationStatusIntel() {
        endAfterDelay(1f);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        super.createIntelInfo(info, mode);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        return super.getIntelTags(map);
    }

    @Override
    protected String getName() {
        return super.getName();
    }

    @Override
    public String getIcon() {
        return super.getIcon();
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        super.createSmallDescription(info, width, height);
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return super.getMapLocation(map);
    }
}
