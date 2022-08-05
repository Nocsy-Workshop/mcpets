# Skins

## What for ?

The skins enable you to have different model for the same pet to create variations of it. For instance if you have color variations of the same model, then you just create one pet and associate it to various skins.

Note that each skin has its unique permission so it's unlockable by players.

Typically this could apply to this kind of pets:

{% embed url="https://www.youtube.com/watch?v=HOBgTiU-m-8" %}
Kingdom Cats - by [Nocsy](https://mcmodels.net/vendors/nocsy/)
{% endembed %}

## How to ?

To implement the skins, you have to go into the pet config and add the YAML code below and tweak it to your needs. Once this is done, you should be able to access the skin tab within the interaction menu of the pet.

If you struggle on how to implement this code into the pet config, you can have a look at the general [pet config template](../config-templates/pet-config.md) to see how it's done at the global scale.

```yaml
Skins:
  # Add the skins of the pet if it has various skins (this is not mandatory)
  Skin1: # Create a section
    Model: modelengine_blueprint_name # put any model engine blueprint here referencing to a skin you'd like to apply
    Permission: skin.permission # Set the permission to access the skin, it can be the same as the pet permission for instance
    Icon: # Set the icon of the skin, just like before almost same syntax
      Material: STONE
      CustomModelData: 0
      DisplayName: "Skin 1"
      Lore:
      - "This is a lore"
      TextureBase64: anybase64youwant # this is not mandatory. Remove the Material field if you wanna use it
  Skin2:
     # Do the same syntax with as many skins as you want. Remember to add your default skin as well !
```
