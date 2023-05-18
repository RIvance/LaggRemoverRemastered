package club.ifcserver.laggremover.main;

import club.ifcserver.laggremover.api.aparser.AnfoParser;
import club.ifcserver.laggremover.api.proto.DelayedLRProtocolResult;
import club.ifcserver.laggremover.api.proto.LRProtocol;
import club.ifcserver.laggremover.api.proto.LRProtocolResult;
import club.ifcserver.laggremover.api.proto.Protocol;
import club.ifcserver.laggremover.inf.Help;
import club.ifcserver.laggremover.proto.bin.CCEntities;
import club.ifcserver.laggremover.proto.bin.CCItems;
import club.ifcserver.laggremover.proto.bin.LRGC;
import club.ifcserver.laggremover.util.BitString;
import club.ifcserver.laggremover.util.DoubleVar;
import club.ifcserver.laggremover.util.DrewMath;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/main/LRCommand.class */
public class LRCommand {
    public static boolean onCommand(final Player p, String[] args) {
        World w;
        String raw_fin;
        World w2;
        EntityType[] ents;
        World w3;
        World w4;
        if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
            if (hasPerm(p, "lr.help")) {
                if (args.length != 1) {
                    try {
                        Help.send(p, Integer.parseInt(args[1]));
                        return true;
                    } catch (Exception e) {
                        Help.sendMsg(p, "§cPlease enter a valid number!", true);
                        return true;
                    }
                }
                Help.send(p, 1);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("master") || args[0].equalsIgnoreCase("m")) {
            if (hasPerm(p, "lr.master")) {
                Runtime r = Runtime.getRuntime();
                long chunks = 0;
                long entities = 0;
                long players = 0;
                long ram_used = ((r.totalMemory() - r.freeMemory()) / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE;
                long ram_total = (r.maxMemory() / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE;
                for (World w5 : Bukkit.getWorlds()) {
                    chunks += w5.getLoadedChunks().length;
                    entities += w5.getEntities().size();
                    players += w5.getPlayers().size();
                }
                double avPing = 0.0d;
                double pps = 0.0d;
                for (Player pp : Bukkit.getOnlinePlayers()) {
                    int ping = pp.getPing();
                    if (ping <= 10000) {
                        avPing += ping;
                        pps += 1.0d;
                    }
                }
                if (pps != 0.0d) {
                    avPing /= pps;
                }
                String s = p == null ? "" : BitString.SQUARE.getComp();
                StringBuilder sb = new StringBuilder();
                sb.append("\n                      §7§l--->> §6§lLaggRemover §7§l<<---§r");
                sb.append("\n§e").append(s).append(" Worlds:§7 ").append(Bukkit.getWorlds().size());
                sb.append("\n§e").append(s).append(" TPS:§7 ").append(Double.toString(DrewMath.round(TPS.getTPS(), 2)));
                sb.append("\n§e").append(s).append(" RAM:§7 ").append(NumberFormat.getNumberInstance().format(ram_used)).append(" / ").append(NumberFormat.getNumberInstance().format(ram_total)).append("MB (").append(Double.toString(DrewMath.round((ram_used / ram_total) * 100.0d, 1))).append("%)");
                sb.append("\n§e").append(s).append(" Loaded Chunks:§7 ").append(NumberFormat.getNumberInstance().format(chunks));
                sb.append("\n§e").append(s).append(" Entities:§7 ").append(NumberFormat.getNumberInstance().format(entities));
                sb.append("\n§e").append(s).append(" Players:§7 ").append(NumberFormat.getNumberInstance().format(players));
                sb.append("\n§e").append(s).append(" Avg. Ping:§7 ");
                // if (VCon.isSupported()) {
                    if (pps == 0.0d) {
                        sb.append("(no players)");
                    } else {
                        sb.append(NumberFormat.getNumberInstance().format(DrewMath.round(avPing, 1))).append("ms");
                    }
                // } else {
                //     sb.append("Not supported");
                // }
                Help.sendMsg(p, sb.toString(), false);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("ram")) {
            if (hasPerm(p, "lr.ram")) {
                Runtime r2 = Runtime.getRuntime();
                long ram_used2 = ((r2.totalMemory() - r2.freeMemory()) / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE;
                long ram_total2 = (r2.maxMemory() / LaggRemover.MEMORY_MBYTE_SIZE) / LaggRemover.MEMORY_MBYTE_SIZE;
                Help.sendMsg(p, "§eRAM:§7 " + NumberFormat.getNumberInstance().format(ram_used2) + " / " + NumberFormat.getNumberInstance().format(ram_total2) + "MB (" + Double.toString(DrewMath.round((ram_used2 / ram_total2) * 100.0d, 1)) + "%)", true);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
            Help.sendMsg(p, "§6§lLaggRemover\n §aAuthor: §edrew6017\n §aVersion: §e" + LaggRemover.lr.getDescription().getVersion() + "\n §aWebsite: §ehttp://dev.bukkit.org/bukkit-plugins/laggremover/\n §aDonate: §ehttps://goo.gl/1q3wN5\n §aInfo: §eLaggRemover §7was created by drew6017 to help server owners grow their servers to a professional level by improving performance and automating server care to keep players and owners happy.", false);
            return true;
        } else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("w")) {
            if (hasPerm(p, "lr.world")) {
                if (args.length == 2) {
                    w = Bukkit.getWorld(args[1]);
                } else if (p == null) {
                    LaggRemover.lr.getLogger().info("You must be a player to not specify an argument here.");
                    return true;
                } else {
                    w = p.getWorld();
                }
                if (w == null) {
                    Help.sendMsg(p, "§cThe world specified was invalid or not found.", true);
                    return true;
                }
                long size = DrewMath.getSize(w.getWorldFolder());
                String s2 = p == null ? "" : BitString.SQUARE.getComp();
                Help.sendMsg(p, "§6§lWorld " + w.getName() + "§r\n§e" + s2 + " Seed:§7 " + Long.toString(w.getSeed()) + "\n§e" + s2 + " Spawn Chunks:§7 " + (w.getKeepSpawnInMemory() ? "Yes" : "No") + "\n§e" + s2 + " Loaded Chunks:§7 " + NumberFormat.getNumberInstance().format(w.getLoadedChunks().length) + "\n§e" + s2 + " Entities:§7 " + NumberFormat.getNumberInstance().format(w.getEntities().size()) + "\n§e" + s2 + " Players:§7 " + NumberFormat.getNumberInstance().format(w.getPlayers().size()) + "\n§e" + s2 + " Time:§7 " + Long.toString(w.getTime()) + " (" + DrewMath.getTagForTime(w.getTime()) + ")\n§e" + s2 + " Size on Disk:§7 " + NumberFormat.getNumberInstance().format(size / 1000) + "KB (" + NumberFormat.getNumberInstance().format(size) + " bytes)", true);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("gc")) {
            if (hasPerm(p, "lr.gc")) {
                Help.sendMsg(p, "§eLaggRemover's garbage collector has been run and has freed §b" + NumberFormat.getNumberInstance().format(Protocol.run(new LRGC(), (Object[]) null).getData()[0]) + "MB§e of RAM on your server.", true);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("tps")) {
            if (hasPerm(p, "lr.tps")) {
                if (p == null) {
                    LaggRemover.lr.getLogger().info("TPS: " + Double.toString(DrewMath.round(TPS.getTPS(), 2)));
                    return true;
                }
                Help.sendMsg(p, "§eTPS: " + TPS.format(), true);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("modules") || args[0].equalsIgnoreCase("mo")) {
            if (hasPerm(p, "lr.modules")) {
                String[] ms = LaggRemover.getModulesList();
                Help.sendMsg(p, "§eModules (" + ms[1] + "): §a" + ms[0], true);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("protocol") || args[0].equalsIgnoreCase("pr")) {
            if (hasPerm(p, "lr.protocol")) {
                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("h")) {
                        if (args.length == 2) {
                            Help.sendMsg(p, "§eYou can use this command to view the help and description of all of the protocols currently loaded into LaggRemover. Simply type:\n /lr protocol(p) help(h) <protocol>", true);
                            return true;
                        }
                        String pname = args[2].toLowerCase();
                        Help.sendMsg(p, "§6§lProtocol " + pname + "§r\n" + Protocol.getProtocol(pname).help(), false);
                        return true;
                    } else if (args[1].equalsIgnoreCase("run") || args[1].equalsIgnoreCase("r")) {
                        Help.sendMsg(p, "§cThis feature is not fully supported yet. Expect bugs.", true);
                        if (args.length < 4) {
                            Help.sendMsg(p, "§cCorrect usage: /lr protocol(p) run(r) <protocol> <(Boolean)seeRawResult> <data>", true);
                            return true;
                        }
                        StringBuilder sb2 = new StringBuilder();
                        if (args.length > 4) {
                            for (int i = 5; i < args.length; i++) {
                                sb2.append(args[i]).append(" ");
                            }
                            String raw_fin2 = sb2.toString();
                            raw_fin = raw_fin2.substring(0, raw_fin2.length() - 1);
                        } else {
                            raw_fin = "{\"Delay\":\"false\"}";
                        }
                        LRProtocol pk = Protocol.getProtocol(args[2]);
                        final boolean seeResult = Boolean.parseBoolean(args[3].toLowerCase());
                        try {
                            DoubleVar<Object[], Boolean> dat = AnfoParser.parse(pk, raw_fin);
                            if (dat.getVar2().booleanValue()) {
                                Protocol.rund(pk, dat.getVar1(), new DelayedLRProtocolResult() { // from class: drew6017.lr.main.LRCommand.1
                                    @Override // drew6017.lr.api.proto.DelayedLRProtocolResult
                                    public void receive(LRProtocolResult result) {
                                        if (seeResult) {
                                            Help.sendProtocolResultInfo(p, result);
                                        }
                                    }
                                });
                                return true;
                            } else if (seeResult) {
                                Help.sendProtocolResultInfo(p, pk.run(dat.getVar1()));
                                return true;
                            } else {
                                return true;
                            }
                        } catch (AnfoParser.AnfoParseException | ParseException e2) {
                            Help.sendMsg(p, "§cError parsing protocol: §7" + e2.getMessage(), true);
                            Help.sendMsg(p, "§cMaybe you used the command invalidly? Correct usage: /lr protocol(p) run(r) <protocol> <(Boolean)seeRawResult> <data>", true);
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("l")) {
                        String[] ms2 = LaggRemover.getProtocolList();
                        Help.sendMsg(p, "§eProtocols (" + ms2[1] + "): §a" + ms2[0], true);
                        return true;
                    } else {
                        return true;
                    }
                }
                Help.sendMsg(p, "§cCorrect usage: /lr protocol(p) [help(h):run(r):list(l)] <options>", true);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("unload") || args[0].equalsIgnoreCase("u")) {
            if (hasPerm(p, "lr.unload")) {
                if (args.length == 2) {
                    World w6 = Bukkit.getWorld(args[1]);
                    if (w6 == null) {
                        Help.sendMsg(p, "§cWorld \"" + args[1] + "\" could not be found.", true);
                        return true;
                    } else if (w6.getPlayers().size() != 0) {
                        Help.sendMsg(p, "§cUnloading the chunks of worlds that contain players has been disabled due to bugs.", true);
                        return true;
                    } else {
                        int chunks2 = 0;
                        for (Chunk c : w6.getLoadedChunks()) {
                            w6.unloadChunk(c);
                            chunks2++;
                        }
                        Help.sendMsg(p, "§e" + Integer.toString(chunks2) + " chunks in world " + w6.getName() + " have been unloaded", true);
                        return true;
                    }
                }
                Help.sendMsg(p, "§cCorrect usage: /lr unload(u) <world>", true);
                return true;
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("ping") || args[0].equalsIgnoreCase("p")) {
            if (hasPerm(p, "lr.ping")) {
                if (args.length == 2) {
                    Player p1 = Bukkit.getPlayer(args[1]);
                    if (p1 != null) {
                        Help.sendMsg(p, "§ePlayer \"" + args[1] + "\" has a ping of §b" + p1.getPing() + "§ems", true);
                        return true;
                    }
                    Help.sendMsg(p, "§c\"" + args[1] + "\" is not a valid player.", true);
                    return true;
                } else if (args.length == 1) {
                    if (p == null) {
                        Help.sendMsg(null, "You must specify a player if using this command from the command line.", true);
                        return true;
                    }
                    Help.sendMsg(p, "§eYour current ping is §b" + p.getPing() + "§ems", true);
                    return true;
                } else {
                    Help.sendMsg(p, "§cCorrect usage: /lr ping(p) <player:none>", true);
                    return true;
                }
            }
            noPerm(p);
            return true;
        } else if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("count") || args[0].equalsIgnoreCase("ct")) {
            if (hasPerm(p, "lr.clear")) {
                boolean isCount = args[0].equalsIgnoreCase("count") || args[0].equalsIgnoreCase("ct");
                if (args.length >= 2) {
                    if (args[1].equalsIgnoreCase("items") || args[1].equalsIgnoreCase("i")) {
                        if (args.length == 2) {
                            int i2 = ((Integer) Protocol.run(new CCItems(), new Object[]{Boolean.valueOf(isCount)}).getData()[0]).intValue();
                            Help.sendMsg(p, new StringBuilder().append(isCount ? "§eThere " + (i2 == 1 ? "is " : "are ") : "§eRemoved ").append(NumberFormat.getNumberInstance(Locale.US).format(i2)).append(i2 == 1 ? " item" : " items").append(" on the ground between all worlds.").toString(), true);
                            return true;
                        }
                        try {
                            w2 = Bukkit.getWorld(args[2]);
                        } catch (Exception e3) {
                            w2 = null;
                        }
                        if (w2 == null) {
                            Help.sendMsg(p, "§cWorld \"" + args[2] + "\" was not found.", true);
                            return true;
                        }
                        int i3 = ((Integer) Protocol.run(new CCItems(), new Object[]{Boolean.valueOf(isCount), w2}).getData()[0]).intValue();
                        Help.sendMsg(p, new StringBuilder().append(isCount ? "§eThere " + (i3 == 1 ? "is " : "are ") : "§eRemoved ").append(NumberFormat.getNumberInstance(Locale.US).format(i3)).append(i3 == 1 ? " item" : " items").append(" in world \"").append(w2.getName()).append("\"").toString(), true);
                        return true;
                    } else if (args[1].equalsIgnoreCase("entities") || args[1].equalsIgnoreCase("e")) {
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("hostile") || args[2].equalsIgnoreCase("h")) {
                                ents = CCEntities.hostile;
                            } else if (args[2].equalsIgnoreCase("peaceful") || args[2].equalsIgnoreCase("p")) {
                                ents = CCEntities.peaceful;
                            } else if (!args[2].equalsIgnoreCase("all") && !args[2].equalsIgnoreCase("a")) {
                                Help.sendMsg(p, "§c" + args[2] + " is an invalid entity generality. Valid generalities are hostile, peaceful, or all.", true);
                                return true;
                            } else {
                                ents = null;
                            }
                            if (args.length == 3) {
                                int i4 = ((Integer) Protocol.run(new CCEntities(), new Object[]{Boolean.valueOf(isCount), ents}).getData()[0]).intValue();
                                Help.sendMsg(p, new StringBuilder().append(isCount ? "§eThere " + (i4 == 1 ? "is " : "are ") : "§eRemoved ").append(NumberFormat.getNumberInstance(Locale.US).format(i4)).append(i4 == 1 ? " entity" : " entities").append(" between all worlds.").toString(), true);
                                return true;
                            } else if (args.length == 4) {
                                try {
                                    w3 = Bukkit.getWorld(args[3]);
                                } catch (Exception e4) {
                                    w3 = null;
                                }
                                if (w3 == null) {
                                    Help.sendMsg(p, "§cWorld \"" + args[3] + "\" was not found.", true);
                                    return true;
                                }
                                int i5 = ((Integer) Protocol.run(new CCEntities(), new Object[]{Boolean.valueOf(isCount), ents, w3}).getData()[0]).intValue();
                                Help.sendMsg(p, new StringBuilder().append(isCount ? "§eThere " + (i5 == 1 ? "is " : "are ") : "§eRemoved ").append(NumberFormat.getNumberInstance(Locale.US).format(i5)).append(i5 == 1 ? " entity" : " entities").append(" from world \"").append(w3.getName()).append("\"").toString(), true);
                                return true;
                            } else {
                                Help.sendMsg(p, "§cCorrect usage: /lr " + (isCount ? "count(ct)" : "clear(c)") + " entities(e) [hostile(h):peaceful(p):all(a)] <world>", true);
                                return true;
                            }
                        }
                        Help.sendMsg(p, "§cCorrect usage: /lr " + (isCount ? "count(ct)" : "clear(c)") + " entities(e) [hostile(h):peaceful(p):all(a)] <world>", true);
                        return true;
                    } else if (args[1].equalsIgnoreCase("type") || args[1].equalsIgnoreCase("t")) {
                        if (args.length >= 3) {
                            if (args[2].equalsIgnoreCase("list") || args[2].equalsIgnoreCase("l")) {
                                StringBuilder sb3 = new StringBuilder();
                                EntityType[] allEnt = EntityType.values();
                                for (EntityType pe : allEnt) {
                                    sb3.append(pe.name());
                                    sb3.append("§7, §a");
                                }
                                String sbs = sb3.toString();
                                if (!sbs.equals("")) {
                                    sbs = sbs.substring(0, sbs.length() - 2);
                                }
                                Help.sendMsg(p, "§eEntity Types (" + Integer.toString(allEnt.length) + "): §a" + sbs, true);
                                return true;
                            } else if (args.length >= 4) {
                                List<EntityType> types = new ArrayList<>();
                                for (int i6 = 3; i6 < args.length; i6++) {
                                    try {
                                        EntityType var = EntityType.valueOf(args[i6].toUpperCase());
                                        types.add(var);
                                    } catch (IllegalArgumentException e5) {
                                        Help.sendMsg(p, "§c" + args[i6] + " is an invalid entity type. Please use /lr " + (isCount ? "count(ct)" : "clear(c)") + " type(t) list(l)", true);
                                        return true;
                                    }
                                }
                                EntityType[] ents2 = (EntityType[]) types.toArray(new EntityType[types.size()]);
                                if (args[2].equalsIgnoreCase("none") || args[2].equalsIgnoreCase("n")) {
                                    int i7 = ((Integer) Protocol.run(new CCEntities(), new Object[]{Boolean.valueOf(isCount), ents2}).getData()[0]).intValue();
                                    Help.sendMsg(p, new StringBuilder().append(isCount ? "§eThere " + (i7 == 1 ? "is " : "are ") : "§eRemoved ").append(NumberFormat.getNumberInstance().format(i7)).append(i7 == 1 ? " entity" : " entities").append(" by the ").append(ents2.length == 1 ? "type" : "types").append(" provided.").toString(), true);
                                    return true;
                                }
                                try {
                                    w4 = Bukkit.getWorld(args[2]);
                                } catch (Exception e6) {
                                    w4 = null;
                                }
                                if (w4 == null) {
                                    Help.sendMsg(p, "§cWorld \"" + args[2] + "\" was not found.", true);
                                    return true;
                                }
                                int i8 = ((Integer) Protocol.run(new CCEntities(), new Object[]{Boolean.valueOf(isCount), ents2, w4}).getData()[0]).intValue();
                                Help.sendMsg(p, new StringBuilder().append(isCount ? "§eThere " + (i8 == 1 ? "is " : "are ") : "§eRemoved ").append(NumberFormat.getNumberInstance().format(i8)).append(i8 == 1 ? " entity" : " entities").append(" by the ").append(ents2.length == 1 ? "type" : "types").append(" provided in world ").append(w4.getName()).append(".").toString(), true);
                                return true;
                            } else {
                                Help.sendMsg(p, "§cPlease list entity types to work with if you are not using the list(l) sub-command. Ex: SNOWBALL FIREWORK", true);
                                return true;
                            }
                        }
                        Help.sendMsg(p, "§cCorrect usage: /lr " + (isCount ? "count(ct)" : "clear(c)") + " type(t) [list(l):none(n):<world>] <none:types>", true);
                        return true;
                    }
                }
                Help.sendMsg(p, "§cCorrect usage: /lr " + (isCount ? "count(ct)" : "clear(c)") + " [items(i):entities(e):type(t)] <options>", true);
                return true;
            }
            noPerm(p);
            return true;
        } else {
            return false;
        }
    }

    private static boolean hasPerm(Player p, String msg) {
        if (p == null) {
            return true;
        }
        return p.hasPermission(msg);
    }

    private static void noPerm(Player p) {
        Help.sendMsg(p, "§cYou do not have permission to use this command. Please contact your administrator if you believe this to be an error.", true);
    }
}
