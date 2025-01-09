package com.app.meurole.model;

public class Event {
    private String eventId;
    private String nome;
    private String data;
    private String local;
    private double valorInscricao;

    // Construtor vazio exigido pelo Firebase
    public Event() {
    }

    public Event(String eventId, String nome, String data, String local, double valorInscricao) {
        this.eventId = eventId;
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.valorInscricao = valorInscricao;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public double getValorInscricao() {
        return valorInscricao;
    }

    public void setValorInscricao(double valorInscricao) {
        this.valorInscricao = valorInscricao;
    }
}
