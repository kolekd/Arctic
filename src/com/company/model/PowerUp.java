package com.company.model;

public class PowerUp extends Wall {

    public static class Builder {

        private boolean isPlaced;
        private int posX;
        private boolean isPowerUp;
        private boolean isBreaker;
        private boolean isShooter;

        public Builder(boolean isPlaced, int posX) {
            this.isPlaced = isPlaced;
            this.posX = posX;
            this.isPowerUp = true;
        }

        public Builder makeBreaker() {
            this.isBreaker = true;
            this.isShooter = false;
            return this;
        }

        public Builder makeShooter() {
            this.isShooter = true;
            this.isBreaker = false;
            return this;
        }

        public PowerUp build() {
            PowerUp powerUp = new PowerUp();
            powerUp.isPlaced = this.isPlaced;
            powerUp.posX = this.posX;
            powerUp.isPowerUp = this.isPowerUp;
            powerUp.isBreaker = this.isBreaker;
            powerUp.isShooter = this.isShooter;

            return powerUp;
        }

    }

    private boolean isBreaker;
    private boolean isShooter;

    private PowerUp() {
        super();
    }

    public boolean isBreaker() {
        return isBreaker;
    }

    public void setBreaker(boolean breaker) {
        isBreaker = breaker;
    }

    public boolean isShooter() {
        return isShooter;
    }

    public void setShooter(boolean shooter) {
        isShooter = shooter;
    }

    public String toString() {
        if(isShooter) {
            return "shooter";
        }

        if(isBreaker) {
            return "breaker";
        }

        return "";
    }
}
