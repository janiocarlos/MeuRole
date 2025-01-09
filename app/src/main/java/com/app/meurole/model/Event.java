package com.app.meurole.model;

public class Event {
    private String eventId;
    private String nome;
    private String data;
    private String local;
    private String tipo;
    private double valorInscricao;
    private String thumbUrl;

    // Construtor vazio exigido pelo Firebase
    public Event() {
    }

    public Event(String eventId, String nome, String data, String local, String tipo, double valorInscricao, String thumbUrl) {
        this.eventId = eventId;
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.tipo = tipo;
        this.valorInscricao = valorInscricao;
        this.thumbUrl = thumbUrl;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
