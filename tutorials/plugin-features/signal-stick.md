# Signal Stick

## What for ?

The Signal stick is a tool that enables you to order your pet to cast skills from MythicMobs. It comes as an item in game that you can customize however you want for each pet.

It comes with two options :

* Left click to switch order (get the next order in the list circularly)
* Right click to cast the order (cooldown can be managed through MythicMobs)

If you want to have a look at what you can achieve with the signal stick, jump at 1:00 on this trailer video:

{% embed url="https://youtu.be/HdhAh1C8RcQ?t=60" %}
Drakonin Pack - by [Nocsy](https://mcmodels.net/vendors/nocsy/)
{% endembed %}

## How to ?

The Signal stick item is unique for each pet. Also it can be customized using a resource pack (customModelData) or a head (using textureBase64).

The exemple below shows you how to implement the signal stick into the pet config. Have a look at the [pet config example](../config-templates/pet-config.md) if you still have some doubts on how to add this YAML code into your pet config.

All of the features you can use are also explained below so you can see what it can be used for, note that some of them are optional so you don't need to fill them all.

```yaml
Signals:
  # List the signals you want to cast to the pet (can be empty)
  # Make sure the pet is actually listening to these signals or nothing will happen
  Values:
  - ATTACK
  - JUMP
  # Setup the Signal Stick item, it uses the same constructor type as the icon
  Item:
    GetFromMenu: true # put false if you want players to get it from another way (like if you added a crafting method, or an NPC that could give it etc...)
    Name: Signal stick name
    Material: STICK
    CustomModelData: 0
    Description:
    - You have a ton to explain here
    - on how to use the stick maybe
```
