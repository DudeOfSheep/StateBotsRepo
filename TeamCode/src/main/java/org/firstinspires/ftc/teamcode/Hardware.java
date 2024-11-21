package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

public class Hardware {

    private HardwareMap hardwareMap;
    private DcMotorEx dt_back_left, dt_back_right, dt_front_left, dt_front_right, pivot;

    double PIVOT_MAX_ANGLE, PIVOT_MIN_ANGLE, CLAW_MAX_POS, CLAW_MIN_POS;
    public ServoImplEx claw,swivel;
    // instances of all hardware
    // Hardware map
    Hardware (HardwareMap hardwareMap_, double CLAW_MIN_POS, double CLAW_MAX_POS, double PIVOT_MIN_ANGLE, double PIVOT_MAX_ANGLE){
        this.hardwareMap = hardwareMap_;
        this.PIVOT_MAX_ANGLE = PIVOT_MAX_ANGLE;
        this.PIVOT_MIN_ANGLE = PIVOT_MIN_ANGLE;
        this.CLAW_MAX_POS = CLAW_MAX_POS;
        this.CLAW_MIN_POS = CLAW_MIN_POS;
    }
    public enum Claw_machine {
        TOP("TOP",45),
        MID("MID",90),
        LOW("BOTTOM",135),
        HOLSTER("HOLSTER",-90);

        private final String ID;
        private final int TARGET;
        Claw_machine(String ID, int TARGET){
            this.ID = ID;
            this.TARGET = TARGET;
        }
        public String get_ID() {
            return this.ID;
        }
        public int get_target_pos(){return this.TARGET;}
    }


    public void init (){
        // The purpose of hardware is to gather together each of the components,
        dt_front_left = hardwareMap.get(DcMotorEx.class, "frontLeft");
        dt_front_right = hardwareMap.get(DcMotorEx.class, "frontRight");
        dt_back_left = hardwareMap.get(DcMotorEx.class, "rearLeft");
        dt_back_right = hardwareMap.get(DcMotorEx.class, "rearRight");
        pivot = hardwareMap.get(DcMotorEx.class, "pivot");
        claw = hardwareMap.get(ServoImplEx.class, "claw");
        swivel = hardwareMap.get(ServoImplEx.class, "swivel");
        /* claw.setPosition(CLAW_MIN_POS);
        claw.scaleRange(CLAW_MIN_POS,CLAW_MAX_POS); */
    }

    public boolean get_essential_object_status() {return (hardwareMap == null);}

    double abs (double a) {return (a <= 0.0d) ? 0.0d - a : a;}
    double max (double a, double b) {return (a > b) ? b : a;}
    void drive (double spd_h, double spd_v, double angle, double speed) {
        /* Math happening here:
        * We get the sum of all the inputs, with the formula determined by the position of the wheel
        * Then divide that by the absolute sum of all inputs, to a maximum of 1, to maintain speed
        *   at all angles.
        * And finally multiply it by a speed limiter (<1), so that players do not lose control.
        * Note: The formula shown here is actually reversed
        * */
        double limit = max(abs(spd_h) + abs(spd_v) + abs(angle), 1.0);
        dt_front_left.setPower(
                ((spd_v + spd_h - angle) / limit) * speed);
        dt_front_right.setPower(
                ((spd_v - spd_h + angle) / limit) * speed);
        dt_back_left.setPower(
                ((spd_v - spd_h - angle) / limit) * speed);
        dt_back_right.setPower(
                ((spd_v + spd_h + angle) / limit) * speed);
    }

    public void set_pivot_pos(int pos, double power) {
        pivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pivot.setTargetPosition(pos);
        pivot.setPower(power);
    }

    public boolean dt_busy() { // STINKS!
        if (dt_front_left.isBusy() || dt_front_right.isBusy() || dt_back_right.isBusy() || dt_back_left.isBusy()) {
            return true;
        }

        return false;
    }

    // If deleted, the claw cannot move.
    public void move_pivot_temp(double power) {
        pivot.setPower(power);
    }

    // Literally just checks if the pivot is in motion.
    public boolean check_pivot(){
        return pivot.isBusy();
    }
    public int get_pivot_pos(){
        return pivot.getCurrentPosition();
    }

    public double get_claw_pos(){
        return claw.getPosition();
    }
    public void shift_swivel(double pos) {
        swivel.setPosition(pos);
    }
}


