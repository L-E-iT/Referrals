package io.github.leit.referrals.config;


import java.util.List;

public class PluginConfig {
    private boolean rewardReferrer;
    private boolean rewardReferred;
    private boolean milestoneRewards;
    private boolean globalCommand;
    private List<String> referrerRewardCommand;
    private List<String> referredRewardCommand;
    private List<String> milestoneCommands;
    private List<String> globalCommands;

    public PluginConfig(boolean rewardReferrer, boolean rewardReferred, boolean milestoneRewards, boolean globalCommand,
                        List<String> referrerRewardCommand, List<String> referredRewardCommand, List<String> milestoneCommands, List<String> globalCommands) {
        this.referredRewardCommand = referredRewardCommand;
        this.referrerRewardCommand = referrerRewardCommand;
        this.milestoneCommands = milestoneCommands;
        this.globalCommands = globalCommands;
        this.rewardReferred = rewardReferred;
        this.rewardReferrer = rewardReferrer;
        this.milestoneRewards = milestoneRewards;
        this.globalCommand = globalCommand;
    }

    public List<String> getReferredRewardCommand() {
        return referredRewardCommand;
    }

    public List<String> getReferrerRewardCommand() {
        return referrerRewardCommand;
    }

    public List<String> getGlobalCommands() {
        return globalCommands;
    }

    public List<String> getMilestoneCommands() {
        return milestoneCommands;
    }

    public boolean isRewardReferrer() {
        return rewardReferrer;
    }

    public boolean isRewardReferred() {
        return rewardReferred;
    }

    public boolean isGlobalCommand() {
        return globalCommand;
    }

    public boolean isMilestoneRewards() {
        return milestoneRewards;
    }
}
