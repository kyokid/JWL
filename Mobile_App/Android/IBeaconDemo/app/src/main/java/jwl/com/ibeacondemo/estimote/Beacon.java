package jwl.com.ibeacondemo.estimote;

import com.estimote.sdk.Region;

import java.util.UUID;

/**
 * Created by HaVH on 1/15/17.
 */

public class Beacon {
    private UUID proximityUUID;
    private int major;
    private int minor;

    public Beacon(UUID proximityUUID, int major, int minor) {
        this.proximityUUID = proximityUUID;
        this.major = major;
        this.minor = minor;
    }

    public Beacon(String UUIDString, int major, int minor) {
        this(UUID.fromString(UUIDString), major, minor);
    }

    public Region toBeaconRegion() {
        return new Region(toString(), getProximityUUID(), getMajor(), getMinor());
    }

    public UUID getProximityUUID() {
        return proximityUUID;
    }

    public void setProximityUUID(UUID proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    @Override
    public String toString() {
        return getProximityUUID().toString() + ":" + getMajor() + ":" + getMinor();
    }
}
