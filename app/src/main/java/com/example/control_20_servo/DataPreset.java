package com.example.control_20_servo;

public class DataPreset {
    private int id;
    private String namePreset;
    private int[] angleServo = new int[20];
    public DataPreset(int id, String namePreset, int[] angleServo){
        this.id = id;
        this.namePreset = namePreset;
        for(int i = 0; i < angleServo.length; i++){
            this.angleServo[i] = angleServo[i];
        }
    }
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.namePreset;
    }

    public void setName(String name) {
        this.namePreset = name;
    }

    public int[] getAngle() {
        return this.angleServo;
    }

    public void setAngle(int[] angle) {
        for(int i = 0; i < angle.length; i++){
            this.angleServo[i] = angleServo[i];
        }
    }
}
