import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.NPC;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;



@ScriptManifest(
        author = "gl0b0t",
        description = "uwu RANGE",
        category = Category.COMBAT,
        version = 1.0,
        name = "uwu RANGE"
)

public class Main  extends AbstractScript { /*START*/
    private static final Color ORANGE = new Color(254, 127, 0);
    private static final Color RED = new Color(254, 0, 0);
    private static final Color PINK = new Color(254, 0, 241);

    // BOW IDS
    public static final int SHORTBOW = 841;
    public static final int OAK_SHORTBOW = 843;
    public static final int WILLOW_SHORTBOW = 849;
    public static final int MAPLE_SHORTBOW = 853;
    public static final int YEW_SHORTBOW = 857;
    public static final int MAGIC_SHORTBOW = 861;


    int killFortressGuards = 1;
    int attackDelay = 650;

    private double startingRangeXP;
    private double startingRangeLVL;
    private double rangeXpHr;
    private double rangeXpGained;
    private double rangeLvlGained;
    private double startingHitPointsXP;
    private long timeBegan;
    private long timeRan;


    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private final Image paintBackground = getImage("https://i.imgur.com/MsUUNnX.png");

    public void onStart() { /*This code ONLY runs 1 time, when script is first started*/
        timeBegan = System.currentTimeMillis();
        startingHitPointsXP = Skills.getExperience(Skill.HITPOINTS);
        startingRangeXP = Skills.getExperience(Skill.RANGED);
        startingRangeLVL = Skills.getRealLevel(Skill.RANGED);
        //Starting_Defense_XP = Skills.getExperience(Skill.DEFENCE);

        checkInventoryForArrows();


    } // end of on start

    public void onExit() { /*This code ONLY runs 1 time, when script is stopped*/
        log("Bye...");
    } //end of on exit

    private enum State {
        KILL_FORTRESS_GUARD_ACTIVITY,
        KILL_CHICKEN_ACTIVITY,
        KILL_MONK_ACTIVITY,
        KILL_LESSER_DEMON_ACTIVITY,
        WAIT
    } // end of State
    private State getState() {

        // ??? see ~ 30 minutes
        if(Skills.getRealLevel(Skill.RANGED) < 25){
            return State.KILL_FORTRESS_GUARD_ACTIVITY;
        }

        if(Skills.getRealLevel(Skill.RANGED) < 40 && Skills.getRealLevel(Skill.RANGED) > 24 ){

        }
        if(Skills.getRealLevel(Skill.RANGED) < 60 && Skills.getRealLevel(Skill.RANGED) > 39 ){

        }


        return State.WAIT;
    } // end of getstate
    public void Check_Run() {
        if ((Walking.getRunEnergy() > Calculations.random(8, 16) ) && !Walking.isRunEnabled()){
            Walking.toggleRun();
        }
    }
    public void walkToGrandExchange() {
        Check_Run();
        Walking.walkExact(GL0ZZ3N_AREAS.GE.getRandomTile());
        sleep(Calculations.random(1500, 4000));
    }

    public void checkInventoryForArrows() {
        log("checkInventoryForArrows()");

        if (!Equipment.contains(x -> x.getName().contains("arrow"))
                && Inventory.contains(x -> x.getName().contains("arrow"))) {
            log("uwu! No Arrows Equipped, But We have Arrows In Inventory!");
            Inventory.get(item -> item.getName().contains("arrow")).interact("Wield");
            sleep(1200,6000);
        }

        if (!Equipment.contains(x -> x.getName().contains("arrow"))
                && !Inventory.contains(x -> x.getName().contains("arrow"))) {
            if (!GL0ZZ3N_AREAS.GE.contains(Players.localPlayer())) {
                walkToGrandExchange();
                return;
            }
            // todo do ge?
            log("uwu! We have no arrows - Stopping Script!");
            stop();
        }
    }

