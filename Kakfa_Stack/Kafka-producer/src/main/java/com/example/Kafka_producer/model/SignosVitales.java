package com.example.Kafka_producer.model;

public class SignosVitales {
    private String pacienteId;
    private int frecuenciaCardiaca;
    private String presionArterial;
    private double temperatura;
    private int saturacionOxigeno;

    public SignosVitales() {
    }

    public SignosVitales(String pacienteID, int frecuenciaCardiaca, String presionArterial, double temperatura,
                        int saturacionOxigeno) {
        this.pacienteId = pacienteID;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.presionArterial = presionArterial;
        this.temperatura = temperatura;
        this.saturacionOxigeno = saturacionOxigeno;
    }

    public int getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public String getPresionArterial() {
        return presionArterial;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public int getSaturacionOxigeno() {
        return saturacionOxigeno;
    }

    public void setFrecuenciaCardiaca(int frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public void setPresionArterial(String presionArterial) {
        this.presionArterial = presionArterial;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public void setSaturacionOxigeno(int saturacionOxigeno) {
        this.saturacionOxigeno = saturacionOxigeno;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"pacienteId\":\"" + pacienteId + "\"," +
                "\"frecuenciaCardiaca\":" + frecuenciaCardiaca + "," +
                "\"presionArterial\":\"" + presionArterial + "\"," +
                "\"temperatura\":" + temperatura + "," +
                "\"saturacionOxigeno\":" + saturacionOxigeno +
                "}";
    }
}
