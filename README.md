# 🎄 kAdvent

A simple plugin adding an advent calendar to your server.

## 🧭Commands

- `/advent` - Opens the advent calendar GUI.
- `/advent reload` - Reloads the plugin.
- `/advent set <day>` - Sets the items for the specified day based on your inventory.
- `/advent give <day>` - Gives you the items for the specified day.
- `/advent reset <player> [<day>]` - Resets the specified player's calendar. If a day is specified, only that day will
  be reset.

## 🧱 Configuration

The advent calendar is defined by a json file in the plugin's data folder. The items are saved in base64 format in order
to support modded items.

```json

```

Every player has their own calendar, which is saved in the plugin's data folder. The file name is the player's UUID.

```json
{
  "openedDays": [
	1,
	2
  ]
}
```