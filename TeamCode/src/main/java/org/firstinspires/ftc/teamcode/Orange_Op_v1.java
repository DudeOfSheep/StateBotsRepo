package org.firstinspires.ftc.teamcode;

import androidx.lifecycle.GenericLifecycleObserver;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.acmerobotics.roadrunner.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TeleOp
public class Orange_Op_v1 extends OpMode {
    private Hardware hardware; // Contains all functions to control the robot's hardware.
    private Gamepad gamepad1_copy,gamepad2_copy;
    private Hardware.Claw_machine claw_state; // You can safely ignore this.


    @Override
    public void init() {
        /* Initialise the Hardware Object, dedicate variables, and make sure the robot doesn't make
            any sudden moves. */
        hardware = new Hardware(hardwareMap,0.0,90.0,0.0,1.0);

        /* The variables of gamepad buttons are checked and changed each frame by the SDK.
            However, frames are processed fast, so a typical tap of the button will last a few
            frames. This means any if statements that check if a button is pressed will run
            multiple times, which can lead to errant behaviour. */
        gamepad1_copy = new Gamepad();
        gamepad2_copy = new Gamepad();
        claw_state = Hardware.Claw_machine.HOLSTER;
        hardware.init();

        /* Control hub doesn't reset how much power is to be sent to the motors when shutoff
            so we have to do it ourselves. */
        hardware.drive(0.0,0.0,0.0,0.0);

    }
    @Override
    public void loop() {
        // Info sent to Driver Station to be shown to players
        // Put it in one place so it stays organized.
        telemetry.addData("VERTICAL: ", gamepad1.left_stick_y);
        telemetry.addData("HORIZONTAL: ", gamepad1.left_stick_x);
        telemetry.addData("ROTATION: ", gamepad1.right_stick_x);
        telemetry.addData("IN MOTION: ", hardware.dt_busy());
        telemetry.addData("CLAW BUSY: ", hardware.check_pivot());
        telemetry.addData("HARDWARE: ", hardwareMap.getAll(LynxModule.class));


        // Why are the inputs mixed around? Because I did the formula wrong.
        hardware.drive(-gamepad1.left_stick_y, -gamepad1.right_stick_x, gamepad1.left_stick_y,0.75);

        // Sets the position of the claw servo to the direct output of the trigger.
        hardware.claw.setPosition(gamepad2.right_trigger);

        // Right bumper rotates the entire claw forward, and the left rotates it backward.
        if (gamepad2.right_bumper && !gamepad2.left_bumper) {
            hardware.move_pivot_temp(1);
        } else if (gamepad2.left_bumper) {
            hardware.move_pivot_temp(-1);
        }
        else { // Since the pivot is a motor, we have to set the power to 0.0, or else.
            hardware.move_pivot_temp(0.0);
        }

        // Shifts the swivel to the Center, Left, and Right.
        if (gamepad2.a) {
            hardware.shift_swivel(0.0);
        }
        else if (gamepad2.b) { // Currently Semi-Broken, only moves halfway to the left.
            hardware.shift_swivel(-1.0);
        } else if (gamepad2.x) {
            hardware.shift_swivel(0.5);
        }


        // Must come last, or else the robot gets silly.
        // The states of the controllers during this frame are copied for logic used in the next frame.
        gamepad1_copy.copy(gamepad1);
        gamepad2_copy.copy(gamepad2);
        telemetry.update();
    }

    // Everything we need to do to make the robot fully stop.
    @Override
    public void stop() {
        hardware.set_pivot_pos(hardware.get_pivot_pos(), 0.0);

    }


}
