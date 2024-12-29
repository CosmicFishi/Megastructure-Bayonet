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
        if(bayonetManager.isBayonetRepairing(fleet)) {
            tooltip.addPara("- Status: %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Repairing");
        }
        if(bayonetManager.isBayonetBuilding(fleet)) {
            tooltip.addPara("- Status: %s", 5f, Misc.getTextColor(), Misc.getHighlightColor(), "Building");
        }
    }

    @Override
    public boolean isUsable() {
        CampaignFleetAPI fleet = bayonetManager.getBayonetStationFleet();
        return bayonetManager.isBayonetFunctional(fleet) && super.isUsable();
    }
}
