package dev.m4nd3l.craftmine.entities;

import dev.m4nd3l.craftmine.coordinates.EntityCoordinates;

public class Hitbox {
    private EntityCoordinates down1, down2, down3, down4;
    private EntityCoordinates up1, up2, up3, up4;

    public Hitbox(EntityCoordinates down1, EntityCoordinates down2, EntityCoordinates down3, EntityCoordinates down4,
                  EntityCoordinates up1, EntityCoordinates up2, EntityCoordinates up3, EntityCoordinates up4) {
        this.down1 = down1; this.down2 = down2; this.down3 = down3; this.down4 = down4;
        this.up1 = up1;   this.up2 = up2;   this.up3 = up3;   this.up4 = up4;
    }

    public double[] generateVertices() {
        return new double[] {
                down1.getX(), down1.getY(), down1.getZ(), down2.getX(), down2.getY(), down2.getZ(),
                down2.getX(), down2.getY(), down2.getZ(), down3.getX(), down3.getY(), down3.getZ(),
                down3.getX(), down3.getY(), down3.getZ(), down4.getX(), down4.getY(), down4.getZ(),
                down4.getX(), down4.getY(), down4.getZ(), down1.getX(), down1.getY(), down1.getZ(),

                up1.getX(), up1.getY(), up1.getZ(), up2.getX(), up2.getY(), up2.getZ(),
                up2.getX(), up2.getY(), up2.getZ(), up3.getX(), up3.getY(), up3.getZ(),
                up3.getX(), up3.getY(), up3.getZ(), up4.getX(), up4.getY(), up4.getZ(),
                up4.getX(), up4.getY(), up4.getZ(), up1.getX(), up1.getY(), up1.getZ(),

                down1.getX(), down1.getY(), down1.getZ(), up1.getX(), up1.getY(), up1.getZ(),
                down2.getX(), down2.getY(), down2.getZ(), up2.getX(), up2.getY(), up2.getZ(),
                down3.getX(), down3.getY(), down3.getZ(), up3.getX(), up3.getY(), up3.getZ(),
                down4.getX(), down4.getY(), down4.getZ(), up4.getX(), up4.getY(), up4.getZ()
        };
    }
}
