# Categories

## What for ?

**Categories** are a way of organizing your pets in the "_/mcpets"_ menu by grouping in them sections.

## How to ?

To create a category, follow these steps:

* Go into "_./plugins/MCPets/Categories"_ folder
* Create a new YAML file calling it however you want.
  * _Exemple : "all\_pets.yml"_
* You can have a look at the category template below to complete the fields

If you want to change the icon in the pet menu, you can either try to do it by hand or import an item from the game using _/mcpets item add \<category\_name>_ to then fetch the item directly into the **menuIcons.yml** in _./plugins/MCPets/_ folder.

```yaml
# What should be the display name of the category in the mcpets menu ?
DisplayName: "Category Name"
# Here you can set your icon. This is not the usual syntax.
# If you want to format any item into that format directly in game,
# take the said item in your hand and add it to MCPets through /mcpets item add <category_name>
# Then fetch it directly into the "menuIcons.yml" folder within MCPets folder
Icon:
  ==: org.bukkit.inventory.ItemStack
  v: 2975
  type: PLAYER_HEAD
  meta:
    ==: ItemMeta
    meta-type: SKULL
    display-name: '{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gold","text":"category_template"}],"text":""}'
    loc-name: '{"extra":[{"text":"MCPets;category_template"}],"text":""}'
    lore:
    - '{"extra":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"gray","text":"Click
      to apply that skin"}],"text":""}'
    Damage: 3
    skull-owner:
      ==: PlayerProfile
      uniqueId: 543765df-1f3a-4f48-9bfe-e3a4e7f4b508
      name: MCPetsHeads
      properties:
      - name: textures
        value: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzhmM2Q3NjkxZDZkNWQ1NDZjM2NmMjIyNDNiM2U4MzA5YTEwNzAxMWYyZWU5Mzg0OGIxZThjNjU3NjgxYTU2ZCJ9fX0=
# Here list the pets IDs you want to add to this category
Pets:
- baby_yokibird1
- otter
```
