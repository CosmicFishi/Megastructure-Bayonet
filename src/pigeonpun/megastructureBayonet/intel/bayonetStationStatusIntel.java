package pigeonpun.megastructureBayonet.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import pigeonpun.megastructureBayonet.structure.bayonetManager;

import java.awt.*;
import java.util.Collections;
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
    protected String getName() {
        return "Bayonet status update";
    }

    @Override
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 3f;
        float opad = 10f;

        float initPad = pad;
        if (mode == ListInfoMode.IN_DESC) initPad = opad;
        Color tc = getBulletColorForMode(mode);
        CampaignFleetAPI fleet = bayonetManager.getBayonetStationFleet();
        bayonetManager.bayonetStatusData data = bayonetManager.getCurrentBayonetStatus(fleet);
        switch (data.status) {
            case DAMAGED:
                info.addPara("Status: %s, %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Repairing", Math.round(Math.ceil(data.dayLeftBeforeFunctional)) + " days left");
                break;
            case FUNCTIONAL:
                info.addPara("Status: %s", 5f, Misc.getTextColor(), Misc.getPositiveHighlightColor(), "Functional");
                break;
            case BUILDING:
                info.addPara("Status: %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Building", Math.round(Math.ceil(data.dayLeftBeforeFunctional)) + " days left");
                break;
        }
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "bayonet_status");
    }

    @Override
    public Color getTitleColor(ListInfoMode mode) {
        return Global.getSector().getPlayerFaction().getBaseUIColor();
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        Collections.addAll(tags, Tags.INTEL_IMPORTANT, "Personal");
        return tags;
    }
    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        CampaignFleetAPI fleet = bayonetManager.getBayonetStationFleet();
        bayonetManager.bayonetStatusData statusData = bayonetManager.getCurrentBayonetStatus(fleet);
        switch (statusData.status) {
            case DAMAGED:
                info.addPara("- Status: %s, %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Repairing", Math.round(Math.ceil(statusData.dayLeftBeforeFunctional)) + " days left");
                break;
            case FUNCTIONAL:
                info.addPara("- Status: %s", 5f, Misc.getTextColor(), Misc.getPositiveHighlightColor(), "Functional");
                break;
            case BUILDING:
                info.addPara("- Status: %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Building", Math.round(Math.ceil(statusData.dayLeftBeforeFunctional)) + " days left");
                break;
        }
        info.addPara("- Location: %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), fleet.getContainingLocation().getNameWithTypeShort());
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        CampaignFleetAPI fleet = bayonetManager.getBayonetStationFleet();
        StarSystemAPI system = (StarSystemAPI) fleet.getContainingLocation();
        return system.getCenter();
    }
}
