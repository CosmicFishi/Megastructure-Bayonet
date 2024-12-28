package pigeonpun.megastructureBayonet.condition;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class bayonetStationCondition extends BaseMarketConditionPlugin {
    @Override
    public void init(MarketAPI market, MarketConditionAPI condition) {
        super.init(market, condition);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        //todo: display information about the station
    }
}
