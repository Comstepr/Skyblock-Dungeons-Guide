package kr.syeyoung.dungeonsguide.roomprocessor;

import kr.syeyoung.dungeonsguide.dungeon.data.OffsetPoint;
import kr.syeyoung.dungeonsguide.dungeon.roomfinder.DungeonRoom;
import kr.syeyoung.dungeonsguide.utils.RenderUtils;
import kr.syeyoung.dungeonsguide.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoomProcessorTrivia extends GeneralRoomProcessor {

    public RoomProcessorTrivia(DungeonRoom dungeonRoom) {
        super(dungeonRoom);
    }


    private List<String> questionDialog = new ArrayList<String>();
    private boolean questionDialogStart = false;

    private static final Map<String, String[]> answers = new HashMap<String,String[]>() {{
        put("what is the status of the watcher?", new String[]{"stalker"});
        put("what is the status of bonzo?", new String[]{"new necromancer"});
        put("what is the status of scarf?", new String[]{"apprentice necromancer"});
        put("what is the status of the professor?", new String[]{"professor"});
        put("what is the status of thorn?", new String[]{"shaman necromancer"});
        put("what is the status of livid?", new String[]{"master necromancer"});
        put("what is the status of sadan?", new String[]{"necromancer lord"});
        put("what is the status of maxor?", new String[]{"young wither"});
        put("what is the status of goldor?", new String[]{"wither soldier"});
        put("what is the status of storm?", new String[]{"elementalist"});
        put("what is the status of necron?", new String[]{"wither lord"});
        put("how many total fairy souls are there?", new String[]{"209 fairy souls"});
        put("how many fairy souls are there in spider's den?", new String[]{"17"});
        put("how many fairy souls are there in the end?", new String[]{"12"});
        put("how many fairy souls are there in the barn?", new String[]{"7"});
        put("how many fairy souls are there in mushroom desert?", new String[]{"8"});
        put("how many fairy souls are there in blazing fortress?", new String[]{"19"});
        put("how many fairy souls are there in the park?", new String[]{"11"});
        put("how many fairy souls are there in jerry's workshop?", new String[]{"5"});
        put("how many fairy souls are there in the hub?", new String[]{"79"});
        put("how many fairy souls are there in deep caverns?", new String[]{"21"});
        put("how many fairy souls are there in gold mine?", new String[]{"12"});
        put("how many fairy souls are there in dungeon hub?", new String[]{"7"});
        put("which brother is on the spider's den?", new String[]{"rick"});
        put("what is the name of rick's brother?", new String[]{"pat"});
        put("what is the name of the painter in the hub?", new String[]{"marco"});
        put("what is the name of the person that upgrades pets?", new String[]{"kat"});
        put("what is the name of the lady of the nether?", new String[]{"elle"});
        put("which villager in the village gives you a rogue sword?", new String[]{"jamie"});
        put("how many unique minions are there?", new String[]{"52"});
        put("which of these enemies does not spawn in the spider's den?", new String[]{"zombie spider","cave spider","broodfather"});
        put("which of these monsters only spawns at night?", new String[]{"zombie villager","ghast"});
        put("which of these is not a dragon in the end?", new String[]{"zoomer dragon","weak dragon","stonk dragon","holy dragon","boomer dragon","stable dragon"});
    }};
    @Override
    public void chatReceived(IChatComponent chat) {
        super.chatReceived(chat);
        String ch2 = chat.getUnformattedText();
        System.out.println(ch2 + " / "+chat.getFormattedText());
        if (chat.getFormattedText().contains("§r§6§lQuestion ")) {
            questionDialogStart = true;
        }

        if (questionDialogStart && (chat.getFormattedText().startsWith("§r       ") || chat.getFormattedText().trim().startsWith("§r§6 "))) {
            questionDialog.add(chat.getFormattedText());
        }

        if (chat.getFormattedText().contains("§r§6 ⓒ")) {
            questionDialogStart = false;
            parseDialog();
        }
    }
    public static final Pattern anwerPattern = Pattern.compile("§r§6 . §a(.+)§r");
    private void parseDialog() {
        String question = TextUtils.stripColor(questionDialog.get(1)).trim();
        String answerA = getAnswer(questionDialog.get(2));
        String answerB = getAnswer(questionDialog.get(2));
        String answerC = getAnswer(questionDialog.get(2));
        String theRealAnswer = match(question, answerA, answerB, answerC);

        if (theRealAnswer == null)
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§eDungeons Guide :::: §cCouldn't determine the answer!"));
        else
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§eDungeons Guide :::: §6"+theRealAnswer+"§f is the correct answer!"));
        correctAnswer = theRealAnswer;
    }
    String correctAnswer;

    private String getAnswer(String answerString) {
        Matcher matcher = anwerPattern.matcher(answerString.trim());
        if (!matcher.matches()) return "";
        return matcher.group(1);
    }
    private String match(String question, String a, String b, String c) {
        String[] answers = RoomProcessorTrivia.answers.get(question.toLowerCase().trim());
        if (match(answers, a)) return "A";
        if (match(answers, b)) return "B";
        if (match(answers, c)) return "C";
        return "Unknown";
    }
    private boolean match(String[] match, String match2) {
        for (String s : match) {
            if (s.equalsIgnoreCase(match2)) return true;
        }
        return false;
    }

    @Override
    public void drawWorld(float partialTicks) {
        super.drawWorld(partialTicks);
        if (correctAnswer == null) return;

        OffsetPoint op = (OffsetPoint) getDungeonRoom().getDungeonRoomInfo().getProperties().get(correctAnswer);
        if (op != null) {
            RenderUtils.highlightBlock(op.getBlockPos(getDungeonRoom()), new Color(0,255,0,50), partialTicks);
        }
    }

    public static class Generator implements RoomProcessorGenerator<RoomProcessorTrivia> {
        @Override
        public RoomProcessorTrivia createNew(DungeonRoom dungeonRoom) {
            RoomProcessorTrivia defaultRoomProcessor = new RoomProcessorTrivia(dungeonRoom);
            return defaultRoomProcessor;
        }
    }
}