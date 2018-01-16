# Referrals ![Logo](https://github.com/L-E-iT/Referrals/blob/master/src/main/resources/images/Referrals%20Logo%20Small.png)
# Referrals
### A Referrals plugin for Sponge
### Commands:
Command | Usage | Example | Alias
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
# command to execute for a reward
rewardActions {
  # Set the command that should execute on a successful referral\
  # do not use a '/' in the command
  # %p = player name
  rewardCommand = "msg %p Thanks for using Referrals!"
}

# which player(s) should receive rewards
rewardPlayers {
  # if true, player will receive rewards for a successful referral action
  # default is true (Both the referred player, and the player that referred them will receive rewards)
  # referrer = player thanked, referred = player executing command
  referrer = true
  referred = true
}
```

[Source](https://github.com/L-E-iT/Referrals)

