Game name: Tag
Creator: Evan Wang
Date: 2024/06/14

Main Idea: Two players chase each other across the map in an intense game of tag
Setting: 2D map with platforms
Objective: If you are "it" try to catch the other player, if you are not "it" run from the player who is it
Winner: First player to avoid being "it" for 80 seconds
Player 1 Controls: Jump(W), Left(A), Right(D)
Player 2 Controls: Jump(Up Arrow), Left (Left Arrow), Right(Right Arrow)

Features:
- Inventory Screen where player's can change to a different set of avatars
- Bounce Pad that allows to player to jump high
- Power Up Cubes that spawns in a random location every 20 seconds and give a random power up, lasting 8 seconds, when picked up (Speed Boost, Low Gravity, Ghost Mode)
- Teleporters that spawn in random locations every 20 seconds that allows the player to teleport between the two teleporters
- Decorations including flowers, bushes, and trees that do not impact gameplay
- Music for the menu, game, and various sound effects

Changes from Proposal and Design:
- Did not implement coin system or shop as I would have to save the number of coins in a file so that it would save after closing the game
- Instead created an inventory system where all skins are already unlocked and can be used

Bugs:
- None that I know of

Info for Marking:
- You can change the global variables GAME_LENGTH and WIN_VALUE to make the game shorter
