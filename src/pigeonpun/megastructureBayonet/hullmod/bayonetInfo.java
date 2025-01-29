package pigeonpun.megastructureBayonet.hullmod;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import pigeonpun.megastructureBayonet.structure.bayonetManager;

import java.awt.*;

public class bayonetInfo extends BaseHullMod {
    String modId = "mega_bayonet_info_status";
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if(!bayonetManager.isBayonetStationFleetInit()) return;
        CampaignFleetAPI bayonetStation = bayonetManager.getBayonetStationFleet();
        bayonetManager.bayonetStatusData statusData = bayonetManager.getCurrentBayonetStatus(bayonetStation);
        switch (statusData.status) {
            case FUNCTIONAL:
                stats.getMaxCombatReadiness().unmodifyFlat(modId);
                break;
            case DAMAGED:
                float totalRepairDays = bayonetManager.getTotalRepairDay(bayonetStation);
                float currentRepairDaysLeft = statusData.dayLeftBeforeFunctional;
                stats.getMaxCombatReadiness().modifyFlat(modId,1-(currentRepairDaysLeft/totalRepairDays), "Bayonet Station undergo repairing");
                break;
            case BUILDING:
                float totalBuildDays = bayonetManager.getTotalBuildDay(bayonetStation);
                float currentBuildDaysLeft = statusData.dayLeftBeforeFunctional;
                stats.getMaxCombatReadiness().modifyFlat(modId,1-(currentBuildDaysLeft/totalBuildDays), "Bayonet Station undergo construction");
                break;
        }
    }
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        Color t = Misc.getTextColor();
        Color g = Misc.getGrayColor();

        tooltip.addPara("Display information related to the Bayonet Station", opad);
        if(!bayonetManager.isBayonetStationFleetInit()) return;
        CampaignFleetAPI bayonetStation = bayonetManager.getBayonetStationFleet();
        bayonetManager.bayonetStatusData data = bayonetManager.getCurrentBayonetStatus(bayonetStation);
        switch (data.status) {
            case DAMAGED:
                tooltip.addPara("- Status: %s, %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Repairing", Math.round(Math.ceil(data.dayLeftBeforeFunctional)) + " days left");
                tooltip.addPara("  + CR: %s", 5f, Misc.getTextColor(), Misc.getNegativeHighlightColor(), Math.round(ship.getCurrentCR() * 100) + " %");
                break;
            case FUNCTIONAL:
                tooltip.addPara("- Status: %s", 5f, Misc.getTextColor(), Misc.getPositiveHighlightColor(), "Functional");
                break;
            case BUILDING:
                tooltip.addPara("- Status: %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Building", Math.round(Math.ceil(data.dayLeftBeforeFunctional)) + " days left");
                tooltip.addPara("  + CR: %s", 5f, Misc.getTextColor(), Misc.getNegativeHighlightColor(), Math.round(ship.getCurrentCR() * 100) + " %");
                break;
        }
    }
}
