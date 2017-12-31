package io.github.leit.referrals.config;


public class PluginConfig {
    private boolean rewardReferrer;
    private boolean rewardReferred;
    private String rewardCommand;

    public PluginConfig(boolean rewardReferrer, boolean rewardReferred, String rewardCommand) {
        this.rewardCommand = rewardCommand;
        this.rewardReferred = rewardReferred;
        this.rewardReferrer = rewardReferrer;
    }

    public String getRewardCommand() {
        return rewardCommand;
    }

    public boolean isRewardReferrer() {
        return rewardReferrer;
    }

    public boolean isRewardReferred() {
        return rewardReferred;
    }
}
