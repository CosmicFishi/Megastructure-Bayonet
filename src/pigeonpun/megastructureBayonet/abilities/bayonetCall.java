package pigeonpun.megastructureBayonet.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import org.apache.log4j.Logger;
import pigeonpun.megastructureBayonet.ModPlugin;
import pigeonpun.megastructureBayonet.structure.bayonetManager;

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
        bayonetManager.recreateBayonetStation();
        activated = true;
    }

    @Override
    protected void deactivateImpl() {
        activated = false;
    }

    @Override
    protected void cleanupImpl() {

    }
}
