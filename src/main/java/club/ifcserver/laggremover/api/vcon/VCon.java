package club.ifcserver.laggremover.api.vcon;

import club.ifcserver.laggremover.api.vcon.v._Unknown;
import club.ifcserver.laggremover.api.vcon.v.v1_10_R1;
import club.ifcserver.laggremover.api.vcon.v.v1_11_R1;
import club.ifcserver.laggremover.api.vcon.v.v1_12_R1;
import club.ifcserver.laggremover.api.vcon.v.v1_13_R1;
import club.ifcserver.laggremover.api.vcon.v.v1_13_R2;
import club.ifcserver.laggremover.api.vcon.v.v1_14_R1;
import club.ifcserver.laggremover.api.vcon.v.v1_7_R4;
import club.ifcserver.laggremover.api.vcon.v.v1_8_R1;
import club.ifcserver.laggremover.api.vcon.v.v1_8_R2;
import club.ifcserver.laggremover.api.vcon.v.v1_8_R3;
import club.ifcserver.laggremover.api.vcon.v.v1_9_R1;
import club.ifcserver.laggremover.api.vcon.v.v1_9_R2;
import org.bukkit.Bukkit;

/* loaded from: LaggRemover-2.0.6.jar:drew6017/lr/api/vcon/VCon.class */
public class VCon {
    public static final VersionAdapter a;
    private static boolean SUPPORTED;

    public static boolean isSupported() {
        return SUPPORTED;
    }

    static {
        SUPPORTED = true;
        String version = getVersion();
        boolean z = true;
        switch (version.hashCode()) {
            case -1497224837:
                if (version.equals("v1_10_R1")) {
                    z = true;
                    break;
                }
                break;
            case -1497195046:
                if (version.equals("v1_11_R1")) {
                    z = true;
                    break;
                }
                break;
            case -1497165255:
                if (version.equals("v1_12_R1")) {
                    z = true;
                    break;
                }
                break;
            case -1497135464:
                if (version.equals("v1_13_R1")) {
                    z = true;
                    break;
                }
                break;
            case -1497135463:
                if (version.equals("v1_13_R2")) {
                    z = true;
                    break;
                }
                break;
            case -1497105673:
                if (version.equals("v1_14_R1")) {
                    z = true;
                    break;
                }
                break;
            case -1156452754:
                if (version.equals("v1_7_R4")) {
                    z = false;
                    break;
                }
                break;
            case -1156422966:
                if (version.equals("v1_8_R1")) {
                    z = true;
                    break;
                }
                break;
            case -1156422965:
                if (version.equals("v1_8_R2")) {
                    z = true;
                    break;
                }
                break;
            case -1156422964:
                if (version.equals("v1_8_R3")) {
                    z = true;
                    break;
                }
                break;
            case -1156393175:
                if (version.equals("v1_9_R1")) {
                    z = true;
                    break;
                }
                break;
            case -1156393174:
                if (version.equals("v1_9_R2")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                a = new v1_7_R4();
                return;
            case true:
                a = new v1_8_R1();
                return;
            case true:
                a = new v1_8_R2();
                return;
            case true:
                a = new v1_8_R3();
                return;
            case true:
                a = new v1_9_R1();
                return;
            case true:
                a = new v1_9_R2();
                return;
            case true:
                a = new v1_10_R1();
                return;
            case true:
                a = new v1_11_R1();
                return;
            case true:
                a = new v1_12_R1();
                return;
            case true:
                a = new v1_13_R1();
                return;
            case true:
                a = new v1_13_R2();
                return;
            case true:
                a = new v1_14_R1();
                return;
            default:
                SUPPORTED = false;
                System.out.println("WARNING: This version is not fully supported. Expect some form of incompatibility.");
                a = new _Unknown();
                return;
        }
    }

    private static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf(46) + 1);
    }
}
