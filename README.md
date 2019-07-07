# Referrals ![Logo](https://github.com/L-E-iT/Referrals/blob/master/src/main/resources/images/Referrals%20Logo%20Small.png)
# Referrals
### A Referrals plugin for Sponge
### Commands:
Command | Usage | Example | Aliases
--- | --- | --- | ---
```/referrals``` | main command, shows the help menu | ```/referrals``` | None
```/referrals top [#]``` | Used to show the top referrers on the server, defaults to 10 if no number is provided | ```/referrals top 10``` | ```/referrals top/t```
```/referrals check [name]``` | Used to show the amount of players the provided player has referred, defaults to the player sending the message if no name is listed. | ```/referrals check BranFlakes``` | ```/referrals check/chk```
```/referrals thanks [name]``` | Used to thank another player for referring you | ```/referrals thanks BranFlakes``` | ```/referrals thanks/thank```
```/referrals help``` | Basic help command to show all command usage | ```/referrals help``` | ```/referrals help/h```


### Permissions:
Permission | Description
---|---
referrals.check | allows a user to use the check command
referrals.check.self | allows a user to use the check command on themselves
referrals.check.other | allows a user to use the check command on others
referrals.top | allows a user to view the top referrers on a server

### Config:
```
#set the command to execute for a reward
rewardActions {
  # Set the command(s) that should execute on a successful referral
  # do not use a '/' in the command
  # %p = player name
  # example provided below
  # EXAMPLES:
  # referredRewardCommand = ["msg %p Thanks for using Referrals!", "msg %p Make sure to refer other players!"]
  # milestoneCommands = ["1:::msg %p Thanks for referring 1 player!","5:::msg %p Thanks for referring 5 players!"]
  #
  referrerRewardCommand = ["msg %p Thanks for using Referrals!", "msg %p Make sure to refer other players!"]
  referredRewardCommand = ["msg %p Thanks for using Referrals!", "msg %p Make sure to refer other players!"]
  # Split by [Number referred]:::[Command to execute]
  milestoneCommands = ["1:::msg %p Thanks for referring 1 player!",
    "5:::msg %p Thanks for referring 5 players!",
    "10:::msg %p Thanks for referring 10 players!",
    "15:::msg %p Thanks for referring 15 players!",
    "20:::msg %p Thanks for referring 20 players!"]
  globalCommands = [""]
}

# set which player(s) should receive rewards
rewardPlayers {
  # if true, player will receive rewards for a successful referral action
  # default is true (Both the referred player, and the player that referred them will receive rewards)
  referrer = true
  referred = true
}

# set which players or actions should trigger on a referral
rewardConfig {
  # If true, the referrer will receive a reward
  referrerReward = true
  # If true, the referred player will receive a reward
  referredReward = true
  # If true, players will receive milestone rewards for referring players
  milestoneRewards = true
  # If true, global commands will be triggered during a referral
  globalCommand = false
}
```

[Source](https://github.com/L-E-iT/Referrals)

