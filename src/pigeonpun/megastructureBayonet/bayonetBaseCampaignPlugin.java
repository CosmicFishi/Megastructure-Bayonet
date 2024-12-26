package pigeonpun.megastructureBayonet;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.OrbitalStationInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import pigeonpun.megastructureBayonet.structure.bayonetManager;

public class bayonetBaseCampaignPlugin extends BaseCampaignPlugin {
    @Override
    public String getId() {
        return "Megastructure_bayonetCampaignPlugin";
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    @Override
    public PluginPick<InteractionDialogPlugin> pickInteractionDialogPlugin(SectorEntityToken interactionTarget) {
        if(interactionTarget.getMemoryWithoutUpdate().get(bayonetManager.BAYONET_ENTITY_ID) != null) {
            return new PluginPick<InteractionDialogPlugin>(new RuleBasedInteractionDialogPluginImpl(), PickPriority.MOD_SPECIFIC);
        }
        return super.pickInteractionDialogPlugin(interactionTarget);
    }
}