    public void CheckEquipment() {

        if (!Equipment.contains("Amulet of power") && Inventory.contains("Amulet of power"))   {
            log("uwu! We Put On Amulet Of Power!");
            Inventory.get("Amulet of power").interact("Wear");
            sleep(1200,6000);
        }

        if ( (!Equipment.contains(x -> x.getName().contains("boots"))) && (Inventory.contains(x -> x.getName().contains("boots")))  ) {
            log("uwu! We Put On Boots!");
            Inventory.get(item -> item.getName().contains("boots")).interact("Wear");
            sleep(1200,6000);
        }

        /*
        magic shortbow <= 50
        yew shortbow <= 40
        maple <= 30
        willow <= 20
        oak <= 5
        shortbow
         */
        int appropriateBowID = getAppropriateBow();
        if (!Equipment.contains(appropriateBowID) && Inventory.contains(appropriateBowID)) {
            Inventory.interact(appropriateBowID, "Wield");
            return;
        }

        // bows end here

        if((Skills.getRealLevel(Skill.RANGED) < 5) &&
                (Skills.getRealLevel(Skill.DEFENCE) < 10) &&
                (!Equipment.contains("Leather body") &&
                        (Inventory.contains("Leather body")))) {
            Inventory.get("Leather body").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) < 5) &&
                (Skills.getRealLevel(Skill.DEFENCE) > 9) &&
                (!Equipment.contains("Hardleather body") &&
                        (Inventory.contains("Hardleather body")))) {
            Inventory.get("Hardleather body").interact("Wear");
        }
        if((Skills.getRealLevel(Skill.RANGED) > 19) &&
                (Skills.getRealLevel(Skill.DEFENCE) > 19) &&
                (!Equipment.contains("Studded body") &&
                        (Inventory.contains("Studded body")))) {
            Inventory.get("Studded body").interact("Wear");
        }
        // bodys end
        // vambraces start
        if((Skills.getRealLevel(Skill.RANGED) < 39)
                && (!Equipment.contains("Leather vambraces") &&
                        (Inventory.contains("Leather vambraces")))) {
            Inventory.get("Leather vambraces").interact("Wear");
        }
        if((Skills.getRealLevel(Skill.RANGED) > 39) &&
                (Skills.getRealLevel(Skill.RANGED) < 50) &&
                (!Equipment.contains("Green d'hide vambraces") &&
                        (Inventory.contains("Green d'hide vambraces")))) {
            Inventory.get("Green d'hide vambraces").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) > 49) &&
                (Skills.getRealLevel(Skill.RANGED) < 60) &&
                (!Equipment.contains("Blue d'hide vambraces") &&
                        (Inventory.contains("Blue d'hide vambraces")))) {
            Inventory.get("Blue d'hide vambraces").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) > 59) &&
                (Skills.getRealLevel(Skill.RANGED) < 70) &&
                (!Equipment.contains("Red d'hide vambraces") &&
                        (Inventory.contains("Red d'hide vambraces")))) {
            Inventory.get("Red d'hide vambraces").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) > 69) &&
                (!Equipment.contains("Black d'hide vambraces") &&
                        (Inventory.contains("Black d'hide vambraces")))) {
            Inventory.get("Black d'hide vambraces").interact("Wear");

        }
        // vambs end
        // chaps start
        if((Skills.getRealLevel(Skill.RANGED) < 20) &&
                (!Equipment.contains("Leather chaps") &&
                        (Inventory.contains("Leather chaps")))) {
            Inventory.get("Leather chaps").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) > 19) &&
                (Skills.getRealLevel(Skill.RANGED) < 39) &&
                (!Equipment.contains("Studded chaps") &&
                        (Inventory.contains("Studded chaps")))) {
            Inventory.get("Studded chaps").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) > 39) &&
                (Skills.getRealLevel(Skill.RANGED) < 50) &&
                (!Equipment.contains("Green d'hide chaps") &&
                        (Inventory.contains("Green d'hide chaps")))) {
            Inventory.get("Green d'hide chaps").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) > 49)
                && Skills.getRealLevel(Skill.RANGED) < 60
                && !Equipment.contains("Blue d'hide chaps")
                && Inventory.contains("Blue d'hide chaps")) {
            Inventory.get("Blue d'hide chaps").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) > 59) &&
                (Skills.getRealLevel(Skill.RANGED) < 70) &&
                (!Equipment.contains("Red d'hide chaps") &&
                        (Inventory.contains("Red d'hide chaps")))) {
            Inventory.get("Red d'hide chaps").interact("Wear");

        }
        if((Skills.getRealLevel(Skill.RANGED) > 69) &&
                (!Equipment.contains("Black d'hide chaps") &&
                        (Inventory.contains("Black d'hide chaps")))) {
            Inventory.get("Black d'hide chaps").interact("Wear");

        }
    }

    public int antiBanTask;
    public int randomTabToOpen;
    public int randomSkillToCheck;
    public int mouseOrHotKey;
    public int shouldClickSkill;

    public void antiBan() {
        log("Anti-Ban.");

        if (antiBanTask == 1) {
            log("Moving Mouse Outside Of Window.");
            if(Mouse.isMouseInScreen()) {
                Mouse.moveMouseOutsideScreen();
                sleep(Calculations.random(5000, 20000));
            }
        } // Move Mouse Outside Of Window

        if (antiBanTask == 2) {
            log("Examine Closest Entity.");
            Entity RandomEntity = GameObjects.closest(n -> n != null && !n.getName().equals("null"));
            if (RandomEntity != null) {
                RandomEntity.interact("Examine");
            }
        } // Examine Closest Entity

        if (antiBanTask == 3) {
            log("Examine Closest NPC.");
            NPC RandomNPC = NPCs.closest(n -> n != null && !n.getName().equals("null"));
            if (RandomNPC != null) {
                RandomNPC.interact("Examine");
            }
        } // Examine Closest NPC

        if (antiBanTask == 4) {
            log("Examine Closest Ground Item.");
            Entity RandomItem = GroundItems.closest(i -> i != null && !i.getName().equals("null"));;
            if (RandomItem != null) {
                RandomItem.interact("Examine");
            }

        } // Examine Closest Ground Item

        if (antiBanTask == 5) {
            log("Opening Random Tab.");
            if (randomTabToOpen == 1) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.ACCOUNT_MANAGEMENT);
                } else {
                    Tabs.openWithFKey(Tab.ACCOUNT_MANAGEMENT);
                }

            } // OPEN TAB - ACCOUNT MANAGEMENT
            if (randomTabToOpen == 2) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.CLAN);
                } else {
                    Tabs.openWithFKey(Tab.CLAN);
                }
            } // OPEN TAB - CLAN
            if (randomTabToOpen == 3) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.COMBAT);
                } else {
                    Tabs.openWithFKey(Tab.COMBAT);
                }
            } // OPEN TAB - COMBAT
            if (randomTabToOpen == 4) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.EMOTES);
                } else {
                    Tabs.openWithFKey(Tab.EMOTES);
                }
            } // OPEN TAB - EMOTES
            if (randomTabToOpen == 5) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.EQUIPMENT);
                } else {
                    Tabs.openWithFKey(Tab.EQUIPMENT);
                }
            } // OPEN TAB - EQUIPMENT
            if (randomTabToOpen == 6) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.FRIENDS);
                } else {
                    Tabs.openWithFKey(Tab.FRIENDS);
                }
            } // OPEN TAB - FRIENDS
            if (randomTabToOpen == 7) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.INVENTORY);
                } else {
                    Tabs.openWithFKey(Tab.INVENTORY);
                }
            } // OPEN TAB - INVENTORY
            if (randomTabToOpen == 8) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.MAGIC);
                } else {
                    Tabs.openWithFKey(Tab.MAGIC);
                }
            } // OPEN TAB - MAGIC
            if (randomTabToOpen == 9) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.MUSIC);
                } else {
                    Tabs.openWithFKey(Tab.MUSIC);
                }
            } // OPEN TAB - MUSIC
            if (randomTabToOpen == 10) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.OPTIONS);
                } else {
                    Tabs.openWithFKey(Tab.OPTIONS);
                }
            }// OPEN TAB - OPTIONS
            if (randomTabToOpen == 11) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.PRAYER);
                } else {
                    Tabs.openWithFKey(Tab.PRAYER);
                }
            }// OPEN TAB - PRAYER
            if (randomTabToOpen == 12) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.QUEST);
                } else {
                    Tabs.openWithFKey(Tab.QUEST);
                }
            } // OPEN TAB - QUEST
            if (randomTabToOpen == 13) {
                if (mouseOrHotKey == 1) {
                    Tabs.openWithMouse(Tab.SKILLS);
                } else {
                    Tabs.openWithFKey(Tab.SKILLS);
                }
            } // OPEN TAB - SKILLS
        } // Flip To A Random Tab

        if (antiBanTask == 6) {
            log("Checking Random Skill.");
            randomSkillToCheck = Calculations.random(1,15);
            int x = Calculations.random(0, 25);
            int y = Calculations.random(0, 15);

            if (!Tabs.isOpen(Tab.SKILLS)) {
                Tabs.openWithMouse(Tab.SKILLS);
            }
            if (randomSkillToCheck != 0) {

                if (randomSkillToCheck == 1) {
                    Point p = new Point(550, 210); // Attack
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);


                }
                if (randomSkillToCheck == 2) {
                    Point p = new Point(550, 240); // Strength
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 3) {
                    Point p = new Point(550, 270); // Defence
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 4) {
                    Point p = new Point(612, 210); // Hitpoints
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 5) {
                    Point p = new Point(550, 304); // Range
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 6) {
                    Point p = new Point(550, 370); // Mage
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 7) {
                    Point p = new Point(550, 336); // Prayer
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 8) {
                    Point p = new Point(550, 400); // Runecrafting
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 9) {
                    Point p = new Point(612, 337); // Crafting
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 10) {
                    Point p = new Point(675, 210); // Mining
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 11) {
                    Point p = new Point(675, 240); // Smithing
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 12) {
                    Point p = new Point(675, 273); // Fishing
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 13) {
                    Point p = new Point(675, 304); // Cooking
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 14) {
                    Point p = new Point(675, 336); // Firemaking
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
                if (randomSkillToCheck == 15) {
                    Point p = new Point(675, 368); // Woodcutting
                    p.setLocation(p.getX() + x, p.getY() + y);
                    Mouse.move(p);

                }
            } // Set point to move mouse
            sleep(Calculations.random(1000, 45000));

        } // Check A Random Skill

    }

    NPC fortressGuard = NPCs.closest(npc -> npc.getName().equals("Fortress Guard")
            && !npc.isInCombat() && (npc.distance(getLocalPlayer().getTile()) < 9)) ;

    NPC chicken = NPCs.closest(npc -> npc.getName().equals("Chicken")
            && !npc.isInCombat() && (npc.distance(getLocalPlayer().getTile()) < 9)) ;

    NPC monk = NPCs.closest(npc -> npc.getName().equals("Monk")
            && !npc.isInCombat() && (npc.distance(getLocalPlayer().getTile()) < 9)) ;

    NPC lesserDemon = NPCs.closest(npc -> npc.getName().equals("Lesser Demon")
            && !npc.isInCombat() && (npc.distance(getLocalPlayer().getTile()) < 9)) ;


    @Override
    public int onLoop() {

        antiBanTask = Calculations.random(1, 50);
        randomTabToOpen = Calculations.random(1, 13);
        mouseOrHotKey = Calculations.random(1, 2);
        killFortressGuards = 1;


        /////COMBAT OPTIONS


        switch (getState()) {

            //Shortbow - Max Distance = 5 / 7 if using Longrange with Defence

            case KILL_FORTRESS_GUARD_ACTIVITY:


                if (!getLocalPlayer().isInCombat()) {


                    if (Equipment.contains(x -> x.getName().contains("arrow"))) {
                        if (GL0ZZ3N_AREAS.ICE_MOUNTAIN_SAFE_SPOT.contains(getLocalPlayer())) {
                            if ((GL0ZZ3N_AREAS.BLACK_KNIGHT_FORTRESS_ENTRANCE.contains(fortressGuard))) {
                                fortressGuard.interact("Attack");
                                rangeXpHr = (int) (rangeXpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
                                log("uwu! Attacking Fortress Guard!");
                                return (Calculations.random(750, 900));
                            } else {
                                antiBan();
                            }
                        } else {
                            Check_Run();
                            log("uwu! Travelling To Ice Mountain!");
                            Walking.walkExact(GL0ZZ3N_AREAS.ICE_MOUNTAIN_SAFE_SPOT.getRandomTile());
                            sleep(Calculations.random(1500, 2800));
                            return (Calculations.random(10, 50));

                        }
                    } else {
                        log("uwu! Checking For Arrows!");
                        checkInventoryForArrows();
                    }

                }

                if (getLocalPlayer().isInCombat()) {
                    if (Dialogues.canContinue()) {
                        log("UWU! - We Gained A Level");
                        Dialogues.continueDialogue();
                        return (Calculations.random(10, 50));
                    }
                    if (!GL0ZZ3N_AREAS.ICE_MOUNTAIN_SAFE_SPOT.contains(getLocalPlayer())) {
                        Check_Run();
                        Walking.walkExact(GL0ZZ3N_AREAS.ICE_MOUNTAIN_SAFE_SPOT.getRandomTile());
                        sleep(Calculations.random(1500, 2800));
                        return (Calculations.random(10, 50));
                    }

                }
                
                break;

            case KILL_CHICKEN_ACTIVITY:

                if (!getLocalPlayer().isInCombat()) {


                    if (Equipment.contains(x -> x.getName().contains("arrow"))) {
                        if (GL0ZZ3N_AREAS.FALADOR_FARM_CHICKENS.contains(getLocalPlayer())) {
                            if ((GL0ZZ3N_AREAS.FALADOR_FARM_CHICKENS.contains(chicken))) {
                                chicken.interact("Attack");
                                rangeXpHr = (int) (rangeXpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
                                log("uwu! Attacking Chicken!");
                                return (Calculations.random(750, 900));
                            } else {
                                antiBan();
                            }
                        } else {
                            Check_Run();
                            log("uwu! Travelling To Falador Farm Chickens!");
                            Walking.walkExact(GL0ZZ3N_AREAS.FALADOR_FARM_CHICKENS.getRandomTile());
                            sleep(Calculations.random(1500, 2800));
                            return (Calculations.random(10, 50));

                        }
                    } else {
                        log("uwu! Checking For Arrows!");
                        checkInventoryForArrows();
                    }

                }

                if (getLocalPlayer().isInCombat()) {
                    if (Dialogues.canContinue()) {
                        log("UWU! - We Gained A Level");
                        Dialogues.continueDialogue();
                        return (Calculations.random(10, 50));
                    }
                    if (!GL0ZZ3N_AREAS.FALADOR_FARM_CHICKENS.contains(getLocalPlayer())) {
                        Check_Run();
                        Walking.walkExact(GL0ZZ3N_AREAS.FALADOR_FARM_CHICKENS.getRandomTile());
                        sleep(Calculations.random(1500, 2800));
                        return (Calculations.random(10, 50));
                    }

                }
                break;
            case KILL_MONK_ACTIVITY:

                if (!getLocalPlayer().isInCombat()) {


                    if (Equipment.contains(x -> x.getName().contains("arrow"))) {
                        if (GL0ZZ3N_AREAS.MONASTERY.contains(getLocalPlayer())) {
                            if ((GL0ZZ3N_AREAS.MONASTERY.contains(monk))) {
                                monk.interact("Attack");
                                rangeXpHr = (int) (rangeXpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
                                log("uwu! Attacking Monk!");
                                return (Calculations.random(750, 900));
                            } else {
                                antiBan();
                            }
                        } else {
                            Check_Run();
                            log("uwu! Travelling To Monastery!");
                            Walking.walkExact(GL0ZZ3N_AREAS.MONASTERY.getRandomTile());
                            sleep(Calculations.random(1500, 2800));
                            return (Calculations.random(10, 50));

                        }
                    } else {
                        log("uwu! Checking For Arrows!");
                        checkInventoryForArrows();
                    }

                }

                if (getLocalPlayer().isInCombat()) {
                    if (Dialogues.canContinue()) {
                        log("UWU! - We Gained A Level");
                        Dialogues.continueDialogue();
                        return (Calculations.random(10, 50));
                    }
                    if (!GL0ZZ3N_AREAS.MONASTERY.contains(getLocalPlayer())) {
                        Check_Run();
                        Walking.walkExact(GL0ZZ3N_AREAS.MONASTERY.getRandomTile());
                        sleep(Calculations.random(1500, 2800));
                        return (Calculations.random(10, 50));
                    }

                }
                break;
            case KILL_LESSER_DEMON_ACTIVITY:

                if (!getLocalPlayer().isInCombat()) {


                    if (Equipment.contains(x -> x.getName().contains("arrow"))) {
                        if (GL0ZZ3N_AREAS.ICE_MOUNTAIN_SAFE_SPOT.contains(getLocalPlayer())) {
                            if ((GL0ZZ3N_AREAS.BLACK_KNIGHT_FORTRESS_ENTRANCE.contains(fortressGuard))) {
                                fortressGuard.interact("Attack");
                                rangeXpHr = (int) (rangeXpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
                                log("uwu! Attacking Fortress Guard!");
                                return (Calculations.random(750, 900));
                            } else {
                                antiBan();
                            }
                        } else {
                            Check_Run();
                            log("uwu! Travelling To Ice Mountain!");
                            Walking.walkExact(GL0ZZ3N_AREAS.ICE_MOUNTAIN_SAFE_SPOT.getRandomTile());
                            sleep(Calculations.random(1500, 2800));
                            return (Calculations.random(10, 50));

                        }
                    } else {
                        log("uwu! Checking For Arrows!");
                        checkInventoryForArrows();
                    }

                }

                if (getLocalPlayer().isInCombat()) {
                    if (Dialogues.canContinue()) {
                        log("UWU! - We Gained A Level");
                        Dialogues.continueDialogue();
                        return (Calculations.random(10, 50));
                    }
                    if (!GL0ZZ3N_AREAS.ICE_MOUNTAIN_SAFE_SPOT.contains(getLocalPlayer())) {
                        Check_Run();
                        Walking.walkExact(GL0ZZ3N_AREAS.ICE_MOUNTAIN_SAFE_SPOT.getRandomTile());
                        sleep(Calculations.random(1500, 2800));
                        return (Calculations.random(10, 50));
                    }

                }
                break;


            case WAIT:
                return Calculations.random(10, 50);
        }
        return Calculations.random(10, 50);
    } // end of onLoop

    int LEFT_ALLIGN = 280;
    int BOTTOM_ALLIGN= 475;
    public void onPaint(Graphics g) {
        timeRan = System.currentTimeMillis() - this.timeBegan;
        rangeXpGained = (Skills.getExperience(Skill.RANGED) - startingRangeXP);
        rangeLvlGained = (Skills.getRealLevel(Skill.RANGED) - startingRangeLVL);



        if (paintBackground != null) {
            //g.drawImage(paintBackground, 1, 338, null);
            g.drawImage(paintBackground, 0, 0, null);
        }


        DecimalFormat df = new DecimalFormat("###,###,###");
        g.setColor(PINK);
        g.setFont(new Font( "Copperplate Gothic",Font.BOLD, 18));

        g.drawString(ft(timeRan), LEFT_ALLIGN, 415);

        g.drawString("" + df.format(rangeXpGained) + "  " +"(" + df.format(rangeXpHr) +" xp/hr)", LEFT_ALLIGN, 445);

        g.drawString("" + df.format(rangeLvlGained), LEFT_ALLIGN, BOTTOM_ALLIGN);




    } // end of onPaint
    private String ft(long duration) {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0) {
            res = (hours + ":" + minutes + ":" + seconds);

        } else {
            res = (days + ":" + hours + ":" + minutes + ":" + seconds);
        }
        return res;
    }

    /**
     *
     * @return the appropriate bow for your level
     */
    private int getAppropriateBow() {
        /*
        magic shortbow <= 50
        yew shortbow <= 40
        maple <= 30
        willow <= 20
        oak <= 5
        shortbow
         */
        int lvl = Skills.getRealLevel(Skill.RANGED);
        if (lvl >= 50) return MAGIC_SHORTBOW;
        if (lvl >= 40) return YEW_SHORTBOW;
        if (lvl >= 30) return MAPLE_SHORTBOW;
        if (lvl >= 20) return WILLOW_SHORTBOW;
        if (lvl >= 5) return OAK_SHORTBOW;
        return SHORTBOW;
    }
}/*END*/


