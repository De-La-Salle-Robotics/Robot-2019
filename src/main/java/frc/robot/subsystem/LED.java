package frc.robot.subsystem;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.LEDChannel;

public class LED {

    private CANifier ledController;

    public LED(CANifier ledController) {
        this.ledController = ledController;
    }

    public void lighting(double red, double green, double blue) {
        ledController.setLEDOutput(red, LEDChannel.LEDChannelB);
        ledController.setLEDOutput(blue, LEDChannel.LEDChannelA);
        ledController.setLEDOutput(green, LEDChannel.LEDChannelC);
    }

}
