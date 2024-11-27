package org.firstinspires.ftc.teamcode;

import static java.lang.Math.abs;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Mecanum Drive")
public class MecanumDriveTutorial extends OpMode {
    DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
    TelemetryPacket packet;
    FtcDashboard dash;
    // Staffing is left & right
    double drive, turn, strafe, fLeftPower, fRightPower, bLeftPower, bRightPower;

    double[] appliedPowers;

    @Override
    public void init() {
        packet = new TelemetryPacket();
        dash = FtcDashboard.getInstance();

        //drive = left joystick y
        //turn = right joystick x
        //strafe = left joystick x

        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftMotor = hardwareMap.get(DcMotor.class, "rearLeft");
        backRightMotor = hardwareMap.get(DcMotor.class, "rearRight");
        telemetry.addData("Hardware: ", "Initialized");
        telemetry.update();

        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    @Override
    public void loop() {
        drive = gamepad1.left_stick_y;
        turn = gamepad1.right_stick_x * -1;
        strafe = gamepad1.left_stick_x * -1;
        //strafe
        fLeftPower = drive + turn + strafe;
        fRightPower = drive - turn - strafe;
        bLeftPower = drive + turn - strafe;
        bRightPower = drive - turn + strafe;

        appliedPowers = scalePowers(fLeftPower, fRightPower, bLeftPower, bRightPower);

        frontLeftMotor.setPower(appliedPowers[0]);
        frontRightMotor.setPower(appliedPowers[1]);
        backLeftMotor.setPower(appliedPowers[2]);
        backRightMotor.setPower(appliedPowers[3]);

        packet.put("powers: ",appliedPowers);
        dash.sendTelemetryPacket(packet);

    }

    public double[] scalePowers(double fLeftPower, double fRightPower, double bLeftPower, double bRightPower){
        double max = Math.max(Math.max(Math.abs(fLeftPower), Math.abs(fRightPower)), Math.max(Math.abs(bLeftPower), Math.abs(bRightPower)));
        if (max > 1){
            fLeftPower /= max;
            fRightPower /= max;
            bLeftPower /= max;
            bRightPower /= max;
        }

        double [] motorPowers = new double[]{fLeftPower,fRightPower,bLeftPower,bRightPower};
        return  motorPowers;
    }
}
