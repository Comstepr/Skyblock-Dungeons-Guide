package kr.syeyoung.dungeonsguide.roomprocessor;

import kr.syeyoung.dungeonsguide.dungeon.data.OffsetPoint;
import kr.syeyoung.dungeonsguide.dungeon.data.OffsetPointSet;
import kr.syeyoung.dungeonsguide.dungeon.roomfinder.DungeonRoom;
import kr.syeyoung.dungeonsguide.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MinecraftError;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

import java.awt.*;
import java.util.List;

public class RoomProcessorTicTacToeSolver extends GeneralRoomProcessor {

    private OffsetPointSet board;
    private byte[][] lastBoard;
    public RoomProcessorTicTacToeSolver(DungeonRoom dungeonRoom) {
        super(dungeonRoom);

        board = (OffsetPointSet) dungeonRoom.getDungeonRoomInfo().getProperties().get("board");
    }

    // -1 com, 1 pla, 0 emp
    private byte[][] buildBoardState() {
        byte[][] board = new byte[3][3];
        World w= getDungeonRoom().getContext().getWorld();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                OffsetPoint op = this.board.getOffsetPointList().get(x * 3 + y);
                BlockPos bpos = op.getBlockPos(getDungeonRoom());
                Block b = w.getChunkFromBlockCoords(bpos).getBlock(bpos);
                if (b == Blocks.stone_button) {
                    board[y][x] = 0;
                } else if (b == Blocks.air){
                    AxisAlignedBB abab = AxisAlignedBB.fromBounds(bpos.getX() , bpos.getY(), bpos.getZ(), bpos.getX() +1, bpos.getY() +1, bpos.getZ() +1);
                    List<EntityItemFrame> frames = getDungeonRoom().getContext().getWorld().getEntitiesWithinAABB(EntityItemFrame.class, abab);
                    if (frames.isEmpty()) board[y][x] = 0;
                    else {
                        ItemStack displayedItem = frames.get(0).getDisplayedItem();
                        if (displayedItem == null || displayedItem.getItem() == null || !displayedItem.getItem().isMap()) {
                            board[y][x] = 0;
                            continue;
                        }
                        MapData mapData = ((ItemMap)displayedItem.getItem()).getMapData(displayedItem, w);
                        byte center = mapData.colors[64 * 128+64];
                        if (center == 114)
                            board[y][x] = -1;
                        else
                            board[y][x] = 1;
                    }
                }
            }
        }
        return board;
    }

    private byte checkWinner(byte[][] board) {
        for (int y = 0; y <3; y++) {
            byte potentialWinner = board[y][0];
            if (potentialWinner == 0) continue;
            boolean found = false;
            for (int x = 0; x < 3; x++) {
                if (potentialWinner != board[y][x]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return potentialWinner;
            }
        }
        for (int x = 0; x <3; x++) {
            byte potentialWinner = board[0][x];
            if (potentialWinner == 0) continue;
            boolean found = false;
            for (int y = 0; y < 3; y++) {
                if (potentialWinner != board[y][x]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return potentialWinner;
            }
        }
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != 0) {
            return board[0][0];
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != 0) {
            return board[0][2];
        }
        return 0;
    }

    private int chosePos = -1;
    private int minimax(byte[][] board, byte player) {
        byte winner = checkWinner(board);
        if (winner != 0) {
            return winner * player;
        }

        int move = -1;
        int score = -2;
        for (int i = 0; i < 9; i++) {
            if (board[i % 3][i / 3] == 0) {
                byte[][] cloned = new byte[3][];
                for(int k = 0; k < 3; k++)
                    cloned[k] = board[k].clone();

                cloned[i % 3][i/3] = player;
                int scoreForMove = -minimax(cloned, (byte) -player);
                if (scoreForMove > score) {
                    score = scoreForMove;
                    move = i;
                }
            }
        }
        chosePos = move;
        if (move == -1) {
            return 0;
        }

        return score;
    }

    private boolean gameEnded = false;

    @Override
    public void tick() {
        super.tick();
        if (board == null) return;
        if (gameEnded) return;
        byte[][] board = buildBoardState();
        if (checkWinner(board) != 0) {
            gameEnded = true;
            return;
        }
        if (lastBoard != null) {
            boolean yesdoit = false;
            label:
            for (int y = 0; y < 3; y++)
                for (int x = 0; x < 3; x++)
                    if (board[y][x] != lastBoard[y][x]) {
                        yesdoit = true;
                        break label;
                    }
            if (!yesdoit) return;
        }
        lastBoard = board;

        minimax(board, (byte) 1);
        if (chosePos == -1) {
            gameEnded = true;
            return;
        }
    }

    @Override
    public void drawWorld(float partialTicks) {
        super.drawWorld(partialTicks);
        if (chosePos != -1) {
            BlockPos block = board.getOffsetPointList().get(chosePos).getBlockPos(getDungeonRoom());
            RenderUtils.highlightBlock(block, new Color(0,255,255,50), partialTicks);
        }
    }

    @Override
    public void drawScreen(float partialTicks) {
        super.drawScreen(partialTicks);
        if (lastBoard == null) return;
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        for (int y = 0; y < 3; y++){
            for(int x = 0; x < 3; x++) {
                fr.drawString(lastBoard[y][x]+"", x * 2 *fr.FONT_HEIGHT, y * fr.FONT_HEIGHT, (x *3 +y) == chosePos ?0xFF00FF00 :0xFFFFFF);
            }
        }
    }

    public static class Generator implements RoomProcessorGenerator<RoomProcessorTicTacToeSolver> {
        @Override
        public RoomProcessorTicTacToeSolver createNew(DungeonRoom dungeonRoom) {
            RoomProcessorTicTacToeSolver defaultRoomProcessor = new RoomProcessorTicTacToeSolver(dungeonRoom);
            return defaultRoomProcessor;
        }
    }
}