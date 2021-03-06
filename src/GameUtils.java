import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameUtils {


    public ArrayList<Card> currentdeck;
    public ArrayList<Card> inventory;
    public ArrayList<Card> overall;
    private int multiplier;
    private Card selected;

    public GameUtils() {
        currentdeck = new ArrayList<Card>() {{
            add(new Card("Wizard", 5, 1, "Basic", "A human with magical abilities.\nKnown to eat sandwiches from time to time.", 1));
            add(new Card("Fire", 3, 0, "Basic", "A very hot gas/fuel substance\nthat can cause severe burns.", 1));
            add(new Card("Water", 2, 0, "Basic", "Essential for life, no \ndefinite volume.", 1));
            add(new Card("Fairy", 4, 2, "Basic", "A tiny magical human that can \nfly.", 1));
            add(new Card("Electricity", 5, 0, "Basic", "A form of energy resulting \nfrom charged particles.", 1));
        }};
        overall = new ArrayList<>(currentdeck);
        inventory = new ArrayList<>(currentdeck);
    }

    public void addCard(ArrayList<Card> deck, String name, int damage, int block, String cardType, String lore) {
        deck.add(new Card(name, damage, block, cardType, lore, 1));
    }

    public void listcards(ArrayList<Card> deck) throws java.lang.Exception {
        if(deck.size() == 0) {
            Main.TypeLine(Main.ANSI_RED + "You do not have any cards in that deck." + Main.ANSI_RESET);
            return;
        }
        System.out.println(Main.ANSI_GREEN + "------------------");
        for(Card item : deck) {
            Main.TypeLine(item.getName() + "\n");
        }
        System.out.println("------------------" + Main.ANSI_RESET);
    }

    public void combineCards(Card card1, Card card2) throws java.lang.Exception {
        String combdata = Main.combofile;
        String line;
        ArrayList<String> data = new ArrayList<>();

        try(FileReader fr = new FileReader(combdata)) {
            BufferedReader br = new BufferedReader(fr);
            while((line = br.readLine()) != null) {
                data.add(line);
            }
        }
        for(String item : data) {
            if(item.contains(card1.getName() + " ## " + card2.getName()) || item.contains(card2.getName() + " ## " + card1.getName())) {
                String[] getdata = item.split(" = ");
                String[] getdata2 = getdata[1].split(" ");
                String[] getdata3 = getdata[1].split(" \"");
                Scanner in = new Scanner(System.in);
                String ph;

                Main.TypeLine(Main.ANSI_YELLOW + "Forging these two cards costs " + getdata2[1] + " dust. Continue? [y/n] : " + Main.ANSI_RESET);
                ph = in.nextLine();
                if(ph.equals("y")) {

                } else {
                    return;
                }

                if(Integer.parseInt(getdata2[0]) > Main.playerlevel) {
                    Main.TypeLine(Main.ANSI_RED + "You need to become player level " + getdata2[0] + " to combine those cards.");
                    return;
                } else if(Main.dust < Integer.parseInt(getdata2[1])) {
                    Main.TypeLine(Main.ANSI_RED + "You do not have enough dust. (" + getdata2[1] + " needed)" + Main.ANSI_RESET);
                    return;
                } else {
                    addCard(inventory, getdata2[2], Integer.parseInt(getdata2[3]), Integer.parseInt(getdata2[4]), getdata2[5], getdata3[1]);
                    addCard(overall, getdata2[2], Integer.parseInt(getdata2[3]), Integer.parseInt(getdata2[4]), getdata2[5], getdata3[1]);
                    for(Card x : inventory) {
                        if(x.getName().equals(getdata2[2])) {
                            Main.dust -= Integer.parseInt(getdata2[1]);
                            Main.TypeLine(Main.ANSI_GREEN + "Successfully forged " + getdata2[2] + ".\n" + Main.ANSI_RESET);
                            Main.TypeLine(Main.ANSI_GREEN + "You now have " + Main.dust + " dust.\n" + Main.ANSI_RESET);
                            x.getFullStats();
                            Main.TypeLine(Main.ANSI_GREEN + "Do you want to add " + getdata2[2] + " to your battle deck? [y/n] : " + Main.ANSI_RESET);
                            ph = in.nextLine();
                            if(ph.equals("y")) {
                                currentdeck.add(new Card(getdata2[2],  Integer.parseInt(getdata2[3]), Integer.parseInt(getdata2[4]), getdata2[5], getdata3[1], 1));
                            } else {

                            }
                            break;
                        }
                    }
                }
                return;
            }
        }
        Main.TypeLine(Main.ANSI_RED + "Those two cards cannot be combined.\n" + Main.ANSI_RESET);
    }

    public void upgradeCard(String name) throws java.lang.Exception {
        Scanner in = new Scanner(System.in);
        String ph;
        for(Card item : overall) {
            if(item.getName().toLowerCase().equals(name.toLowerCase())) {
                switch(item.getCardType()) {
                    case "Basic":
                        multiplier = 8;
                        break;
                    case "Magic":
                        multiplier = 10;
                        break;
                    case "Science":
                        multiplier = 9;
                        break;
                    case "Life":
                        multiplier = 10;
                        break;
                }
                Main.TypeLine(Main.ANSI_YELLOW + "Upgrading " + item.getName() + " costs " + (item.getLevel() * multiplier) + " dust. Continue? [y/n] : " + Main.ANSI_RESET);
                ph = in.nextLine();
                if(ph.equals("y")) {
                    if(item.getLevel()*multiplier > Main.dust) {
                        Main.TypeLine(Main.ANSI_RED + "Not enough dust." + Main.ANSI_RESET);
                        return;
                    }
                    Main.dust -= item.getLevel() * multiplier;
                    item.setLevel(item.getLevel() + 1);
                    Main.TypeLine(Main.ANSI_GREEN + "Successfully upgraded " + item.getName() + " to level " + item.getLevel() + ".\nYou now have " + Main.dust + " dust left.\nAfter Upgrade: \n" + Main.ANSI_RESET);
                    item.setDamage(item.getDamage() + 1);
                    item.setBlock(item.getBlock() + 1);
                    for(Card part : currentdeck) {
                        if(part.getName().equals(item.getName())) {
                            part.setDamage(item.getDamage());
                            part.setBlock(item.getBlock());
                            part.setLevel(item.getLevel());
                        }
                    }
                    for(Card element : inventory) {
                        if(element.getName().equals(item.getName())) {
                            element.setDamage(item.getDamage());
                            element.setBlock(item.getBlock());
                            element.setLevel(item.getLevel());
                        }
                    }
                    item.getFullStats();
                    return;
                } else {
                    return;
                }
            }
        }
        Main.TypeLine(Main.ANSI_RED + "The card specified is not in your inventory/Battle deck, or does not exist." + Main.ANSI_RESET);
    }

    void modifyDeck() throws java.lang.Exception {
        Scanner in = new Scanner(System.in);
        String ph;
        Main.TypeLine(Main.ANSI_YELLOW + "To add a card from inventory to battle deck, type 'ADD (cardName)'\nTo remove a card from battledeck, type 'REMOVE (cardName)'\n" +
                "To see battle deck, type 'b'\nTo see inventory, type 'i'\nTo quit, type 'q'\n" + Main.ANSI_RESET);
        while(true) {
            Main.TypeLine(Main.ANSI_YELLOW + "\n>> " + Main.ANSI_RESET);
            ph = in.nextLine();
            if(ph.toLowerCase().equals("b")) {
                listcards(currentdeck);
            } else if(ph.toLowerCase().equals("i")) {
                listcards(inventory);
            } else if(ph.toLowerCase().equals("q")) {
                return;
            } else if(ph.length() > 1) {
                if(ph.substring(0,3).toLowerCase().equals("add")) {
                    if(isCard(ph.substring(4), overall)) {
                        if(isCard(ph.substring(4), currentdeck)) {
                            Main.TypeLine(Main.ANSI_RED + "This card is already in the battle deck." + Main.ANSI_RESET);
                        } else {
                            currentdeck.add(toCard(ph.substring(4), overall));
                        }
                    } else {
                        Main.TypeLine(Main.ANSI_RED + "That card does not exist." + Main.ANSI_RESET);
                    }

                } else if(ph.substring(0,6).toLowerCase().equals("remove")) {
                    if(isCard(ph.substring(7), overall)) {
                        if(isCard(ph.substring(7), currentdeck)) {
                            currentdeck.remove(toCard(ph.substring(7), currentdeck));
                        } else {
                            Main.TypeLine(Main.ANSI_RED + "This card is not in your battle deck." + Main.ANSI_RESET);
                        }
                    } else {
                        Main.TypeLine(Main.ANSI_RED + "That card does not exist." + Main.ANSI_RESET);
                    }
                }
            }
        }
    }

    void playlevel() throws java.lang.Exception {
        Scanner in = new Scanner(System.in);
        String ph;
        String[] enemyCardNames = {"Wizard", "Electricity", "Fairy", "Fire", "Water", "Pyromancer", "Hydromancer", "Knight", "Snake", "Illusion", "Elf", "Arch Mage", "Golem", "Death", "Angel",
                "Necromancer", "Guardian", "Nightmare", "Horse", "Troll", "Dark Mage", "Superhero"};
        String[] enemyNames = {"Dugthomoth the Demon", "Brag'than the Human", "Thoz'gog the Werewolf", "Orzok the Ghost", "Sauron the Eyeball", "Vor'an the Witch", "Hobnigs the Goblin", "Zoggarth the Elf",
                "Mel the Troll", "Edzar the Human", "Eddrin the Dragon", "Tehung the Whale", "Pyragon the Warlock", "Cherdir the Dragon", "VyKun the Scientist"};
        String activeName = enemyNames[Main.playGamelevel - 1];
        int[] enemyCardAttack = new int[enemyCardNames.length];
        int[] enemyCardBlock = new int[enemyCardNames.length];
        Card[] enemyCards = new Card[enemyCardNames.length];
        int enemyHP = 40;

        Card[] playerDeck = new Card[3];

        for(int x = 0; x < enemyCardNames.length; x++) {
            int randomNum = ThreadLocalRandom.current().nextInt(Main.playerlevel + 1,  Main.playerlevel + 4);
            enemyCardAttack[x] = randomNum;
        }
        for(int y = 0; y < enemyCardNames.length; y++) {
            int randomNum = ThreadLocalRandom.current().nextInt(0,  Main.playerlevel + 2);
            enemyCardBlock[y] = randomNum;
        }
        for(int z = 0; z < enemyCardNames.length; z++) {
            enemyCards[z] = new Card(enemyCardNames[z], enemyCardAttack[z], enemyCardBlock[z], "???", "???", 1);
        }
        Main.TypeLine(Main.ANSI_GREEN + Main.ANSI_BOLD + "LEVEL " + Main.playGamelevel + "\n" + Main.ANSI_RESET);
        Main.TypeLine(Main.ANSI_GREEN + "You are fighting " + activeName + " (40 HP).\nYou will choose 1 of 3 cards in your battle deck to play\nagainst " + activeName + " repeatedly, and the first to bring the other's" +
                "\nhealth points to 0, wins. (Type 's' to surrender) \n(You start with 40 HP)\n" + Main.ANSI_RESET);
        int check = 0;
        while(true) {
            Main.TypeLine(Main.ANSI_BLUE + "Enter to Continue... " + Main.ANSI_RESET);
            in.nextLine();
            refreshCards(playerDeck);
            do {
                Main.TypeLine(Main.ANSI_BOLD + Main.ANSI_PURPLE + "\nSelect a card (from above) to play: " + Main.ANSI_RESET);
                ph = in.nextLine();
                if(ph.equals("s")) {
                    Main.TypeLine(Main.ANSI_RED + activeName + " has defeated you! Good luck next time." + Main.ANSI_RESET);
                    return;
                }
            } while(!isCard(ph, new ArrayList<Card>(Arrays.asList(playerDeck))));
            for(Card item : playerDeck) {
                if(item.getName().toLowerCase().equals(ph.toLowerCase())) {
                    selected = item;
                    check = 4;
                    break;
                } else {
                    check++;
                }
            }
            int rnd = new Random().nextInt(enemyCards.length);
            Card enemyCurrent = enemyCards[rnd];
            Main.TypeLine(Main.ANSI_GREEN + "You play " + Main.ANSI_BOLD + selected.getName() + Main.ANSI_RESET + Main.ANSI_GREEN + " || Damage = " + selected.getDamage() + " || DmgBlock = " + selected.getBlock() + "\n" + Main.ANSI_RESET);
            Main.TypeLine(Main.ANSI_PURPLE + activeName + " plays " + Main.ANSI_BOLD + enemyCurrent.getName() + Main.ANSI_RESET + Main.ANSI_PURPLE + " || Damage = " + enemyCurrent.getDamage() + " || DmgBlock = " + enemyCurrent.getBlock() + "\n" + Main.ANSI_RESET);
            TimeUnit.SECONDS.sleep(1);
            int enemyToDamage = selected.getDamage() - enemyCurrent.getBlock();
            if(enemyToDamage < 0) enemyToDamage = 0;
            int playerToDamage = enemyCurrent.getDamage() - selected.getBlock();
            if(playerToDamage < 0) playerToDamage = 0;

            Main.TypeLine(Main.ANSI_BLUE + activeName + " : -" + enemyToDamage + " Hit Points\n");
            Main.TypeLine("Player : -" + playerToDamage + " Hit Points\n");
            Main.playerHP -= playerToDamage;
            enemyHP -= enemyToDamage;
            displayHealth("Player", Main.playerHP);
            displayHealth(activeName, enemyHP);
            if(Main.playerHP <= 0) {
                Main.TypeLine(Main.ANSI_RED + activeName + " won the round! Good luck next time.\n" + Main.ANSI_RESET);
                return;
            } else if(enemyHP <= 0) {
                Main.TypeLine(Main.ANSI_GREEN + "You have defeated " + activeName + "! Earned 10 dust, and 15 EXP\n");
                Main.dust += 10;
                Main.playGamelevel += 1;
                Main.playerEXP += 15;
                Main.playerHP = 40;
                if(Main.playerEXP >= 50) {
                    Main.TypeLine(Main.ANSI_YELLOW + "Player level increased to " + (Main.playerlevel + 1) + ".\n" + Main.ANSI_RESET);
                    Main.playerlevel += 1;
                }
                return;
            }

        }
    }

    void refreshCards(Card[] playerDeck) throws java.lang.Exception {
        int y = 0;
        while(true) {
            int rnd = new Random().nextInt(currentdeck.size());
            if(Arrays.asList(playerDeck).contains(currentdeck.get(rnd))) {
                rnd = new Random().nextInt(currentdeck.size());
            }
            playerDeck[y] = currentdeck.get(rnd);
            playerDeck[y].getFullStats();
            TimeUnit.MILLISECONDS.sleep(500);
            y++;
            if(y == 3) {
                break;
            }
        }
    }

    void displayHealth(String who, int health) throws java.lang.Exception {
        Main.TypeLine(Main.ANSI_YELLOW + who + "'s HP : " + Main.ANSI_RESET);
        for(int x = 1; x < health + 1; x++) {
            System.out.print(Main.ANSI_BLACK + "=" + Main.ANSI_RESET);
        }
        for(int y = 1; y < (40 - health) + 1; y++) {
            System.out.print(Main.ANSI_BLACK + "-" + Main.ANSI_RESET);
        }
        System.out.print(Main.ANSI_GREEN + " (" + health + ") \n" + Main.ANSI_RESET);
    }

     boolean isCard(String ph, ArrayList<Card> deck) {
        for(Card item : deck) {
            if(item.getName().toLowerCase().equals(ph.toLowerCase())) {
                return true;
            }
        }
        return false;
     }

     Card toCard(String ph, ArrayList<Card> deck) {
        for(Card item : deck) {
            if(item.getName().toLowerCase().equals(ph.toLowerCase())) {
                return item;
            }
        }
        return new Card("Blank", 0, 0, "Blank", "Blank", 0);
     }
}
