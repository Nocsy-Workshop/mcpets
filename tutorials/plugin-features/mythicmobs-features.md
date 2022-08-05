# MythicMobs features

## Mechanics

Here you will find every mechanics that you can use when MCPets is installed on your server.

| Mechanic name | What for                                                       | Usage                             | Requirements            |
| ------------- | -------------------------------------------------------------- | --------------------------------- | ----------------------- |
| GivePet       | Gives permission to the targeted player to use a certain pet   | givepet{id=#}                     | target must be a player |
| SetPet        | Turns the caster into a certain pet own by the targeted player | setpet{id=#;permCheck=true/false} | target must be a player |
| PetFollow     | Turns on/off the following AI of the pet                       | petfollow{follow=true/false}      | target must be a pet    |

## Targeters

Here is a table referencing the targeters and their usage.

You can use these targeters in MythicMobs in your skills.

| Usage                  | Targeter  |
| ---------------------- | --------- |
| target the pet's owner | @PetOwner |
