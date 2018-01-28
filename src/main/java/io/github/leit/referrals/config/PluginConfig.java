package io.github.leit.referrals.config;


public class PluginConfig {
    private boolean rewardReferrer;
    private boolean rewardReferred;
    private String referrerRewardCommand;
    private String referredRewardCommand;

    public PluginConfig(boolean rewardReferrer, boolean rewardReferred, String referrerRewardCommand, String referredRewardCommand) {
        this.referredRewardCommand = referredRewardCommand;
        this.referrerRewardCommand = referrerRewardCommand;
        this.rewardReferred = rewardReferred;
        this.rewardReferrer = rewardReferrer;
    }

    public String getReferredRewardCommand() {
        return referredRewardCommand;
    }

    public String getReferrerRewardCommand() {
        return referrerRewardCommand;
    }

    public boolean isRewardReferrer() {
        return rewardReferrer;
    }

    public boolean isRewardReferred() {
        return rewardReferred;
    }
}
