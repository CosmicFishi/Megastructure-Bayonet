package pigeonpun.megastructureBayonet.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import pigeonpun.megastructureBayonet.ModPlugin;
import pigeonpun.megastructureBayonet.structure.bayonetManager;

import java.awt.*;

public class bayonetCall extends BaseDurationAbility {
    public static final Logger log = Global.getLogger(bayonetCall.class);
    protected boolean activated = false;
    @Override
    protected void activateImpl() {

    }

    @Override
    protected void applyEffect(float amount, float level) {
        SectorEntityToken station;
        if(activated) {
            return;
        }
        bayonetManager.summonBayonetStation();
        activated = true;
    }

    @Override
    protected void deactivateImpl() {
        activated = false;
    }

    @Override
    protected void cleanupImpl() {

    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        Color gray = Misc.getGrayColor();
        Color highlight = Misc.getHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();
        float pad = 10f;
        LabelAPI title = tooltip.addTitle("Bayonet Hail");

        tooltip.addPara("Call in Bayonet Station", pad);super.createTooltip(tooltip, expanded);

        //todo: add in days of repair/building
        CampaignFleetAPI fleet = bayonetManager.getBayonetStationFleet();
        bayonetManager.bayonetStatusData data = bayonetManager.getCurrentBayonetStatus(fleet);
        switch (data.status) {
            case REPAIRING:
                tooltip.addPara("- Status: %s, %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Repairing", Math.round(Math.ceil(data.dayLeftBeforeFunctional)) + " days left");
                break;
            case FUNCTIONAL:
                tooltip.addPara("- Status: %s", 5f, Misc.getTextColor(), Misc.getPositiveHighlightColor(), "Functional");
                break;
            case BUILDING:
                tooltip.addPara("- Status: %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Building", Math.round(Math.ceil(data.dayLeftBeforeFunctional)) + " days left");
                break;
        }
        if(fleet.getBattle() != null) {
            LabelAPI status = tooltip.addPara("- Action: %s %s", 5f,
                    Misc.getTextColor(), Misc.getHighlightColor(), "In combat with",
                    fleet.getBattle().getSideTwo().size() > 1?
                            "One or more fleets" :
                            fleet.getBattle().getSideTwo().get(0).getFaction().getDisplayName() + " fleet"
            );
            status.setHighlightColors(Misc.getTextColor(), fleet.getBattle().getSideTwo().size() > 1? Misc.getNegativeHighlightColor() : fleet.getBattle().getSideTwo().get(0).getFaction().getColor());
        }
        if(Global.getSettings().isDevMode()) {
            tooltip.addPara("Devmode always enable", Misc.getGrayColor(), 5f);
        }
    }

    /**
     * Disable ability when:
     * - In combat with another fleet
     * - Repairing / building station
     * - Always usable when dev mode enabled
     * @return
     */
    @Override
    public boolean isUsable() {
        if(Global.getSettings().isDevMode()) return true;
        CampaignFleetAPI fleet = bayonetManager.getBayonetStationFleet();
        if(fleet.getBattle() != null) {
            return false;
        }
        return bayonetManager.isBayonetFunctional(fleet) && super.isUsable();
    }
}
